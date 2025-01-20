package game.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerHandler implements Runnable {
    private final Socket socket;

    private int playerID;
    private static final List<Integer> playerList = new ArrayList<>();
    private static final Map<Integer, ConcurrentLinkedQueue<String>> playerEventQueues = new HashMap<>();

    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicInteger playerCounter = new AtomicInteger();

    private static final Ball ball = new Ball();
    private static final Random random = new Random();

    public PlayerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        playerID = playerCounter.incrementAndGet();

        lock.lock();
        playerList.add(playerID);

        if (playerList.size() == 1)
            ball.passTo(playerID);
        lock.unlock();

        playerEventQueues.put(playerID, new ConcurrentLinkedQueue<>());

        System.out.println("Player " + playerID + " has connected.\n");

        System.out.println(Lobby());

        broadcast("Player " + playerID + " has connected.");

        try (Scanner scanner = new Scanner(socket.getInputStream());
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                String line = scanner.nextLine();
                String[] substrings = line.split(" ");
                switch (substrings[0].toLowerCase()) {
                    case "id" -> writer.println(playerID);
                    case "lobby" -> writer.println(playerList);
                    case "holder" -> writer.println(ball.getHolder());
                    case "event" -> {
                        Thread.onSpinWait();
                        Thread.sleep(200);
                        writer.println(playerEventQueues.get(playerID).poll());
                    }
                    case "pass" -> {
                        int otherID;
                        try {
                            otherID = Integer.parseInt(substrings[1]);
                        } catch (NumberFormatException nfe) {
                            writer.println("ERROR: Command syntax is PASS <Player ID>.");
                            break;
                        }
                        if (ball.getHolder() == playerID) {

                            lock.lock();
                            if (playerList.contains(otherID)) {
                                ball.passTo(otherID); //synchronise
                                lock.unlock();
                            } else {
                                writer.println("No player with ID " + otherID + ".");
                                break;
                            }

                            if (playerID == otherID) {
                                System.out.println("Player " + playerID + " passed the ball to themself.\n");
                                broadcast("Player " + playerID + " passed the ball to themself.");

                                writer.println("You passed the ball to yourself.");
                            } else {
                                System.out.println("Player " + playerID + " has passed the ball to Player " + otherID + ".\n");
                                broadcast("Player " + playerID + " has passed the ball to Player " + otherID + ".");

                                writer.println("You passed the ball to Player " + otherID + ".");
                            }

                            System.out.println(Lobby());
                        } else
                            writer.println("You currently do not have the ball.");
                    }
                    default -> writer.println("Unknown command: " + substrings[0]);
                }
            }
        } catch (Exception ignored) {
        } finally {
            lock.lock();
            playerList.remove((Integer) playerID); //synchronise
            lock.unlock();
            playerEventQueues.remove(playerID);

            System.out.println("Player " + playerID + " has left the game.\n");

            broadcast("Player " + playerID + " has left the game.");

            if (!(playerList.isEmpty()) && ball.getHolder() == playerID) {
                int otherID = playerList.get(random.nextInt(playerList.size()));
                ball.passTo(otherID);
            }

            System.out.println(Lobby());
        }
    }

    private StringBuilder Lobby() {
        StringBuilder lobby = new StringBuilder();

        if (playerList.isEmpty()) {
            lobby.append("Lobby is empty.\n");
            return lobby;
        }

        lobby.append("Lobby:\n");

        for (int id : playerList) {
            lobby.append("- Player ").append(id);

            if (id == ball.getHolder())
                lobby.append(" (Ball holder)");

            lobby.append("\n");
        }

        return lobby;
    }

    private void broadcast(String message) {
        lock.lock();
        for (int playerID : playerEventQueues.keySet()) {
            if (this.playerID == playerID) {
                continue;
            }

            playerEventQueues.get(playerID).add(message);
        }
        lock.unlock();
    }
}
