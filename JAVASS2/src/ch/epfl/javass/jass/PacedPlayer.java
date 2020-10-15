package ch.epfl.javass.jass; 

import java.util.Map;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** This class ensures that a player takes a minimum time to play **/
public final class PacedPlayer implements Player {

    private final Player underlyingPlayer;
    private final double minTime;

    /** constructor of the class **/
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startingTime = System.currentTimeMillis();
        Card cardToPlay = underlyingPlayer.cardToPlay(state, hand);
        long endingTime = System.currentTimeMillis();
        double minTimeInMilliSeconds = minTime * 1000;
        if (endingTime - startingTime < minTimeInMilliSeconds) {
            try {
                Thread.sleep((long)( minTimeInMilliSeconds - (endingTime - startingTime) ));
            } catch (InterruptedException e) { /* ignore */ }
        }
        return cardToPlay;
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
    
    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chooseTrump(ch.epfl.javass.jass.PlayerId)
     */
    @Override
    public Color chooseTrump(CardSet hand) {
        return underlyingPlayer.chooseTrump(hand);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#selectMeldSet(java.util.List)
     */
    @Override
    public MeldSet selectMeldSet(CardSet hand) {
        return underlyingPlayer.selectMeldSet(hand);
    }
    
    

}