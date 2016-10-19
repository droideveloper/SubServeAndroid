/*
 * SubServe Android Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.sub.common;

import android.support.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import okhttp3.ResponseBody;
import org.fs.common.BusManager;
import org.fs.exception.AndroidException;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.sub.model.SrtEntity;
import org.fs.sub.net.IDownloadHelper;
import org.fs.sub.net.IServiceEndpoint;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public final class DownloadHelper implements IDownloadHelper {

  private final static int CHUNK_SIZE = 4 * 1024;

  private final static String KEY_CONTENT_TYPE = "Content-Type";
  private final static String KEY_CONTENT_DISP = "Content-Disposition";

  private final static String CONTENT_VALUE_ZIP = "application/zip";
  private final static String CONTENT_VALUE_GZ = "application/x-gzip";
  private final static String CONTENT_VALUE_GZ_2 = "application/force-download";

  private final static int TYPE_ZIP = 0;
  private final static int TYPE_GZIP = 1;
  private final static int TYPE_GZIP_2 = 2;
  private final static int TYPE_NONE = -1;

  private final static String REGEX_STRING = "\"(.+?)\"";
  private final static Pattern fileNameRegex = Pattern.compile(REGEX_STRING);

  private Subscription subscription = null;
  private IDatabaseHelper databaseProxy = null;

  public DownloadHelper(IDatabaseHelper databaseProxy) {
    this.databaseProxy = databaseProxy;
  }

  /**
   * this is the magic method where we download subtitles through the interface and push them into
   * view system
   */
  @Override public synchronized void withServiceProxyAndUrlAndHash(IServiceEndpoint serviceProxy,
      String URL, String hash, String lang) {

    subscription = createWithURLAndServiceProxyAndHash(URL, serviceProxy, hash, lang).subscribeOn(
        Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(successCallback(), errorCallback());
  }

  @Override
  public synchronized void withServiceProxyAndUrlAndImdb(IServiceEndpoint serviceProxy, String URL,
      String imdb, String lang) {

    subscription = createWithURLAndServiceProxyAndImdb(URL, serviceProxy, imdb, lang).subscribeOn(
        Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(successCallback(), errorCallback());
  }

  /**
   *
   * @param URL
   * @param serviceProxy
   * @return
   */
  private Observable<SrtEntity> createWithURLAndServiceProxyAndHash(String URL, IServiceEndpoint serviceProxy, String hash, String lang) {
    return Observable.just(new Object[] { URL, serviceProxy, hash, lang }).flatMap(new Func1<Object[], Observable<SrtEntity>>() {
      @Override public Observable<SrtEntity> call(Object[] objects) {
        try {
          String URL = (String) objects[0];//URL
          IServiceEndpoint serviceProxy = (IServiceEndpoint) objects[1];//ServiceProxy instance

          SrtEntity srtEntity = parseResponse(serviceProxy.withDownloadUrl(URL).execute());
          srtEntity.hash((String) objects[2]);//hash to save in db
          srtEntity.lang((String) objects[3]);//lang to save in db

          return Observable.just(srtEntity);
        } catch (IOException ioError) {
          throw new AndroidException(ioError);
        }
      }
    });
  }

  private Observable<SrtEntity> createWithURLAndServiceProxyAndImdb(String URL, IServiceEndpoint serviceProxy, String imdb, String lang) {
    return Observable.just(new Object[] { URL, serviceProxy, imdb, lang }).flatMap(new Func1<Object[], Observable<SrtEntity>>() {
      @Override public Observable<SrtEntity> call(Object[] objects) {
        try {
          String URL = (String) objects[0];//URL
          IServiceEndpoint serviceProxy = (IServiceEndpoint) objects[1];//ServiceProxy instance

          SrtEntity srtEntity = parseResponse(serviceProxy.withDownloadUrl(URL).execute());
          srtEntity.imdb((String) objects[2]);//imdb to save in db
          srtEntity.lang((String) objects[3]);//lang to save in db

          return Observable.just(srtEntity);
        } catch (IOException ioError) {
          throw new AndroidException(ioError);
        }
      }
    });
  }

  /**
   *
   * @return
   */
  private Action1<SrtEntity> successCallback() {
    return new Action1<SrtEntity>() {
      @Override public void call(SrtEntity srtEntity) {
        if (subscription != null) {
          subscription.unsubscribe();
        }
        //save it into local storage
        if (databaseProxy != null) {
          databaseProxy.withUpdateOrSaveSrtEntity(srtEntity);
        }
        //say that we found it
        BusManager.send(new SrtEntityFoundEvent(srtEntity, SrtEntityFoundEvent.REMOTE));
      }
    };
  }

  /**
   *
   * @return
   */
  private Action1<Throwable> errorCallback() {
    return new Action1<Throwable>() {
      @Override public void call(Throwable throwable) {
        if (subscription != null) {
          subscription.unsubscribe();
        }
        //we can't grab file so sorry
        BusManager.send(new SrtEntityNotFoundEvent(SrtEntityNotFoundEvent.REMOTE));
      }
    };
  }

  /**
   * Parsing response into next level of data
   *
   * @throws IOException
   */
  private SrtEntity parseResponse(Response<ResponseBody> response) throws IOException {
    if (response.isSuccessful()) {
      int type = parseTypeHeader(response.headers().get(KEY_CONTENT_TYPE));
      String fileName = parseDispositionHeader(response.headers().get(KEY_CONTENT_DISP));
      switch (type) {
        case TYPE_ZIP: {
          byte[] srt = parseZipInput(response);
          SrtEntity srtEntity = new SrtEntity();
          srtEntity.data(srt);
          srtEntity.name(fileName);
          return srtEntity;
        }

        case TYPE_GZIP:
        case TYPE_GZIP_2: {
          byte[] srt = parseGzipInput(response);
          SrtEntity srtEntity = new SrtEntity();
          srtEntity.data(srt);
          srtEntity.name(fileName);
          return srtEntity;
        }

        default:
          throw new IOException("unknown stream type, not zip or gz then what?");
      }
    } else {
      throw new IOException("we can not reach file on remote, server not accessible");
    }
  }

  /**
   * if we are not allowed to allocate as many as file size we are screwed
   *
   * @throws IOException
   */
  private byte[] parseZipInput(Response response) throws IOException {
    ZipInputStream zis = null;
    ByteArrayOutputStream out = null;
    try {
      zis = new ZipInputStream(parse(response).byteStream());
      out = new ByteArrayOutputStream();
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        String fileName = entry.getName();
        if (fileName.contains(".srt")) {
          int cursor = 0;
          int read;
          byte[] buffer = new byte[CHUNK_SIZE];
          int size = (int) entry.getSize();
          while ((read = zis.read(buffer)) != -1 && cursor < size) {
            out.write(buffer, 0, read);
            cursor += read;
          }
          break;
        }
      }
      return out.toByteArray();
    } finally {
      if (zis != null) {
        zis.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * if we are not allowed to allocate as file memory we are screwed.
   *
   * @throws IOException
   */
  private byte[] parseGzipInput(Response response) throws IOException {
    InputStream is = null;
    ByteArrayOutputStream out = null;
    try {
      is = new GZIPInputStream(parse(response).byteStream());
      out = new ByteArrayOutputStream();

      int cursor;
      byte[] buffer = new byte[CHUNK_SIZE];
      while ((cursor = is.read(buffer)) != -1) {
        out.write(buffer, 0, cursor);
      }
      return out.toByteArray();
    } finally {
      if (is != null) {
        is.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   *
   * @param response
   * @return
   */
  private ResponseBody parse(Response response) {
    return (ResponseBody) response.body();
  }

  /**
   *
   * @param hValue
   * @return
   */
  private String parseDispositionHeader(@NonNull final String hValue) {
    final Matcher matcher = fileNameRegex.matcher(hValue);
    return matcher.find() ? matcher.group() : null;
  }

  /**
   *
   * @param hValue
   * @return
   */
  private int parseTypeHeader(@NonNull final String hValue) {
    if (CONTENT_VALUE_GZ.equalsIgnoreCase(hValue)) {
      return TYPE_GZIP;
    } else if (CONTENT_VALUE_GZ_2.equalsIgnoreCase(hValue)) {
      return TYPE_GZIP_2;
    } else if (CONTENT_VALUE_ZIP.equalsIgnoreCase(hValue)) {
      return TYPE_ZIP;
    }
    return TYPE_NONE;
  }
}
