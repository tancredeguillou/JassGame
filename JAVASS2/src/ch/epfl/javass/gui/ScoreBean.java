/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.gui; 

import ch.epfl.javass.jass.MeldSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/** bean JavaFX class containing the scores **/
public final class ScoreBean {

    private final SimpleIntegerProperty turnPointsTeam1;
    private final SimpleIntegerProperty turnPointsTeam2;

    private final SimpleIntegerProperty gamePointsTeam1;
    private final SimpleIntegerProperty gamePointsTeam2;

    private final SimpleIntegerProperty totalPointsTeam1;
    private final SimpleIntegerProperty totalPointsTeam2;

    private final SimpleObjectProperty<TeamId> winningTeam;

    private final SimpleObjectProperty<Boolean> canWatchMeldResults;
    private final SimpleObjectProperty<PlayerId> meldWinningPlayer;
    private final SimpleObjectProperty<Boolean> canMakeAnnounce;
    private final SimpleObjectProperty<MeldSet> winningMeldSet;
    /**
     * single public constructor initializing the different properties
     */
    public ScoreBean() {
        this.turnPointsTeam1 = new SimpleIntegerProperty();
        this.turnPointsTeam2 = new SimpleIntegerProperty();
        this.gamePointsTeam1 = new SimpleIntegerProperty();
        this.gamePointsTeam2 = new SimpleIntegerProperty();
        this.totalPointsTeam1 = new SimpleIntegerProperty();
        this.totalPointsTeam2 = new SimpleIntegerProperty();
        this.winningTeam = new SimpleObjectProperty<>();
        this.canWatchMeldResults = new SimpleObjectProperty<>(false);
        this.meldWinningPlayer = new SimpleObjectProperty<>();
        this.canMakeAnnounce = new SimpleObjectProperty<>(false);
        this.winningMeldSet = new SimpleObjectProperty<>();
    }

    /**
     * return the read-only property of the given team
     * 
     * @param team
     *            the team concerned
     * @return the property of the given team
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return readOnlyPropOfTheTeam(team, turnPointsTeam1, turnPointsTeam2);
    }

    /**
     * modify the turnPoints of the given team
     * 
     * @param team
     *            the team concerned
     * @param newTurnPoints
     *            the new turnPoints
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        setSomethingToTheGivenTeam(team, turnPointsTeam1, turnPointsTeam2, newTurnPoints);
    }

    /*********************************************************************************/
    /**
     * return the read-only property of the given team
     * 
     * @param team
     *            the team concerned
     * @return the property of the given team
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return readOnlyPropOfTheTeam(team, gamePointsTeam1, gamePointsTeam2);
    }

    /**
     * modify the turnPoints of the given team
     * 
     * @param team
     *            the team concerned
     * @param newGamePoints
     *            the new turnPoints
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        setSomethingToTheGivenTeam(team, gamePointsTeam1, gamePointsTeam2, newGamePoints);
    }

    /*********************************************************************************/
    /**
     * return the read-only property of the given team
     * 
     * @param team
     *            the team concerned
     * @return the property of the given team
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return readOnlyPropOfTheTeam(team, totalPointsTeam1, totalPointsTeam2);
    }

    /**
     * modify the turnPoints of the given team
     * 
     * @param team
     *            the team concerned
     * @param newTotalPoints
     *            the new turnPoints
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        setSomethingToTheGivenTeam(team, totalPointsTeam1, totalPointsTeam2, newTotalPoints);
    }

    /**
     * return the read-only property of the winning-Team
     * 
     * @return the read-only property of the winning-Team
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        //it will automatically be wrapped up
        return winningTeam;
    }

    /**
     * modify the winning Team (in theory, only once in a game)
     * 
     * @param winningTeam
     *            the team to set as winner
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }

    /**
     * return the read-only property of the winning-player of the meld
     * 
     * @return the read-only property of the winning-player of the
     */
    public ReadOnlyObjectProperty<PlayerId> meldWinningPlayerProperty() {
        // it will automatically be wrapped up
        return meldWinningPlayer;
    }

    /**
     * modify the winning player of the melds (in theory, only once in a turn)
     * 
     * @param winner
     *            the player to set as winner
     */
    public void setMeldWinningPlayer(PlayerId winner) {
        this.meldWinningPlayer.set(winner);
    }


    /**
     * return the read-only property of the boolean canWatchMeldResults
     * 
     * @return the read-only property of the boolean canWatchMeldResults
     */
    public ReadOnlyObjectProperty<Boolean> canWatchMeldResultsProperty() {
        // it will automatically be wrapped up
        return canWatchMeldResults;
    }

    /**
     * modify the boolean which indicate if we can watch the meld results
     * 
     * @param can
     *            the boolean to set
     */
    public void setCanWatchMeldResults(boolean can) {
        this.canWatchMeldResults.set(can);
    }

    /**
     * return the read-only property of the boolean canMakeAnnounce
     * 
     * @return the read-only property of the boolean canMakeAnnounce
     */
    public ReadOnlyObjectProperty<Boolean> canMakeAnnounceProperty() {
        // it will automatically be wrapped up
        return canMakeAnnounce;
    }

    /**
     * modify the boolean which indicate if we can make an announcement
     * 
     * @param can
     *            the boolean to set
     */
    public void setCanMakeAnnounce(boolean can) {
        this.canMakeAnnounce.set(can);
    }

    /**
     * return the read-only property of the winningMeldSet of the Announces
     * 
     * @return the read-only property of the winningMeldSet of the Announces
     */
    public ReadOnlyObjectProperty<MeldSet> winningMeldSetProperty() {
        // it will automatically be wrapped up
        return winningMeldSet;
    }

    /**
     * set WinningMeldSet of the Announces
     * 
     * @param newMeld
     *            the MeldSet to set
     */
    public void setWinningMeldSet(MeldSet newMeld) {
        this.winningMeldSet.set(newMeld);
    }

    /*********************************************************************************/

    //avoid duplicate code
    private void setSomethingToTheGivenTeam(TeamId team,
            SimpleIntegerProperty propTeam1, SimpleIntegerProperty propTeam2,
            int thingToSet) {
        if (team.equals(TeamId.TEAM_1))
            propTeam1.set(thingToSet);
        else
            propTeam2.set(thingToSet);
    }

    private ReadOnlyIntegerProperty readOnlyPropOfTheTeam(TeamId team,
            SimpleIntegerProperty propTeam1, SimpleIntegerProperty propTeam2) {
        // it will automatically be wrapped up
        return (team.equals(TeamId.TEAM_1) ? propTeam1 : propTeam2);
    }

}
