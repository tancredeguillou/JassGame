package ch.epfl.javass.jass; 

import ch.epfl.javass.bits.Bits32;

/**
 *  @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining the behavior of a card in compact representation **/
public final class PackedCard {
    private PackedCard() {
    }

    /** Defines what would be the compact representation of an invalid card **/
    public final static int INVALID = 0b111111;
    /* the start index of bits representing the rank in the packed representation of the card */
    private final static int START_RANK = 0;
    /* the range of bits representing the rank in the packed representation of the card */
    private final static int RANGE_RANK = 4;
    /* the start index of bits representing the color in the packed representation of the card */
    private final static int START_COLOR = 4;
    /* the range of bits representing the color in the packed representation of the card */
    private final static int RANGE_COLOR = 2;

    /**
     * returns true if the value given is a valid packed card, i.e if the bits
     * containing the rank contain a value between 0 and 8 (inclusive) and the
     * unused bits are all 0
     * 
     * @param pkCard
     *            card to check
     * @return if the card is a valid packed card
     */
    public static boolean isValid(int pkCard) {
        int rank = Bits32.extract(pkCard, START_RANK, RANGE_RANK);

        return (rank < Card.Rank.COUNT && rank >= 0 && (~INVALID & pkCard) == 0);
    }

    /**
     * return the packaged card of given color and rank
     * 
     * @param c
     *            color of the card
     * @param r
     *            rank of the card
     * @return the packaged card of given color and rank,
     */
    public static int pack(Card.Color c, Card.Rank r) {
        /* we are supposed to have a color and a rank in arguments */
        assert (c != null && r != null);

        return Bits32.pack(r.ordinal(), RANGE_RANK, c.ordinal(), RANGE_COLOR);
    }

    /**
     * returns the color of the given bundled card
     * 
     * @param pkCard
     *            the packed card
     * @return the color of the given bundled card
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);

        return Card.Color.ALL.get(Bits32.extract(pkCard, START_COLOR, RANGE_COLOR));
    }

    /**
     * returns the rank of the given bundled card
     * 
     * @param pkCard
     *            the packed card
     * @return the rank of the given bundled card
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);

        return Card.Rank.ALL.get(Bits32.extract(pkCard, START_RANK, RANGE_RANK));
    }

    /**
     * return true if the first given card is greater than the second, knowing
     * that trump is trump; note that this implies that this method returns
     * false if the two cards are not comparable
     * 
     * @param trump
     *            the trump color
     * @param pkCardL
     *            the first card
     * @param pkCardR
     *            the second card
     * @return true if the first given card is greater than the second, false if
     *         the two cards are not comparable
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert trump != null && isValid(pkCardL) && isValid(pkCardR);

        if (color(pkCardL).equals(color(pkCardR))) {
            if (color(pkCardL).equals(trump)) {
                return rank(pkCardL).trumpOrdinal() > rank(pkCardR)
                        .trumpOrdinal();
            } else {
                return rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
            }
        }

        return color(pkCardL).equals(trump);
    }

    /**
     * returns the value of the given bundled card, knowing that trump is trump
     * 
     * @param trump
     *            the trump color
     * @param pkCard
     *            the card we want to calculate the value
     * @throws Error as a default outcome
     * @return the value of the given bundled card, knowing that trump is trump
     */
    public static int points(Card.Color trump, int pkCard) throws Error {
        assert isValid(pkCard) && trump != null;

        if (color(pkCard).equals(trump)) {
            switch (rank(pkCard).ordinal()) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
                return 14;
            case 4:
                return 10;
            case 5:
                return 20;
            case 6:
                return 3;
            case 7:
                return 4;
            case 8:
                return 11;
            default:
                throw new Error("the rank is not valid");
            }
        } else {
            switch (rank(pkCard).ordinal()) {
            case 0:
            case 1:
            case 2:
            case 3:
                return 0;
            case 4:
                return 10;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 4;
            case 8:
                return 11;
            default:
                throw new Error("the rank is not valid");
            }
        }
    }

    /**
     * returns a representation of the packed map given as a string consisting
     * of the color symbol and the abbreviated rank name.
     * 
     * @param pkCard
     *            the card we want to represent
     * @return a representation of the packed map given as a string consisting
     *         of the color symbol and the abbreviated rank name.
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);

        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
