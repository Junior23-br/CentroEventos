package co.edu.uniquindio.edu.co.centroeventosuq.controller;


import co.edu.uniquindio.edu.co.centroeventosuq.controller.service.Observer;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Evento;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.io.IOException;

public class InicioController implements Observer {

    @FXML
    public Text txtTitulo;
    @FXML
    private TableColumn<Evento, String> ctvFecha;

    @FXML
    private TableColumn<Evento, String> ctvHoraInicio;

    @FXML
    private TableColumn<Evento, String> ctvIdEvento;

    @FXML
    private TableColumn<Evento, String> ctvLugar;

    @FXML
    private TableColumn<Evento, String> ctvVoletasDisponibles;
    @FXML
    private TableColumn<Evento, String> ctvNombre;

    @FXML
    private TableView<Evento> tvEventos;

    ControladorPrincipal controladorPrincipal;
    @FXML
    private void initialize() throws IOException {
        controladorPrincipal=ControladorPrincipal.getInstance();
        controladorPrincipal.setInicioController(this);
        llenarTabla();
        formatearColumnas();

    }

    private void formatearColumnas() {
        ctvFecha.setCellValueFactory(cellData->new SimpleStringProperty(String.valueOf(cellData.getValue().getFecha())));
        ctvNombre.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getNombre()));
        ctvLugar.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getLugar()));
        ctvIdEvento.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getIdEvento()));
        ctvHoraInicio.setCellValueFactory(cellData->new SimpleStringProperty(String.valueOf(cellData.getValue().getHoraInicioEvento())));
        ctvVoletasDisponibles.setCellValueFactory(cellData->new SimpleStringProperty(String.valueOf(cellData.getValue().numeroVoletasTotales())));
    }

    public void llenarTabla() {
        tvEventos.getItems().clear();
        tvEventos.getItems().addAll(controladorPrincipal.getCentroEventos().getEventos());
    }

    @FXML
    void ingresar(ActionEvent event) {
       FXMLLoader loader= controladorPrincipal.navegar("login.fxml");
       LoginController controller= loader.getController();
       controller.incializarObserver(this);
    }

    @FXML
    void registrar(ActionEvent event) {
        FXMLLoader loader= controladorPrincipal.navegar("RegistroUsuario.fxml");

    }

    @Override
    public void notificar() {
        controladorPrincipal.cerrarVentana(txtTitulo);
    }
}
