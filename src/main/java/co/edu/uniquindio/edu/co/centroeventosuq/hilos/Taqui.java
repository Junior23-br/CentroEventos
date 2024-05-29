package co.edu.uniquindio.edu.co.centroeventosuq.hilos;

import co.edu.uniquindio.edu.co.centroeventosuq.controller.ControladorPrincipal;
import co.edu.uniquindio.edu.co.centroeventosuq.sokets.ServerTaquillas;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public class Taqui implements Runnable{
    private Cola colaTaquilla;
    private final Semaphore semaphore;
    private String idSoket;
    private String idEvento;
    private ServerTaquillas server;
    private LocalTime horaApertura;
    private LocalDate fecha;
    private boolean open;
    private  CountDownLatch latch;

    public Taqui(Semaphore semaphore, String idSoket, String idEvento, ServerTaquillas server,String fecha,String hora,Cola cola,CountDownLatch countDownLatch) {
        this.latch= countDownLatch;
        this.semaphore = semaphore;
        this.colaTaquilla=cola;
        this.idSoket=idSoket;
        this.idEvento= idEvento;
        this.server= server;
        this.fecha=LocalDate.parse(fecha);
        this.horaApertura= LocalTime.parse(hora);
        this.open=false;


    }

    public boolean existeBoletas() throws IOException {
        return ControladorPrincipal.getInstance().obtenerEvento(this.idEvento).numeroVoletasTotales()==0;
    }

    public boolean faltaUnaHoraParaElEvento() {
        LocalTime ahora = LocalTime.now();
        return ahora.isAfter(horaApertura.minusHours(1)) && ahora.isBefore(horaApertura);
    }


//    @Override
//    public void run() {
//        try {
//            System.out.println("entro en el semáforo " + this.hashCode());
//            System.out.println("el soket " + idSoket + " entró en la cola de " + idEvento);
//            this.colaTaquilla.addCola(idSoket);
//            latch.countDown();
//            semaphore.acquire();
//            System.out.println("el soket " + idSoket + " está comprando " + idEvento);
//            Thread.sleep(200000); // Espera de 200 ms
//            semaphore.release(); // Libera el semáforo
//            this.colaTaquilla.removeCola(idSoket);
//            System.out.println("terminó de comprar ");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void run() {
        try {
            if(existeBoletas()){ //verificamos si quedan 0 boletas)
                ControladorPrincipal.getInstance().obtenerEvento(this.idEvento).setTaquillaAbierta(false);
                ControladorPrincipal.getInstance().getCentroEventos().guardarDatos();

            }
            if(faltaUnaHoraParaElEvento()){ //verificamos si ya falta 1 hora para el evento
                ControladorPrincipal.getInstance().obtenerEvento(this.idEvento).setTaquillaAbierta(false);
                ControladorPrincipal.getInstance().getCentroEventos().guardarDatos();
            }
            //en caso de que falle ninguna de esas cosas
            this.colaTaquilla.addCola(idSoket);  //agregamos al objeto cola la id del soket que queremos que compre
            latch.countDown();            //notificamos que ya puede averiguar la posicion debido a que se agrego
            semaphore.acquire(); //el semaforo accepta el hilo
            Thread.sleep(200000); // Esperamos el tiempo que se crea pruedente para realizar la compra
            semaphore.release(); // Libera el semáforo
            this.colaTaquilla.removeCola(idSoket); //sacamos de la cola a los que ya terminaron de comprar

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void liberar() {

    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean getOpen(){
        return open;
    }


    public int getPersonasEnCola() {
        return this.colaTaquilla.getIdSokets().size();
    }

    public int obtenerPocionActual(String idSoket){
        return this.colaTaquilla.getIdSokets().indexOf(idSoket)+1;
    }


}
