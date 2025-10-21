package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StatsDisplay extends HBox {

    private Label labelEnergia;
    private Label labelOleada;
    private Label labelVidaNucleo;

    public StatsDisplay() {
        labelEnergia = new Label("Energía: 0");
        labelOleada = new Label("Oleada: 0");
        labelVidaNucleo = new Label("Vida Núcleo: 100");

        labelEnergia.getStyleClass().add("stats-label");
        labelOleada.getStyleClass().add("stats-label");
        labelVidaNucleo.getStyleClass().add("stats-label-vital");

        this.setSpacing(20);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10, 15, 10, 15));
        this.getStyleClass().add("stats-bar"); // Estilo para la barra de fondo

        this.getChildren().addAll(labelEnergia, labelOleada, labelVidaNucleo);
    }


    public void actualizarStats(int energia, int oleada, int vidaNucleo) {
        labelEnergia.setText("Energía: " + energia);
        labelOleada.setText("Oleada: " + oleada);
        labelVidaNucleo.setText("Vida Núcleo: " + vidaNucleo);
    }
}