package ch.epfl.javass.gui; 

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** bean JavaFX containing the hand of a player **/
public final class HandBean {

    private final ObservableList<Card> hand;
    private final ObservableSet<Card> playableCards;
    private final SimpleObjectProperty<Card> hintPlayerCard;
    private final SimpleObjectProperty<Boolean> showBestCard;


    /**
     * only constructor of the bean, which initialize the playableCards and the
     * hand to nine empty cards
     */
    public HandBean() {
        hand = FXCollections.observableArrayList();
        // at the begining we have an empty hand with 9 "empty" cards
        hand.addAll(null, null, null, null, null, null, null, null, null);
        playableCards = FXCollections.observableSet();
        hintPlayerCard = new SimpleObjectProperty<>();
        this.showBestCard = new SimpleObjectProperty<>(false);

    }

    /*********************************************************************************/

    /**
     * return the read-only property of the hand
     * 
     * @return the read-only property of the hand
     */
    public ObservableList<Card> hand() {
        // it will automatically be wrapped up
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * modifies the hand of the bean
     * @param newHand
     *              the new hand
     */
    public void setHand(CardSet newHand) {
        int size = newHand.size();
        assert size <= Jass.HAND_SIZE;

        /* if size is 9, then we replace all the cards */
        if (size == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {
                hand.set(i, newHand.get(i));
            }
        }else {
            /*
             * if size is less than 9, then all cards that are in hand but not
             * in newHand are assigned to null
             */
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {
                /* if a card is already null, it remains null no matter what */
                if (hand.get(i) != null) {
                    /*
                     * if size == 0, then all cards must be null
                     * 
                     * if the i-TH card in our hand equals the first card of
                     * newHand, then we remove this card from newHand to test
                     * the next one ELSE we assign the card in hand to null, and
                     * we continue
                     */
                    if (size != 0 && hand.get(i).equals(newHand.get(0))) {
                        newHand = newHand.remove(newHand.get(0));
                        size = newHand.size();
                    } else {
                        hand.set(i, null);
                    }
                }
            }
        }
    }

    /*********************************************************************************/

    /**
     * return the read-only property of the playable cards
     * 
     * @return the read-only property of the playable cards
     */
    public ObservableSet<Card> playableCards() {
        // it will automatically be wrapped up
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    /**
     * modifies the playableCards
     * @param newPlayableCards
     *              the new playableCards
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        //each time it's a new set !
        playableCards.clear();
        for (int i = 0; i < newPlayableCards.size(); ++i) {
            playableCards.add(newPlayableCards.get(i));
        }
    }

    /**
     * return the read-only property of the hintPlayerCard
     * 
     * @return the read-only property of the hintPlayerCard
     */
    public ReadOnlyObjectProperty<Card> hintPlayerCardProperty() {
        return hintPlayerCard;
    }

    /**
     * modifies the HintPlayerCard
     * @param newCard
     *              the hint card
     */
    public void setHintPlayerCard(Card newCard) {
        hintPlayerCard.set(newCard);
    }

    /**
     * return the read-only property of the showBestCard
     * 
     * @return the read-only property of the showBestCard
     */
    public ReadOnlyObjectProperty<Boolean> showBestCardProperty() {
        return showBestCard;
    }

    /**
     * modifies the showBestCard property
     * @param value
     *              the boolean value
     */
    public void setshowBestCard(Boolean value) {
        showBestCard.set(value);
    }
}