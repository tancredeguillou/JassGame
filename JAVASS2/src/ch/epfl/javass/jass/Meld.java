/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */
package ch.epfl.javass.jass; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/** class representing the behavior of a meld **/
public final class Meld implements Comparable<Meld> {
    /**
     * List containing all the melds
     */
    public final static List<Meld> ALL = computeAll();

    private static List<Meld> computeAll() {
        List<Meld> all = new ArrayList<>();
        addAllQuartetsInto(all);
        addAllSuitsInto(all);
        return Collections.unmodifiableList(all);
    }

    private static int quartetPoints(Rank rank) {
        switch (rank) {
        case NINE:
            return 150;
        case JACK:
            return 200;
        case TEN:
        case QUEEN:
        case KING:
        case ACE:
            return 100;
        default:
            throw new Error();
        }
    }

    private static void addAllQuartetsInto(List<Meld> melds) {
        List<Rank> ranksFrom9 = Rank.ALL.subList(Rank.NINE.ordinal(),
                Rank.COUNT);
        for (Rank rank : ranksFrom9) {
            CardSet quartet = CardSet.EMPTY;
            for (Color color : Color.ALL) {
                quartet = quartet.add(Card.of(color, rank));
            }
            melds.add(of(quartet, quartetPoints(rank)));
        }
    }

    private static int suitPoints(int size) {
        switch (size) {
        case 3:
            return 20;
        case 4:
            return 50;
        case 5:
            return 100;
        default:
            throw new Error();
        }
    }

    private static void addAllSuitsInto(List<Meld> melds) {
        for (Color color : Color.ALL) {
            for (int size = 3; size <= 5; size += 1) {
                List<Card> cards = new ArrayList<>();
                for (Rank rank : Rank.ALL) {
                    cards.add(Card.of(color, rank));
                }
                for (int i1 = 0, i2 = size; i2 <= cards
                        .size(); i1 += 1, i2 += 1) {
                    CardSet suit = CardSet.of(cards.subList(i1, i2));
                    melds.add(of(suit, suitPoints(size)));
                }
            }
        }
    }

    /**
     * return the list of all the melds in this hand
     * 
     * @param hand
     *            given Cardset
     * @return the list of all the melds in this hand
     */
    public static List<Meld> allIn(CardSet hand) {
        List<Meld> allIn = new ArrayList<>();
        for (Meld m : ALL) {
            if (hand.containsAll(m.cards()))
                allIn.add(m);
        }
        return allIn;
    }

    private final CardSet cards;
    private final int points;

    private Meld(CardSet cards, int points) {
        this.cards = cards;
        this.points = points;
    }

    /**
     * public constructor of a Meld (here we define it in public because we want
     * to be able to obtain a meld only with his cardSet and his points in the
     * RemotePlayerServeur)
     * 
     * @param cards
     *            cards of the Meld
     * @param points
     *            points of the Meld
     * @return the Meld corresponding to the given cards and points
     */
    public static Meld of(CardSet cards, int points) {
        if (!(0 < points))
            throw new IllegalArgumentException("invalid points: " + points);
        return new Meld(cards, points);
    }

    /**
     * return the CardSet of the cards composing this meld
     * 
     * @return the CardSet of the cards composing this meld
     */
    public CardSet cards() {
        return cards;
    }

    /**
     * return the points of this meld
     * 
     * @return the points of this meld
     */
    public int points() {
        return points;
    }

    @Override
    public String toString() {
        return String.format("%3d: %s", points, cards);
    }

    private int numberOfCards() {
        return cards.size();
    }

    private Card highestCard() {
        // we suppose the size is positive
        Card c = null;
        int best = 0;
        for (int i = 0; i < numberOfCards(); ++i) {
            int rankValue = cards.get(i).rank().ordinal();
            if (rankValue > best) {
                best = rankValue;
                c = cards.get(i);
            }
        }
        return c;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Meld that) {
        if (this.cards.equals(that.cards)) {
            // if they have the same cards they are equal
            return 0;
        }

        if (this.points() != that.points()) {
            return Integer.compare(this.points(), that.points());
        }

        // here they have the same number of points
        if (this.numberOfCards() != that.numberOfCards()) {
            return Integer.compare(this.numberOfCards(), that.numberOfCards());
        }

        // they have the same number of cards so we have the same kind of Meld
        if (!this.highestCard().equals(that.highestCard())) {
            return this.highestCard().rank()
                    .compareTo(that.highestCard().rank());
        }

        /*
         * if we arrive here, it means we have the same suits for example but in
         * a different color so the rules of the game indicate that it's the
         * player who played the first who beat the other, so here the meld on
         * which we call the method CompareTo is higher !
         */
        return 1;
    }
}
