package nl.quintor.solitaire.game.moves;

import nl.quintor.solitaire.game.moves.ex.MoveException;
import nl.quintor.solitaire.models.state.GameState;

import java.util.List;

/**
 * Class that represents a player action to revert another move that implements {@link RevertibleMove}.
 */
public class Revert implements Move {
    private final static String name = System.getProperty("os.name").contains("Windows") ? "Revert" : "R̲evert";

    /**
     * Applies the {@link RevertibleMove#revert(GameState)}-method of the previous RevertibleMove contained in the
     * {@link GameState#moves} list to the Gamestate object.
     *
     * @param gameState GameState object to which the revert operation will be applied
     * @return return value of the reverting Move's {@link RevertibleMove#revert(GameState)} method
     * @throws MoveException if the GameState object's moves list is empty
     */
    @Override
    public String apply(GameState gameState) throws MoveException{
        // TODO: Write implementation
        return null;
    }

    @Override
    public Move createInstance(String playerInput) {
        return new Revert();
    }

    @Override
    public String toString() {
        return name;
    }
}
