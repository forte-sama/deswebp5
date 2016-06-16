/**
 * Created by forte on 15/06/16.
 */

//abriendo el websocket solo cuando se cargue el documento
var ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/forte");

$(document).ready(function() {

    //indicando eventos:
    ws.onmessage = function(mensaje) {
        alert(mensaje.data);
    };
    ws.onopen = function(e) {
        console.log("Conectado - status " + this.readyState);
    };
    ws.onclose = function(e) {
        console.log("Desconectado - status " + this.readyState);
    };

    $("#chat_init").click(function(event) {
        event.preventDefault();

        // ws.send("iniciar_chat_lector~prueba");
    });

    $("#send_message").click( function(event) {
        var mensaje = $("#new_message");

        ws.send("mensaje_lector~" + mensaje.val());

        mensaje.val("");
    });

    $("#connect_as").click(function(event) {
        var mensaje = $("#new_message");

        ws.send("iniciar_chat_lector~" + mensaje.val());
        
        mensaje.val("");
    });

});
