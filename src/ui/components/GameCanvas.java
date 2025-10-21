package ui.components;

import game.Juego;
import entities.engineers.Ingeniero;
import entities.zombies.Zombie;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

public class GameCanvas extends Canvas {

    private static final int TILE_SIZE = 50;

    private Juego juegoBackend;
    private EngineerShop engineerShop;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    public GameCanvas(Juego juegoBackend, EngineerShop engineerShop) {


        super(juegoBackend.getMapa().getAncho() * TILE_SIZE,
                juegoBackend.getMapa().getAlto() * TILE_SIZE);

        this.juegoBackend = juegoBackend;
        this.engineerShop = engineerShop;
        this.gc = getGraphicsContext2D();

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarYRenderizar();
            }
        };
        gameLoop.start();

        setOnMouseClicked(event -> {
            String tipoIngeniero = engineerShop.getIngenieroSeleccionado();

            if (tipoIngeniero != null) {

                int col = (int) (event.getX() / TILE_SIZE);
                int fila = (int) (event.getY() / TILE_SIZE);

                System.out.println("UI: Intento de colocar " + tipoIngeniero + " en (fila:" + fila + ", col:" + col + ")");

                // Enviar la acción al backend
                juegoBackend.intentarColocarIngeniero(fila, col, tipoIngeniero);

                engineerShop.deseleccionar();
            }
        });
    }


    private void actualizarYRenderizar() {
        gc.setFill(Color.web("#121212"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.web("#00FFFF", 0.2));
        gc.setLineWidth(1);
        for (int x = 0; x <= getWidth(); x += TILE_SIZE) {
            gc.strokeLine(x, 0, x, getHeight());
        }
        for (int y = 0; y <= getHeight(); y += TILE_SIZE) {
            gc.strokeLine(0, y, getWidth(), y);
        }

        List<Ingeniero> ingenieros = juegoBackend.getMapa().getIngenierosActivos();
        List<Zombie> zombies = juegoBackend.getMapa().getZombiesActivos();

        gc.setFill(Color.GREEN);
        if (ingenieros != null && !ingenieros.isEmpty()) {
            for (Ingeniero ing : ingenieros) {
                double px = ing.getPosicionX() * TILE_SIZE;
                double py = ing.getPosicionY() * TILE_SIZE;
                gc.fillRect(px + 5, py + 5, TILE_SIZE - 10, TILE_SIZE - 10); // Cuadrado verde
            }
        }

        gc.setFill(Color.RED);
        if (zombies != null && !zombies.isEmpty()) {
            for (Zombie zom : zombies) {
                double px = zom.getPosicionX() * TILE_SIZE;
                double py = zom.getPosicionY() * TILE_SIZE;
                gc.fillOval(px + 10, py + 10, TILE_SIZE - 20, TILE_SIZE - 20);
            }
        }
    }
}