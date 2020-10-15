package ch.epfl.javass.jass; 

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining a trick in a game **/
public final class Trick {

    private final int packedRepresentation;

    private Trick(int packedRepresentation) {
        this.packedRepresentation = packedRepresentation;
    }

    /** representation an invalid trick **/
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    /**
     * return the empty trick with the trump and the first player given
     * 
     * @param trump
     *            trump given
     * @param firstPlayer
     *            first player given
     * @return the empty trick with the trump and the first player given
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return ofPacked(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * returns the trick for a given compact represented trick
     * 
     * @param packed
     *            compact representation of the trick
     * @throws IllegalArgumentException if packed does not represent a valid packed trick
     * @return the trick given by packed
     */
    public static Trick ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * return the packaged version of the trick
     * 
     * @return the packaged version of the trick
     */
    public int packed() {
        return packedRepresentation;
    }

    /**
     * acts similarly as its corresponding method in PackedTrick, or throws
     * exception IllegalStateException if the trick is not full
     * 
     * @return an empty trick, following the previous one
     * 
     * @throws IllegalStateException
     *             if the trick is not full
     */
    public Trick nextEmpty() throws IllegalStateException {
        if (!PackedTrick.isFull(packedRepresentation)) {
            throw new IllegalStateException("The trick is not full");
        }
        if (PackedTrick.nextEmpty(packedRepresentation) == PackedTrick.INVALID) 
            return INVALID;
        else
            return ofPacked(PackedTrick.nextEmpty(packedRepresentation));
    }

    /**
     * return true if the trick is empty, i.e if it contains no cards
     * 
     * @return true if the trick is empty
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packedRepresentation);
    }

    /**
     * returns true if the trick is full, i.e if it contains 4 cards
     * 
     * @return true if the fold is full
     */
    public boolean isFull() {
        return PackedTrick.isFull(packedRepresentation);
    }

    /**
     * returns true if the trick is the last of the turn
     * 
     * @return true if the trick is the last of the turn
     */
    public boolean isLast() {
        return PackedTrick.isLast(packedRepresentation);
    }

    /**
     * returns the size of the trick, i.e the number of cards it contains
     * 
     * @return the size of the trick, i.e the number of cards it contains
     */
    public int size() {
        return PackedTrick.size(packedRepresentation);
    }

    /**
     * return the trump of the trick
     * 
     * @return the trump of the trick
     */
    public Color trump() {
        return PackedTrick.trump(packedRepresentation);
    }

    /**
     * return the index of the trick
     * 
     * @return the index of the trick
     */
    public int index() {
        return PackedTrick.index(packedRepresentation);
    }

    /**
     * returns the given index player in the trick, the index player 0 being the
     * first of the trick
     * 
     * @param index
     *            the index of the player we want
     * @return the given index player in the trick, the index player 0 being the
     *         first of the trick
     */
    public PlayerId player(int index) {
        return PackedTrick.player(packedRepresentation,
                Preconditions.checkIndex(index, PlayerId.COUNT));
    }

    /**
     * returns the packaged version of the trick card at the given index
     * (assumed to have been set)
     * 
     * @param index
     *            the index of the card we want
     * @return the packaged version of the trick card at the given index
     */
    public Card card(int index) {
        return Card.ofPacked(PackedTrick.card(packedRepresentation,
                Preconditions.checkIndex(index, size())));
    }

    /**
     * returns the packed version of a trick identical to the given one (assumed
     * not full), to which we added the given card
     * 
     * @param c
     *            the card that was added in the trick
     * @return the packed version of the trick to which we added card c
     * @throws IllegalStateException
     *             if the trick is already full
     */
    public Trick withAddedCard(Card c) throws IllegalStateException {
        if (isFull())
            throw new IllegalStateException("The trick is full");
        return ofPacked(
                PackedTrick.withAddedCard(packedRepresentation, c.packed()));
    }

    /**
     * returns the base color of the trick, i.e the color of its first card
     * (assumed to have been played)
     * 
     * @return the base color of the trick, i.e the color of its first card
     *         (assumed to have been played)
     * @throws IllegalStateException
     *             if the trick is empty
     */
    public Color baseColor() throws IllegalStateException {
        if (isEmpty())
            throw new IllegalStateException("The trick is empty");
        return PackedTrick.baseColor(packedRepresentation);
    }

    /**
     * return the subset of the hand cards that can be played as the next trick
     * card (assumed not full)
     * 
     * @param hand
     *            the given hand
     * @return the subset of the hand cards that can be played as the next trick
     *         card (assumed not full)
     * @throws IllegalStateException
     *             if the trick is already full
     */
    public CardSet playableCards(CardSet hand) throws IllegalStateException {
        if (isFull())
            throw new IllegalStateException("The trick is full");
        return CardSet.ofPacked(
                PackedTrick.playableCards(packedRepresentation, hand.packed()));
    }

    /**
     * returns the trick's value, taking into account the 5 "last trick" points
     * 
     * @return the trick's value
     */
    public int points() {
        return PackedTrick.points(packedRepresentation);
    }

    /**
     * returns the identity of the player leading the trick (assumed not empty)
     * 
     * @return the identity of the player leading the fold (assumed not empty)
     * 
     * @throws IllegalStateException
     *             if the trick is empty
     */
    public PlayerId winningPlayer() throws IllegalStateException {
        if (isEmpty())
            throw new IllegalStateException("The trick is empty");
        return PackedTrick.winningPlayer(packedRepresentation);
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return packedRepresentation;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trick)
            return packedRepresentation == ((Trick) obj).packed();
        else
            return false;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packedRepresentation);
    }

}