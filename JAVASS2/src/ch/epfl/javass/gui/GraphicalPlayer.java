package ch.epfl.javass.gui;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.MeldSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** class representing the graphic interface of a human player **/
public final class GraphicalPlayer {

    // we avoid recalculations by initializing them as static
    private final static ObservableMap<Card, Image> CARD_IMAGES_240 = createMapCards(
            240);
    private final static ObservableMap<Card, Image> CARD_IMAGES_160 = createMapCards(
            160);

    private final Scene scene;

    private final PlayerId ownId;
    private final Map<PlayerId, String> playerNames;

    private final static int WIDTH_TRUMP = 101;
    private final static int HEIGHT_TRUMP = 101;
    private final static int WIDTH_TRICK_CARD = 120;
    private final static int HEIGHT_TRICK_CARD = 180;
    private final static int WIDTH_HAND_CARD = 80;
    private final static int HEIGHT_HAND_CARD = 120;
    private final static int WIDTH_ANNOUNCE_CARD = 60;
    private final static int HEIGHT_ANNOUNCE_CARD = 90;
    private final static int RAY_GAUSSIAN_BLUR = 4;
    private final static int PLAYABLE_CARD_OPACITY = 1;
    private final static double UNPLAYABLE_CARD_OPACITY = 0.2;
    private final static int TRICKPANE_MAX_WIDTH = 600;
    private final static int TRICK_LEFT_COLUMN = 0;
    private final static int TRICK_UPPER_ROW = 0;
    private final static int TRICK_CENTER_COLUMN = 1;
    private final static int TRICK_CENTER_ROW = 1;
    private final static int TRICK_RIGHT_COLUMN = 2;
    private final static int TRICK_BOTTOM_ROW = 2;
    private final static int COLUMN_SPAN = 1;
    private final static int ROW_SPAN = 3;
    private final static int MARGIN_SPACE_FOR_ICON = 20;
    private final static int SIZE_TEXT_RULES = 15;
    private final static int ICON_HEIGHT = 40;
    private final static int ICON_WIDTH = 30;
    private final static int ANNOUNCE_WIDTH = 280;
    private final static int TRANSLATE_WHEN_MOUSE_ON_CARD = -18;
    /*ANNOUNCE_WIDTH
     * here we choose to let the possibilities to change quickly if we want to
     * have a smaller or a bigger scrollPane for the rules
     */
    private final static int MAX_WIDTH_SCROLL = 600;
    private final static int MAX_HEIGHT_SCROLL = 600;

