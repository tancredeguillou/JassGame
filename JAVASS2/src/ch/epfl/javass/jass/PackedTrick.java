package ch.epfl.javass.jass; 

import java.util.StringJoiner;
import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class containing methods allowing to handle the tricks in a game **/
public final class PackedTrick implements Jass {

    private PackedTrick() {
    }

    /** representation of an invalid packed trick **/
    public static final int INVALID = ~0;
    /* the start index of bits representing the first card in the packed representation of the trick */
    private final static int START_CARD_0 = 0;
    /* the range of bits representing a card in the packed representation of the trick */
    private final static int RANGE_CARD = 6;
    /* the start index of bits representing the index in the packed representation of the trick */
    private final static int START_INDEX = 24;
    /* the range of bits representing the index in the packed representation of the trick */
    private final static int RANGE_INDEX = 4;
    /* the start index of bits representing the first player in the packed representation of the trick */
    private final static int START_FIRST_PLAYER = 28;
    /* the range of bits representing the first player in the packed representation of the trick */
    private final static int RANGE_FIRST_PLAYER = 2;
    /* the start index of bits representing the trump in the packed representation of the trick */
    private final static int START_TRUMP = 30;
    /* the range of bits representing the trump in the packed representation of the trick */
    private final static int RANGE_TRUMP = 2;

    /**
     * return true if the given integer represents a valid packaged trick, i.e
     * if the index is between 0 and 8 (inclusive) and any invalid cards are
     * grouped in the upper indexes-i.e, the trick has either no invalid card,
     * only one at index 3, two at indexes 3 and 2, three at indexes 3, 2 and 1,
     * or four at indexes 3, 2, 1 and 0
     * 
     * @param pkTrick
     *            the Packed Trick we want to check
     * @return true if the given integer represents a valid packaged trick
     */
    public static boolean isValid(int pkTrick) {

        int index = 0;
        while (index < NB_CARDS_PER_TRICK && PackedCard.isValid(card(pkTrick, index))) {
            ++index;
        }
        for (int j = index; j < NB_CARDS_PER_TRICK; ++j) {
            if (card(pkTrick, j) != PackedCard.INVALID)
                return false;
        }
        return Bits32.extract(pkTrick, START_INDEX, RANGE_INDEX) < TRICKS_PER_TURN;
    }

