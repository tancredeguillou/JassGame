package ch.epfl.javass.jass; 

import java.util.List;
import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Interface representing a player **/
public interface Player {

    /**
     * returns the card that the player wants to play, knowing the actual state
     * of the turn and the cards in the player's hand
     * 
     * @param state
     *            the actual state of the turn
     * @param hand
     *            the actual hand of the player
     * @return the card that the player wants to play
     */
    abstract Card cardToPlay(TurnState state, CardSet hand);

    /**
     * return the trump chosen by the player
     * 
     * @param hand
     *            actual hand of the player
     * @return the trump chosen by the player
     */
    abstract Color chooseTrump(CardSet hand);

    /**
     * Return the selected meldSet from the list in argument
     * 
     * @param melds
     *            the list of MeldSets
     * @return the selected meldSet from the list in argument
     */
    default MeldSet selectMeldSet(CardSet hand) {
        assert hand != null;
        //the default value is the best MeldSet
        List<MeldSet> meldSets = MeldSet.allIn(hand);
        //we reverse the order : index 0 give the best MeldSet
        meldSets.sort((m1,m2)-> Integer.compare(m2.points(), m1.points()));
        return meldSets.get(0);
    }

    /**
     * return if the player wants to chibrer (by default it's false)
     * 
     * @return if the player wants to chibrer
     */
    default boolean choseToChibrer() {
        return false;
    }

    /**
     * informs the player that its identity is ownId and that the different
     * players (including himself) are named according to the contents of the
     * map playerNames
     * 
     * @param ownId
     * @param playerNames
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}

    /**
     * informs the player about his new hand
     * 
     * @param newHand
     */
    default void updateHand(CardSet newHand) {}

    /**
     * informs the player about the actual trump
     * 
     * @param trump
     */
    default void setTrump(Color trump) {}

    /**
     * updates the current trick, every time a card is played,
     * or when a trick is over, collected and replaced by the next (empty) one
     * 
     * @param newTrick
     */
    default void updateTrick(Trick newTrick) {}

    /**
     * updates the current score, each time a trick is collected
     * 
     * @param score
     */
    default void updateScore(Score score) {}

    /**
     * sets the winning team as soon as a team has won,
     * reaching 1000 points or above
     * 
     * @param winningTeam
     */
    default void setWinningTeam(TeamId winningTeam) {}
    
    /**
     * inform which player had the best MeldSet
     * 
     * @param newHand
     */
    default void setWinningPlayerOfMelds(PlayerId winningPlayer,MeldSet meldset) {}

}
