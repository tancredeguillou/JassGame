package ch.epfl.javass.jass; 

import java.util.Arrays;

import java.util.Collections;
import java.util.List;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */


/** Enumeration representing the different teams **/
public enum TeamId {
    TEAM_1,
    TEAM_2;

    /** list containing all the values in TeamId, in their declaration order **/
    public final static List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    /** number of possible values in TeamId **/
    public final static int COUNT = ALL.size();

    /**
     * @return the other team as the one calling the method
     */
    public TeamId other() {
        return (this == TEAM_1) ? TEAM_2 : TEAM_1;
    }

}
