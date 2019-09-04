package nl.quintor.solitaire.game

import nl.quintor.solitaire.TestUtil
import nl.quintor.solitaire.game.moves.ex.MoveException
import nl.quintor.solitaire.models.card.Card
import nl.quintor.solitaire.models.card.Rank
import nl.quintor.solitaire.models.card.Suit
import nl.quintor.solitaire.models.deck.Deck
import nl.quintor.solitaire.models.deck.DeckType
import spock.lang.Specification
import spock.lang.Unroll

class CardMoveChecksSpec extends Specification {

    @Unroll
    def "Invalid input calling checkPlayerInput whith #input will throw #exception.getSimpleName() with message |#message| when testing: #testcase" () {
        when:
        CardMoveChecks.checkPlayerInput(input)
        then:
        def ex = thrown(exception)
        ex.getMessage() == message
        where:
        testcase| input | exception | message
        "Invalid source move"|["M", "Z", "G"] as String[] | MoveException | 'Invalid Move syntax. "Z" is not a valid source location.\nSee H̲elp for instructions.'
        "Invalid destination move"|["M", "O", "Z"] as String[] | MoveException | 'Invalid Move syntax. "Z" is not a valid destination location.\nSee H̲elp for instructions.'
    }

    @Unroll
    def "Valid moves will not throw exceptions testing: #testcase" () {
        expect:
        CardMoveChecks.checkPlayerInput(input)
        where:
        testcase| input
        "Valid move between columns"|["M", "A2", "B"] as String[]
        "Valid move between column and stack"|["M", "C10", "SB"] as String[]
        "Valid move between stock and stack"|["M", "O", "SC"] as String[]
        "Valid move between stock and column"|["M", "O", "G"] as String[]
    }

    @Unroll
    def "Valid input calling deckLevelChecks will not throw exception testing: #testcase" () {
        given:
            sourceDeck.deckType = sourceType
            destinationDeck.deckType = destinationType
        expect:
            CardMoveChecks.deckLevelChecks(sourceDeck, index, destinationDeck)
        where:
            sourceDeck   | destinationDeck | sourceType | destinationType | index || testcase
            [new Card(Suit.HEARTS, Rank.ACE),new Card(Suit.DIAMONDS, Rank.ACE)] as Deck | new Deck()| DeckType.COLUMN | DeckType.STACK | 1     || "Column source and Stack destination"
            [new Card(Suit.HEARTS, Rank.ACE),new Card(Suit.DIAMONDS, Rank.ACE)] as Deck | new Deck()| DeckType.COLUMN | DeckType.COLUMN | 99   || "Column source and destination"
            [new Card(Suit.HEARTS, Rank.ACE),new Card(Suit.DIAMONDS, Rank.ACE)] as Deck | new Deck()| DeckType.COLUMN | DeckType.COLUMN | 99   || "Stack source and Column destination"
    }

    def "Calling deckLevelChecks with same source and destination will throw exception" () {
        given:
        def sourceDeck = new Deck(DeckType.COLUMN)
        def destinationDeck = sourceDeck
        when:
        CardMoveChecks.deckLevelChecks(sourceDeck, 99, destinationDeck)
        then:
        def exception = thrown(MoveException)
        exception.message == "Move source and destination can't be the same"
    }

    @Unroll
    def "Invalid input calling deckLevelChecks will throw exception with message: |#exceptionMessage| testing: #testcase" () {
        when:
            CardMoveChecks.deckLevelChecks(sourceDeck, index, destinationDeck)
        then:
            def exception = thrown(MoveException)
            exception.message == exceptionMessage
        where:
        sourceDeck                                     | destinationDeck                         |  index | exceptionMessage                                              || testcase
        new Deck(DeckType.COLUMN)                      | new Deck(DeckType.COLUMN )              | 0      |  'You can\'t move a card from an empty deck'                  || 'Moving cards from empty deck should not be possible'
        TestUtil.createTestDeck(DeckType.COLUMN, 1)    | TestUtil.createTestDeck(DeckType.STOCK) | 888    |  'You can\'t move cards to the stock'                         || 'Moving cards to stock is not allowed'
        TestUtil.createTestDeck(DeckType.COLUMN, 3)    | TestUtil.createTestDeck(DeckType.STACK) | 0      |  'You can\'t move more than 1 card at a time to a Stack Pile' || 'Moving multiple cards is not allowed'
        TestUtil.createTestDeck(DeckType.COLUMN, 3, 2) | TestUtil.createTestDeck(DeckType.STACK) | 0      |  'You can\'t move an invisible card'                          || 'Moving invisible cards is not allowed'
    }

