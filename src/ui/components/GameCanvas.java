package ui.components;

import game.Juego;
import entities.zombies.Zombie;
import entities.engineers.Ingeniero;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameCanvas extends Canvas {

    private Juego juegoBackend;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    public GameCanvas(Juego juegoBackend, double width, double height) {
        super(width, height);
        this.juegoBackend = juegoBackend;
        this.gc = getGraphicsContext2D();

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarYRenderizar();
            }
        };
        gameLoop.start();

        setOnMouseClicked(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            System.out.println("Clic en canvas: " + x + ", " + y);
        });
    }

    public GameCanvas(Juego juegoBackend, EngineerShop engineerShop) {

    }

    private void actualizarYRenderizar() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.CYAN);
        gc.setLineWidth(0.5);

        gc.setFill(Color.GREEN);
        if(juegoBackend.getMapa() != null) {
            for (Ingeniero ing : juegoBackend.getMapa().getIngenierosActivos()) {
                double px = ing.getPosicionX() * 50;
                double py = ing.getPosicionY() * 50;
                gc.fillRect(px, py, 40, 40);
            }
        }

        gc.setFill(Color.RED);
        if(juegoBackend.getMapa() != null) {
            for (Zombie zom : juegoBackend.getMapa().getZombiesActivos()) {
                double px = zom.getPosicionX() * 50;
                double py = zom.getPosicionY() * 50;
                gc.fillOval(px, py, 30, 30);
            }
        }
    }
}