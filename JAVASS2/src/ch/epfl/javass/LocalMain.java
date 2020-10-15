/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass;

import java.io.IOError;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

/** class which control the game **/
public final class LocalMain extends Application {
    /**
     * main which launch the application : it controls the whole game
     * 
     * @param args
     *            definition of the different players of the game like :
     *            <j1>…<j4> [<graine>] (see the method numberArgsInTheMainError
     *            for details on how to declare each player and the optional
     *            see)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** in public because we wait the same time when we are an external player **/
    public static final int WAIT_TIME_END_TRICK_IN_MILLIS = 1000;

    private static final String[] DEFAULT_NAMES = {"Aline", "Bastien","Colette", "David" };
    private static final int DEFAULT_NB_ITERATIONS = 10_000;
    private static final String DEFAULT_HOST_NAME = "localhost";

    private static final int INDEX_OF_PLAYER_TYPE = 0;
    private static final int INDEX_OF_NAME = 1;
    private static final int INDEX_OF_SEED_OR_HOST = 2;
    private static final int POSISTION_OF_OPTIONAL_SEED = 5;
    private static final int NUMBER_OF_SEEDS = 5;
    private static final int MIN_TIME_FOR_PLAYING_IN_S = 2;

    /*/
     * (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage arg0) throws Exception {

        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);

        List<String> listOfParams = getParameters().getRaw();
        if (listOfParams.size() != PlayerId.COUNT
                && listOfParams.size() != POSISTION_OF_OPTIONAL_SEED) {
            numberArgsInTheMainError();
        }

        Random generator = createGenerator(listOfParams);

        /*
         * we create a tab because we want to be able to reproduce the SAME game
         * with the SAME seed in every project
         */
        Long[] seeds = new Long[NUMBER_OF_SEEDS];
        for (int i = 0; i < NUMBER_OF_SEEDS; ++i) {
            seeds[i] = generator.nextLong();
        }

