package co.edu.uniquindio.edu.co.centroeventosuq.controller;



import co.edu.uniquindio.edu.co.centroeventosuq.controller.service.Observer;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Evento;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.LocalDate;

public class PanelAdministrador implements Observer {

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
        controladorPrincipal.setPanelAdministrador(this);
        controladorPrincipal.inicializarObersevado(this);
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

    private void llenarTabla() {
        tvEventos.getItems().clear();
        tvEventos.getItems().addAll(controladorPrincipal.getCentroEventos().getEventos());
    }

    @FXML
    void agregar(ActionEvent event) {
        FXMLLoader loader= controladorPrincipal.navegar("CrearShow.fxml");
        CrearEventoController controller=loader.getController();
        controller.inicializar(this);
    }

    @FXML
    void eliminar(ActionEvent event) {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem();
        if(evento!=null){
            if(controladorPrincipal.eliminarEvento(evento)){
                controladorPrincipal.mostrarAlerta("evento eliminado correctamente", Alert.AlertType.INFORMATION);
                notificar();
            }else {
                controladorPrincipal.mostrarAlerta("el evento no pudo ser eliminado", Alert.AlertType.INFORMATION);
            }
        }
    }

    @Override
    public void notificar() {
        llenarTabla();
    }

    @FXML
    public void activarTaquilla(ActionEvent actionEvent) {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem();
        if(evento!=null){
            if(LocalDate.parse(evento.getFecha()).equals(LocalDate.now())){
                FXMLLoader loader= controladorPrincipal.navegar("asignarHoraApertura.fxml");
                AsignarHoraAperturaController controller= loader.getController();
                controller.inicializarDatos(evento);
            }else {
                controladorPrincipal.mostrarAlerta("todavia no es el dia del evento", Alert.AlertType.INFORMATION);
            }
        }else {
            controladorPrincipal.mostrarAlerta("debes selecionar un evento para poder activar su taquilla", Alert.AlertType.ERROR);
        }
    }
    @FXML
    void cerrarSeccion(ActionEvent event) {
        controladorPrincipal.navegar("Inicio.fxml");
        controladorPrincipal.cerrarSeccion();
        controladorPrincipal.cerrarVentana(tvEventos);
    }

    public void verHoraApertura(ActionEvent actionEvent) {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem();
        if(evento!=null){
            if(evento.getHoraAperturaTaquilla()!=null){
                controladorPrincipal.mostrarAlerta("la hora de apertura es "+evento.getHoraAperturaTaquilla(), Alert.AlertType.INFORMATION);
            }else {
                controladorPrincipal.mostrarAlerta("al evento no se le ha asignado una hora de apertura", Alert.AlertType.INFORMATION);
            }
        }else {
            controladorPrincipal.mostrarAlerta("debe selecionar un evento para ver su hora de apertura", Alert.AlertType.INFORMATION);
        }
    }

    public void cerrarTaquilla(ActionEvent actionEvent) throws IOException {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem();
        if(evento!=null){
            evento.setTaquillaAbierta(false);
            controladorPrincipal.getCentroEventos().guardarDatos();
        }
    }
}