    private final static String STYLE_OF_SCOREPANE = "-fx-font: 16 Optima; "
            + "-fx-background-color: lightgray; "
            + "-fx-padding: 5px;";
    private final static String STYLE_OF_TRICKPANE = "-fx-background-image: url(\"card_table.png\"); " + 
            "-fx-background-repeat: stretch; -fx-background-position: center center;" +
            "-fx-background-size: 500 500;  -fx-alignment: center;" +
            "-fx-padding: 5px; -fx-border-width: 3px 1px; "
            + "-fx-border-style: solid; -fx-border-color: gray;";
    private final static String STYLE_OF_TRICK_HALO = "-fx-arc-width: 20; -fx-arc-height: 20;"
            + " -fx-fill: transparent;"
            + "-fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.8;";
    private final static String STYLE_OF_HAND_HALO = "-fx-arc-width: 10; -fx-arc-height: 10;"
            + " -fx-fill: transparent;"
            + "-fx-stroke: lime; -fx-stroke-width: 5; -fx-opacity: 0.8;";
    private final static String STYLE_OF_HANDPANE = "-fx-background-color: lightgray; "
            + "-fx-spacing: 5px; -fx-padding: 5px;";
    private final static String STYLE_OF_VICTORYPANE = "-fx-font: 16 Optima; "
            + "-fx-background-color: white;";
    private final static String STYLE_OF_ANNOUNCE = "-fx-font: 16 Optima; -fx-alignment: center; "
            + "-fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray;"
            + "-fx-spacing: 20px; -fx-background-image: url(\"background_color.png\"); " 
            + "-fx-background-repeat: repeat; -fx-background-size: 350 550;";
    private final static String RULES = "CARDS ORDER \n\n" + 
            "   In trump's colour : Jack - 9 - Ace - King - Queen - 10 - 8 - 7 - 6. \n" +
            "   Usual order: Ace - King - Queen - Jack - 10 -9 - 8 - 7 - 6. " + 
            "\n\n" +
            "TRUMP \n\n" + 
            "   In the first round, the player with the 7 of diamonds chooses the trump, \n"
            + "   and becomes the dealer of the next turn. \n"
            + "   In the next rounds, it is the person after the donor who will make trump. \n"
            + "   \"Make trump\" means \"choose trump's colour\". \n"
            + "   If the player cannot or will not choose \n"
            + "   he has the possibility to \"chibrer\" and his partner must choose \n"
            + "   a trump colour in its place. \n"
            + "   It is the player who was supposed to make the trump that starts (even if it is his \n"
            + "   partner who has chosen the trump). " + "\n\n" + "ANNOUNCEMENTS \n\n"
            + "   When placing his first card, each player can declare one or more announcements. \n"
            + "   Here, the search as well as the declaration of the announcements is automatic. \n"
            + "   Only the announcements of the team that made the strongest announcement are counted.\n"
            + "\n\n" + "POSSIBLE ANNOUNCEMENTS \n\n" + "   3 cards = 20 points \n"
            + "       (announced \"Tree cards!\"). Three cards following each other in the same suit \n "
            + "     (for exemple :ace-king-queen) \n"
            + "   4 cards = 50 points \n"
            + "       (annoncé \"Fifty!\"). Four cards following each other in the same suit \n"
            + "     (for exemple :ace-king-queen-jack) \n"
            + "   5 cartes = 100 points \n"
            + "       (announced \"Hundred!\"). Five cards following each other in the same suit. \n"
            + "   Square of 4 cards = 100 points \n"
            + "       (announced \"Hundred!\"). Four 10, queens, kings or aces. \n"
            + "   Square of nines= 150 points \n"
            + "       (announced \"Hundred-Fifty!\"). These are the 4 nines. \n"
            + "   Square of jacks = 200 points \n"
            + "       (announced \"Two-Hundreds the bauers!\"). These are the 4 jacks. \n"
            + "   If a person has 100 in five cards, he or she is stronger than a person \n"
            + "   having 100 in 4 cards, i. e. his announcement will be valid and not that of his \n"
            + "   opponent. \n"
            + "   Similarly, a person with 100 aces will be stronger than someone else \n"
            + "   having 100 the kings."
            + "\n\n" + "POINTS \n\n"
            + "   The team that won the announcements immediately scores the points of its announcements. \n"
            + "   At the end of the round, each team scores the points of the tricks it has won: \n"
            + "   The ace is worth 11 points, the king is worth 4 points, \n"
            + "   the queen is worth 3 points the jack is worth 2 points, the 10 is worth ten points. \n"
            + "   The trump jack is 20, the 9 of trump worth 14. \n"
            + "   The other cards are not worth any points. The last trick is worth 5 extra points. \n"
            + "   The game has 157 points. If a team wins all the tricks, it has made a chelem, \n"
            + "   and gets a bonus of 100 points, for a total of 257 points. \n"
            + "   The first team to reach 1000 points wins the game."
            + "\n";

    /**
     * Only public constructor of GraphicalPlayer which create the scene from
     * the different arguments
     * 
     * @param ownId
     *            the id of the player who play with this interface
     * @param players
     *            a map which assign the name of each PlayerId
     * @param score
     *            the scoreBean of this game
     * @param trick
     *            the trickBean of this game
     * @param hand
     *            the handBean of this game
     * @param comQueue
     *            the communication queue used to interact with the graphical
     *            interface
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> players,
            ScoreBean score, TrickBean trick, HandBean hand,
            ArrayBlockingQueue<Card> cardQueue,
            ArrayBlockingQueue<Color> trumpQueue,
            ArrayBlockingQueue<Boolean> booleanQueue,
            ArrayBlockingQueue<MeldSet> meldSetQueue) {

        this.ownId = ownId;
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(players));

        BorderPane scorePane = createScorePane(score, players, trick,hand);
        GridPane trickPane = createTrickPane(players, trick);
        StackPane chooseTrumpPane = createTrumpChoosingPane(trumpQueue, hand, trick, booleanQueue);
        StackPane announcePane = createAnnouncePane(hand,trick,meldSetQueue, score, booleanQueue);
        HBox handPane = createHandPane(hand, cardQueue);
        BorderPane[] victoryPanes = createVictoryPanes(players, score);
        VBox winningPlayerMeldPane = createWinPlayerMeldPane(score);

        StackPane rulesPane = createRulesPane(trick);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(scorePane);
        mainPane.setCenter(new StackPane(rulesPane,trickPane));
        mainPane.setRight(chooseTrumpPane);
        mainPane.setLeft(new StackPane(announcePane,winningPlayerMeldPane));
        mainPane.setBottom(handPane);
        StackPane stkPane = new StackPane(mainPane, victoryPanes[TeamId.TEAM_1.ordinal()], 
                victoryPanes[TeamId.TEAM_2.ordinal()]);
        scene = new Scene(stkPane);
    }

    /**
     * create the stage in which the scene is hung : will be display in the
     * window (so public!)
     * 
     * @return the stage in which the scene is hung
     */
    public Stage createStage() {
        Stage s = new Stage();
        s.setTitle("Javass-" + playerNames.get(ownId));
        s.setScene(scene);
        return s;
    }


