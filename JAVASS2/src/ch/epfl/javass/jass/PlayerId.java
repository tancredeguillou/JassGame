package ch.epfl.javass.jass; 

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Enumeration representing the different players **/
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    /** list containing all the values in PlayerId, in their declaration order **/
    public final static List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    /** number of possible values in PlayerId **/
    public final static int COUNT = ALL.size();

    /**
     * @return the team in which the receptor (a player) belongs
     */
    public TeamId team() {
        return (this == PLAYER_1 || this == PLAYER_3) ? TeamId.TEAM_1 : TeamId.TEAM_2;
    }

    /**
     * return the teammate of the given player
     * 
     * @return the teammate of the given player
     */
    public PlayerId teamMate() {
        return ALL.get((this.ordinal() + 2) % COUNT);
    }

}
