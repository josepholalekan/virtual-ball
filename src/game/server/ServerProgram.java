package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerProgram {

    private final static int port = 8888;

    public static void main(String[] args) {
        runServer();
    }

    private static void runServer() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("\nWaiting for players to join...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new PlayerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
