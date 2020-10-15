package ch.epfl.javass.jass; 

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class representing the state of a turn in the game **/
public final class TurnState {

    private TurnState(long score, long cards, int trick) {
        actualScore = score;
        unplayedCards = cards;
        actualTrick = trick;
    }

    private final long actualScore;
    private final long unplayedCards;
    private final int actualTrick;

    /**
     * returns the initial state corresponding to a turn,
     * where the trump, the initial score and the first player are those given
     * 
     * @param trump
     *          the initial trump
     * @param score
     *          the initial score
     * @param firstPlayer
     *          the first player
     * @return the initial state corresponding to a turn
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        return ofPackedComponents(score.packed(), PackedCardSet.ALL_CARDS,
                PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * return the turn state for the given components, 
     * or throws an exception if one of them is invalid
     * 
     * @param pkScore
     *              the actual score
     * @param pkUnplayedCards
     *              the set of current non played cards
     * @param pkTrick
     *              the actual trick
     * @throws IllegalArgumentException if pkScore, pkUnlplayedCards or pkTrick is not valid
     * @return the turn state for the given components
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) 
            throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * getter for the packed version of the actual score
     * @return actualScore
     */
    public long packedScore() {
        return actualScore;
    }

    /**
     * getter for the packed version of the set of non played cards
     * @return unplayedCards
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * getter for the packed version of the actual trick
     * @return actualTrick
     */
    public int packedTrick() {
        return actualTrick;
    }

    /**
     * getter for the object version of the actual score
     * @return actualScore
     */
    public Score score() {
        return Score.ofPacked(actualScore);
    }

    /**
     * getter for the object version of the set of non played cards
     * @return unplayedCards
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * getter for the object version of the actual trick
     * @return actualTrick
     */
    public Trick trick() {
        return Trick.ofPacked(actualTrick);
    }

    /**
     * returns true if the state is terminal, i.e if the last trick
     * of the turn has been played
     * 
     * @return true if the trick is terminal
     */
    public boolean isTerminal() {
        return actualTrick == PackedTrick.INVALID;
    }

    /**
     * returns the identity of the player who has to play next,
     * or throws an exception if the current trick is full
     * 
     * @return the identity of the player who has to play next
     * @throws IllegalStateException if the current trick is full
     */
    public PlayerId nextPlayer() throws IllegalStateException {
        if (PackedTrick.isFull(actualTrick)) 
            throw new IllegalStateException("The current trick is full");
        return PackedTrick.player(actualTrick,PackedTrick.size(actualTrick));
    }

    /**
     * returns the state corresponding to the state of the receptor,
     * to which we add the card played by the next player,
     * or throws an exception if the current trick is full
     * 
     * @param card
     *          the card played by the next player
     * @return the state of the receptor having added the card
     * @throws IllegalStateException if the current trick is full
     */
    public TurnState withNewCardPlayed(Card card) throws IllegalStateException {
        assert unplayedCards().contains(card);
        if (PackedTrick.isFull(actualTrick)) 
            throw new IllegalStateException("The current trick is full");
        return ofPackedComponents(actualScore,
                PackedCardSet.remove(unplayedCards, card.packed()),
                PackedTrick.withAddedCard(actualTrick, card.packed()));
    }

    /**
     * returns the state corresponding to the state of the receptor
     * after the current trick was collected,
     * or throws an exception if the current trick is not over
     * 
     * @return the state of the receptor after the current trick was collected
     * @throws IllegalStateException if the current trick is not full
     */
    public TurnState withTrickCollected() throws IllegalStateException {
        if (!(PackedTrick.isFull(actualTrick)))
            throw new IllegalStateException("The trick is not over");

        long newScore = PackedScore.withAdditionalTrick(actualScore,
                PackedTrick.winningPlayer(actualTrick).team(),
                PackedTrick.points(actualTrick));
        /* if next trick is INVALID, we must check that the next score is valid,
         * because the private constructor does not check it */
        if (PackedTrick.isLast(actualTrick) && PackedScore.isValid(newScore))
            return new TurnState(newScore, unplayedCards, PackedTrick.INVALID);

        return ofPackedComponents(newScore, unplayedCards, PackedTrick.nextEmpty(actualTrick));
    }

    /**
     * returns the state corresponding to the state of the receptor,
     * after the next player has played, and the trick has been 
     * collected if it is now full;
     * 
     * @param card
     *          the card played by the next player   
     * @return the state of the receptor having added the card and collected the trick
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        TurnState withNewCardPlayed = withNewCardPlayed(card);
        if (PackedTrick.isFull(withNewCardPlayed.actualTrick))
            return withNewCardPlayed.withTrickCollected();
        return withNewCardPlayed; 
    }

}