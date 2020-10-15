/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/** class which play the role of server for distant player **/
public final class RemoteMain extends Application {

    /**
     * main which launch the application : when we run, the server is connected to
     * the port 5108 and drives a local player based on the received messages
     * 
     * @param args not important
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*/
     * (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage arg0) throws Exception {
        Player player = new GraphicalPlayerAdapter();
        RemotePlayerServer server = new RemotePlayerServer(player);
        System.out.println(" The game will start at the client's connexion… ");

        Thread gameThread = new Thread(() -> {
            server.run();
            try {
                // we wait 1s after each trick to be able to see the last card
                Thread.sleep(LocalMain.WAIT_TIME_END_TRICK_IN_MILLIS);
            } catch (Exception e) {
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

}
