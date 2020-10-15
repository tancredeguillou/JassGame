package ch.epfl.javass.jass; 

import java.util.StringJoiner;
import ch.epfl.javass.bits.Bits64;

/**
 * @author tancrede guillou (287334)
 * @author Ouriel Sebbagh (287796)
 */

/** Class defining the behavior of a set of cards, represented has a unique Long number **/
public final class PackedCardSet implements Jass {
    private PackedCardSet() {
    }

    /** the Long representing an empty set of cards **/
    public final static long EMPTY = 0L;
    /** the Long representing the set of all cards **/
    public final static long ALL_CARDS = ~0 ^ cardSetMask();
    private final static long[] TAB_TRUMP_ORDER = createTabTrump();
    private final static long[] TAB_COLOR = createTabColor();

    /**
     * returns true if the given value represents a valid set of cards, i.e if
     * none of the 28 unused bits is a 1
     * 
     * @param pkCardSet
     *            : the compact set of cards representation
     * @return true if pkCardSet represents a valid set of cards
     */
    public static boolean isValid(long pkCardSet) {
        return (cardSetMask() & pkCardSet) == 0;
    }

    /**
     * returns the set of cards that are strictly stronger then the given
     * compact card, given that it is a trump card
     * 
     * @param pkCard
     *            : the compact representation of the trump card
     * @return the set of cards that are stronger then pkCard
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return TAB_TRUMP_ORDER[PackedCard.rank(pkCard).ordinal()
                               + HAND_SIZE * PackedCard.color(pkCard).ordinal()];
    }

    /**
     * returns a representation of the set of cards only containing the given
     * compact card
     * 
     * @param pkCard
     *            : the given compact representation of the card
     * @return a representation of the set of cards only containing the given
     *         compact card
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return 1l<<pkCard;
    }

    /**
     * returns true if the given set of cards is empty
     * 
     * @param pkCardSet
     *            : the compact representation of the set of cards
     * @return true if pkCardSet is an empty set of cards
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * returns the size of the given set of cards
     * 
     * @param pkCardSet
     *            : the compact representation of the set of cards
     * @return the number of 1s in pkCardSet
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * return the packaged version of the given index card of the given packaged
     * card set, the index card 0 being that corresponding to the least
     * significant bit equal to 1,
     * 
     * @param pkCardSet
     *            : the packed card set
     * @param index
     *            : the index of the card we want in the above pkCardSet
     * @return the packaged version of the given index card of the given
     *         packaged card set, the index card 0 being that corresponding to
     *         the least significant bit equal to 1
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet) && index >= 0 && index < size(pkCardSet);
        for (int i = 0; i < index; ++i) {
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * return the given bundled card set to which the given bundled card was
     * added
     * 
     * @param pkCardSet
     *            the packed card set
     * @param pkCard
     *            the packed card we want to add
     * @return the given bundled card set to which the given bundled card was
     *         added
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return pkCardSet | singleton(pkCard);
    }

    /**
     * return the given bundled card set to which the given bundled card was
     * removed
     * 
     * @param pkCardSet
     *            the packed card set
     * @param pkCard
     *            the packed card we want to remove
     * @return the given bundled card set to which the given bundled card was
     *         added
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return (pkCardSet ^ singleton(pkCard)) & pkCardSet;
    }

    /**
     * return true if and only if the given packaged card set contains the given
     * bundled card
     * 
     * @param pkCardSet
     *            the packed card set
     * @param pkCard
     *            the packed card we want to check
     * @return true if and only if the given packaged card set contains the
     *         given bundled card
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return (pkCardSet & singleton(pkCard)) != EMPTY;
    }
    
    /**
     * return true if and only if the given packaged card set 1 contains all the
     * cards of the second one
     * 
     * @param pkCardSet1
     *            the first packed card set
     * @param pkCardSet1
     *            the second packed card set
     * @return true if and only if the given packaged card set contains all the
     *         cards of the second one
     */
    public static boolean containsAll(long pkCardSet1, long pkCardset2) {
        for (int i = 0; i < size(pkCardset2); ++i) {
            if(! contains(pkCardSet1,get(pkCardset2,i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns the complement of the given compact representation of a set of
     * cards
     * 
     * @param pkCardSet
     *            : the compact representation of a set of cards
     * @return the complement of pkCardSet, as a valid compact representation of
     *         a set of cards
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        return ~pkCardSet ^ cardSetMask();
    }

    /**
     * returns the union between the compact representations of two sets of
     * cards
     * 
     * @param pkCardSet1
     *            : the compact representation of the first set of cards
     * @param pkCardSet2
     *            : the compact representation of the second set of cards
     * @return a compact representation of the union between pkCardSet1 &
     *         pkCardSet2
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * returns the intersection between the compact representations of two sets
     * of cards
     * 
     * @param pkCardSet1
     *            : the compact representation of the first set of cards
     * @param pkCardSet2
     *            : the compact representation of the second set of cards
     * @return a compact representation of the intersection between pkCardSet1 &
     *         pkCardSet2
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * returns the difference between the first set of maps bundled given and
     * the second, i.e all the cards that are in the first set but not in the
     * second
     * 
     * @param pkCardSet1
     *            the first packed card set
     * @param pkCardSet2
     *            the second packed card set
     * @return the difference between the first set of maps bundled given and
     *         the second, i.e all the cards that are in the first set but not in
     *         the second
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 ^ (pkCardSet1 & pkCardSet2);
    }

    /**
     * return the subset of the given bundle of cards consisting of only the
     * cards of the given color
     * 
     * @param pkCardSet
     *            the packed card set
     * @param color
     *            the color of which we are interested
     * @return the subset of the given bundle of cards consisting of only the
     *         cards of the given color
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet) && color != null;
        return pkCardSet & TAB_COLOR[color.ordinal()];
    }

    /**
     * return the text representation of the given bundle of cards
     * 
     * @param pkCardSet
     *            the packed card set
     * @return the text representation of the given bundle of cards
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkCardSet); ++i) {
            j.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return j.toString();
    }

    /***************************** private methods useful to clarify the code ******************************/

    private static long cardSetMask() {
        return Bits64.mask(9, 7) | Bits64.mask(25, 7) | Bits64.mask(41, 7)
                | Bits64.mask(57, 7);
    }

    private static long[] createTabTrump() {
        long[] tab = new long[TOTAL_CARDS];
        for (Card.Color color : Card.Color.ALL) {
            for (Card.Rank rank1 : Card.Rank.ALL) {
                for (Card.Rank rank2 : Card.Rank.ALL) {
                    if (Card.of(color, rank2).isBetter(color,
                            Card.of(color, rank1))) {
                        tab[color.ordinal() * HAND_SIZE + rank1.ordinal()] = add(
                                tab[color.ordinal() * HAND_SIZE + rank1.ordinal()],
                                Card.of(color, rank2).packed());
                    }
                }
            }
        }
        return tab;
    }

    private static long[] createTabColor() {
        long[] tab = new long[Card.Color.COUNT];
        for(int i = 0; i< Card.Color.COUNT;++i) {
            tab[i] = Bits64.mask(i* (Long.SIZE/Card.Color.COUNT), Card.Rank.COUNT);
        }
        return tab;
    }
}
