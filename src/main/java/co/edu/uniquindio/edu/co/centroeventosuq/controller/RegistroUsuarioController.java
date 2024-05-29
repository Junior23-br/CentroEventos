package co.edu.uniquindio.edu.co.centroeventosuq.controller;

import co.edu.uniquindio.edu.co.centroeventosuq.model.Usuario;
import co.edu.uniquindio.edu.co.centroeventosuq.model.enums.TipoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistroUsuarioController {

    @FXML
    private TextField textFContrasena;

    @FXML
    private TextField textFCorreo;

    @FXML
    private TextField textFDireccion;

    @FXML
    private TextField textFNombre;

    @FXML
    private ComboBox<TipoUsuario> cbTipoUsuario;
    ControladorPrincipal controladorPrincipal;
    @FXML
    private void  initialize() throws IOException {
        controladorPrincipal=ControladorPrincipal.getInstance();
        llenarCombox();
    }

    private void llenarCombox() {
        cbTipoUsuario.getItems().addAll(TipoUsuario.values());
    }

    @FXML
    void registrar(ActionEvent event) {
        Usuario usuario = ConstruirUsuario();
        if(usuario !=null){
            int caso=  controladorPrincipal.registrarUsuario(usuario);
            switch (caso){
                case 1:
                    System.out.println(" el correo ya existe no se agrego el usuario");
                    break;
                case 2:
                    System.out.println("se agrego correctamente el usuario");
                    controladorPrincipal.cerrarVentana(textFContrasena);
            }

        }
        }


    private Usuario ConstruirUsuario() {
        return new Usuario(textFNombre.getText(),textFCorreo.getText(),textFContrasena.getText(),cbTipoUsuario.getValue());
    }

}