    /**
     * return the packed bundle empty-i.e with no index-card 0 with the trump
     * and the first player given
     * 
     * @param trump
     *            trump given
     * @param firstPlayer
     *            first player given
     * @return the packed bundle empty-i.e with no index-card 0 with the trump
     *         and the first player given
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        assert trump != null && firstPlayer != null;
        return Bits32.pack(PackedCard.INVALID, RANGE_CARD, PackedCard.INVALID, RANGE_CARD,
                PackedCard.INVALID, RANGE_CARD, PackedCard.INVALID, RANGE_CARD, 0, RANGE_INDEX,
                firstPlayer.ordinal(), RANGE_FIRST_PLAYER, trump.ordinal(), RANGE_TRUMP);
    }

    /**
     * return the empty wrapped trick following the given one (assumed to be
     * full), i.e the empty trick whose trump is identical to that of the given
     * trick, the index is the successor to that of the given trick and the
     * first player is the winner of the given trick; if the given trick is the
     * last of the turn, then the invalid trick (INVALID above) is returned
     * 
     * @param pkTrick
     *            the given packed trick
     * @return the empty wrapped trick following the given one (assumed to be
     *         full)
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return (isLast(pkTrick) ? INVALID
                : Bits32.pack(PackedCard.INVALID, RANGE_CARD, PackedCard.INVALID, RANGE_CARD,
                        PackedCard.INVALID, RANGE_CARD, PackedCard.INVALID, RANGE_CARD,
                        index(pkTrick) + 1, RANGE_INDEX, winningPlayer(pkTrick).ordinal(),
                        RANGE_FIRST_PLAYER, trump(pkTrick).ordinal(), RANGE_TRUMP));
    }

    /**
     * returns true if the trick is the last of the turn, i.e if its index is 8
     * 
     * @param pkTrick
     *            the packed Trick given
     * @return true if the trick is the last of the turn, i.e if its index is 8
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, START_INDEX, RANGE_INDEX) == INDEX_LAST_TRICK;
    }

    /**
     * return true if the trick is empty, i.e if it contains no cards
     * 
     * @param pkTrick
     *            the packed Trick given
     * @return true if the trick is empty, i.e if it contains no cards
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return card(pkTrick, 0) == PackedCard.INVALID;
    }

    /**
     * returns true if the trick is full, i.e if it contains 4 cards
     * 
     * @param pkTrick
     *            the packed Trick given
     * 
     * @return true if the trick is full, i.e if it contains 4 cards
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return card(pkTrick, 3) != PackedCard.INVALID;
    }

    /**
     * returns the size of the trick, i.e the number of cards it contains
     * 
     * @param pkTrick
     *            the packed Trick given
     * @return the size of the trick, i.e the number of cards it contains
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        for (int i = 0; i < NB_CARDS_PER_TRICK; ++i) {
            if (card(pkTrick, i) == PackedCard.INVALID) {
                return i;
            }
        }
        return NB_CARDS_PER_TRICK;
    }

    /**
     * return the trump of the trick
     * 
     * @param pkTrick
     *            the packed Trick given
     * @return the trump of the trick
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Card.Color.ALL.get(Bits32.extract(pkTrick, START_TRUMP, RANGE_TRUMP));
    }

    /**
     * returns the given index player in the trick, the index player 0 being the
     * first of the trick
     * 
     * @param pkTrick
     *            the packed Trick given
     * @param index
     *            the index of the player we want
     * @return the given index player in the trick, the index player 0 being the
     *         first of the trick
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick) && (index < NB_CARDS_PER_TRICK && index >= 0);
        return PlayerId.ALL.get((Bits32.extract(pkTrick, START_FIRST_PLAYER,
                RANGE_FIRST_PLAYER) + index) % PlayerId.COUNT);
    }

    /**
     * return the index of the trick
     * 
     * @param pkTrick
     *            the Packed Trick given
     * @return the index of the trick
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, START_INDEX, RANGE_INDEX);
    }

    /**
     * returns the packaged version of the trick card at the given index
     * (assumed to have been set)
     * 
     * @param pkTrick
     *            the packed Trick given
     * @param index
     *            the index of the card we want
     * @return the packaged version of the trick card at the given index
     */
    public static int card(int pkTrick, int index) {
        assert index >= 0 && index < NB_CARDS_PER_TRICK;
        return Bits32.extract(pkTrick, index * RANGE_CARD, RANGE_CARD);
    }

    /**
     * returns the packed version of a trick identical to the given one (assumed
     * not full), to which we added the given card
     * 
     * @param pkTrick
     *            the given packed trick
     * @param pkCard
     *            the card we want to add to the trick
     * @return the packed version of pkTrick to which we added pkCard
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick) && PackedCard.isValid(pkCard)
        && !isFull(pkTrick);
        int index = 0;
        while (!(card(pkTrick, index) == PackedCard.INVALID)) {
            ++index;
        }
        return (Bits32.mask(RANGE_CARD * index, RANGE_CARD) ^ (pkCard << RANGE_CARD * index)) ^ pkTrick;
    }

    /**
     * returns the base color of the trick, i.e the color of its first card
     * (assumed to have been played)
     * 
     * @param pkTrick
     *            the Packed Trick given
     * @return the base color of the trick, i.e the color of its first card
     *         (assumed to have been played)
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick) && size(pkTrick) > 0;
        return PackedCard.color(Bits32.extract(pkTrick, START_CARD_0, RANGE_CARD));
    }

    /**
     * return the subset (packaged) of the pkHand hand cards that can be played
     * as the next pkTrick fold card (assumed not full)
     * 
     * @param pkTrick
     *            the given packed trick
     * @param pkHand
     *            the given packed hand
     * @return the subset (packaged) of the pkHand hand cards that can be played
     *         as the next pkTrick fold card (assumed not full)
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick) && PackedCardSet.isValid(pkHand)
        && !isFull(pkTrick) && !PackedCardSet.isEmpty(pkHand);

        if (isEmpty(pkTrick)) return pkHand;

        /* in either case, all cards of the same color as the base color can be played,
         * EVEN if the base color is the trump */
        long cardsFollowing = PackedCardSet.subsetOfColor(pkHand,
                baseColor(pkTrick));

