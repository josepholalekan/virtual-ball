package game.player;

import java.util.Scanner;

public class PlayerProgram {
    public static void main(String[] args) {
        try (Player player = new Player()) {
            Scanner in = new Scanner(System.in);
            int playerID = player.getID();

            System.out.println("\nYou have joined the lobby.\n");

            while (true) {
                player.printLobby();

                int holderID = player.getHolder();

                if (playerID == holderID) {
                    System.out.println("Who would you like to pass the ball to? (Enter player ID).\n");
                    while (true) {
                        if (System.in.available() != 0) {
                            player.passTo(in.nextLine());
                            break;
                        }

                        if (player.checkForEvent())
                            break;
                    }
                    continue;
                }

                System.out.println("Waiting for Player " + holderID + " to pass the ball...\n");
                player.waitForEvent();

                while (System.in.available() > 0)
                    in.nextLine();
            }
        } catch (Exception ignored) {
        }
    }
}