        for (int i = 0; i < Jass.NB_CARDS_PER_TRICK; ++i) {
            //informations concerning each player
            String argDef = listOfParams.get(i);
            //the different parts of the definition of each player
            String[] args = StringSerializer.splitStrings(":", argDef);

            String nameI = args.length > INDEX_OF_NAME
                    && args[INDEX_OF_NAME] != "" ? args[INDEX_OF_NAME]
                            /*
                             * we could have use a map for the names but a tab
                             * is more convenient
                             */
                            : DEFAULT_NAMES[i];

                    // we actualize the name of the next player
                    playerNames.put(PlayerId.ALL.get(i), nameI);

                    // (..+1 because we switch from an index to a position
                    if (args.length > (INDEX_OF_SEED_OR_HOST + 1)) {
                        tooMuchArgsForOnePlayerError(argDef);
                    }

                    assert args.length >= 1;
                    /*
                     * we ensure ourself that the call args[0] won't cause a null
                     * pointer Exception
                     */

                    switch (args[INDEX_OF_PLAYER_TYPE]) {
                    case "h":
                        if (args.length > INDEX_OF_SEED_OR_HOST) {
                            tooMuchArgsForOnePlayerError(argDef);
                        }
                        players.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());
                        break;
                    case "s":
                        int seedI = createSeedI(args, argDef);

                        if (seedI < 10) {
                            numberIterationsForSimulatedPlayerInvalidError(argDef);
                        }
                        players.put(PlayerId.ALL.get(i), new PacedPlayer(new MctsPlayer(
                                /*
                                 * i+1 because the first seed in the tab is reserved for
                                 * the instantiation of JassGame
                                 */
                                PlayerId.ALL.get(i), seeds[i + 1], seedI),
                                MIN_TIME_FOR_PLAYING_IN_S));
                        break;
                    case "r":
                        String hostNameI = args.length > INDEX_OF_SEED_OR_HOST
                        && args[INDEX_OF_SEED_OR_HOST] != ""
                        ? args[INDEX_OF_SEED_OR_HOST]
                                : DEFAULT_HOST_NAME;
                        try {
                            players.put(PlayerId.ALL.get(i),
                                    new RemotePlayerClient(hostNameI));
                        } catch (IOError e) {
                            connectionServeurError(argDef);
                        }
                        break;
                    default:
                        specificationOfPlayerError(argDef);
                        break;
                    }
        }

        Thread gameThread = new Thread(() -> {
            /*
             * the first seed in the tab is reserved for the instantiation of
             * JassGame !
             */
            JassGame g = new JassGame(seeds[0], players, playerNames);
            while (!g.isGameOver()) {
                try {
                    Thread.sleep(WAIT_TIME_END_TRICK_IN_MILLIS);
                } catch (Exception e) {
                }
                g.advanceToEndOfNextTrick();
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    /**************************************************************************/
    private void numberArgsInTheMainError() {
        System.err.println(
                "Use on the console: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>] \n"
                        + "\n"
                        + " The program accepts 4 or 5 arguments. The first 4 specify the players, and the\n"
                        + " last, optional, specifies the seed to be used to generate the seeds of the different\n"
                        + " random generators of the program.\n"
                        + "\n"
                        + " Each player <ji> is specified by means of a string of characters \n"
                        + " composed of one to three components, separated from each other \n"
                        + " by a two-point (:). \n"
                        + "\n"
                        + " The first component consists of only one letter, which can be :\n" 
                        + "    either h for a human player (local),\n" 
                        + "    either s for a simulated player (local),\n" 
                        + "    either r for a remote player.\n"
                        + "\n" 
                        + " The second component, optional, is the player's name.\n"
                        + " By default, the following names are assigned to players, in order : \n"
                        + "    Aline, Bastien, Colette and David. \n"
                        + "\n"
                        + " The third component, also optional, depends on the type of player :\n"
                        + "    -for a simulated player, it gives the number of iterations of the MCTS algorithm\n"
                        + "    (10 000 by default), it must be greater than or equal to 10.\n" 
                        + "    -for a remote player, it gives the name or IP address of the host on which\n"
                        + "    the player's server is running (default localhost).\n"
                        + "\n"
                        + " For example, the following arguments : s h:Marie r:Céline:128.178.243.14 s::20000 \n"
                        + " specify a game in which the following players participate :\n"
                        + "    -a simulated player named Aline, with 10,000 iterations,\n"
                        + "    -a human player named Marie,\n"
                        + "    -a remote player named Celine whose server is running on the computer"
                        + "    whose IP address is 128.178.243.14,\n"
                        + "    -a simulated player named David, with 20,000 iterations.");        
    }
    /**************************************************************************/
    private void specificationOfPlayerError(String argDef) {
        showError("invalid player specification",argDef);
    }
    /**************************************************************************/
    private void tooMuchArgsForOnePlayerError(String argDef) {
        showError("too many arguments in the player specification",argDef);
    }
    /**************************************************************************/
    private void connectionServeurError(String argDef) {
        showError("connection to the server impossible",argDef);
    }
    /**************************************************************************/
    private void randomSeedInvalidError(String argDef) {
        showError("invalid random seed", argDef);
    }
    /**************************************************************************/
    private void numberIterationsForSimulatedPlayerInvalidError(String argDef) {
        showError("number of iterations of the invalid simulated player or less than 10", argDef);
    }
    /**************************************************************************/
    //use to avoid duplicate code
    private void showError(String nameError, String argDef) {
        System.err.printf("Erreur : %s : %s",nameError, argDef);
        //by default we chose the error 1 
        System.exit(1);
    }

    /*************use to avoid a try catch covering the whole code***********/

    private Random createGenerator(List<String> listOfParams) {
        Random generator = new Random();
        try {
            /*
             * if the size is 5 it means that the fifth element is different from ""
             * so we have a seed for the generator
             */
            if (listOfParams.size() == POSISTION_OF_OPTIONAL_SEED) {
                generator = new Random(Long.parseLong(
                        // -1 because we switch from a position to an index
                        listOfParams.get(POSISTION_OF_OPTIONAL_SEED - 1)));
            }
        } catch (NumberFormatException n) {
            // -1 because we want an index
            randomSeedInvalidError(
                    listOfParams.get(POSISTION_OF_OPTIONAL_SEED - 1));
        }
        return generator;
    }

    private int createSeedI(String[] args,String argDef) {
        int seedI = DEFAULT_NB_ITERATIONS;
        try {
            if (args.length > INDEX_OF_SEED_OR_HOST) {
                seedI = Integer.parseInt(args[INDEX_OF_SEED_OR_HOST]);
            }
        } catch (NumberFormatException n) {
            numberIterationsForSimulatedPlayerInvalidError(argDef);
        }
        return seedI;
    }
}
