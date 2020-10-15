package ch.epfl.javass.jass; 

import ch.epfl.javass.Preconditions;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class representing the score in a game **/
public final class Score {

    private Score(long packedScores) {
        this.packedScore = packedScores;
    }

    private long packedScore;

    /** the score at the beginning of a game **/
    public static final Score INITIAL = ofPacked(0);

    /**
     * returns scores whose packed is the packed version
     * 
     * @param packed
     *            packed version of the score we want
     * @throws IllegalArgumentException if packed does not represent a valid packed score
     * @return score whose packed is the packed version
     * 
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument(PackedScore.isValid(packed));

        return new Score(packed);
    }

    /**
     * return the packed version of all the scores
     * 
     * @return the packed version of all the scores
     */
    public long packed() {
        return packedScore;
    }

    /**
     * return the number of folds won by the given team in the current turn of
     * the receiver
     * 
     * @param t
     *            the team we want to check
     * @return the number of folds won by the given team in the current turn of
     *         the receiver
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packedScore, t);
    }

    /**
     * return the number of points won by the given team in the current turn of
     * the receiver
     * 
     * @param t
     *            the team we want to check
     * @return the number of points won by the given team
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packedScore, t);
    }

    /**
     * return the number of points reported by the given team in the previous
     * rounds (without including the current lap) of the receiver
     * 
     * @param t
     *            the team we want to check
     * @return the number of points reported by the given team in the previous
     *         rounds (without including the current lap) of the receiver
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packedScore, t);
    }

    /**
     * returns the total number of points won by the given team in the current
     * part of the given packed scores, i.e the sum of points earned in previous
     * rounds and those won in the current round
     * 
     * @param t
     *            : the team
     * @return the number of points reported by the given team in the previous
     *         rounds and those won in the current round
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packedScore, t);
    }

    /**
     * return the updated packaged scores to reflect the fact that the
     * winningTeam team won a trick worth considering points (if this update
     * causes the winning team to win all the folds of the round, then their
     * score has to be increased by 100 additional points since they have
     * matched)
     * 
     * @param winningTeam
     *            the team who won the fold
     * @param trickPoints
     *            the number of points earned this fold
     * @throws IllegalArgumentException if trickPoints is a negative number (hence not valid)
     * @return the updated packaged scores to reflect the fact that the
     *         winningTeam team won a trick worth considering points
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints){
        Preconditions.checkArgument(trickPoints >= 0);

        return ofPacked(PackedScore.withAdditionalTrick(packedScore,
                winningTeam, trickPoints));
    }

    /**
     * return the updated score with the meldPoints 
     * 
     * @param pkScore
     *            the current score
     * @param winningTeam
     *            the winning team
     * @param meldPoints
     *            the points to add
     * @return the updated score
     */
    public Score withMeldPoints(TeamId winningTeam, int meldPoints) {
        Preconditions.checkArgument(meldPoints >= 0);

        return ofPacked(PackedScore.withMeldPoints(packedScore,
                winningTeam, meldPoints));
    }

    /**
     * return the given packed scores updated for the next round, i.e with the
     * points obtained by each team in the current round added to their number
     * of points won during the game, and the other two components reset to 0
     * 
     * @return the given packed scores updated for the next round, i.e with the
     *         points obtained by each team in the current round added to their
     *         number of points won during the game, and the other two
     *         components reset to 0
     */
    public Score nextTurn() {
        return ofPacked(PackedScore.nextTurn(packedScore));
    }
    

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedScore);
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Score)
            return packedScore == ((Score) obj).packed();
        else
            return false;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedScore.toString(packedScore);
    }

}