    /*
     * here we create the pane of the scores and we bind the different
     * properties
     */
    private BorderPane createScorePane(ScoreBean score, Map<PlayerId, String> players, TrickBean trick, HandBean hand) {        
        GridPane scores = new GridPane();
        scores.setAlignment(Pos.CENTER);

        /******************************************************************/
        ImageView rules = new ImageView("/rules_cranium.png");
        configureIcon(rules);
        rules.setOnMouseClicked( q -> {
            trick.setShowRules(!trick.showRulesProperty().get());
        });
        /******************************************************************/

        ImageView hint = new ImageView("/idea_bulb.png");
        configureIcon(hint);
        hint.setOnMouseClicked(q -> {
            hand.setshowBestCard(!hand.showBestCardProperty().get());
        });
        /******************************************************************/

        for (TeamId team : TeamId.ALL) {
            int ordinal = team.ordinal();
            Text names = new Text(createTextNames(team, players) + " : ");

            /*******************************newTurnPoints*************************************/
            Text newTurnPoints = new Text();
            // we bind a textProperty to another textProperty !
            newTurnPoints.textProperty()
            .bind(Bindings.convert(score.turnPointsProperty(team)));

            /********************************plusPoints*************************************/
            Text plusPoints = new Text();
            score.turnPointsProperty(team)
            .addListener((obs, oldValue, newValue) ->
            // here if the turnPoints are 0 we don't display the "(+0)"
            plusPoints
            .setText(
                    newValue.intValue() == 0 ? ""
                            : " (+" + (newValue.intValue()
                                    - oldValue.intValue())
                            + ")"));

            /******************************TotalPoints*************************************/
            Text newTotalPoints = new Text();
            // we bind a textProperty to another textProperty !
            newTotalPoints.textProperty()
            .bind(Bindings.convert(score.gamePointsProperty(team)));

            /**************************set and alignment*****************************/
            scores.addRow(ordinal,
                    names, newTurnPoints, plusPoints, new Text("/ Total : "),
                    newTotalPoints);

            // alignment to the left by default
            GridPane.setHalignment(names, HPos.RIGHT);
            GridPane.setHalignment(newTurnPoints, HPos.RIGHT);
            GridPane.setHalignment(newTotalPoints, HPos.RIGHT);

        }
        ;


        BorderPane scoresRulesAndHint = new BorderPane(scores, null, hint, null, rules);

        scoresRulesAndHint.setStyle(STYLE_OF_SCOREPANE);
        //just better visual effect
        BorderPane.setMargin(hint, new Insets(0, MARGIN_SPACE_FOR_ICON,0, 0));
        BorderPane.setMargin(rules, new Insets(0, 0, 0, MARGIN_SPACE_FOR_ICON));

        return scoresRulesAndHint;
    }

    /****************************************************************************************************************/

    private void configureIcon(ImageView im) {
        im.setFitHeight(ICON_HEIGHT);
        im.setFitWidth(ICON_WIDTH);
        // picking is computed by intersecting with the bounds of this node
        im.setPickOnBounds(true);
    }

    /****************************************************************************************************************/

