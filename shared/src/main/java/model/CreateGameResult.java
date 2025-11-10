package model;

public record CreateGameResult(Integer gameID) {
    @Override
    public String toString() {
        return "CreateGameResult{" +
                "gameID=" + gameID +
                '}';
    }
}
