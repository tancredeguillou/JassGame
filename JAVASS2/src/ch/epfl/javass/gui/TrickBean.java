/**
 *	@author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.gui; 

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/** bean JavaFX containing the current trick **/
public final class TrickBean {

    private final SimpleObjectProperty<Card.Color> trump;
    private final SimpleObjectProperty<PlayerId> winningPlayer;
    private final SimpleObjectProperty<Boolean> isTheChooser;
    private final SimpleObjectProperty<Boolean> isAskedToChoose;
    private final SimpleObjectProperty<Boolean> showRules;

    /*
     * The idea is to expose the trick as an observable associative table
     * associating to each player the card he played in the current trick, which
     * is null if the player in question has not played yet.
     */
    private final ObservableMap<PlayerId, Card> trick;

    /**
     * only constructor of the bean, which initialize the trump, the trick and
     * the winning player
     */
    public TrickBean() {
        this.trump = new SimpleObjectProperty<Card.Color>();
        this.winningPlayer = new SimpleObjectProperty<PlayerId>();
        this.isTheChooser = new SimpleObjectProperty<Boolean>(false);
        this.isAskedToChoose = new SimpleObjectProperty<Boolean>(false);
        this.showRules = new SimpleObjectProperty<>(false);
        this.trick = FXCollections.observableHashMap();
    }

    /*********************************************************************************/
    /**
     * return the read-only property of the trump
     * 
     * @return the read-only property of the trump
     */
    public ReadOnlyObjectProperty<Card.Color> trumpProperty() {
        // it will automatically be wrapped up
        return trump;
    }

    /**
     * update the value of the current trump
     * 
     * @param newTrump
     *            the new Trump
     */
    public void setTrump(Card.Color newTrump) {
        trump.set(newTrump);
    }

    /*********************************************************************************/
    /**
     * return the Property of the trick, here it's a map because it's more
     * convenient
     * 
     * @return the Property of the trick
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * modify the trick of the bean and update the winning Player
     * 
     * @param newTrick
     *            the trick to be set
     */
    public void setTrick(Trick newTrick) {
        for (int i = 0; i < PlayerId.COUNT; i++) {
            if (i < newTrick.size()) {
                trick.put(newTrick.player(i), newTrick.card(i));
            } else {
                trick.put(newTrick.player(i), null);
            }
        }
        winningPlayer.set(newTrick.isEmpty() ? null : newTrick.winningPlayer());
    }
    /*********************************************************************************/
    /**
     * return the read-only property of the winning player
     * 
     * @return the read-only property of the winning player
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        // it will automatically be wrapped up
        return winningPlayer;
    }

    /**
     * return the read-only property of the chooser
     * 
     * @return the read-only property of the chooser
     */
    public ReadOnlyObjectProperty<Boolean> chooserProperty() {
        return isTheChooser;
    }

    /**
     * set the given value to the property isTheChooser
     * 
     * @param value
     *            the boolean to set
     */
    public void setChooser(Boolean value) {
        isTheChooser.set(value);
    }


    /**
     * return the read-only property of the boolean property isAskedToChoose
     * 
     * @return the read-only property of the boolean property isAskedToChoose
     */
    public ReadOnlyObjectProperty<Boolean> isAskedToChooseProperty() {
        return isAskedToChoose;
    }

    /**
     * set the given value to the property IsAskedToChoose
     * 
     * @param value
     *            the boolean to set
     */
    public void setIsAskedToChoose(Boolean value) {
        isAskedToChoose.set(value);
    }

    /**
     * return the read-only property of the showRulesProperty
     * 
     * @return the read-only property of the showRulesProperty
     */
    public ReadOnlyObjectProperty<Boolean> showRulesProperty() {
        return showRules;
    }

    /**
     * modifies the setShowRules property
     * @param value
     *              the boolean value
     */
    public void setShowRules(Boolean value) {
        showRules.set(value);
    }
}
