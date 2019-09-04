package nl.quintor.solitaire.game;

import nl.quintor.solitaire.game.moves.Help;
import nl.quintor.solitaire.game.moves.Move;
import nl.quintor.solitaire.game.moves.ex.MoveException;
import nl.quintor.solitaire.models.card.Card;
import nl.quintor.solitaire.models.card.Rank;
import nl.quintor.solitaire.models.card.Suit;
import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.deck.DeckType;

import java.util.regex.Pattern;

/**
 * Library class for card move legality checks. The class is not instantiable, all constructors are private and all methods are
 * static. The class contains several private helper methods. All methods throw {@link MoveException}s, which can
 * contain a message that is fed to the {@link nl.quintor.solitaire.ui.UI}-implementation as error messages to be
 * shown to the user.
 */
public class CardMoveChecks {
    private CardMoveChecks(){}
    private final static String helpInstructions = new Help().toString();

    /**
     * Verifies that the player input for a CardMove is syntactically legal. Legal input consists of three parts:
     * the move command "M", the source location and the destination location.
     * The source location has to be the stock header, a stack header or a column coordinate.
     * The destination location has to be the stock header, a stack header or a column header (the column row is not
     * relevant because cards can only be added at the end of a column). The method verifies the syntax using regular
     * expressions.
     *
     * @param input the user input, split on the space character, cast to uppercase
     * @throws MoveException on syntax error
     */
    public static void checkPlayerInput(String[] input) throws MoveException {
        if (input.length != 3) throw new MoveException("Invalid Move syntax. See H̲elp for instructions.");
        if(!input[1].matches("O|S[A-D]|[A-G]\\d{1,2}")) {
            throw new MoveException("Invalid Move syntax. \"" + input[1] + "\" is not a valid source location.\n" +
                "See H̲elp for instructions.");
        }
        if(!input[2].matches("O|S[A-D]|[A-G]")) {
            throw new MoveException("Invalid Move syntax. \"" + input[2] + "\" is not a valid destination location.\n" +
                "See H̲elp for instructions.");
        }



    }

    /**
     * Verifies that a card move is possible given the source deck, the source card index and the destination deck.
     * Assumes that the {@link #checkPlayerInput(String[])} checks have passed.
     * {@link Deck} objects have a {@link DeckType} that is used in this method. The rank and suit of the actual cards
     * are not taken into consideration here.
     *
     * @param sourceDeck deck that the card(s) originate from
     * @param sourceCardIndex index of the (first) card
     * @param destinationDeck deck that the card(s) will be transferred to
     * @throws MoveException on illegal move
     */
    public static void deckLevelChecks(Deck sourceDeck, int sourceCardIndex, Deck destinationDeck) throws MoveException {
        if (destinationDeck == sourceDeck) {
            throw new MoveException("Move source and destination can't be the same");
        }
        if (sourceDeck.isEmpty()) {
            throw new MoveException("You can't move a card from an empty deck");
        }
        if (DeckType.STOCK == destinationDeck.getDeckType()){
            throw new MoveException("You can't move cards to the stock");
        }

    }

    /**
     * Verifies that a card move is possible given the rank and suit of the card or first card to be moved. Assumes the
     * {@link #checkPlayerInput(String[])} and {@link #deckLevelChecks(Deck, int, Deck)} checks have passed. The checks
     * for moves to a stack pile or to a column are quite different, so the method calls one of two helper methods,
     * {@link #checkStackMove(Card, Card)} and {@link #checkColumnMove(Card, Card)}.
     *
     * @param targetDeck deck that the card(s) will be transferred to
     * @param cardToAdd (first) card
     * @throws MoveException on illegal move
     */
    public static void cardLevelChecks(Deck targetDeck, Card cardToAdd) throws MoveException {
        if(targetDeck.getDeckType() != DeckType.STACK && targetDeck.getDeckType() != DeckType.COLUMN){
            throw new MoveException("Target deck is neither Stack nor Column.");}
        else if (!targetDeck.isEmpty() && !opposingColor(targetDeck.get(0), cardToAdd) && targetDeck.getDeckType() == DeckType.COLUMN) {
            throw new MoveException("Column cards have te alternate colors (red and black)");
        } else if (targetDeck.isEmpty() && targetDeck.getDeckType() == DeckType.STACK && cardToAdd.getRank() != Rank.ACE) {
            throw new MoveException("An Ace has to be the first card of a Stack Pile");
        } else if (!targetDeck.isEmpty() && targetDeck.getDeckType() == DeckType.STACK && targetDeck.get(0).getSuit() != cardToAdd.getSuit()) {
            throw new MoveException("Stack Piles can only contain same-suit cards");
        } else if (!targetDeck.isEmpty() && targetDeck.getDeckType() == DeckType.STACK ) {
            throw new MoveException("Stack Piles hold same-suit cards of increasing Rank from Ace to King");
        } else if (targetDeck.getDeckType() == DeckType.COLUMN && cardToAdd.getRank() != Rank.KING && targetDeck.isEmpty()) {
            throw new MoveException("A King has to be the first card of a Column");
        } else if (!targetDeck.isEmpty() && targetDeck.getDeckType() == DeckType.COLUMN && opposingColor(targetDeck.get(0), cardToAdd) && cardToAdd.getOrdinal()-1 == targetDeck.get(0).getOrdinal()){
            throw new MoveException("Columns hold alternating-color cards of decreasing rank from King to Two");
        }


    }

    // Helper methods

    /**
     * Verifies that the proposed move is legal given that the targetCard is the top of a stack pile.
     *
     * @param targetCard top card of a stack or null if the stack is empty
     * @param cardToAdd card to add to the stack
     * @throws MoveException on illegal move
     */
    static void checkStackMove(Card targetCard, Card cardToAdd) throws MoveException {
        if(targetCard.getOrdinal() - 1 == cardToAdd.getOrdinal() && opposingColor(targetCard,cardToAdd))
        {
            //Geen idee, kaart move goedkeuren?
        }

        else{throw new MoveException("Illegal move"); }
    }

    /**
     * Verifies that the proposed move is legal given that the targetCard is the last card of a column.
     *
     * @param targetCard last card of a column or null if the column is empty
     * @param cardToAdd card to add to the column
     * @throws MoveException on illegal move
     */
    static void checkColumnMove(Card targetCard, Card cardToAdd) throws MoveException {
        /// TODO: Write implementation
    }

    /**
     * Helper method to determine if the provided cards are of opposing color (red versus black).
     *
     * @param card1 first card
     * @param card2 second card
     * @return true if the cards are of different colors
     */
    static boolean opposingColor(Card card1, Card card2){
        // TODO: Write implementation
        return redSuit(card1) && !redSuit(card2) || !redSuit(card1) && redSuit(card2);
    }

    /**
     * Helper method to determine if the card's suit is colored red (Diamonds or Hearts).
     *
     *
     * @param card card to be tested for red color
     * @return true if card is either of suit Diamonds or Hearts
     * @throws RuntimeException exception when Joker card is checked with message 'Method redSuit() should not be used with Jokers'
     */
    static boolean redSuit(Card card){
        if (card.getSuit() == Suit.JOKER) throw new RuntimeException("Method redSuit() should not be used with Jokers");
        return card.getSuit() == Suit.DIAMONDS || card.getSuit() == Suit.HEARTS;
    }
}
