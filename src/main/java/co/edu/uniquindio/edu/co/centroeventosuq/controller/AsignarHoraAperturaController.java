package co.edu.uniquindio.edu.co.centroeventosuq.controller;

import co.edu.uniquindio.edu.co.centroeventosuq.model.Evento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AsignarHoraAperturaController {

    @FXML
    private ComboBox<String> cbHoraApertura;

    @FXML
    private TextField textFIdEvento;
    ControladorPrincipal controladorPrincipal;

    Evento eventoSelecionado;
    @FXML
    private void initialize() throws IOException {
        controladorPrincipal=ControladorPrincipal.getInstance();
        llenarComboxHora();
    }

    private void llenarComboxHora() {
        cbHoraApertura.getItems().addAll(generarHorasDelDia());
    }

    private List<String> generarHorasDelDia() {
        List<String> horasDelDia = new ArrayList<>();

        // Define el formato de hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Genera las 24 horas del día
        for (int hora = 0; hora < 24; hora++) {
            for (int minuto = 0; minuto < 60; minuto += 15) { // Incrementa los minutos en 15
                LocalTime horaActual = LocalTime.of(hora, minuto);
                horasDelDia.add(horaActual.format(formatter));
            }
        }

        return horasDelDia;
    }

    public void inicializarDatos(Evento evento){
        this.eventoSelecionado=evento;
        textFIdEvento.setText(evento.getIdEvento());
    }


    @FXML
    void asignarHora(ActionEvent event) {
        if (cbHoraApertura.getValue() != null) {
            LocalTime horaApertura = LocalTime.parse(cbHoraApertura.getValue());
            LocalTime horaInicioEvento = LocalTime.parse(eventoSelecionado.getHoraInicioEvento());

            if (horaApertura.isBefore(horaInicioEvento.minusHours(1))) {
                if (controladorPrincipal.asignarHoraAperturaEvento(eventoSelecionado.getIdEvento(), cbHoraApertura.getValue())) {
                    controladorPrincipal.mostrarAlerta("Hora agregada correctamente", Alert.AlertType.INFORMATION);
                    controladorPrincipal.cerrarVentana(textFIdEvento);
                } else {
                    controladorPrincipal.mostrarAlerta("No se pudo asignar la hora de apertura", Alert.AlertType.INFORMATION);
                }
            } else if (horaApertura.isAfter(horaInicioEvento.minusHours(1)) && horaApertura.isBefore(horaInicioEvento)) {
                controladorPrincipal.mostrarAlerta("No puedes iniciar la taquilla 1 hora o menos antes de la hora del evento. Debe ser antes de " + horaInicioEvento.minusHours(1).toString(), Alert.AlertType.INFORMATION);
            } else {
                controladorPrincipal.mostrarAlerta("No se puede abrir la taquilla después de la hora de inicio", Alert.AlertType.INFORMATION);
            }
        } else {
            controladorPrincipal.mostrarAlerta("Debe ingresar una hora para abrir la taquilla", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
        controladorPrincipal.cerrarVentana(textFIdEvento);
    }


}