    @Unroll
    def "valid input calling deckLevelChecks does not throw exceptions testing: #testCase" (){
        expect:
            CardMoveChecks.deckLevelChecks(sourceDeck, index, destinationDeck)
        where:
        sourceDeck                                  | destinationDeck                         |  index || testCase
        TestUtil.createTestDeck(DeckType.COLUMN, 1) |TestUtil.createTestDeck(DeckType.STACK)  | 1      || "Moving card from column to stack should be allowed"
        TestUtil.createTestDeck(DeckType.COLUMN, 1) |TestUtil.createTestDeck(DeckType.COLUMN) | 0      || "Moving card from column to column should be allowed"
        TestUtil.createTestDeck(DeckType.STOCK, 1)  |TestUtil.createTestDeck(DeckType.COLUMN) | 0      || "Moving card from stock to column should be allowed"
    }

    @Unroll
    def "Invalid input calling cardLevelCheck will throw exception with message #exceptionMessage testing: #testCase" () {
        when:
            CardMoveChecks.cardLevelChecks(targetDeck, cardToAdd)
        then:
            def ex = thrown(MoveException)
            ex.message == exceptionMessage
        where:
        targetDeck                     | cardToAdd                           | exceptionMessage                           || testCase
        TestUtil.createTestDeck(DeckType.STOCK) | new Card(Suit.DIAMONDS, Rank.EIGHT) | "Target deck is neither Stack nor Column." || "Moving card to deck should only be allowed to Stack or column deck"
        TestUtil.createTestDeck(DeckType.STACK) | new Card(Suit.DIAMONDS, Rank.TWO) | "An Ace has to be the first card of a Stack Pile" || "Moving card not being an ace to an empty stack"
        TestUtil.createTestDeckWithCards(DeckType.STACK, [new Card(Suit.HEARTS, Rank.ACE)]) | new Card(Suit.HEARTS, Rank.THREE) | 'Stack Piles hold same-suit cards of increasing Rank from Ace to King' || "Moving card to stack should be increasing"
        TestUtil.createTestDeckWithCards(DeckType.STACK, [new Card(Suit.HEARTS, Rank.ACE)]) | new Card(Suit.DIAMONDS, Rank.TWO) | 'Stack Piles can only contain same-suit cards' || "Moving card to stack should be same suit"
        new Deck(DeckType.COLUMN)| new Card(Suit.DIAMONDS, Rank.TWO) | 'A King has to be the first card of a Column' || "Moving card to empty column can only be a King"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.HEARTS, Rank.KING)]) | new Card(Suit.DIAMONDS, Rank.QUEEN) | 'Column cards have te alternate colors (red and black)' || "Moving card to column must be opposite suit"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.HEARTS, Rank.KING)]) | new Card(Suit.SPADES, Rank.JACK) | 'Columns hold alternating-color cards of decreasing rank from King to Two' || "Moving card to column must be in descending rank"
    }

    @Unroll
    def "Valid input calling cardLevelChecks will not throw exceptions testing : #testCase" () {
        expect:
            CardMoveChecks.cardLevelChecks(targetDeck, cardToAdd)
        where:
            targetDeck                  | cardToAdd                                             | testCase
        new Deck(DeckType.STACK)| new Card(Suit.CLUBS, Rank.ACE)|"Ace should be allowed on empty stack"
        TestUtil.createTestDeckWithCards(DeckType.STACK, [new Card(Suit.DIAMONDS, Rank.ACE)])| new Card(Suit.DIAMONDS, Rank.TWO)|"two should be allowed in stack containing ace"
        new Deck(DeckType.COLUMN)| new Card(Suit.DIAMONDS, Rank.KING)| "King should be allowed as first card in a column deck"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.DIAMONDS, Rank.KING)])| new Card(Suit.CLUBS, Rank.QUEEN)| "column deck with Diamond card should allow club card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.DIAMONDS, Rank.KING)])| new Card(Suit.SPADES, Rank.QUEEN)| "column deck with Diamond card should allow spade card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.HEARTS, Rank.KING)])| new Card(Suit.SPADES, Rank.QUEEN)| "column deck with hearts card should allow spade card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.HEARTS, Rank.KING)])| new Card(Suit.CLUBS, Rank.QUEEN)| "column deck with hearts card should allow club card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.CLUBS, Rank.KING)])| new Card(Suit.HEARTS, Rank.QUEEN)| "column deck with clubs card should allow hearts card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.CLUBS, Rank.KING)])| new Card(Suit.DIAMONDS, Rank.QUEEN)| "column deck with clubs card should allow diamond card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.SPADES, Rank.KING)])| new Card(Suit.HEARTS, Rank.QUEEN)| "column deck with spade card should allow hearts card with decreased value of one"
        TestUtil.createTestDeckWithCards(DeckType.COLUMN, [new Card(Suit.SPADES, Rank.KING)])| new Card(Suit.DIAMONDS, Rank.QUEEN)| "column deck with spade card should allow diamond card with decreased value of one"
    }

    @Unroll
    def "Given the cards #cardLeft, #cardRight the method opposingCards will return #expectedResult" () {
        expect:
            def actualResult = CardMoveChecks.opposingColor(cardLeft, cardRight)
            actualResult == expectedResult
        where:
            cardLeft                            | cardRight                              | expectedResult
            new Card(Suit.DIAMONDS, Rank.JACK)  | new Card(Suit.DIAMONDS, Rank.FIVE)     | false
            new Card(Suit.DIAMONDS, Rank.JACK)  | new Card(Suit.HEARTS, Rank.FIVE)       | false
            new Card(Suit.DIAMONDS, Rank.JACK)  | new Card(Suit.SPADES, Rank.SEVEN)      | true
            new Card(Suit.DIAMONDS, Rank.JACK)  | new Card(Suit.CLUBS, Rank.FIVE)        | true
            new Card(Suit.HEARTS, Rank.TWO)     | new Card(Suit.HEARTS, Rank.KING)       | false
            new Card(Suit.HEARTS, Rank.TWO)     | new Card(Suit.DIAMONDS, Rank.KING)     | false
            new Card(Suit.HEARTS, Rank.TWO)     | new Card(Suit.SPADES, Rank.KING)       | true
            new Card(Suit.HEARTS, Rank.TWO)     | new Card(Suit.CLUBS, Rank.KING)        | true
            new Card(Suit.CLUBS, Rank.FIVE)     | new Card(Suit.CLUBS, Rank.ACE)         | false
            new Card(Suit.CLUBS, Rank.FIVE)     | new Card(Suit.SPADES, Rank.ACE)        | false
            new Card(Suit.CLUBS, Rank.FIVE)     | new Card(Suit.DIAMONDS, Rank.ACE)      | true
            new Card(Suit.CLUBS, Rank.FIVE)     | new Card(Suit.HEARTS, Rank.ACE)        | true
            new Card(Suit.SPADES, Rank.TEN)     | new Card(Suit.SPADES, Rank.QUEEN)      | false
            new Card(Suit.SPADES, Rank.TEN)     | new Card(Suit.CLUBS, Rank.QUEEN)       | false
            new Card(Suit.SPADES, Rank.TEN)     | new Card(Suit.DIAMONDS, Rank.QUEEN)    | true
            new Card(Suit.SPADES, Rank.TEN)     | new Card(Suit.HEARTS, Rank.QUEEN)      | true
    }

    @Unroll
    def "Given #card redSuit should return #expectedResult" () {
        expect:
            def actualResult = CardMoveChecks.redSuit(card)
            actualResult == expectedResult
        where:
        card                                || expectedResult
        new Card(Suit.SPADES, Rank.TEN)     || false
        new Card(Suit.CLUBS, Rank.TEN)      || false
        new Card(Suit.DIAMONDS, Rank.TEN)   || true
        new Card(Suit.HEARTS, Rank.TEN)     || true
    }

    @Unroll
    def "Given a #joker redSuit should throw a exception" () {
        when:
            CardMoveChecks.redSuit(joker)
        then:
            def exception = thrown(RuntimeException)
            exception.message == expectedMessage
        where:
        joker                                       || expectedMessage
        new Card(Suit.JOKER, Rank.JOKER_BLACK)      || "Method redSuit() should not be used with Jokers"
        new Card(Suit.JOKER, Rank.JOKER_RED)        || "Method redSuit() should not be used with Jokers"

    }


}
