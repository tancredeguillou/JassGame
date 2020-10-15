/**
 *	@author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Meld;
import ch.epfl.javass.jass.MeldSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * represents the server of a player, who is waiting for a connection on port
 * 5108 and drives a local player based on the received messages
 */
public final class RemotePlayerServer {

    public static final int PORT = 5108;
    private final Player player;
    private boolean gameContinue;

    /* number of arguments in the messages CARD and PLRS */
    private final static int NB_OF_ARGS_FOR_CARD_WMEL_AND_PLRS = 3;
    /* number of arguments in the messages TRMP,HAND,TRCK,SCOR, and WINR */
    private final static int NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP = 2;
    /* index of the only one argument in some messages*/
    private final static int INDEX_OF_ONLY_ARG= 1;

    private final static int INDEX_JASS_COMMAND = 0;

    //concerning the command CARD
    private final static int INDEX_TURNSTATE = 1;
    private final static int NB_OF_ATTRIBUTES_IN_TURNSTATE = 3;
    private final static int INDEX_TURNSTATE_PK_SCORE = 0;
    private final static int INDEX_TURNSTATE_PK_UNPLAYED_CARDS= 1;   
    private final static int INDEX_TURNSTATE_PK_TRICK= 2;

    private final static int INDEX_HAND = 2;

    //concerning the command PLRS
    private final static int INDEX_OWN_ID_ORDINAL = 1;
    private final static int INDEX_NAMES_AND_MELDS = 2;

    //concerning WMEL
    private final int INDEX_POINTS = 0;
    private final int INDEX_CARDSET= 1;