    /*
     * here we create the pane of the trick and we bind the different properties
     * of the trickBean to display the correct cards
     */
    private GridPane createTrickPane(Map<PlayerId, String> players, TrickBean trick) {
        GridPane trickPane = new GridPane();

        ImageView trumpImage = new ImageView();
        trick.trumpProperty()
        .addListener((obs, oldValue, newValue) -> trumpImage.setImage(
                new Image("/trump_" + newValue.ordinal() + ".png")));
        trumpImage.setFitHeight(HEIGHT_TRUMP);
        trumpImage.setFitWidth(WIDTH_TRUMP);
        trickPane.add(trumpImage, TRICK_CENTER_COLUMN, TRICK_CENTER_ROW);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

        for (PlayerId player : PlayerId.ALL){
            VBox couple = setNewCouple(player, trick, players);
            if (player.team().equals(ownId.team()))
                trickPane.add(couple, TRICK_CENTER_COLUMN,
                        player.equals(ownId) ? TRICK_BOTTOM_ROW
                                : TRICK_UPPER_ROW);
            else
                trickPane.add(couple,
                        isAfter(player, ownId) ? TRICK_RIGHT_COLUMN
                                : TRICK_LEFT_COLUMN,
                                TRICK_UPPER_ROW, COLUMN_SPAN, ROW_SPAN);
        };
        trickPane.setMaxWidth(TRICKPANE_MAX_WIDTH);
        trickPane.setStyle(STYLE_OF_TRICKPANE);

        trickPane.visibleProperty().bind(trick.showRulesProperty().isEqualTo(false));

        return trickPane;
    }
    /**********************************************************************************************************/
    //use to simplify the code
    private boolean isAfter(PlayerId supposedAfter, PlayerId supposedBefore) {
        if (supposedAfter.equals(PlayerId.PLAYER_1))
            return supposedBefore.equals(PlayerId.PLAYER_4);
        else
            return supposedAfter.ordinal() - supposedBefore.ordinal() == 1;
    }
    /**********************************************************************************************************/
    private VBox setNewCouple(PlayerId player, TrickBean trick, Map<PlayerId, String> players) {

        Text name = new Text(players.get(player));
        name.setStyle("-fx-font: 14 Optima;-fx-stroke: white;");

        ImageView image = new ImageView();
        image.imageProperty().bind(Bindings.valueAt(CARD_IMAGES_240,
                Bindings.valueAt(trick.trick(), player)));
        image.setFitWidth(WIDTH_TRICK_CARD);
        image.setFitHeight(HEIGHT_TRICK_CARD);

        StackPane haloAndCard = new StackPane(halo(WIDTH_TRICK_CARD, HEIGHT_TRICK_CARD,
                STYLE_OF_TRICK_HALO, trick.winningPlayerProperty().isEqualTo(player)),
                image);

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        // the name of our player is at the bottom of his card
        if (player.equals(ownId))
            box.getChildren().addAll(haloAndCard, name);
        else
            box.getChildren().addAll(name, haloAndCard);

        box.setStyle("-fx-padding: 5px; -fx-alignment: center;");

        return box;
    }

    /**********************************************************************************************************/
    private Rectangle halo(int width, int height, String style, BooleanBinding visibleBinding) {

        Rectangle halo = new Rectangle(width, height);
        halo.setStyle(style);
        halo.setEffect(new GaussianBlur(RAY_GAUSSIAN_BLUR));
        halo.visibleProperty().bind(visibleBinding);

        return halo;
    }

    /****************************************************************************************************************/    

    /**
     * here we create the victory panes and we bind their visible properties to
     * the winning team property of each of them
     */
    private BorderPane[] createVictoryPanes(Map<PlayerId, String> players,
            ScoreBean score) {
        BorderPane[] BPane = new BorderPane[TeamId.COUNT];
        for (TeamId team : TeamId.ALL) {
            BorderPane victoryPane = createVictoryPane(team, players, score);
            victoryPane.visibleProperty()
            .bind(score.winningTeamProperty().isEqualTo(team));

            BPane[team.ordinal()] = victoryPane;
        }
        return BPane;
    }

    //to avoid duplicate code
    private BorderPane createVictoryPane(TeamId team, Map<PlayerId, String> players, ScoreBean score) {
        BorderPane victoryPane = new BorderPane();
        Text victory = new Text();
        victory.textProperty().bind(Bindings.format("%s have won with %d points against %d.",
                createTextNames(team, players),
                score.totalPointsProperty(team),
                score.totalPointsProperty(team.other())));
        victoryPane.setCenter(victory);
        victoryPane.setStyle(STYLE_OF_VICTORYPANE);

        return victoryPane;
    }

