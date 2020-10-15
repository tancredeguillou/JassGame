package ch.epfl.javass.jass; 

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * @author tancrede guillou (287334)
 * @author Ouriel Sebbagh (287796)
 */

/** Class containing methods allowing to handle a game's scores **/
public final class PackedScore implements Jass {
    private PackedScore() {
    }

    /** initial score at the beginning of a game (0) **/
    public final static long INITIAL = 0L;
    /* the start index of bits representing the number of tricks in the packed representation of the score */
    private final static int START_TRICK = 0;
    /* the range of bits representing the number of tricks in the packed representation of the score */
    private final static int RANGE_TRICK = 4;
    /* the start index of bits representing the number of turn points in the packed representation of the score */
    private final static int START_POINTS_TURN = 4;
    /* the range of bits representing the number of turn points in the packed representation of the score */
    private final static int RANGE_POINTS_TURN = 9;
    /* the start index of bits representing the number of game points in the packed representation of the score */
    private final static int START_POINTS_GAME = 13;
    /* the range of bits representing the number of game points in the packed representation of the score */
    private final static int RANGE_POINTS_GAME = 11;
    /* the range of the score of one team (32) */
    private final static int RANGE_TEAM_SCORE = 32;

    /**
     * returns true if the value given is a valid packed score, i.e if the 6
     * components are values included in their respective bound, and all unused
     * bits are 0
     * 
     * @param pkScore
     *            : score to check
     * @return true if the score is a valid packed score
     */
    public static boolean isValid(long pkScore) {

        long CardWithMask = (Bits64.mask(24, 8) | Bits64.mask(56, 8)) & pkScore;
        int[] scores = {(int) Bits64.extract(pkScore, 0, RANGE_TEAM_SCORE),
                (int) Bits64.extract(pkScore, 32, RANGE_TEAM_SCORE)};
        for (int i = 0; i < TeamId.COUNT; ++i) {
            if (nbTricks(scores[i]) > TRICKS_PER_TURN || ptsTurn(scores[i]) > MAX_POINTS_PER_TURN
                    || ptsGame(scores[i]) > MAX_POINTS_PER_GAME)
                return false;
        }

        return CardWithMask == 0;
    }

