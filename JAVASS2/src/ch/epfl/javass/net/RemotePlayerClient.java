package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.MeldSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** class representing the client of a player **/
public final class RemotePlayerClient implements Player, AutoCloseable {

    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Socket socket;

    /**
     * public constructor of the class, takes as parameter the name of the host
     * on which the remote player's server is running, and connects to it
     * 
     * @param host
     *            the name of the host
     * @throws UnknownHostException
     *             thrown to indicate that the IP address of a host could not be
     *             determined
     * @throws IOException
     *             thrown if an I/O exception has occurred, produced by failed
     *             or interrupted I/O operations
     */
    public RemotePlayerClient(String host) throws IOException {
        /*
         * we will pay attention to put the creation of a RemotePlayerClient
         * inside a try catch : see the local main
         */
        this.socket = new Socket(host, RemotePlayerServer.PORT);
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(),
                        US_ASCII));
        this.writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(),
                        US_ASCII));
    }


    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        try {
            writer.write(StringSerializer.combineStrings(" ",
                    JassCommand.CARD.name(), combineTurnState(state),
                    StringSerializer.serializeLong(hand.packed())));
            writer.write('\n');
            writer.flush();
            int packedCardToPlay = StringSerializer
                    .deserializeInt(reader.readLine());
            return Card.ofPacked(packedCardToPlay);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chibrer()
     */
    @Override
    public boolean choseToChibrer() {
        try {
            writer.write(JassCommand.CHBR.name());
            writer.write('\n');
            writer.flush();
            int value = StringSerializer
                    .deserializeInt(reader.readLine());
            if (value == 1)
                return true;
            else
                return false;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#chooseTrump(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color chooseTrump(CardSet hand) {
        try {
            writer.write(StringSerializer.combineStrings(" ", JassCommand.CHTP.name(),
                    StringSerializer.serializeLong(hand.packed())));
            writer.write('\n');
            writer.flush();
            int trumpIndex = StringSerializer.deserializeInt(reader.readLine());
            return Color.ALL.get(trumpIndex);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.PLRS.name(),
                        StringSerializer.serializeInt(ownId.ordinal()),
                        combinePlayerNames(playerNames)));
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.HAND.name(),
                        StringSerializer.serializeLong(newHand.packed())));
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.TRMP.name(),
                        StringSerializer.serializeInt(trump.ordinal())));
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.TRCK.name(),
                        StringSerializer.serializeInt(newTrick.packed())));
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.SCOR.name(),
                        StringSerializer.serializeLong(score.packed())));
    }

    /*/
     * (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.WINR.name(),
                        StringSerializer.serializeInt(winningTeam.ordinal())));
    }

    @Override
    public MeldSet selectMeldSet(CardSet hand) {
        try {
            writer.write(StringSerializer.combineStrings(" ", JassCommand.MELD.name(),
                    StringSerializer.serializeLong(hand.packed())));
            writer.write('\n');
            writer.flush();
            int indexOfSelectedMeldSet = StringSerializer
                    .deserializeInt(reader.readLine());
            return MeldSet.allIn(hand).get(indexOfSelectedMeldSet);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void setWinningPlayerOfMelds(PlayerId winningPlayer,
            MeldSet meldset) {
        writeNextMessage(
                StringSerializer.combineStrings(" ", JassCommand.WMEL.name(),
                        StringSerializer.serializeInt(winningPlayer.ordinal()),
                        combineMedlSet(meldset)));
    }


    /*/
     * (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        socket.close();
    }

    /*
     * send the string to the server (avoid to write the same portion of code
     * everywhere)
     * 
     * @param message the string we want to send to the server
     */
    private void writeNextMessage(String message) {
        try {
            writer.write(message);
            writer.write('\n');
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    private String combinePlayerNames(Map<PlayerId, String> playerNames) {
        return StringSerializer.combineStrings(",",
                StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_1)),
                StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_2)),
                StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_3)),
                StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_4)));
    }

    private String combineTurnState(TurnState state) {
        return StringSerializer.combineStrings(",",
                StringSerializer.serializeLong(state.packedScore()),
                StringSerializer.serializeLong(state.packedUnplayedCards()),
                StringSerializer.serializeInt(state.packedTrick()));
    }

    private String combineMedlSet(MeldSet meldset) {
        assert meldset.getCardSets().size() == meldset.getPoints().size();
        // we send pointsMeld1:cardsetMeld1,pointMelds2:cardsetMeld2,...

        StringJoiner join = new StringJoiner(",");
        for (int i=0;i<meldset.size();++i) {
            join.add(StringSerializer.combineStrings(":", 
                    StringSerializer.serializeInt(meldset.getPoints().get(i)),
                    StringSerializer.serializeLong(meldset.getCardSets().get(i).packed())));
        }
        return join.toString();
    }
}