    /****************************************************************************************************************/
    // for the ScorePane we use this to purify the code
    private String createTextNames(TeamId team, Map<PlayerId, String> players) {

        PlayerId firstPlater = PlayerId.ALL.get(team.ordinal());
        // we respect the order of the enumeration
        return players.get(firstPlater) + " and "
        + players.get(firstPlater.teamMate());
    }


    /****************************************************************************************************************/

    /**
     * we create the HandPane and connect it with the given blockingQueue to
     * allow the player to select the card of his choice
     */
    private HBox createHandPane(HandBean hand, ArrayBlockingQueue<Card> comQueue) {
        HBox handPane = new HBox();
        handPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < Jass.HAND_SIZE; ++i) {
            /*
             * it doesn't look really nice to use another final variable but an
             * assistant told us it was the best way
             */
            final int k = i;
            BooleanBinding isPlayable = Bindings.createBooleanBinding(
                    () -> hand.playableCards().contains(hand.hand().get(k)),
                    hand.hand(),
                    hand.playableCards());
            Rectangle halo = halo(WIDTH_HAND_CARD,
                    HEIGHT_HAND_CARD, STYLE_OF_HAND_HALO,
                    hand.hintPlayerCardProperty()
                    .isEqualTo(Bindings.valueAt(hand.hand(), i))
                    .and(hand.showBestCardProperty().isEqualTo(true)).and(isPlayable));

            ImageView image = new ImageView();
            image.imageProperty().bind(Bindings.valueAt(CARD_IMAGES_160, Bindings.valueAt(hand.hand(), i)));
            image.setFitWidth(WIDTH_HAND_CARD);
            image.setFitHeight(HEIGHT_HAND_CARD);

            StackPane imageAndHalo = new StackPane(halo,image);

            imageAndHalo.setOnMouseEntered(q -> {
                imageAndHalo.setTranslateY(TRANSLATE_WHEN_MOUSE_ON_CARD);
            });
            imageAndHalo.setOnMouseExited(q -> {
                imageAndHalo.setTranslateY(0);
            });
            imageAndHalo.opacityProperty().bind(Bindings.when(isPlayable).then(PLAYABLE_CARD_OPACITY)
                    .otherwise(UNPLAYABLE_CARD_OPACITY));
            imageAndHalo.disableProperty().bind(isPlayable.not());

            imageAndHalo.setOnMouseClicked( q -> {
                try {
                    comQueue.put(hand.hand().get(k));
                } catch (InterruptedException e) { 
                    //according to the forum, we replace e.printStackTrace() by  
                    throw new Error(e); 
                }
            });
            handPane.getChildren().add(imageAndHalo);
        }
        handPane.setStyle(STYLE_OF_HANDPANE);

