/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.net;

/**
 * Enumeration representing 7 types of messages exchanged by the client and the
 * server
 **/
public enum JassCommand {

    //we don't actually need the name of the methods...
    PLRS,//("setPlayers") 
    TRMP,//("setTrump")
    HAND,//("updateHand") 
    TRCK,//("updateTrick")
    CARD,//("cardToPlay")
    SCOR,//("updateScore")
    WINR,//("setWinningTeam")
    CHTP,//("chooseTrump")
    MELD,//("selectMeldSet")
    WMEL,//("setWinningPlayerOfMelds")
    CHBR;//("chibrer")

    // number of letters in each jassCommand :=4//
    public final static int SIZE_MESSAGE = 4;
}
