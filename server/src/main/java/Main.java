import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = 8080;
        server.run(port);

        System.out.println("♕ 240 Chess Server");
        System.out.println("Running on port " + port);
    }
}