package wrappers.realtime;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by forte on 14/06/16.
 */

@WebSocket
public class WebSocketHandler {
    private static String  usuarioActivo = null;
    private static Session sesionUsuario = null;
    private static HashMap<String,Session> lectores = new HashMap<>();

    @OnWebSocketConnect
    public void conectar(Session usuario) {
        String user_location = usuario.getLocalAddress().getAddress().toString();
        System.out.println("Conectando Usuario: " + user_location);
    }

    @OnWebSocketClose
    public void cerrar(Session usuario, int statusCode, String reason) {
        String user_location = usuario.getLocalAddress().getAddress().toString();
        System.out.println("Desconectando el usuario: " + user_location);
    }

    @OnWebSocketMessage
    public void mensaje(Session usuario, String mensaje) {
        String user_location = usuario.getLocalAddress().getAddress().toString();
        System.out.println("Recibiendo del cliente: " + user_location + " - Mensaje: " + mensaje);

        String respuesta;

        //dividir mensaje en bloques, para determinar que tipo de mensaje es
        String[] msj = mensaje.split("~");

        switch (msj[0]) {
            case "iniciar_chat_autor":
                respuesta = "se inicio el chateador";
                conectarAutor(msj[1].trim(),usuario);
                break;
            case "iniciar_chat_lector":
                respuesta = "se conecto el lector";
                conectarLector(msj[1].trim(),usuario);
                enviarMensaje(usuario,respuesta);
                break;
            case "mensaje_lector":
                enviarMensaje(sesionUsuario,msj[1].trim());
                break;
            case "mensaje_autor":
                enviarMensaje(lectores.get(msj[1].trim()),msj[2].trim());
                break;
            case "despedir_lector":
                respuesta = "se fue un lector";
                enviarMensaje(sesionUsuario,respuesta);
                break;

            default:
                respuesta = "Tipo de mensaje invalido.. Bai";
                enviarMensaje(usuario,respuesta);
        }

//        try {
//            usuario.getRemote().sendString(respuesta);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        if(mensaje.contains("registrar")) {
//            if (mensaje.contains("autor")) {
//                autoresActivos.put(mensaje.split(" ")[2], usuario);
//            } else if (mensaje.contains("lector")) {
//                Session autor = autoresActivos.get(mensaje.split(" ")[2]);
//                lectoresActivos.put(usuario,autor);
//            }
//        }
//        else {
//            try {
//                lectoresActivos.get(usuario).getRemote().sendString(mensaje);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        //Enviar un simple mensaje al cliente que mando al servidor..
//        try {
//            usuario.getRemote().sendString(li("Cliente, tu mensaje se recibio, me dijiste '" + mensaje + "' ?").render());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void conectarLector(String user, Session userSession) {
        lectores.put(user,userSession);
    }

    public boolean enviarMensaje(Session target, String mensaje) {
        boolean exito;

        try {
            target.getRemote().sendString(mensaje);

            exito = true;
        } catch (IOException e) {
            e.printStackTrace();

            exito = false;
        }

        return exito;
    }

    public static String getUsuarioActivo() {
        return usuarioActivo;
    }

    private static void conectarAutor(String user, Session sesion) {
        usuarioActivo = user;
        sesionUsuario = sesion;
    }

    public static void desconectar(String user) {
        if(user.contentEquals(usuarioActivo)) {
            usuarioActivo = null;
            sesionUsuario = null;
        }
    }
}

