package game.server;

public class Ball {
    private static int holder;

    public void passTo(int player) { holder = player; }

    public int getHolder() { return holder; }
}
