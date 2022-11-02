package us.mcmagic.capturetheflag.handlers;

/**
 * Created by Marc on 12/29/14
 */
public enum GameState {
    SERVER_STARTING(false), IN_LOBBY(true), PREPARING(false), SEARCHING(false), POSTGAME(false);

    private boolean canJoin;

    private static GameState currentState;

    GameState(boolean canJoin) {
        this.canJoin = canJoin;
    }

    public boolean canJoin() {
        return this.canJoin;
    }

    public static void setState(GameState state) {
        GameState.currentState = state;
    }

    public static boolean isState(GameState state) {
        return GameState.currentState == state;
    }

    public static GameState getState() {
        return currentState;
    }
}