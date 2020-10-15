package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.MeldSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/**
 * adapter allowing to adapt the GUI (i.e the class GraphicalPlayer) into a
 * player
 **/
public final class GraphicalPlayerAdapter implements Player {

    private final TrickBean trickBean;
    private final ScoreBean scoreBean;
    private final HandBean handBean;

    private GraphicalPlayer graphicalPlayer;
    private Player hintPlayer;
    private final ArrayBlockingQueue<Card> cardQueue;
    private final ArrayBlockingQueue<Color> trumpQueue;
    private final ArrayBlockingQueue<MeldSet> meldSetQueue;
    /*
     * we use this queue for choose to Chibrer and for announcePossible because
     * both wait for boolean value
     * 
     */
    private final ArrayBlockingQueue<Boolean> booleanQueue;
    // selected values for the hint player
    private final static int HINT_ITERATIONS = 1_000;
    private final static int HINT_SEED = 10;

    /*
     * we define how much time each method wait before putting the default value
     */
    private final static long MAX_TIME_FOR_PLAYING_A_CARD_IN_S = 10;
    private final static long MAX_TIME_FOR_MAKING_ANNOUNCEMENT_IN_S = 10;
    private final static long MAX_TIME_FOR_CHOOSING_TO_CHIBRER_IN_S = 10;
    private final static long MAX_TIME_FOR_CHOOSING_THE_TRUMP_IN_S = 10;

    /* when we know which player won the melds we wait 5s */
    private final static int WAIT_TIME_BEST_MELD_IN_MILLIS = 5000;
    /* if the player has no possible announcements, we let him know and wait 2s */
    private final static int WAIT_TIME_WHEN_NO_ANNOUNCEMENTS = 2000;

    /**
     * only constructor of the GraphicalPlayerAdapter which initialize the
     * different beans and the communication queue
     */
    public GraphicalPlayerAdapter() {
        trickBean = new TrickBean();
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        cardQueue = new ArrayBlockingQueue<>(1);
        trumpQueue = new ArrayBlockingQueue<>(1);
        meldSetQueue = new ArrayBlockingQueue<>(1);
        booleanQueue = new ArrayBlockingQueue<>(1);
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        try {
            Card hintPlayerCard = hintPlayer.cardToPlay(state, hand);
            Platform.runLater(() -> {
                handBean.setPlayableCards(state.trick().playableCards(hand));
                handBean.setHintPlayerCard(hintPlayerCard);
            });

            /*
             * Here we decide that if the player don't select a card within the
             * given max time we select a card for him using the MctsPlayer
             */
            Card cardToPlay = cardQueue.poll(MAX_TIME_FOR_PLAYING_A_CARD_IN_S,
                    TimeUnit.SECONDS);
            if (cardToPlay == null) cardToPlay = hintPlayerCard;
            /*
             * we can click on other cards during the time separating our
             * selection to the next player's one, so we set all the playable
             * cards to null which is conceptually correct because you can't
             * play when it's not your turn
             */
            Platform.runLater(() -> {
                handBean.setPlayableCards(CardSet.EMPTY);
            });
            return cardToPlay;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chibrer()
     */
    @Override
    public boolean choseToChibrer() {
        try {
            /*
             * if a player is asked if he wants to "chibrer" or not, that means
             * he is the first player of the turn, the one that can choose the
             * trump
             */
            Platform.runLater(() -> {
                setChooserParameters(true, false);
            });
            //by default if we don't receive any response the player don't chibre
            boolean response = false;
            Boolean bool = booleanQueue.poll(MAX_TIME_FOR_CHOOSING_TO_CHIBRER_IN_S,
                    TimeUnit.SECONDS);

            if (bool != null) response = bool;

            if (response) {
                Platform.runLater(() -> {
                    /*
                     * if the player decides to let his team mate choose, he
                     * just needs to know it is not his turn to choose anymore
                     * and he is not asked to choose
                     */
                    setChooserParameters(false, false);
                });
            }
            return response;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chooseTrump(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color chooseTrump(CardSet hand) {
        try {
            /* at this point, the player is considered has if he were team mate asked
             * to choose, in either cases, has he cannot choose to "chibrer" anymore */
            Platform.runLater(() -> {
                setChooserParameters(false, true);
            });

            Color trump = trumpQueue.poll(MAX_TIME_FOR_CHOOSING_THE_TRUMP_IN_S,
                    TimeUnit.SECONDS);
            if (trump == null) trump = hintPlayer.chooseTrump(hand);

            Platform.runLater(() -> {
                /*
                 * the player has chosen, it is not his turn to choose anymore and
                 * he is not asked to choose
                 */
                setChooserParameters(false, false);
            });
            return trump;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /*
     * / (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean,
                trickBean, handBean, cardQueue, trumpQueue, booleanQueue,
                meldSetQueue);
        hintPlayer = new MctsPlayer(ownId, HINT_SEED, HINT_ITERATIONS);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
    }

    /*
     * / (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            trickBean.setTrump(trump);
        });
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            trickBean.setTrick(newTrick);
        });
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            TeamId.ALL.forEach(team -> {
                scoreBean.setTurnPoints(team, score.turnPoints(team));
                scoreBean.setGamePoints(team, score.gamePoints(team));
                scoreBean.setTotalPoints(team, score.totalPoints(team));
            });
        });
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }
    /**************************************************************************************/


    @Override
    public MeldSet selectMeldSet(CardSet hand) {
        assert hand != null;
        try {
            MeldSet meldSet = null;
            Platform.runLater(() -> {
                scoreBean.setCanMakeAnnounce(true);
            });
            Boolean announcePossible = booleanQueue.poll(
                    MAX_TIME_FOR_MAKING_ANNOUNCEMENT_IN_S, TimeUnit.SECONDS);

            if (announcePossible == null) {
                meldSet = hintPlayer.selectMeldSet(hand);
            } else {
                if (!announcePossible) {
                    Thread.sleep(WAIT_TIME_WHEN_NO_ANNOUNCEMENTS);
                }
                meldSet = meldSetQueue.take();
            }

            Platform.runLater(() -> {
                scoreBean.setCanMakeAnnounce(false);
            });
            return meldSet;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**************************************************************************************/
    @Override
    public void setWinningPlayerOfMelds(PlayerId winningPlayer,
            MeldSet meldset) {
        try {
            Platform.runLater(() -> {
                scoreBean.setCanWatchMeldResults(true);
                scoreBean.setMeldWinningPlayer(winningPlayer);
                scoreBean.setWinningMeldSet(meldset); });

            Thread.sleep(WAIT_TIME_BEST_MELD_IN_MILLIS);

            Platform.runLater(() -> {
                scoreBean.setCanWatchMeldResults(false);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /****************************************************************************************/

    private void setChooserParameters(boolean firstPlayer, boolean askedChoose) {
        trickBean.setChooser(firstPlayer);
        trickBean.setIsAskedToChoose(askedChoose);
    }
}
