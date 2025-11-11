package server;

import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResult;
import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {

    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    public RegisterResult register(String username, String pass, String email) throws ResponseException {
        try {
            var path = "/user";
            RegisterRequest request = new RegisterRequest(username, pass, email);
            return this.makeRequest("POST", path, request, RegisterResult.class);
        } catch (ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    public LoginResult login(String username, String pass) throws ResponseException {
        try {
            var path = "/session";
            LoginRequest request = new LoginRequest(username, pass);
            return this.makeRequest("POST", path, request, LoginResult.class);
        }
        catch(ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    public CreateGameResult create(String name) throws ResponseException {
        try{
            var path = "/session";
            CreateGameNameRequest request = new CreateGameNameRequest(name);
            return this.makeRequest("DELETE", path, request, CreateGameResult.class);
        }
        catch(ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }




    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
        catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ClientError, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful (HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(ResponseException.Code.ClientError, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
