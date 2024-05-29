package co.edu.uniquindio.edu.co.centroeventosuq.sokets;

import co.edu.uniquindio.edu.co.centroeventosuq.controller.ControladorPrincipal;
import co.edu.uniquindio.edu.co.centroeventosuq.hilos.Cola;
import co.edu.uniquindio.edu.co.centroeventosuq.hilos.XMLFileMonitor;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Evento;
import co.edu.uniquindio.edu.co.centroeventosuq.utils.Persistencia;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ServerTaquillas {

    private final ConcurrentHashMap<String, Socket> listaSokets;
    private final ConcurrentHashMap<String, Cola> listaDeColas;
    private ArrayList<Evento> eventos = new ArrayList<>();

    public ServerTaquillas() throws IOException {
        listaSokets = new ConcurrentHashMap<>();
        listaDeColas= new ConcurrentHashMap<>();
        obtenerEventos();
        XMLFileMonitor xmlFileMonitor = new XMLFileMonitor("src/main/resources/Persistencia/Evento.xml", this);
        xmlFileMonitor.monitor();
    }

    public void obtenerEventos() throws IOException {
        eventos = Persistencia.cargarEventosXML();
        System.out.println(eventos.size());
    }

    public void start(int puerto) {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de taquillas iniciado en el puerto " + puerto + "...");
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket);
                listaSokets.put(String.valueOf(clienteSocket.hashCode()), clienteSocket);
                new Thread(new ClientHandler(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clienteSocket;

        public ClientHandler(Socket socket) {
            this.clienteSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream flujoSalida = new ObjectOutputStream(clienteSocket.getOutputStream());
                 ObjectInputStream flujoEntrada = new ObjectInputStream(clienteSocket.getInputStream())) {
                while (true) {

                    Object object= flujoEntrada.readObject();
                    String comando= (String) object;
                    procesarComando(comando,flujoSalida);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void procesarComando(String comando, ObjectOutputStream flujoSalida) throws IOException, InterruptedException {
            if (comando.startsWith("comprar:")) {
                manejarCompra(comando, flujoSalida);
            } else if (comando.startsWith("liberar:")) {
                manejarLiberacion(comando, flujoSalida);
            } else if (comando.startsWith("Posicion:")) {
                manejarPosicion(comando, flujoSalida);
            } else if (comando.equals("salir")) {
                listaSokets.remove(String.valueOf(clienteSocket.hashCode()));
            } else {
                flujoSalida.writeUTF("Comando no reconocido");
                flujoSalida.flush();
            }
        }

        private void manejarCompra(String comando, ObjectOutputStream flujoSalida) throws IOException, InterruptedException {
            String idEvento = comando.substring(8);
            Evento evento = ControladorPrincipal.getInstance().obtenerEvento(idEvento);
            if (evento != null) {
                CountDownLatch latch = new CountDownLatch(1);
                evento.ingresarTaquilla(clienteSocket.hashCode(), ServerTaquillas.this, latch);
                if(!esClaveUsada(evento.getIdEvento())){
                    listaDeColas.put(evento.getIdEvento(),evento.getCola());
                }
                listaDeColas.get(evento.getIdEvento()).addCola(String.valueOf(clienteSocket.hashCode()));
                if (listaDeColas.get(idEvento).getIdSokets().size() <= 1) {
                    System.out.println("hay "+listaDeColas.get(idEvento).getIdSokets().size());
                    flujoSalida.writeObject("Comprando");
                } else {
                    int posicionActual = listaDeColas.get(idEvento).getIdSokets().indexOf(String.valueOf(clienteSocket.hashCode()));
                    System.out.println("la posicion es"+posicionActual);
                    flujoSalida.writeObject("Agregado:" + posicionActual);
                }
                flujoSalida.flush();
            } else {
                flujoSalida.writeObject("Evento no encontrado: " + idEvento);
                flujoSalida.flush();
            }
        }
        public boolean esClaveUsada(String clave) {
            return listaDeColas.containsKey(clave);
        }


        private void manejarLiberacion(String comando, ObjectOutputStream flujoSalida) throws IOException {
            String idEvento = comando.substring(8);
            Evento evento = ControladorPrincipal.INSTANCE.obtenerEvento(idEvento);
            if (evento != null) {
                evento.getTaqui().liberar();
                flujoSalida.writeObject("espacio liberado");
            } else {
                flujoSalida.writeObject("Evento no encontrado: " + idEvento);
            }
            flujoSalida.flush();
        }

        private void manejarPosicion(String comando, ObjectOutputStream flujoSalida) throws IOException {
            String idEvento = comando.substring(9);
            int resultado =listaDeColas.get(idEvento).getIdSokets().indexOf(String.valueOf(clienteSocket.hashCode()));
            if (resultado <= 3) {
                flujoSalida.writeUTF("Comprando");
            } else {
                flujoSalida.writeUTF("Agregado:" + resultado);
            }
            flujoSalida.flush();
        }
    }

    public void enviarPersonasEspera(int hashCode, int numeroEspera) {
        Socket clienteSocket = listaSokets.get(String.valueOf(hashCode));
        try (ObjectOutputStream salida = new ObjectOutputStream(clienteSocket.getOutputStream())) {
            String comando = "personasEspera:" + numeroEspera;
            salida.writeObject(comando);
            salida.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerTaquillas serverTaquillas = new ServerTaquillas();
        serverTaquillas.start(8081);
    }
}