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
package org.fs.sub.presenters;

import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.util.Iterator;
import org.fs.common.AbstractPresenter;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.presenter.ILoginServiceViewPresenter;
import org.fs.sub.view.ILoginServiceView;
import org.fs.sub.views.LoginServiceView;
import org.fs.xml.net.XMLRpcRequest;
import org.fs.xml.net.XMLRpcResponse;
import org.fs.xml.type.Parameter;
import org.fs.xml.type.XMLRpcObject;
import org.fs.xml.type.XMLRpcParam;
import retrofit2.Call;
import retrofit2.Response;


public class LoginServiceViewPresenter extends AbstractPresenter<ILoginServiceView> implements ILoginServiceViewPresenter {

  //private final static long MINS_FOURTEEN = 14 * 60 * 1000L;

  public LoginServiceViewPresenter(ILoginServiceView view) {
    super(view);
  }

  @Override protected String getClassTag() {
    return LoginServiceViewPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override public void onCommand(Bundle args) {
    try {
      int keyCode = args.getInt(LoginServiceView.KEY_COMMAND, 0);
      if (keyCode == LoginServiceView.COMMAND_LOGIN) {
        String userName = view.preferenceProxy().userName();
        String password = view.preferenceProxy().password();
        withUserNameAndPassword(userName, password);
      } else if (keyCode == LoginServiceView.COMMAND_SESSION) {
        String token = view.preferenceProxy().accessToken();
        withSessionToken(token);
      } else if (keyCode == LoginServiceView.COMMAND_LOGOUT) {
        String token = view.preferenceProxy().accessToken();
        withTerminateSession(token);
      }
    } catch (IOException ioError) {
      log(ioError);
    }
  }

  @Override public void withUserNameAndPassword(String userName, String password) throws IOException {
    Call<XMLRpcResponse> serviceCall = requestLogin(createLoginRequest(userName, password));
    Response<XMLRpcResponse> response = serviceCall.execute();
    if (response.isSuccessful()) {
      XMLRpcResponse rpcResponse = response.body();
      if (rpcResponse.isSuccess()) {
        Parameter first = firstParam(rpcResponse);
        String token = (String) first.asMap().get("token");
        view.preferenceProxy().setAccessToken(token);
      } else {
        String faultString = (String) rpcResponse.fault();
        throw new AndroidException(faultString);
      }
    } else {
      String errorString = response.errorBody().toString();
      throw new AndroidException(errorString);
    }
  }

  @Override public void withSessionToken(String token) throws IOException {
    Call<XMLRpcResponse> serviceCall = requestSession(createSessionRequest(token));
    Response<XMLRpcResponse> response = serviceCall.execute();
    if (response.isSuccessful()) {
      XMLRpcResponse rpcResponse = response.body();
      if (rpcResponse.isSuccess()) {
        Parameter first = firstParam(rpcResponse);
        String status = (String) first.asMap().get("status");
        if ("200 OK".equalsIgnoreCase(status)) {
          log(Log.ERROR, status);
        } else {
          throw new AndroidException(status);
        }
      } else {
        String faultString = (String) rpcResponse.fault();
        throw new AndroidException(faultString);
      }
    } else {
      String errorString = response.errorBody().toString();
      throw new AndroidException(errorString);
    }
  }

  @Override public void withTerminateSession(String token) throws IOException {
    Call<XMLRpcResponse> serviceCall = requestLogout(createLogoutRequest(token));
    Response<XMLRpcResponse> response = serviceCall.execute();
    if (response.isSuccessful()) {
      XMLRpcResponse rpcResponse = response.body();
      if (rpcResponse.isSuccess()) {
        Parameter first = firstParam(rpcResponse);
        String status = (String) first.asMap().get("status");
        if ("200 OK".equalsIgnoreCase(status)) {
          view.preferenceProxy().setAccessToken("");
        } else {
          throw new AndroidException(status);
        }
      } else {
        String faultString = (String) rpcResponse.fault();
        throw new AndroidException(faultString);
      }
    } else {
      String errorString = response.errorBody().toString();
      throw new AndroidException(errorString);
    }
  }

  private Call<XMLRpcResponse> requestLogout(XMLRpcRequest request) {
    return view.serviceProxy().withLogoutRequest(request);
  }

  private Call<XMLRpcResponse> requestSession(XMLRpcRequest request) {
    return view.serviceProxy().withNoOperationRequest(request);
  }

  private Call<XMLRpcResponse> requestLogin(XMLRpcRequest request) {
    return view.serviceProxy().withLoginRequest(request);
  }

  private XMLRpcRequest createLoginRequest(String userName, String password) {
    return XMLRpcRequest.create(IServiceEndpoint.ApiConstants.METHOD_LOG_IN)
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue(userName)))
        )
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue(password)))
        )
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue("en")))
        )
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue(IServiceEndpoint.ApiConstants.VALUE_USER_AGENT)))
        );
  }

  private XMLRpcRequest createSessionRequest(String token) {
    return XMLRpcRequest.create(IServiceEndpoint.ApiConstants.METHOD_NO_OPERATION)
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue(token)))
        );
  }

  private XMLRpcRequest createLogoutRequest(String token) {
    return XMLRpcRequest.create(IServiceEndpoint.ApiConstants.METHOD_LOG_OUT)
        .addParameter(
            Parameter.create(XMLRpcParam.withObject(XMLRpcObject.withValue(token)))
        );
  }

  /*
      life-cycles are useless
  */
  @Override public void onCreate()  {}
  @Override public void onDestroy() {}
  @Override public void onStart()   {}
  @Override public void onStop()    {}

  private Parameter firstParam(XMLRpcResponse resp) {
    Parameter param = null;
    Iterator<Parameter> iter = resp.response().iterator();
    if (iter.hasNext()) {
      param = iter.next();
    }
    return param;
  }
}