package ui;

import game.Juego;
import ui.components.StatsDisplay;
import ui.views.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Juego juegoBackend;
    private GameView gameView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Project Z: The Last Bastion");

        this.juegoBackend = new Juego(this);

        this.gameView = new GameView(juegoBackend);

        Scene scene = new Scene(gameView.getRoot(), 1280, 720);

        try {

            String cssPath = getClass().getResource("/ui/styles/theme.css").toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            } else {
                System.err.println("No se encontró el archivo theme.css en /ui/styles/theme.css");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar theme.css: " + e.getMessage());
            e.printStackTrace();
        }

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando aplicación... deteniendo hilos del backend.");
            if (juegoBackend != null) {
                juegoBackend.terminarJuego();
            }
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        new Thread(() -> juegoBackend.iniciarJuego()).start();
    }

    public void actualizarUI(Runnable tarea) {
        Platform.runLater(tarea);
    }

    public StatsDisplay getStatsDisplay() {
        return gameView.getStatsDisplay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}