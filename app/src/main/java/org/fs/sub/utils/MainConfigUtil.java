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
package org.fs.sub.utils;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.net.ProxyInterceptor;
import org.fs.xml.okhttp.SimpleXMLConverterFactory;
import org.fs.xml.transform.XMLRpcMatcher;
import org.fs.xml.type.XMLRpcArray;
import org.fs.xml.type.XMLRpcObject;
import org.fs.xml.type.XMLRpcStruct;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import retrofit2.Retrofit;

public class MainConfigUtil {

  private final static String KEY_SUB_LANGUAGE_ID = "SubLanguageID";
  private final static String KEY_LANGUAGE_NAME = "LanguageName";

  public static void main(String... args) throws Exception {
    Serializer serializer = new Persister(new XMLRpcMatcher(), new Format("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
    //IServiceEndpoint serviceProxy = createRetrofit().create(IServiceEndpoint.class);
    //        XMLRpcRequest requestGetSubtitleLanguages = new XMLRpcRequest()
    //                .withMethodName("SampleMethodName")
    //                .add(XMLRpcParam.withObject(XMLRpcObject.withValue("param1")))//string
    //                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(1d)))//double
    //                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(true)))//boolean
    //                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(new Date())))//date
    //                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(1)));//integer
    XMLRpcStruct struct = new XMLRpcStruct().add("key", XMLRpcObject.withValue("value"))
        .add("key1", XMLRpcObject.withValue(new java.util.Date()));

    serializer.write(struct, System.out);

    //        XMLRpcResponse response = serviceProxy.withGetSubtitleLanguages(requestGetSubtitleLanguages).execute().body();
    //        if(response.isSuccess()) {
    //            XMLRpcArray data = response.firstParam().getValue().asStruct().getValue("data").asArray();
    //            AndroidOutput output = parse(data);
    //            serializer.write(output, new File("./app/src/main/res/raw/output.xml"));
    //        }


        /*Serializer serializer = new Persister(new XMLRpcMatcher(), new Format("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
        XMLRpcResponse response = serializer.read(XMLRpcResponse.class, new File("./app/src/main/res/raw/input.xml"));
        if(response.isSuccess()) {
            Array data = response.firstParam()
                                 .getValue().asStruct().getValue("data")
                                 .asArray();
            AndroidOutput output = parse(data);
            serializer.write(output, new File("./app/src/main/res/raw/output.xml"));
        }*/
  }

  private static Retrofit createRetrofit() {
    return new Retrofit.Builder().baseUrl(IServiceEndpoint.ApiConstants.VALUE_BASE_URL)
        .client(createHttpClient())
        .addConverterFactory(SimpleXMLConverterFactory.createNonStrict())
        .build();
  }

  private static OkHttpClient createHttpClient() {
    OkHttpClient httpClient = new OkHttpClient();
    httpClient.interceptors().add(new ProxyInterceptor());
    return httpClient;
  }

  public static AndroidOutput parse(XMLRpcArray data) {
    AndroidOutput output = new AndroidOutput();
    for (XMLRpcObject o : data.getValues()) {
      XMLRpcStruct s = o.asStruct();
      output = output.addKeyAndValue(s.getValue(KEY_SUB_LANGUAGE_ID).asString(),
          s.getValue(KEY_LANGUAGE_NAME).asString());
    }
    return output;
  }

  @Root(name = "resource") public static class AndroidOutput {

    @Path("key") @ElementList(entry = "item", inline = true) private List<String> key;

    @Path("value") @ElementList(entry = "item", inline = true) private List<String> value;

    public AndroidOutput addKeyAndValue(String k, String v) {
      if (key == null) key = new ArrayList<>();
      if (value == null) value = new ArrayList<>();
      key.add(k);
      value.add(v);
      return this;
    }
  }
}
