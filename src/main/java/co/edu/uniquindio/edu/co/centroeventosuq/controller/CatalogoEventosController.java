package co.edu.uniquindio.edu.co.centroeventosuq.controller;

import co.edu.uniquindio.edu.co.centroeventosuq.controller.service.Encolado;
import co.edu.uniquindio.edu.co.centroeventosuq.controller.service.Observer;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Compra;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Evento;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalTime;

public class CatalogoEventosController implements Observer{

    public TableColumn<Compra,String> ctvIdEvento2;
    public TableColumn<Compra,String> ctvTotalCompra;
    public TableColumn<Compra,String> ctvCodioCompra;
    public TableColumn<Compra,String> ctvMetodoPago;
    public TableView<Compra> tvCompras;
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

    @FXML
    private DatePicker dpFecha;

    ControladorPrincipal controladorPrincipal;

    @FXML
    private void initialize() throws IOException {
        controladorPrincipal=ControladorPrincipal.getInstance();  //instanciamos el controlador principal
        controladorPrincipal.setCatalogoEventosController(this);  //inicializamos el contolador tipo CatalogoEventosController del controladorPrincipal
        controladorPrincipal.inicializarObersevado(this); ///????? no se pa que usted ya (lo quitare)
        formatearColumnas(); // formateamos las columnas de los eventos
        formatearColumnasCompra(); // formateamos las columnas de las compras que ha realizado el cliente
        llenarTabla(); // obtenemos los elementos de la tabla de eventos
        llenarTablaCompras(); //obtenemos los elementos de la tabla de compras
    }

    private void llenarTablaCompras() {
        tvCompras.getItems().clear(); //limpiamos los elementos de la lista para que al momento de agregarlos no se dupliquen
        tvCompras.getItems().addAll(controladorPrincipal.obtenerComprasUsuarioLogeado()); //obtenemos los elementos del modelo
    }

    private void formatearColumnasCompra() {
        ctvIdEvento2.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getEvento().getIdEvento()));
        ctvMetodoPago.setCellValueFactory(cellData->new SimpleStringProperty(String.valueOf(cellData.getValue().getMetodoPago())));
        ctvCodioCompra.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getCodigoCompra()));
        ctvTotalCompra.setCellValueFactory(cellData-> new SimpleStringProperty(String.valueOf(cellData.getValue().calcularPrecio())));
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
        tvEventos.getItems().clear(); //limpiamos los elementos de la tabla para evitar que esten duplicados
        tvEventos.getItems().addAll(controladorPrincipal.getCentroEventos().getEventos()); //obtenemos la lista de eventos del centro de eventos
    }

    @FXML
    void buscarEvento(ActionEvent event) {
        tvEventos.getItems().clear(); //limpiamos los elementos para evitar que se dupliquen
        tvEventos.getItems().addAll(controladorPrincipal.filtrarEvetos(dpFecha.getValue())); //agregamos los elementos que fueron filtrados atravez de la fecha
    }

    @FXML
    void limpiarCampos(ActionEvent event) {
        dpFecha.setValue(null);
        dpFecha.setPromptText("ingresar la fecha por la que desea filtrar");
        llenarTabla();
    }

    @FXML
    void verPrecioBoletas(ActionEvent event) {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem(); //obtenemos el evento que se selecione al momento de tocar la tabla
        if(evento!=null){  //verificamos que si se halla escogido algun elemento
            FXMLLoader loader= controladorPrincipal.navegar("PrecioBoletas.fxml");
            PrecioBoletasController controller= loader.getController();
            controller.cambiarPrecios(evento);
        }else {
            controladorPrincipal.mostrarAlerta("debe selecionar un evento para ver el precio de las boletas", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void verPresentadores(ActionEvent event) {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem();//obtenemos el evento que se selecione al momento de tocar la tabla
        if(evento!=null){//verificamos que si se halla escogido algun elemento
            FXMLLoader loader= controladorPrincipal.navegar("InformacionDelPersonal.fxml");
            InformacionDelPersonalController controller=loader.getController();
            controller.llenarLista(evento);
        }else {
            controladorPrincipal.mostrarAlerta("debe elegir un evento para ver los participantes", Alert.AlertType.INFORMATION);
        }

    }


    @FXML
    void comprarBoleta(ActionEvent event) throws IOException {
        Evento evento= tvEventos.getSelectionModel().getSelectedItem(); //obtenemos el evento que se selecione al momento de tocar la tabla
            if(evento!=null){ //verificamos que si se halla escogido algun elemento
                if (evento.getHoraAperturaTaquilla() != null) {//verificamos si al evento ya le han asignado el atributo HoraApertura
                    if(!evento.isTaquillaAbierta()){
                        LocalTime horaAperturaTaquilla = LocalTime.parse(evento.getHoraAperturaTaquilla());  //obtenemos la hora guardada
                        LocalTime ahora = LocalTime.now(); //obtenemos la hora actual
                        if (ahora.equals(horaAperturaTaquilla) || ahora.isAfter(horaAperturaTaquilla)) { //verificamos la hora actual es igual o mayor a la hora de apertura
                            evento.setTaquillaAbierta(true); //cambiamos el estado de la taquilla
                            controladorPrincipal.getCentroEventos().guardarDatos();
                        }
                    }
                }
                if (evento.isTaquillaAbierta()) {
                    FXMLLoader loader = controladorPrincipal.navegar("comprarBoletas.fxml");
                    ComprarBoletasController controller = loader.getController();
                    controller.inicializarDatos(controladorPrincipal.getCentroEventos().getUsuarioLogeado().getCorreo(), evento.getIdEvento(), this);
                    controller.encolarCompra(evento);
                } else {
                    if (evento.getHoraAperturaTaquilla() != null) {
                        controladorPrincipal.mostrarAlerta("La taquilla abre a las: " + evento.getHoraAperturaTaquilla(), Alert.AlertType.INFORMATION);
                    } else {
                        controladorPrincipal.mostrarAlerta("Lo sentimos, el evento todavía no abre su taquilla", Alert.AlertType.INFORMATION);
                    }
                }
            } else {
                controladorPrincipal.mostrarAlerta("no seleciono ningun evento", Alert.AlertType.INFORMATION);
            }


    }

    @FXML
    void cerrarSeccion(ActionEvent event) {
        controladorPrincipal.navegar("Inicio.fxml");
        controladorPrincipal.cerrarSeccion();
        controladorPrincipal.cerrarVentana(tvEventos);
    }

    @Override
    public void notificar() {
        llenarTablaCompras();
        llenarTabla();
    }


}
