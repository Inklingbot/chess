package server;

import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResult;
import model.*;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.net.http.*;

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

    public CreateGameResult create(String name, String authToken) throws ResponseException {
        try{
            var path = "/game";
            CreateGameRequest request = new CreateGameRequest(authToken, name);
            return this.makeRequest("POST", path, request, CreateGameResult.class);
        }
        catch(ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    public void join(String playerColor, String gameID, String authToken) throws ResponseException {
        Integer gameIDInt = Integer.valueOf(gameID);
        try{
            var path = "/game";
            JoinGameRequest request = new JoinGameRequest(authToken, playerColor, gameIDInt);
            this.makeRequest("PUT", path, request);
        }
        catch(ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }

    }

    public ListGamesResult list(String authToken) throws ResponseException {
        try{
            var path = "/game";
            ListGamesRequest request = new ListGamesRequest(authToken);
            return this.makeRequest("GET", path, request, ListGamesResult.class);
        }
        catch(ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    public void logout(String authToken) throws ResponseException {
        try {
            var path = "/session";
            LogoutRequest request = new LogoutRequest(authToken);
            this.makeRequest("DELETE", path, request);
        } catch (ResponseException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    private void makeRequest(String method, String path, Object request) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
        }
        catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ClientError, ex.getMessage());
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
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
        //this should work
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