        return handPane;
    }

    /****************************************************************************************************************/

    private static ObservableMap<Card, Image> createMapCards(int pixels) {

        Map<Card, Image> map = new HashMap<>();
        CardSet set = CardSet.ALL_CARDS;

        for (int i = 0; i < set.size(); ++i) {
            Card card = set.get(i);
            String imageGetter = "/card_" + card.color().ordinal() + "_" + card.rank().ordinal()
                    + "_" + pixels + ".png";
            Image cardImage = new Image(imageGetter);
            map.put(card, cardImage);
        }
        return FXCollections.observableMap(map);
    }

    /****************************************************************************************************************/

    private StackPane createTrumpChoosingPane(ArrayBlockingQueue<Color> trumpQueue,
            HandBean hand, TrickBean trick, ArrayBlockingQueue<Boolean> booleanQueue) {

        VBox trumpChoosing = new VBox();
        trumpChoosing.setAlignment(Pos.CENTER);
        Text title = new Text("Choose the Trump !");
        title.setStyle("-fx-font: 14 Optima;-fx-stroke: white;");
        trumpChoosing.getChildren().add(title);

        for (int i = 0; i < Color.COUNT; ++i) {
            ImageView image = new ImageView("/trump_" + i + ".png");
            image.setFitHeight(HEIGHT_TRUMP);
            image.setFitWidth(WIDTH_TRUMP);
            final int k = i;
            image.setOnMouseClicked( q -> {
                try {
                    if (trick.chooserProperty().get()) {
                        booleanQueue.put(false);
                    }
                    trumpQueue.put(Color.ALL.get(k));
                } catch (InterruptedException e) { e.printStackTrace(); }
            });
            trumpChoosing.getChildren().add(image);
        }
        Button chibrer = new Button("CHIBRER! \n I let " + 
                playerNames.get(ownId.teamMate()) + " choose the trump");
        chibrer.setTextAlignment(TextAlignment.CENTER);
        chibrer.setStyle("-fx-font: 14 Optima;-fx-stroke: lightgrey;");
        chibrer.setOnMouseClicked( q -> {
            try {
                booleanQueue.put(true);
            } catch (InterruptedException e) { e.printStackTrace(); }
        });
        trumpChoosing.getChildren().add(chibrer);

        trumpChoosing.visibleProperty().bind(
                trick.isAskedToChooseProperty().isEqualTo(true)
                .or
                (trick.chooserProperty().isEqualTo(true)));

        chibrer.visibleProperty().bind(trick.isAskedToChooseProperty().isEqualTo(false));

        trumpChoosing.setMinWidth(280);
        trumpChoosing.setStyle(
                "-fx-font: 16 Optima; -fx-spacing: 10px ; -fx-padding: 5px; "
                        + "-fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray;"
                        + "-fx-background-image: url(\"background_color.png\"); " 
                        + "-fx-background-repeat: repeat;");

        return putBackgroundBehind(trumpChoosing);
    }

    private StackPane putBackgroundBehind(Node n) {
        BorderPane waitingPane = new BorderPane();
        waitingPane.setMinWidth(280);
        waitingPane.setStyle("-fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray;"
                + "-fx-background-image: url(\"background_color.png\"); " 
                + "-fx-background-repeat: repeat;");

        return new StackPane(waitingPane, n);
    }
    /****************************************************************************************************************/

    private StackPane createAnnouncePane(HandBean hand, TrickBean trick,
            ArrayBlockingQueue<MeldSet> meldSetQueue, ScoreBean score,
            ArrayBlockingQueue<Boolean> announceIsPossibleQueue) {
        VBox announces = new VBox();
        announces.setAlignment(Pos.CENTER_LEFT);
        announces.visibleProperty().bind(score.canMakeAnnounceProperty());

        score.canMakeAnnounceProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                announces.getChildren().clear();
            } else {
                List<MeldSet> meldSets = MeldSet.allIn(CardSet.of(hand.hand()));
                if (meldSets.size() == 1) {
                    try {
                        Text nothing = new Text("No possible announcements...");
                        announces.getChildren().add(nothing);
                        announceIsPossibleQueue.put(false);
                        meldSetQueue.put(meldSets.get(0));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Button choosingNot = new Button("I prefer not announcing anything");
                    choosingNot.setOnMouseClicked(q -> {
                        try {
                            announceIsPossibleQueue.put(true);
                            meldSetQueue.put(meldSets.get(0));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    announces.getChildren().add(choosingNot);

                    /* before creating the melds, we need to set the ratio for the size of
                     * the images, which will depend on the meldSet with the most melds.
                     * We need to iterate two times, separately, on the size of the meldSets,
                     * but it's OK has this size is always very small. */
                    /* The first time, we look at the meldSet with the most melds, to
                     * determine the ratio */
                    double ratio = 1;
                    for (int k = 1; k < meldSets.size(); ++k) {
                        // We chose this function for the ratio has it looks nice
                        double newRatio = ratioFunction(meldSets.get(k).size());
                        // if the ratio of the new meld is smaller, it means we must change the old ratio
                        if (newRatio < ratio) {
                            ratio = newRatio;
                        }
                        /* now we check the size of meldSets, which represent the number of
                         * rows in VBox. If there are too many, we need to set a new ratio */
                        newRatio = ratioFunction(meldSets.size());
                        if (newRatio < ratio) {
                            // in this case though, we don't need to set the ratio too low
                            ratio = newRatio * 1.4;
                        }
                    }
                    /* Now that we know the ratio we want to use, we create the pane of meldSets */
                    for (int k = 1; k < meldSets.size(); ++k) {

                        // in each meldSet we can have multiple Melds
                        List<CardSet> listOfCardsetK = meldSets.get(k).getCardSets();

                        HBox meldSetK = putOneMeldSetInHBox(listOfCardsetK, ratio);
                        // need to re initialize because we can't use k in the lambda
                        int finalK = k;
                        meldSetK.setOnMouseClicked(q -> {
                            try {
                                announceIsPossibleQueue.put(true);
                                meldSetQueue.put(meldSets.get(finalK));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        meldSetK.getChildren().add(0, new Text(meldSets.get(k).points()+ " points : "));
                        announces.getChildren().add(meldSetK);
                    }
                }
            }
        });
        /*************************************************************************/
        announces.setMinWidth(ANNOUNCE_WIDTH);
        announces.setMaxWidth(ANNOUNCE_WIDTH);
        announces.setStyle(STYLE_OF_ANNOUNCE);
        return putBackgroundBehind(announces);
    }

    private double ratioFunction(double size) {
        return Math.sqrt(1 / size);
    }

    private HBox putOneMeldSetInHBox(List<CardSet> listOfCardsetK, double ratio) {
        HBox meldSetK = new HBox();
        meldSetK.setAlignment(Pos.CENTER);
        meldSetK.setStyle("-fx-spacing: 20px;");
        for (int i = 0; i < listOfCardsetK.size(); ++i) {
            StackPane meldI = new StackPane();

            for (int cardJ = 0; cardJ < listOfCardsetK.get(i).size(); ++cardJ) {
                ImageView imageOfCardJ = new ImageView();
                imageOfCardJ.imageProperty().bind(Bindings.valueAt(
                        CARD_IMAGES_160, listOfCardsetK.get(i).get(cardJ)));
                imageOfCardJ.setFitWidth(WIDTH_ANNOUNCE_CARD * ratio);
                imageOfCardJ.setFitHeight(HEIGHT_ANNOUNCE_CARD * ratio);

                imageOfCardJ.setRotate(-20 + cardJ*(20));
                imageOfCardJ.setTranslateX(cardJ*10);
                meldI.getChildren().add(imageOfCardJ);
            }
            // we want to have all the Meld in a given MeldSet on a row
            meldSetK.getChildren().add(meldI);
        }
        return meldSetK;
    }

    /****************************************************************************************************************/

    private VBox createWinPlayerMeldPane(ScoreBean score) {
        VBox winMeldPane = new VBox();
        winMeldPane.setAlignment(Pos.CENTER);
        winMeldPane.visibleProperty()
        .bind(score.canWatchMeldResultsProperty());
        Text winningSentence = new Text("");
        score.winningMeldSetProperty()
        .addListener((obs, oldValue, newValue) -> {

            winMeldPane.getChildren().clear();

            MeldSet bestMeldSet = newValue;
            if (bestMeldSet.points() == 0) {
                winningSentence.setText("No one made an announcement !");
                winMeldPane.getChildren().add(winningSentence);
            } else {
                PlayerId winner = score.meldWinningPlayerProperty().get();
                winningSentence.setText((playerNames.get(winner)
                        + " wins "
                        + bestMeldSet.points()
                        + " points with :\n"));
                winMeldPane.getChildren().addAll(winningSentence,
                        putOneMeldSetInHBox(bestMeldSet.getCardSets(),
                                ratioFunction(bestMeldSet.size())));
            }
        });
        winMeldPane.setStyle(STYLE_OF_ANNOUNCE);
        return winMeldPane;
    }


    /****************************************************************************************************************/

    private StackPane createRulesPane(TrickBean trick) {

        Text rules = new Text(RULES);
        rules.setStyle("-fx-font-family: serif;-fx-font-size: "+SIZE_TEXT_RULES+
                ";-fx-text-fill: ligthblack;"
                + "-fx-background-color: lightgray; "
                + "-fx-spacing: 5px; -fx-padding: 5px;");

        ScrollPane scrollPane = new ScrollPane(rules);

        scrollPane.visibleProperty().bind(trick.showRulesProperty());
        rules.textProperty().length().addListener((obs, old,
                newValue) -> scrollPane.setHvalue((Double) newValue));
        scrollPane.setMaxWidth(MAX_WIDTH_SCROLL);
        scrollPane.setMaxHeight(MAX_HEIGHT_SCROLL);

        return putBackgroundBehind(scrollPane);
    }

}