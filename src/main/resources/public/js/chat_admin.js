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

    $("#iniciador").click(function(event) {
        event.preventDefault();
        var id = $("#id");

        ws.send("iniciar_chat_autor~" + id.attr("value"));
        id.attr("value","");

        $(this).removeClass("btn-danger");
        $(this).addClass("btn-success");
        $(this).attr("disabled",true);
        $(this).html("Chat Establecido");
    });

    $("#send_message_autor").click(function (event) {
        var target = $("#target");
        var mensaje = $("#new_message_autor");

        ws.send("mensaje_autor~" + target.val() + "~" + mensaje.val());

        mensaje.val("");
        target.val("");
    });
});
