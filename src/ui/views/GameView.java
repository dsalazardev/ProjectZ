package ui.views;

import game.Juego;
import ui.components.EngineerShop;
import ui.components.GameCanvas;
import ui.components.StatsDisplay;
import javafx.scene.layout.BorderPane;

public class GameView {

    private BorderPane root;
    private StatsDisplay statsDisplay;
    private GameCanvas gameCanvas;
    private EngineerShop engineerShop;

    public GameView(Juego juegoBackend) {
        root = new BorderPane();

        statsDisplay = new StatsDisplay();
        engineerShop = new EngineerShop();

        gameCanvas = new GameCanvas(juegoBackend, engineerShop);

        root.setTop(statsDisplay);
        root.setCenter(gameCanvas);
        root.setRight(engineerShop);
    }

    public BorderPane getRoot() {
        return root;
    }

    public StatsDisplay getStatsDisplay() {
        return statsDisplay;
    }
}