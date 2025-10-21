package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class EngineerShop extends VBox {

    private ToggleGroup toggleGroup;
    private String ingenieroSeleccionado = null;

    public EngineerShop() {
        this.toggleGroup = new ToggleGroup();

        Label titulo = new Label("Estaciones");
        titulo.getStyleClass().add("shop-title");

        ToggleButton btnSistemas = new ToggleButton("Ing. Sistemas (75 E)");
        btnSistemas.setToggleGroup(toggleGroup);
        btnSistemas.setUserData("SISTEMAS");
        btnSistemas.getStyleClass().add("shop-button");
        btnSistemas.setPrefWidth(180);

        ToggleButton btnCivil = new ToggleButton("Ing. Civil (100 E)");
        btnCivil.setToggleGroup(toggleGroup);
        btnCivil.setUserData("CIVIL");
        btnCivil.getStyleClass().add("shop-button");
        btnCivil.setPrefWidth(180);

        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                ingenieroSeleccionado = newToggle.getUserData().toString();
                System.out.println("Tienda: Seleccionado " + ingenieroSeleccionado);
            } else {
                ingenieroSeleccionado = null;
            }
        });

        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_CENTER);
        this.getStyleClass().add("shop-pane");
        this.setPrefWidth(200);

        this.getChildren().addAll(titulo, btnSistemas, btnCivil);
    }

    public String getIngenieroSeleccionado() {
        return ingenieroSeleccionado;
    }

    public void deseleccionar() {
        if (toggleGroup.getSelectedToggle() != null) {
            toggleGroup.getSelectedToggle().setSelected(false);
        }
    }
}