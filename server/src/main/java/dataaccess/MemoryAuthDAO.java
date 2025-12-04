package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public AuthData createAuth(String username) {
        AuthData auth = new AuthData(AuthData.generateToken(),username);
        auths.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }



    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
