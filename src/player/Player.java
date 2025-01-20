package game.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Player implements AutoCloseable {
    final int port = 8888;

    private final Scanner reader;
    private final PrintWriter writer;

    public Player() throws IOException {
        Socket socket = new Socket("localhost", port);
        reader = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public int getID() {
        writer.println("ID");

        return Integer.parseInt(reader.nextLine());
    }

    public int getHolder() {
        writer.println("HOLDER");

        return Integer.parseInt(reader.nextLine());
    }

    public void printLobby() {
        int holderID = getHolder();
        int thisID = getID();

        writer.println("LOBBY");

        // Reading the response into an array of player IDs
        String line = reader.nextLine();
        line = line.substring(1, line.length() - 1);
        String[] playerIDs = line.split(", ");

        System.out.println("Lobby:");

        for (String playerID : playerIDs) {
            int id = Integer.parseInt(playerID);

            if (id == holderID) {
                if (id == thisID)
                    System.out.println("- Player " + playerID + " (You) (Ball holder)");
                else
                    System.out.println("- Player " + playerID + " (Ball holder)");
            } else if (id == thisID)
                System.out.println("- Player " + playerID + " (You)");
            else
                System.out.println("- Player " + playerID);
        }

        System.out.println();
    }

    public void passTo(String otherID) {
        writer.println("PASS " + otherID);

        System.out.println("\n" + reader.nextLine() + "\n");
    }

    public boolean checkForEvent() {
        String response;
        writer.println("EVENT");
        response = reader.nextLine();
        if (Objects.equals(response, "null")) {
            return false;
        }
        System.out.println(response + "\n");
        return true;
    }

    public void waitForEvent() {
        String response;
        while (true) {
            writer.println("EVENT");
            response = reader.nextLine();
            if (Objects.equals(response, "null")) {
                continue;
            }
            System.out.println(response + "\n");
            break;
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}
