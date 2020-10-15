/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */
package ch.epfl.javass.jass; 

import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/** class representing the behavior of a set of melds **/
public final class MeldSet implements Comparable<MeldSet>{

    private final Set<Meld> melds;

    /**
     * public constructor
     * 
     * @param melds
     *            the different melds
     * @return the medlSet of the given melds
     */
    public static MeldSet of(Collection<Meld> melds) {
        if (!mutuallyDisjoint(melds))
            throw new IllegalArgumentException();
        return new MeldSet(melds);
    }

    private MeldSet(Collection<Meld> melds) {
        /*
         * Tree set to assure ourself that each "for each loop" will be executed
         * in the same order
         */
        this.melds = unmodifiableSet(new TreeSet<>(melds));
    }

    private static boolean mutuallyDisjoint(Collection<Meld> melds) {
        // we keep set<Card> to be able to use mutuallyDisjoint !
        List<Set<Card>> allSetsOfCards = new ArrayList<>();
        for (Meld m : melds) {
            Set<Card> cardsInM = new HashSet<>();
            // we create a set of card because we can't add a card set
            for (int i = 0; i < m.cards().size(); ++i) {
                cardsInM.add(m.cards().get(i));
            }
            allSetsOfCards.add(cardsInM);
        }
        return Sets.mutuallyDisjoint(allSetsOfCards);
    }

    /**
     * @param hand
     *            the given CardSet
     * @return the list of all the disjoint MeldSets
     */
    public static List<MeldSet> allIn(CardSet hand) {
        List<MeldSet> r = new ArrayList<>();
        for (Set<Meld> melds : Sets.powerSet(Meld.allIn(hand))) {
            if (mutuallyDisjoint(melds))
                r.add(new MeldSet(melds));
        }
        return r;
    }

    /**
     * return the number of points of this meldSet
     * 
     * @return the number of points of this meldSet
     */
    public int points() {
        int points = 0;
        for (Meld m : melds)
            points += m.points();
        return points;
    }

    /**
     * return the number of Meld in this MeldSet
     * 
     * @return the number of Meld in this MeldSet
     */
    public int size() {
        return melds.size();
    }

    /**
     * return a list of the cardSet of the different Melds
     * 
     * @return a list of the cardSet of the different Melds
     */
    public List<CardSet> getCardSets() {
        List<CardSet> cardSets = new ArrayList<>();
        melds.forEach(meld -> cardSets.add(meld.cards()));
        return cardSets;
    }

    /**
     * return a list of the points of the different Melds (we can ensure ourself
     * that the points at index i correspond to the cardSet at the index i in
     * the getCardset() method because the set of Melds is a TREE SET)
     * 
     * @return a list of the points of the different Melds
     */
    public List<Integer> getPoints() {
        List<Integer> listPoints = new ArrayList<>();
        melds.forEach(meld -> listPoints.add(meld.points()));
        return listPoints;
    }

    @Override
    public int compareTo(MeldSet that) {
        if(this.size() ==0 && that.size() == 0) 
            //both have 0 points
            return 0;
        if(this.size() ==0)
            //we have 0 points
            return -1;
        if(that.size() ==0)
            //the other have 0 points
            return 1;

        //both have at least one meld
        return this.highestMeld().compareTo(that.highestMeld());
    }

    /**
     * @return the best Meld of the set of melds in arguments
     */
    private Meld highestMeld() {
        // we use a treeSet when initializing the melds so the last is the best
        return Meld.of(getCardSets().get(size() - 1),
                getPoints().get(size() - 1));
    }

    @Override
    public String toString() {
        StringJoiner s = new StringJoiner(", ", "{", "}");
        for (Meld m: melds)
            s.add(m.cards().toString());
        return String.format("%3d: %s", points(), s);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((melds == null) ? 0 : melds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MeldSet other = (MeldSet) obj;
        if (melds == null) {
            if (other.melds != null)
                return false;
        } else if (!melds.equals(other.melds))
            return false;
        return true;
    }


}
