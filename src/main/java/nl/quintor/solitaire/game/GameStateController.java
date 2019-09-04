package nl.quintor.solitaire.game;

import nl.quintor.solitaire.models.card.Card;
import nl.quintor.solitaire.models.card.Rank;
import nl.quintor.solitaire.models.card.Suit;
import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.deck.DeckType;
import nl.quintor.solitaire.models.state.GameState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static java.util.Collections.shuffle;

/**
 * Library class for GameState initiation and status checks that are called from {@link nl.quintor.solitaire.Main}.
 * The class is not instantiable, all constructors are private and all methods are static.
 */
public class GameStateController {
    private GameStateController(){}

    /**
     * Creates and initializes a new GameState object. The newly created GameState is populated with shuffled cards. The
     * stack pile and column maps are filled with headers and Deck objects. The column decks have an appropriate number
     * of invisible cards set.
     *
     * @return a new GameState object, ready to go
     */
    public static GameState init(){
        // TODO: Write implementation WIP
        Deck deck = Deck.createDefaultDeck();
        shuffle(deck);
        GameState ResultingGameState = new GameState();
        ResultingGameState.getStock().add(deck.remove(0));
        for (int i = 0; i < 23; i++) {
            ResultingGameState.getWaste().add(deck.remove(0));
        }
        ResultingGameState.getStackPiles().put("SA", new Deck(DeckType.STACK));
        ResultingGameState.getStackPiles().put("SB", new Deck(DeckType.STACK));
        ResultingGameState.getStackPiles().put("SC", new Deck(DeckType.STACK));
        ResultingGameState.getStackPiles().put("SD", new Deck(DeckType.STACK));
        ResultingGameState.getColumns().put("A", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("B", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("C", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("D", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("E", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("F", new Deck(DeckType.COLUMN));
        ResultingGameState.getColumns().put("G", new Deck(DeckType.COLUMN));
        ArrayList<String> keysetcolumns = new ArrayList<>(ResultingGameState.getColumns().keySet());
        Collections.sort(keysetcolumns);
        ResultingGameState.getColumns().forEach((key, column) ->
            {
                int i = keysetcolumns.indexOf(key);
                for (int j = 0; j < i ; j++){
                    column.add(deck.remove(0));
                }
                column.setInvisibleCards(i - 1);
            }
        );
        ResultingGameState.setStartTime(LocalDateTime.of(2019, 1, 1, 0, 0, 0, 0));
        return ResultingGameState;
    }

    /**
     * Applies a score penalty to the provided GameState object based on the amount of time passed.
     * The following formula is applied : "duration of game in seconds" / 10 * -2
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyTimePenalty(GameState gameState){
        // TODO: Write implementation
    }

    /**
     * Applies a score bonus to the provided GameState object based on the amount of time passed. Assumes the game is won.
     * When the duration of the game is more than 30 seconds then apply : 700000 / "duration of game in seconds"
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyBonusScore(GameState gameState){

    }

    /**
     * Detects if the game has been won, and if so, sets the gameWon flag in the GameState object.
     * The game is considered won if there are no invisible cards left in the GameState object's columns and the stock
     * is empty.
     *
     * @param gameState GameState object of which it is determined if the game has been won
     */
    public static void detectGameWin(GameState gameState){
        // TODO: Write implementation
    }
}
