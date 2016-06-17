package wrappers.realtime;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by forte on 14/06/16.
 */

@WebSocket
public class WebSocketHandler {
    private static String  usuarioActivo = null;
    private static Session sesionUsuario = null;
    private static HashMap<String,Session> lectoresSesiones = new HashMap<>();
    private static HashMap<Session,String> sesionesLectores = new HashMap<>();

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
                conectarLector(msj[1].trim(),usuario);
                enviarMensaje(usuario,"Ya estas conectado.");
                break;
            case "mensaje_lector":
                enviarMensajeAdmin(usuario,sesionesLectores.get(usuario) + "~" + msj[1].trim());
                break;
            case "mensaje_autor":
                enviarMensaje(lectoresSesiones.get(msj[1].trim()),msj[2].trim());
                break;
            case "despedir_lector":
                respuesta = "se fue un lector";
                enviarMensaje(sesionUsuario,respuesta);
                break;

            default:
                respuesta = "Tipo de mensaje invalido.. Bai";
                enviarMensaje(usuario,respuesta);
        }
    }

    private void conectarLector(String user, Session userSession) {
        lectoresSesiones.put(user,userSession);
        sesionesLectores.put(userSession,user);
    }

    private void enviarMensajeAdmin(Session sesionLector, String mensaje) {
        try {
            if(sesionLector != null) {
                if(sesionUsuario == null) {
                    sesionLector.getRemote().sendString("El admin no esta disponible.");
                }
                else {
                    sesionUsuario.getRemote().sendString(mensaje);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(Session target, String mensaje) {
        try {
            if(target != null) {
                target.getRemote().sendString(mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsuarioActivo() {
        return usuarioActivo;
    }

    private static void conectarAutor(String user, Session sesion) {
        usuarioActivo = user;
        sesionUsuario = sesion;
    }

    public static void desconectar(String user) {
        if(user != null && usuarioActivo != null) {
            if (user.contentEquals(usuarioActivo)) {
                usuarioActivo = null;
                sesionUsuario = null;
            }
        }
    }
}

