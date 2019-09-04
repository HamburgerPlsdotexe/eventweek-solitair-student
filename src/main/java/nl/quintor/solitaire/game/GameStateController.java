package nl.quintor.solitaire.game;

import nl.quintor.solitaire.models.card.Card;
import nl.quintor.solitaire.models.card.Rank;
import nl.quintor.solitaire.models.card.Suit;
import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.deck.DeckType;
import nl.quintor.solitaire.models.state.GameState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        // TODO: Write implementation DONE
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
                for (int j = 0; j <= i ; j++){
                    column.add(deck.remove(0));
                }
                column.setInvisibleCards(i);
            }
        );
        //ResultingGameState.setStartTime(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
        //    LocalDateTime.now().getDayOfMonth(), 0, 0, 0, 0));
        ResultingGameState.setStartTime(LocalDateTime.now());
        return ResultingGameState;
    }

    /**
     * Applies a score penalty to the provided GameState object based on the amount of time passed.
     * The following formula is applied : "duration of game in seconds" / 10 * -2
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyTimePenalty(GameState gameState){
        // TODO: Write implementation WIP
        int startTimeinSeconds = gameState.getStartTime().getHour() * 60 * 60 + gameState.getStartTime().getMinute() * 60 + gameState.getStartTime().getSecond();
        int endTimeinSeconds = gameState.getEndTime().getHour() * 60 * 60 + gameState.getEndTime().getMinute() * 60 + gameState.getEndTime().getSecond();
        gameState.setTimeScore((endTimeinSeconds - startTimeinSeconds) / 10 * - 2);
    }

    /**
     * Applies a score bonus to the provided GameState object based on the amount of time passed. Assumes the game is won.
     * When the duration of the game is more than 30 seconds then apply : 700000 / "duration of game in seconds"
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyBonusScore(GameState gameState){
        // TODO: Write implementation DONE
        int startTimeinSeconds = gameState.getStartTime().getHour() * 60 * 60 + gameState.getStartTime().getMinute() * 60 + gameState.getStartTime().getSecond();
        int endTimeinSeconds = gameState.getEndTime().getHour() * 60 * 60 + gameState.getEndTime().getMinute() * 60 + gameState.getEndTime().getSecond();
        if (endTimeinSeconds - startTimeinSeconds > 30) {
            gameState.setTimeScore(gameState.getTimeScore() + (700000 / (endTimeinSeconds - startTimeinSeconds)));
        }
        else {
            gameState.setTimeScore(0);
        }

    }

    /**
     * Detects if the game has been won, and if so, sets the gameWon flag in the GameState object.
     * The game is considered won if there are no invisible cards left in the GameState object's columns and the stock
     * is empty.
     *
     * @param gameState GameState object of which it is determined if the game has been won
     */
    public static void detectGameWin(GameState gameState) {
        // TODO: Write implementation DONE
        // gameState.getStackPiles()
        ArrayList<Integer> result = new ArrayList<>();
        for(Deck column : gameState.getColumns().values()){
            result.add(column.getInvisibleCards());
            }
        if (gameState.sumOfArray(result) == 0){
            gameState.setGameWon(true);
        }
    }

//    public static void endGame(GameState gameState) {
//        if (gameState.isGameOver()) {
//            int startTimeinSeconds = gameState.getStartTime().getHour() * 60 * 60 + gameState.getStartTime().getMinute() * 60 + gameState.getStartTime().getSecond();
//            int endTimeinSeconds = LocalDateTime.now().getHour() * 60 * 60 + LocalDateTime.now().getMinute() * 60 + LocalDateTime.now().getSecond();
//            gameState.setEndTime(LocalDateTime.now());
//        }
//    }
}
