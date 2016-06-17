/**
 * Created by forte on 15/06/16.
 */

//abriendo el websocket solo cuando se cargue el documento
var ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/forte");

$(document).ready(function() {

    //indicando eventos:
    ws.onopen = function(e) {
        console.log("Conectado - status " + this.readyState);
    };
    ws.onclose = function(e) {
        console.log("Desconectado - status " + this.readyState);
    };
    ws.onmessage = function(mensaje) {
        //crear elemento para visualizar el mensaje
        var new_message_item = $("#dummy_mensaje_admin").clone();
        new_message_item.removeClass("hidden");
        new_message_item.find(".cuerpo").html(mensaje.data);

        //agregar nuevo elemento de mensaje al cuerpo del chat
        $("#chat_box_body").append(new_message_item);
    };

    //desactivar boton de envio de mensajes
    $("#send_message").attr("disabled",true);

    //evento de enviar mensaje cuando se habilite
    $("#send_message").click( function(event) {
        //capturar elemento donde esta el mensaje
        var mensaje = $("#new_message");

        //enviar mensaje del tipo correspondiente al admin
        ws.send("mensaje_lector~" + mensaje.val());

        //crear elemento para visualizar el mensaje
        var new_message_item = $("#dummy_mensaje_lector").clone();
        new_message_item.removeClass("hidden");
        new_message_item.find(".cuerpo").html(mensaje.val());

        //agregar nuevo elemento de mensaje al cuerpo del chat
        $("#chat_box_body").append(new_message_item);

        //limpiar input donde estaba el mensaje
        mensaje.val("");
    });

    //evento de conectar como usuario X
    $("#connect_as").click(function(event) {
        //capturar elemento donde esta el nombre
        var chat_user = $("#chat_user");

        //enviar mensaje para iniciar conexion
        ws.send("iniciar_chat_lector~" + chat_user.val());
        //desactivar input donde para no conectarse como nadie mas
        chat_user.attr("disabled",true);
        //cambiar mensajes del boton recien presionado
        $(this).html("Conectado");
        $(this).attr("disabled",true);

        //habilitar boton de enviar mensaje
        $("#send_message").attr("disabled",false);
    });
});
