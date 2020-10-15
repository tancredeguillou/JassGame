package ch.epfl.javass.jass; 

import java.util.ArrayList;
import java.util.SplittableRandom;
import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** class representing a simulated player **/
public final class MctsPlayer implements Player{

    private final PlayerId ownId;
    private final SplittableRandom rng;
    private final int iterations;

    /**
     * public constructor of the class
     * @throws IllegalArgumentException if the number of iterations is inferior
     *          to the size of a hand (i.e 9 cards)
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations){
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        /* just for informational purpose to avoid overflow (Integer) */
        assert (iterations <= 8355967);
        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        /* if the player can only play one card, no need to enter the for loop */
        if (state.trick().playableCards(hand).size() == 1)
            return state.trick().playableCards(hand).get(0);

        long packedHand = hand.packed();
        Node root = new Node(state, packedHand, ownId);
        for (int i = 0; i < iterations; ++i) {
            ArrayList<Node> path = root.addChildrenToPath(new ArrayList<>(), packedHand, ownId);
            long newScore = randomTurnScore(path.get(path.size() - 1).nodeTurnState, packedHand);
            updateScores(path, newScore);
        }
        return state.trick().playableCards(hand).get(root.indexBestChild(0));
    }

    /************************** private methods useful to clarify the code *********************************/ 

    /*
     * return the set of playable cards for the given turnState, taking into
     * account the cards that are in the player's hand
     * 
     * @param state
     *          the given state of the turn
     * @param handOfSimulatedPlayer
     *          the hand of the simulated player
     * @param simulatedPlayer
     *          the I.D of the simulated player
     * 
     * @return the set of playable cards for the given turnState
     */
    private static long playableCards(TurnState state,
            long handOfSimulatedPlayer, PlayerId simulatedPlayerId) {

        return (state.nextPlayer().equals(simulatedPlayerId)
                ? PackedTrick.playableCards(state.packedTrick(), 
                        PackedCardSet.intersection(handOfSimulatedPlayer, state.packedUnplayedCards()))
                        /* we have to select the playable cards of the unplayedCards */
                        : PackedTrick.playableCards(state.packedTrick(), 
                                PackedCardSet.difference(state.packedUnplayedCards(), handOfSimulatedPlayer)));
    }


    /*
     * returns the final score of a turn completed randomly, starting from a
     * given state, and knowing the hand of the simulated player
     * 
     * @param state
     *            the given turn state
     * @param hand
     *            the hand of the simulated player
     * @return the final score of a randomly completed turn
     */
    private long randomTurnScore(TurnState state, long hand) {
        while (!state.isTerminal()) {
            state = state.withNewCardPlayedAndTrickCollected(Card.ofPacked(
                    PackedCardSet.get(playableCards(state, hand, ownId), 
                            rng.nextInt(PackedCardSet.size(playableCards(state, hand, ownId))))));
        }
        return state.packedScore();
    }

    /*
     * updates the score of each node given in a list, by adding the given score
     * to the total points of each node
     * 
     * @param nodes
     *            the list of nodes to be updated
     * @param score
     *            the score of the random turn
     */
    private void updateScores(ArrayList<Node> nodes, long score) {
        assert !nodes.isEmpty() && PackedScore.isValid(score);

        int ownIdTeamScore = PackedScore.turnPoints(score, ownId.team());
        int otherTeamScore = Jass.MAX_POINTS_PER_TURN_WITHOUT_A_MATCH - ownIdTeamScore;

        if (ownIdTeamScore == 0 || ownIdTeamScore == Jass.MAX_POINTS_PER_TURN) {
            otherTeamScore = Jass.MAX_POINTS_PER_TURN - ownIdTeamScore;
        }

        nodes.get(0).totalNodePoints += otherTeamScore;
        ++nodes.get(0).numberOfNodeTurns;

        for (int i = 0; i < nodes.size()-1; ++i) {
            if (nodes.get(i).nodeTurnState.nextPlayer().team()
                    .equals(ownId.team())) {
                nodes.get(i+1).totalNodePoints += ownIdTeamScore;
            } else {
                nodes.get(i+1).totalNodePoints += otherTeamScore;
            }
            ++nodes.get(i+1).numberOfNodeTurns;
        }
    }

    /************************************************************************************/

    private static final class Node {

        private final TurnState nodeTurnState;
        private final Node[] nodeChildren;
        private long nodeInexistingChildren;
        private int totalNodePoints = 0;
        private int numberOfNodeTurns = 0;

        private final static int CONSTANT_FOR_V = 40;

        private Node(TurnState nodeTurnState, long handOfSimulatedPlayer,
                PlayerId simulatedPlayerId) {

            this.nodeTurnState = nodeTurnState;
            this.nodeChildren = new Node[PackedCardSet.size(playableCards(nodeTurnState,
                    handOfSimulatedPlayer, simulatedPlayerId))];
            this.nodeInexistingChildren = playableCards(nodeTurnState,
                    handOfSimulatedPlayer, simulatedPlayerId);
        }

        /*
         * returns the same list as the one given, having added
         * the best children of the given node, given the hand
         * of the simulated player
         * 
         * @param node
         *          the father of the nodes we will assess
         * @param path
         *          the path that has lead from the root to the node
         * @param hand
         *          the hand of the simulated player
         * @return
         */
        private ArrayList<Node> addChildrenToPath(ArrayList<Node> path,
                long hand, PlayerId simulatedPlayerId) {
            path.add(this);

            for (int i = 0; i < nodeChildren.length; ++i) {
                if (nodeChildren[i] == null) {
                    int newCard = PackedCardSet.get(nodeInexistingChildren, 0);
                    TurnState newState = nodeTurnState.withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(newCard));
                    if (newState.isTerminal())
                        return path;
                    nodeChildren[i] = new Node(newState,
                            hand, simulatedPlayerId);
                    nodeInexistingChildren = PackedCardSet.remove(nodeInexistingChildren, newCard);
                    path.add(nodeChildren[i]);
                    return path;
                }
            }
            int index = this.indexBestChild(CONSTANT_FOR_V);

            return (nodeChildren.length == 0 ? path
                    : nodeChildren[index].addChildrenToPath(path, hand,simulatedPlayerId));
        }

        /*
         * returns the index of the "best" child of a node, being the one for
         * which the value of V is the highest
         * 
         * @param c
         *            constant needed to calculate V
         * @return the index of the "best" child of the node
         */
        private int indexBestChild(int c) {
            double Vmax = 0;
            int index = 0;
            for (int i = 0; i < nodeChildren.length; ++i) {
                if (this.V(nodeChildren[i], c) > Vmax) {
                    Vmax = V(nodeChildren[i], c);
                    index = i;
                }
            }
            return index;
        }

        private double V(Node node, int c) {
            return ((double) node.totalNodePoints / (double) node.numberOfNodeTurns)
                    + c * Math.sqrt((2 * Math.log(numberOfNodeTurns)
                            / node.numberOfNodeTurns));
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chooseTrump(ch.epfl.javass.jass.PlayerId)
     */
    @Override
    public Color chooseTrump(CardSet hand) {
        /* As a start, we say that the player chooses the trump
         * considering the "value" of his cards */
        int biggestValue = 0;
        Color trump = Color.SPADE;

        for (Color c : Color.ALL) {

            CardSet sub = hand.subsetOfColor(c);
            int colorSize = sub.size();
            int value = 0;

            for (int i = 0; i < colorSize; ++i) {
                value += sub.get(i).rank().trumpOrdinal();
            }
            if (value > biggestValue) {
                biggestValue = value;
                trump = c;
            }
        }
        return trump;
    }
}