        /* if the player has no cards of the base color in his hand, he can play everything */
        if (cardsFollowing == 0L) 
            return PackedCardSet.union(PackedCardSet.difference(pkHand, 
                    PackedCardSet.subsetOfColor(pkHand, trump(pkTrick))), possibleCuts(pkTrick, pkHand));

        /* if the base color is the trump color, and the player has only the jack */
        if (baseColor(pkTrick).equals(trump(pkTrick))
                && cardsFollowing == PackedCardSet.singleton(
                        PackedCard.pack(trump(pkTrick), Card.Rank.JACK)))
            return pkHand;

        /* finally we return the union between our set of playable cards, 
         * along with the set of trump cards we could use to cut */
        return PackedCardSet.union(cardsFollowing, possibleCuts(pkTrick, pkHand));

    }

    /**
     * returns the trick's value, taking into account the 5 "last trick" points
     * 
     * @param pkTrick
     *            the trick we want to know the value
     * @return pkTrick's value
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int points = 0;
        for (int i = 0; i < size(pkTrick); ++i) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        return (isLast(pkTrick) ? points + LAST_TRICK_ADDITIONAL_POINTS : points);
    }

    /**
     * returns the identity of the player leading the trick (assumed not empty)
     * 
     * @param pkTrick
     *            the Packed trick given
     * @return the identity of the player leading the trick (assumed not empty)
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick) && size(pkTrick) > 0;
        return player(pkTrick, winningIndexCard(pkTrick));
    }

    /**
     * returns a visual representation of the trick
     * 
     * @param pkTrick
     *            the packed version of the trick we want to represent
     * @return a visual representation of pkTrick
     */
    public static String toString(int pkTrick) {
        assert !isEmpty(pkTrick);
        StringJoiner s = new StringJoiner(", ");
        for (int i = 0; i < size(pkTrick); ++i) {
            s.add(PackedCard.toString(card(pkTrick, i)));
        }
        return s.toString();
    }

    /****************************** private methods useful to clarify the code ********************************/

    private static int winningIndexCard(int pkTrick) {
        int winningIndex = 0;
        for (int i = 1; i < size(pkTrick); ++i) {
            if (isValid(card(pkTrick, i)) && PackedCard.isBetter(trump(pkTrick),
                    card(pkTrick, i), card(pkTrick, winningIndex))) {
                winningIndex = i;
            }
        }
        return winningIndex;
    }

    private static long possibleCuts(int pkTrick, long pkHand) {
        int bestPkCard = card(pkTrick, winningIndexCard(pkTrick));
        long possibilities;

        /* if the best card is a trump, then the player can either cut with
         * stronger trumps, or cut with smaller trumps if he has ONLY smaller
         * trump */
        if (PackedCard.color(bestPkCard).equals(trump(pkTrick))) {
            possibilities = PackedCardSet.intersection(pkHand,
                    PackedCardSet.trumpAbove(bestPkCard));
            /* if the player has no higher trumps, he can cut with smaller ones
             * (if he has some)
             * AND if he has no other cards than trump cards */
            if (possibilities == 0 && pkHand == PackedCardSet
                    .subsetOfColor(pkHand, trump(pkTrick)))
                return PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));

            return possibilities;
        }

        /* if the best card is not a trump, the player can cut with everyone of
         * his trumps */
        return PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
    }

}