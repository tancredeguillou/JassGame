package ch.epfl.javass.jass; 

/**
 *@author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */


/** interface containing various useful constants for the Jass game **/
public interface Jass {
    
    /** number of cards in a trick (4)**/
    public final static int NB_CARDS_PER_TRICK = 4;
    /** number of cards in a hand, at the beginning of a turn (9) **/
    public final static int HAND_SIZE = 9;
    /** number of tricks in a turn (9) **/
    public final static int TRICKS_PER_TURN = 9;
    /** the index of the last trick of a turn (8) **/
    public final static int INDEX_LAST_TRICK = 8;
    /** number of necessary points in order to win (1000) **/
    public final static int WINNING_POINTS = 1000;
    /** number of additional points, if a team wins all tricks in one turn (100) **/
    public final static int MATCH_ADDITIONAL_POINTS = 100;
    /** number of additional points, if a team wins the last trick (5) **/
    public final static int LAST_TRICK_ADDITIONAL_POINTS = 5;
    /** maximum number of points per team and per turn (257) **/
    public final static int MAX_POINTS_PER_TURN = 257;
    /** maximum number of points per team and per turn, if no MATCH has been made (157) **/
    public final static int MAX_POINTS_PER_TURN_WITHOUT_A_MATCH = 157;
    /** maximum number of points per team and per game (2000) **/
    public final static int MAX_POINTS_PER_GAME = 2000;
    /** total number of cards in a game (36) **/
    public final static int TOTAL_CARDS = 36;

}
