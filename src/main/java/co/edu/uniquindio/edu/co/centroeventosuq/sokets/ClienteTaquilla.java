package co.edu.uniquindio.edu.co.centroeventosuq.sokets;

import co.edu.uniquindio.edu.co.centroeventosuq.controller.ControladorPrincipal;
import co.edu.uniquindio.edu.co.centroeventosuq.model.Usuario;

import java.io.*;
import java.net.Socket;

public class ClienteTaquilla implements Runnable {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    private Usuario usuarioAsociado;

    public Usuario getUsuarioAsociado() {
        return usuarioAsociado;
    }

    public void setUsuarioAsociado(Usuario usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
    }

    public void conectar(String servidorHost, int puertoServidor) {
        try {
            socket = new Socket(servidorHost, puertoServidor);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarSolicitudCompra(String idEvento) {
//        conectar("localhost", 8081);  // Conectar antes de enviar solicitud
        try {
            salida.writeUTF("comprar:" + idEvento);
            salida.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalizarCompra(String idEvento) {
        try {
            salida.writeUTF("liberar:" + idEvento);
            salida.flush();
            String respuesta = entrada.readUTF();
            if (respuesta.equals("espacio liberado")) {
                System.out.println("Espacio liberado en la taquilla del evento " + idEvento);
            } else {
                System.out.println("Error al liberar espacio: " + respuesta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    public void cerrarConexion() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
            System.out.println("Se desconectó del servidor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int pedirPosiconCola(String idEvento) {
        try {
            salida.writeObject("Posicion:"+idEvento);
            salida.flush();
            System.out.println("si se envia");
        }catch (IOException e){
            System.out.println("es por aca que llega?");
            return 0;
        }
        return 0;
    }

    @Override
    public void run() {
        try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
            while (true){
                try {
                    Object object= entrada.readObject();
                    System.out.println("si leyo");
                    if(object instanceof String){
                        String respuesta=(String) object;
                        System.out.println("si lee el objeto acaaaaaaaaa");
                        if(respuesta.startsWith("Agregado:")){
                            int poscion=Integer.parseInt(respuesta.substring(9));
                            System.out.println("ingreso aca"+poscion);

                            ControladorPrincipal.getInstance().setPosicionCola(poscion);
                        } else if(respuesta.equals("Comprando")){
                            System.out.println("esta en la cola");
                            ControladorPrincipal.getInstance().setPosicionCola(0);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}