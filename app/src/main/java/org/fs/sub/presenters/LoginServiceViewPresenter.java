package org.fs.sub.presenters;

import android.os.Bundle;
import android.util.Log;

import org.fs.common.AbstractPresenter;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.presenter.ILoginServiceViewPresenter;
import org.fs.sub.view.ILoginServiceView;
import org.fs.sub.views.LoginServiceView;
import org.fs.xml.net.XMLRpcRequest;
import org.fs.xml.net.XMLRpcResponse;
import org.fs.xml.type.XMLRpcObject;
import org.fs.xml.type.XMLRpcParam;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by Fatih on
 * as org.fs.sub.presenters.LoginServiceViewPresenter
 */
public class LoginServiceViewPresenter extends AbstractPresenter<ILoginServiceView> implements ILoginServiceViewPresenter {

    //private final static long MINS_FOURTEEN = 14 * 60 * 1000L;

    public LoginServiceViewPresenter(ILoginServiceView view) {
        super(view);
    }

    @Override
    protected String getClassTag() {
        return LoginServiceViewPresenter.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    @Override
    public void onCommand(Bundle args) {
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

    @Override
    public void withUserNameAndPassword(String userName, String password) throws IOException {
        Call<XMLRpcResponse> serviceCall = requestLogin(createLoginRequest(userName, password));
        Response<XMLRpcResponse> response = serviceCall.execute();
        if(response.isSuccess()) {
            XMLRpcResponse rpcResponse = response.body();
            if(rpcResponse.isSuccess()) {
                String token = rpcResponse.firstParam()
                        .getValue()
                        .asStruct()
                        .getValue("token")
                        .asString();
                view.preferenceProxy().setAccessToken(token);
            } else {
                String faultString = rpcResponse.getFault().toString();
                throw new AndroidException(faultString);
            }
        } else {
            String errorString = response.errorBody().toString();
            throw new AndroidException(errorString);
        }
    }

    @Override
    public void withSessionToken(String token) throws IOException {
        Call<XMLRpcResponse> serviceCall = requestSession(createSessionRequest(token));
        Response<XMLRpcResponse> response = serviceCall.execute();
        if(response.isSuccess()) {
            XMLRpcResponse rpcResponse = response.body();
            if(rpcResponse.isSuccess()) {
                String status = rpcResponse.firstParam()
                        .getValue()
                        .asStruct()
                        .getValue("status")
                        .asString();
                if("200 OK".equalsIgnoreCase(status)) {
                    log(Log.ERROR, status);
                } else {
                    throw new AndroidException(status);
                }
            } else {
                String faultString = rpcResponse.getFault().toString();
                throw new AndroidException(faultString);
            }
        } else {
            String errorString = response.errorBody().toString();
            throw new AndroidException(errorString);
        }
    }

    @Override
    public void withTerminateSession(String token) throws IOException {
        Call<XMLRpcResponse> serviceCall = requestLogout(createLogoutRequest(token));
        Response<XMLRpcResponse> response = serviceCall.execute();
        if(response.isSuccess()) {
            XMLRpcResponse rpcResponse = response.body();
            if(rpcResponse.isSuccess()) {
                String status = rpcResponse.firstParam()
                        .getValue()
                        .asStruct()
                        .getValue("status")
                        .asString();
                if("200 OK".equalsIgnoreCase(status)) {
                    view.preferenceProxy().setAccessToken("");
                } else {
                    throw new AndroidException(status);
                }
            } else {
                String faultString = rpcResponse.getFault().toString();
                throw new AndroidException(faultString);
            }
        } else {
            String errorString = response.errorBody().toString();
            throw new AndroidException(errorString);
        }
    }

    private Call<XMLRpcResponse> requestLogout(XMLRpcRequest request) {
        return view.serviceProxy()
                .withLogoutRequest(request);
    }

    private Call<XMLRpcResponse> requestSession(XMLRpcRequest request) {
        return view.serviceProxy()
                .withNoOperationRequest(request);
    }

    private Call<XMLRpcResponse> requestLogin(XMLRpcRequest request) {
        return view.serviceProxy()
                .withLoginRequest(request);
    }

    private XMLRpcRequest createLoginRequest(String userName, String password) {
        return new XMLRpcRequest()
                .withMethodName(IServiceEndpoint.ApiConstants.METHOD_LOG_IN)
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(userName)))
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(password)))
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue("en")))
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(IServiceEndpoint.ApiConstants.VALUE_USER_AGENT)));
    }

    private XMLRpcRequest createSessionRequest(String token) {
        return new XMLRpcRequest()
                .withMethodName(IServiceEndpoint.ApiConstants.METHOD_NO_OPERATION)
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(token)));
    }

    private XMLRpcRequest createLogoutRequest(String token) {
        return new XMLRpcRequest()
                .withMethodName(IServiceEndpoint.ApiConstants.METHOD_LOG_OUT)
                .add(XMLRpcParam.withObject(XMLRpcObject.withValue(token)));
    }

    /*
        life-cycles are useless
    */
    @Override
    public void onCreate() { }

    @Override
    public void onDestroy() { }

    @Override
    public void onResume() { }

    @Override
    public void onPause() { }
}