    /**
     * returns a packed representation of the score, adding all the components
     * together
     * 
     * @param turnTricks1
     *            : number of tricks won by team 1 during the current turn
     * @param turnPoints1
     *            : number of points won by team 1 during the current turn
     * @param gamePoints1
     *            : total number of points of team 1
     * @param turnTricks2
     *            : number of tricks won by team 2 during the current turn
     * @param turnPoints2
     *            : number of points won by team 2 during the current turn
     * @param gamePoints2
     *            : total number of points of team 2
     * @return a packed representation of the score
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {

        long score1 = (long) Bits32.pack(turnTricks1, RANGE_TRICK, turnPoints1, RANGE_POINTS_TURN,
                gamePoints1, RANGE_POINTS_GAME);
        long score2 = (long) Bits32.pack(turnTricks2, RANGE_TRICK, turnPoints2, RANGE_POINTS_TURN,
                gamePoints2, RANGE_POINTS_GAME);

        long packed = Bits64.pack(score1, RANGE_TEAM_SCORE, score2, RANGE_TEAM_SCORE);
        assert isValid(packed);
        return packed;
    }

    /**
     * returns the number of tricks won by the given team in the current turn
     * and scores
     * 
     * @param pkScore
     *            : current scores in packed representation
     * @param t
     *            : the team
     * @return the number of tricks won by the given team in the current turn
     *         and scores
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore) && t != null;
        return (int) Bits64.extract(pkScore, t.ordinal()*RANGE_TEAM_SCORE + START_TRICK, RANGE_TRICK);
    }

    /**
     * returns the number of points won by the given team in the current turn
     * and scores
     * 
     * @param pkScore
     *            : current scores in packed representation
     * @param t
     *            : the team
     * @return the number of points won by the given team in the current turn
     *         and scores
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore) && t != null;
        return (int) Bits64.extract(pkScore, t.ordinal()*RANGE_TEAM_SCORE + START_POINTS_TURN, RANGE_POINTS_TURN);
    }

    /**
     * return the number of points reported by the given team in the previous
     * rounds (without including the current lap) of the given packed scores,
     * 
     * @param pkScore
     *            : current scores in packed representation
     * @param t
     *            : the team
     * @return the number of points reported by the given team in the previous
     *         rounds
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore) && t != null;
        return (int) Bits64.extract(pkScore,
                t.ordinal() * RANGE_TEAM_SCORE + START_POINTS_GAME,
                RANGE_POINTS_GAME);
    }

    /**
     * returns the total number of points won by the given team in the current
     * part of the given packed scores, i.e the sum of points earned in previous
     * rounds and those won in the current round
     * 
     * @param pkScore
     *            : current scores in packed representation
     * @param t
     *            : the team
     * @return the number of points reported by the given team in the previous
     *         rounds and those won in the current round
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore) && t != null;
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * returns the updated packed scores, given that the winning team won the trick worth 
     * trickPoints, and taking into account if winning team has won all tricks, in which
     * case 100 more points are added to the score
     * 
     * @param pkScore : the actual score of the game
     * @param winningTeam : the team that has won the trick
     * @param trickPoints : the total value of the trick
     * @return the updated packed scores
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert isValid(pkScore) && winningTeam != null 
                && trickPoints <= MAX_POINTS_PER_TURN_WITHOUT_A_MATCH && trickPoints >= 0; 

                if (turnTricks(pkScore, winningTeam) == INDEX_LAST_TRICK) {
                    trickPoints += MATCH_ADDITIONAL_POINTS;
                }
                return pack(
                        turnTricks(pkScore, TeamId.TEAM_1)+(1-winningTeam.ordinal())*1, 
                        turnPoints(pkScore, TeamId.TEAM_1)+(1-winningTeam.ordinal())*trickPoints, 
                        gamePoints(pkScore, TeamId.TEAM_1), 
                        turnTricks(pkScore, TeamId.TEAM_2)+ winningTeam.ordinal()*1, 
                        turnPoints(pkScore, TeamId.TEAM_2)+ winningTeam.ordinal()*trickPoints, 
                        gamePoints(pkScore, TeamId.TEAM_2));

    }

    /**
     * returns updated packed scores for next turn, which means adding the points obtained
     * in the previous turn by each team to their total game points, and the other two 
     * components back to zero
     * 
     * @param pkScore : current packed score
     * @return updated packed scores
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);
        return pack(0, 0, totalPoints(pkScore, TeamId.TEAM_1),
                0, 0, totalPoints(pkScore, TeamId.TEAM_2));
    }

    /**
     * returns a textual representation of the scores
     * @param pkScore : current packed scores
     * @return a textual representation of the scores
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);
        return ("(" +   turnTricks(pkScore, TeamId.TEAM_1) + "," +
                turnPoints(pkScore, TeamId.TEAM_1) + "," +
                gamePoints(pkScore, TeamId.TEAM_1) + ")  /  (" +
                turnTricks(pkScore, TeamId.TEAM_2) + "," +
                turnPoints(pkScore, TeamId.TEAM_2) + "," +
                gamePoints(pkScore, TeamId.TEAM_2) + ")" );
    }

    /**
     * return the updated score
     * 
     * @param pkScore
     *            the current score
     * @param winningTeam
     *            the winning team
     * @param meldPoints
     *            the points to add
     * @return the updated score
     */
    public static long withMeldPoints(long pkScore, TeamId winningTeam,
            int meldPoints) {
        assert isValid(pkScore) && winningTeam != null && meldPoints >= 0;

                return pack(
                        turnTricks(pkScore, TeamId.TEAM_1), 
                        turnPoints(pkScore, TeamId.TEAM_1), 
                        gamePoints(pkScore, TeamId.TEAM_1)+(1-winningTeam.ordinal())*meldPoints, 
                        turnTricks(pkScore, TeamId.TEAM_2), 
                        turnPoints(pkScore, TeamId.TEAM_2), 
                        gamePoints(pkScore, TeamId.TEAM_2)+ winningTeam.ordinal()*meldPoints);

    }
    /***************************** private methods useful to clarify the code *********************************/


    private static int nbTricks(int pkScore) {
        return Bits32.extract(pkScore, START_TRICK, RANGE_TRICK);
    }


    private static int ptsTurn(int pkScore) {
        return Bits32.extract(pkScore, START_POINTS_TURN, RANGE_POINTS_TURN);
    }

    private static int ptsGame(int pkScore) {
        return Bits32.extract(pkScore, START_POINTS_GAME, RANGE_POINTS_GAME);
    }
}
