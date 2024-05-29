package co.edu.uniquindio.edu.co.centroeventosuq.controller;

import co.edu.uniquindio.edu.co.centroeventosuq.controller.service.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    public TextField textFCorreo;
    @FXML
    public PasswordField textFContraseña;
    ControladorPrincipal controladorPrincipal;
    @FXML
    private void initialize() throws IOException {
        controladorPrincipal= ControladorPrincipal.getInstance();
    }
    Observer observer;
    public void incializarObserver(Observer inicioController) {
        this.observer=inicioController;
    }


    public void Logear(ActionEvent actionEvent) {
        int caso= controladorPrincipal.verificar(textFCorreo.getText(),textFContraseña.getText());
        switch (caso) {
            case 1:
                FXMLLoader loader= controladorPrincipal.navegar("CatalogoEventos.fxml");
                observer.notificar();
                controladorPrincipal.cerrarVentana(textFContraseña);
                break;
            case 2:
                FXMLLoader loader2= controladorPrincipal.navegar("panelAdministrador.fxml");
                observer.notificar();
                controladorPrincipal.cerrarVentana(textFContraseña);
                break;
            case 0:
                System.out.println("no existe");
        }


    }

    public void cancelar(ActionEvent actionEvent) {
        controladorPrincipal.cerrarVentana(textFContraseña);
    }

}
