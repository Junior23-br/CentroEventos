package co.edu.uniquindio.edu.co.centroeventosuq.controller;


import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class VentanEspera {
    @FXML
    public AnchorPane archonCerrar;
    ControladorPrincipal controladorPrincipal;
    @FXML
    private void initialize(){
        try {
            controladorPrincipal=ControladorPrincipal.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Text txtPersonaEnCola;


    @FXML
    private TextField textFCerrar;


    public void ponerPersonaEspera(int posicionCola) {
        txtPersonaEnCola.setText(String.valueOf(posicionCola));
    }

    public void cerrarVentan() {
        controladorPrincipal.cerrarVentana(archonCerrar);
    }
}

