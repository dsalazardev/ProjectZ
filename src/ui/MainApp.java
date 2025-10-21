package ui;

import game.Juego; // Importa el Backend
import ui.components.StatsDisplay;
import ui.views.GameView;
import javafx.application.Application;
import javafx.application.Platform; // ¡Importante para la concurrencia!
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Juego juegoBackend;
    private GameView gameView;

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Ingenieros vs Zombies");

        this.juegoBackend = new Juego( this);

        this.gameView = new GameView(juegoBackend);

        Scene scene = new Scene(gameView.getRoot(), 1280, 720);

        try {
            String cssPath = getClass().getResource("/ui/styles/theme.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Error al cargar theme.css: " + e.getMessage());
            e.printStackTrace();
        }

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando aplicación... deteniendo hilos del backend.");
            juegoBackend.terminarJuego(); // Llama al método de apagado del backend
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