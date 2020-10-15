package ch.epfl.javass.jass; 

import java.util.List;
import ch.epfl.javass.Preconditions;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class representing a set of cards **/
public final class CardSet {
    private CardSet(long packedCardSet) {
        this.packedCardSet = packedCardSet;
    }

    /** the set containing none of the cards **/
    public static final CardSet EMPTY = ofPacked(PackedCardSet.EMPTY);
    /** the set containing all of the cards **/
    public static final CardSet ALL_CARDS = ofPacked(PackedCardSet.ALL_CARDS);
    private long packedCardSet;

    /**
     * returns the set containing the cards given in a list
     * 
     * @param cards
     *          the list of cards
     * @return the set containing the cards in parameter cards
     */
    public static CardSet of(List<Card> cards) {
        long pkCardSet = PackedCardSet.EMPTY;
        for (Card c : cards) {
            pkCardSet = PackedCardSet.add(pkCardSet, c.packed());
        }
        return new CardSet(pkCardSet);
    }

    /**
     * returns the set of cards for a given compact represented set
     * 
     * @param packed
     *          the compact representation of the set of cards
     * @throws IllegalArgumentException if packed doesn't represent a valid card set
     * @return the set of cards of its compact representation
     */
    public static CardSet ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * return the packaged version of the set of cards
     * 
     * @return the packaged version of the set of cards.
     */
    public long packed() {
        return packedCardSet;
    }

    /**
     * return if the card set is empty
     * 
     * @return if the card set is empty
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packedCardSet);
    }

    /**
     * return the size of the card set
     * 
     * @return the size of the card set
     */
    public int size() {
        return PackedCardSet.size(packedCardSet);
    }

    /**
     * return the card at the index specified in the card set
     * 
     * @param index
     *            the index of the card we want
     * @return the card at the index specified in the card set
     */
    public Card get(int index) {
        assert index >= 0 && index < size();
        return Card.ofPacked(PackedCardSet.get(packedCardSet, index));
    }

    /**
     * return the set which has been increased by another card
     * 
     * @param card
     *            the card we want to add in our card set
     * @return the set which has been increased by the given card
     */
    public CardSet add(Card card) {
        assert card != null;
        return ofPacked(PackedCardSet.add(packedCardSet, card.packed()));
    }

    /**
     * return the set which has been decreased by another card
     * 
     * @param card
     *            the card we want to remove in our card set
     * @return the set which has been decreased by the given card
     */
    public CardSet remove(Card card) {
        assert card != null;
        return ofPacked(PackedCardSet.remove(packedCardSet, card.packed()));
    }

    /**
     * return if the given card is in the set of card
     * 
     * @param card
     *            we want to check
     * @return if the given card is in the set of
     */
    public boolean contains(Card card) {
        assert card != null;
        return PackedCardSet.contains(packedCardSet, card.packed());
    }
    
    /**
     * return if all the cards are in the set of card
     * 
     * @param cardSet
     *            we want to check
     * @return if all the cards are in the set of card
     */
    public boolean containsAll(CardSet cards) {
        assert cards != null;
        return PackedCardSet.containsAll(packedCardSet, cards.packed());
    }

    /**
     * return the complement of the set of card
     * 
     * @return the complement of the set of card
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(packedCardSet));
    }

    /**
     * return the union of the two card set
     * 
     * @param that
     *            the second card set
     * @return the union of the two card set
     */
    public CardSet union(CardSet that) {
        assert that != null;
        return ofPacked(PackedCardSet.union(packedCardSet, that.packedCardSet));
    }

    /**
     * return the intersection of the two card set
     * 
     * @param that
     *            the second card set
     * @return the intersection of the two card set
     */
    public CardSet intersection(CardSet that) {
        assert that != null;
        return ofPacked(
                PackedCardSet.intersection(packedCardSet, that.packedCardSet));
    }

    /**
     * return the difference of the two card set
     * 
     * @param that
     *            the second card set
     * @return the difference of the two card set
     */
    public CardSet difference(CardSet that) {
        assert that != null;
        return ofPacked(
                PackedCardSet.difference(packedCardSet, that.packedCardSet));
    }

    /**
     * return the subset of the given bundle of cards consisting of only the
     * cards of the given color
     * 
     * @param color
     *            the color we want to extract from the set
     * @return the subset of the given bundle of cards consisting of only the
     *         cards of the given color
     */
    public CardSet subsetOfColor(Card.Color color) {
        assert color != null;
        return ofPacked(PackedCardSet.subsetOfColor(packedCardSet, color));
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedCardSet);
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardSet)
            return packedCardSet == ((CardSet) obj).packed();
        else
            return false;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(packedCardSet);
    }

}
