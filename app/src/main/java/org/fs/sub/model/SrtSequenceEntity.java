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
package org.fs.sub.model;

import android.os.Parcel;
import android.text.Html;
import android.text.SpannableString;

import org.fs.core.AbstractEntity;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.util.PreconditionUtility;
import org.fs.util.StringUtility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class SrtSequenceEntity extends AbstractEntity {

  private final static String TIME_SPLIT = " --> ";
  private final static String TIME_FORMAT = "HH:mm:ss,SSS";
  private final static SimpleDateFormat timeParser = new SimpleDateFormat(TIME_FORMAT, Locale.US);

  static {
    timeParser.setTimeZone(TimeZone.getTimeZone("GMT"));//without this Date.getTime(); returns weird value.
  }

  /*
      hash table for iso lang code matching for results
   */
  private final static Map<String, String> LANG_TABLE;

  static {
    LANG_TABLE = new HashMap<>();
    LANG_TABLE.put("fre", "ISO-8859-16");
    LANG_TABLE.put("fin", "ISO-8859-15");
    LANG_TABLE.put("est", "ISO-8859-15");
    LANG_TABLE.put("hun", "ISO-8859-2");
    LANG_TABLE.put("rum", "ISO-8859-2");
    LANG_TABLE.put("tur", "ISO-8859-9");
    LANG_TABLE.put("jpn", "ISO-2022-JP");
    LANG_TABLE.put("nor", "ISO-8859-1");
    LANG_TABLE.put("eng", "UTF-8");
  }

  private int index;
  private long begin;
  private long end;
  private SpannableString text;

  /**
   * @return SrtSequence entity or throw exception
   */
  public static SrtSequenceEntity create(String... data) {
    PreconditionUtility.checkNotNull(data, "you are screwed");
    int size = data.length;
    StringBuilder str = new StringBuilder(128);
    SrtSequenceEntity entity = new SrtSequenceEntity();
    for (int i = 0; i < size; i++) {
      if (i == 0) {
        entity.setIndex(toInteger(data[i]));
      } else if (i == 1) {
        entity.setBegin(toTime(data[i], 0));
        entity.setEnd(toTime(data[i], 1));
      } else if (i >= 2) {
        str.append(Html.fromHtml(data[i]));//better there are some html tags like <i> or <b> for text usage
        str.append("\n");
      }
    }
    entity.setText(new SpannableString(str.toString()));
    return entity;
  }

  /**
   * @return LinkedList instance
   * @throws IOException
   */
  public static LinkedList<SrtSequenceEntity> create(byte[] array, String lang) throws IOException {
    LinkedList<SrtSequenceEntity> queue = new LinkedList<>();
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(array);
      //if such key exists get charset else use defaults....
      String charset = LANG_TABLE.containsKey(lang) ? LANG_TABLE.get(lang) : "UTF-8";
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));//bug fix since Turkish char not supported
      List<String> strArray = new ArrayList<>();
      String read;
      for (; (read = reader.readLine()) != null; ) {
        if (StringUtility.isNullOrEmpty(read)) {
          String[] strings = new String[strArray.size()];
          for (int i = 0; i < strArray.size(); i++) {
            strings[i] = strArray.get(i);
          }
          queue.add(create(strings));
          strArray = new ArrayList<>();
        } else {
          strArray.add(read);
        }
      }
      return queue;
    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

  /**
   * computes if passed time as long equals then object's alive time
   *
   * @param ms time
   * @param entity entity
   * @return true or false depends of variables passed in method
   */
  public static boolean isBetween(long ms, SrtSequenceEntity entity) {
    return ms >= entity.getBegin() && ms <= entity.getEnd();
  }

  /**
   * computes if passed time as long lower then object's alive time
   */
  public static boolean isNotYet(long ms, SrtSequenceEntity entity) {
    return ms < entity.getBegin() && ms < entity.getEnd();
  }

  /**
   * computes if passed time as long bigger then object's alive time
   */
  public static boolean isOverDue(long ms, SrtSequenceEntity entity) {
    return ms > entity.getBegin() && ms > entity.getEnd();
  }

  /**
   * strips text from unnecessary tags of html and matching ones placed with font options
   * in textView conjecture
   */
  private static int toInteger(String str) {
    try {
      return Integer.parseInt(str.replaceAll("[^\\d]", ""));//try to replace anything expect digits
    } catch (NumberFormatException formatError) {
      throw new AndroidException(formatError);
    }
  }

  private static long toTime(String str, int index) {
    try {
      String[] data = str.split(TIME_SPLIT);
      if (index > data.length || index < 0) {
        throw new AndroidException("out of index error");
      }
      return timeParser.parse(data[index].trim()).getTime();
    } catch (ParseException parseError) {
      throw new AndroidException(parseError);
    }
  }

  private SrtSequenceEntity() {
    super();
  }

  public SrtSequenceEntity(Parcel input) {
    super(input);
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setBegin(long begin) {
    this.begin = begin;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public void setText(SpannableString text) {
    this.text = text;
  }

  public int getIndex() {
    return index;
  }

  public long getBegin() {
    return begin;
  }

  public long getEnd() {
    return end;
  }

  public SpannableString getText() {
    return text;
  }

  @Override protected void readParcel(Parcel input) {
    setIndex(input.readInt());
    setBegin(input.readLong());
    setEnd(input.readLong());
    setText(new SpannableString(input.readString()));
  }

  @Override protected String getClassTag() {
    return SrtSequenceEntity.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeInt(getIndex());
    out.writeLong(getBegin());
    out.writeLong(getEnd());
    out.writeString(text.toString());
  }

  public final static Creator<SrtSequenceEntity> CREATOR = new Creator<SrtSequenceEntity>() {

    @Override public SrtSequenceEntity createFromParcel(Parcel input) {
      return new SrtSequenceEntity(input);
    }

    @Override public SrtSequenceEntity[] newArray(int size) {
      return new SrtSequenceEntity[size];
    }
  };
}