    /**
     * single public constructor to which we pass the local player of type
     * Player
     * 
     * @param player
     *            the player concerned
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
        this.gameContinue = true;
    }

    /**
     * method which wait for messages from the client and respond in consequence
     */
    public final void run() {
        try(ServerSocket initS0 = new ServerSocket(PORT);
                Socket s = initS0.accept();
                //init of the reader
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(s.getInputStream(),
                                        US_ASCII));
                //init of the writer
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream(),
                                US_ASCII))) {

            while (gameContinue) {
                String nextLine = reader.readLine();

                /* we received something !*/
                if (nextLine != null) {
                    /*
                     * we split what we received in a tab and remember 
                     * that its first element is the name of the command
                     */
                    String[] args = StringSerializer.splitStrings(" ",
                            nextLine);
                    assert args[INDEX_JASS_COMMAND]
                            .length() == JassCommand.SIZE_MESSAGE;
                    String message = args[INDEX_JASS_COMMAND];

                    switch (JassCommand.valueOf(message)) {
                    case PLRS:
                        responseForPLRS(args);
                        break;

                    case TRMP:
                        responseForTRMP(args);
                        break;

                    case HAND:
                        responseForHAND(args);
                        break;

                    case TRCK:
                        responseForTRCK(args);
                        break;

                    case CARD:
                        // we respond to the client
                        writer.write(StringSerializer
                                .serializeInt(selectedCard(args)));
                        writer.write('\n');
                        // we guarantee that it has been sent
                        writer.flush();
                        break;

                    case SCOR:
                        responseForSCOR(args);
                        break;

                    case WINR:
                        responseForWINR(args);
                        break;

                    case CHTP:
                        writer.write(StringSerializer
                                .serializeInt(selectedTrump(args)));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case MELD:
                        writer.write(StringSerializer
                                .serializeInt(selectedMeldIndex(args)));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case WMEL:
                        responseForWMEL(args);
                        break;

                    case CHBR:
                        writer.write(StringSerializer.serializeInt(responseForCHBR()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*************Here we define the different responses from the server*************/

    private final int responseForCHBR() {
        if (player.choseToChibrer()) return 1;
        return 0;
    }

    private final int selectedTrump(String[] args) {
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        CardSet hand = CardSet.ofPacked(
                StringSerializer.deserializeLong(args[INDEX_OF_ONLY_ARG]));
        return player.chooseTrump(hand).ordinal();
    }

    private final int selectedMeldIndex(String[] args) {
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        /*we remove 1 for the name of the jassCommand*/
        CardSet hand = CardSet.ofPacked(
                StringSerializer.deserializeLong(args[INDEX_OF_ONLY_ARG]));
        return MeldSet.allIn(hand).indexOf(player.selectMeldSet(hand));
    }

    private final void responseForWMEL(String[] args) {
        assert args.length <= NB_OF_ARGS_FOR_CARD_WMEL_AND_PLRS;
        PlayerId winnerOfMeld = PlayerId.ALL.get(
                StringSerializer.deserializeInt(args[INDEX_OWN_ID_ORDINAL]));

        Set<Meld> melds = new TreeSet<>();

        if (args.length == NB_OF_ARGS_FOR_CARD_WMEL_AND_PLRS) {
            String[] meldsPointsAndCards = StringSerializer.splitStrings(",",
                    args[INDEX_NAMES_AND_MELDS]);

            for (String s : meldsPointsAndCards) {
                String[] pts_cards = StringSerializer.splitStrings(":", s);
                assert pts_cards.length == 2;
                int points = StringSerializer
                        .deserializeInt(pts_cards[INDEX_POINTS]);
                CardSet cards = CardSet.ofPacked(StringSerializer
                        .deserializeLong(pts_cards[INDEX_CARDSET]));
                melds.add(Meld.of(cards, points));
            }
        }
        player.setWinningPlayerOfMelds(winnerOfMeld, MeldSet.of(melds));
    }

    private final void responseForPLRS(String[] args){
        assert args.length == NB_OF_ARGS_FOR_CARD_WMEL_AND_PLRS;
        /*
         * we don't really need to deserialize as the integer value is 0,1,2 or
         * 3 here but we stay as generic as possible
         */
        PlayerId identity = PlayerId.ALL.get(
                StringSerializer.deserializeInt(args[INDEX_OWN_ID_ORDINAL]));

        String[] names = StringSerializer.splitStrings(",", args[INDEX_NAMES_AND_MELDS]);
        Map<PlayerId, String> playerNames = new HashMap<>();
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            playerNames.put(PlayerId.ALL.get(i), StringSerializer.deserializeString(names[i]));
        }
        player.setPlayers(identity, playerNames);
    } 

    private final void responseForTRMP(String[] args){
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        Card.Color color = Card.Color.ALL
                .get(StringSerializer.deserializeInt(args[INDEX_OF_ONLY_ARG]));
        player.setTrump(color);
    }

    private final void responseForHAND(String[] args){
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        long pkCardSet = StringSerializer
                .deserializeLong(args[INDEX_OF_ONLY_ARG]);
        player.updateHand(CardSet.ofPacked(pkCardSet));
    }

    private final void responseForTRCK(String[] args) {
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        int pkTrick = StringSerializer.deserializeInt(args[INDEX_OF_ONLY_ARG]);
        player.updateTrick(Trick.ofPacked(pkTrick));
    }

    private final int selectedCard(String[] args){
        assert args.length == NB_OF_ARGS_FOR_CARD_WMEL_AND_PLRS;
        String[] tuStArgs = StringSerializer.splitStrings(",", args[INDEX_TURNSTATE]);
        assert tuStArgs.length == NB_OF_ATTRIBUTES_IN_TURNSTATE;
        TurnState turnstate = TurnState.ofPackedComponents(
                StringSerializer.deserializeLong(tuStArgs[INDEX_TURNSTATE_PK_SCORE]), 
                StringSerializer.deserializeLong(tuStArgs[INDEX_TURNSTATE_PK_UNPLAYED_CARDS]), 
                StringSerializer.deserializeInt(tuStArgs[INDEX_TURNSTATE_PK_TRICK]));
        long pkCardSet = StringSerializer.deserializeLong(args[INDEX_HAND]);
        //we return the selected card !
        return player.cardToPlay(turnstate, CardSet.ofPacked(pkCardSet)).packed();
    }

    private final void responseForSCOR(String[] args){
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        long pkScore = StringSerializer
                .deserializeLong(args[INDEX_OF_ONLY_ARG]);
        player.updateScore(Score.ofPacked(pkScore));
    }

    private final void responseForWINR(String[] args){
        assert args.length == NB_OF_ARGS_FOR_TRMP_HAND_TRCK_SCOR_WINR_UPCH_MELD_AND_CHTP;
        TeamId winningTeam = TeamId.ALL
                .get(StringSerializer.deserializeInt(args[INDEX_OF_ONLY_ARG]));
        player.setWinningTeam(winningTeam);

        //if we set the winning team, the game is over !
        gameContinue = false;
    }

}
