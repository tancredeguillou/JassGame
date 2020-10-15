package ch.epfl.javass.jass; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** class representing a JASS game **/
public final class JassGame implements Jass {

    private final Random shuffleRng;
    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private TurnState turnState;
    private Map<PlayerId, CardSet> playerHands;
    private PlayerId currentFirstPlayerOfTheTurn;
    private Color currentTrump;
    private TeamId winningTeam;

    private Map<PlayerId, MeldSet> playerMeldSets;

    /** public constructor **/
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));
        this.playerMeldSets = new EnumMap<>(PlayerId.class);
    }

    /**
     * returns true if the game is over
     * 
     * @return true if the game is over
     */
    public boolean isGameOver() {
        /* we need to check that if we call this method before advanceToEndOfNextTrick */
        if (turnState == null) {
            return false;
        }
        /* one of the teams exceeded 1000 points */
        for (TeamId team : TeamId.ALL) {
            if (PackedScore.totalPoints(turnState.packedScore(),
                    team) >= WINNING_POINTS) {
                winningTeam = team;
                return true;
            }
        }
        return false;
    }

    /**
     * advance the state of the game until the end of the next trick, or do
     * nothing if the game is over.
     */
    public void advanceToEndOfNextTrick() {

        /* If the game is over, stop ... */
        if (!isGameOver()) {

            /* If start of game, begin a new game */
            if (turnState == null) {
                beginANewGame();
            } else {
                // select the best MeldSet !
                if (turnState.trick().index() == 0) {
                    selectBestMeldSetAndGivePoints();
                }
                /*
                 * If it is not the beginning of the game the trick must be
                 * assumed full and must be picked up
                 */
                assert PackedTrick.isFull(turnState.packedTrick());
                turnState = turnState.withTrickCollected();
            }

            if (isGameOver()) {
                stopTheGame();
            }
            /* If the current turn is over, start a new turn and find the
                     first player (whoever succeeds the first previous player) */
            if (turnState.isTerminal()) {
                beginANewTurn();
            }

            if (!isGameOver()) {
                /* If the game is not over : */

                /* Announce to the player the current scores and the current turn */
                for (PlayerId p : PlayerId.ALL) {
                    players.get(p).updateScore(turnState.score());
                    players.get(p).updateTrick(turnState.trick());
                }

                /*
                 * make the players play / update scores until the current trick
                 * is over
                 */
                for (int i = 0; i < PlayerId.COUNT; ++i) {
                    //collect the meldSets
                    if (turnState.trick().index() == 0) {
                        askMeld(turnState.nextPlayer());
                    } 

                    playACard(turnState.nextPlayer());
                    for (PlayerId p : PlayerId.ALL) {
                        players.get(p).updateTrick(turnState.trick());

                    }
                }
            }
        }
    }

    /***************************** useful methods to clarify the code *************************************/

    private void shuffleAndDistributeCards() {
        /* initializing the playerHands */
        if (playerHands == null) {
            playerHands = new HashMap<>();
        } else {
            playerHands.clear();
        }

        /* shuffle the card */
        List<Card> deck = new ArrayList<Card>(36);
        for (int i = 0; i < TOTAL_CARDS; ++i) {
            deck.add(CardSet.ALL_CARDS.get(i));
        }
        Collections.shuffle(deck, shuffleRng);

        /* distribution of 9 cards for 4 players */
        for (PlayerId p : PlayerId.ALL) {
            playerHands.put(p,
                    CardSet.of(deck.subList(HAND_SIZE * p.ordinal(),
                            HAND_SIZE * (p.ordinal() + 1))));
            players.get(p).updateHand(playerHands.get(p));
        }
    }

    private void setCurrentFirstPlayerOfTheGame() {
        for (PlayerId p : playerHands.keySet()) {
            /* seven of diamond */
            if (playerHands.get(p)
                    .contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                currentFirstPlayerOfTheTurn = p;
            }
        }
    }

    private void setCurrentFirstPlayerOfTheTurn() {
        /* next player after the precedent firstPlayer ( PLAYER_2 before , now PLAYER_3...) */
        currentFirstPlayerOfTheTurn = PlayerId.ALL.get(
                (currentFirstPlayerOfTheTurn.ordinal() + 1) % PlayerId.COUNT);
    }

    private void setCurrentTrump() {
        /*
         * for the human players, we need to check if they chose to let their
         * team mate choose! if so, we call the method chooseTrump for their
         * team mate.
         */
        if (players.get(currentFirstPlayerOfTheTurn).choseToChibrer()) {
            PlayerId teamMate = PlayerId.ALL
                    .get((currentFirstPlayerOfTheTurn.ordinal() + 2)
                            % PlayerId.COUNT);
            currentTrump = players.get(teamMate)
                    .chooseTrump(playerHands.get(teamMate));
        } else {
            currentTrump = players.get(currentFirstPlayerOfTheTurn)
                    .chooseTrump(playerHands.get(currentFirstPlayerOfTheTurn));
        }

        for (PlayerId pId : PlayerId.ALL) {
            players.get(pId).setTrump(currentTrump);
        }
    }

    private void beginANewGame() {
        for (PlayerId pId : PlayerId.ALL) {
            players.get(pId).setPlayers(pId, playerNames);
        }
        shuffleAndDistributeCards();
        setCurrentFirstPlayerOfTheGame();
        setCurrentTrump();
        /* it's the first round, so the score will be INITIAL */
        turnState = TurnState.initial(currentTrump, Score.INITIAL,
                currentFirstPlayerOfTheTurn);
    }

    private void beginANewTurn() {
        shuffleAndDistributeCards();
        setCurrentFirstPlayerOfTheTurn();
        setCurrentTrump();
        turnState = TurnState.initial(currentTrump,
                /* we need to update the score at the end of each turn ! */
                turnState.score().nextTurn(), currentFirstPlayerOfTheTurn);
    }

    private void playACard(PlayerId p) {
        Card card = players.get(p).cardToPlay(turnState, playerHands.get(p));
        turnState = turnState.withNewCardPlayed(card);
        /* we remove the selected card from the hand of the player */
        playerHands.replace(p, playerHands.get(p).remove(card));
        players.get(p).updateHand(playerHands.get(p));
    }

    private void stopTheGame() {
        for (PlayerId p : PlayerId.ALL) {
            turnState = TurnState.initial(currentTrump,
                    turnState.score().nextTurn(), currentFirstPlayerOfTheTurn);
            players.get(p).updateScore(turnState.score());
            players.get(p).setWinningTeam(winningTeam);
        }
    }

    private void askMeld(PlayerId player) {
        // the map is actualized when we collect the points
        playerMeldSets.put(player, players.get(player)
                .selectMeldSet(playerHands.get(player)));
    }

    private void selectBestMeldSetAndGivePoints() {
        int winnerIndex = 0;
        for (PlayerId p : PlayerId.ALL) {
            if (playerMeldSets.get(PlayerId.ALL.get(winnerIndex))
                    .compareTo(playerMeldSets.get(p)) < 0) {
                winnerIndex = p.ordinal();
            }
        }
        PlayerId winner = PlayerId.ALL.get(winnerIndex);
        //we say which player won to all the players
        PlayerId.ALL.forEach(player -> players.get(player)
                .setWinningPlayerOfMelds(winner, playerMeldSets.get(winner)));

        // we directly add the points to the team which has the best MeldSet
        turnState = TurnState.ofPackedComponents(
                turnState.score()
                .withMeldPoints(winner.team(),
                        playerMeldSets.get(winner).points())
                .packed(),
                turnState.packedUnplayedCards(), turnState.packedTrick());
        playerMeldSets.clear();
    }
}