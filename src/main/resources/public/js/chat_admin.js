/**
 * Created by forte on 15/06/16.
 */

//abriendo el websocket solo cuando se cargue el documento
var ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/forte");

var chats = [];


//TODO habilitar cierre de un chat
$(document).ready(function() {

    //indicando eventos:
    ws.onopen = function(e) {
        console.log("Conectado - status " + this.readyState);
    };
    ws.onclose = function(e) {
        console.log("Desconectado - status " + this.readyState);
    };
    ws.onmessage = function(mensaje) {
        procesarMensaje(mensaje);
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
        //buscar destinatario del mensaje
        var target = $("li.chat_tab.active");

        //capturar texto a enviar
        var mensaje = $("#new_message_autor");

        //crear elemento para visualizar el mensaje
        var new_message_item = $("#dummy_mensaje_lector").clone();
        new_message_item.removeClass("hidden");
        new_message_item.find(".cuerpo").html(mensaje.val());

        //agregar nuevo elemento de mensaje al cuerpo del chat
        $("#chat_box_body").append(new_message_item);

        //enviar mensaje
        ws.send("mensaje_autor~" + target.attr("target") + "~" + mensaje.val());

        //agregar nuevo elemento de mensaje al cuerpo del chat
        $("#" + target.attr("target")).append(new_message_item);

        //limpiar input
        mensaje.val("");
    });

    function procesarMensaje(raw_mensaje) {
        var mensaje = raw_mensaje.data.split("~");

        //ver si el mensaje viene de una persona nueva
        if(chats.indexOf(mensaje[0]) == -1) {
            //crear nuevo tab si es una persona nueva
            chats.push(mensaje[0]);

            //setear atributos de nuevo tab
            var nuevo_chat_tab = $("#dummy_chat_tab").clone();
            nuevo_chat_tab.removeClass("hidden");
            nuevo_chat_tab.removeAttr("id");
            nuevo_chat_tab.attr("target",mensaje[0]);
            var tab = nuevo_chat_tab.find("a.chat_head");
            tab.attr("href","#" + mensaje[0]);
            tab.html(mensaje[0]);

            //agregar tab a lista de tabs
            $("#chat_heads").prepend(nuevo_chat_tab);

            //crear box del chat actuals
            var tab_box = $("#dummy_chat_tab_box").clone();
            tab_box.removeClass("hidden");
            tab_box.attr("id",mensaje[0]);

            $("#chat_bodies").append(tab_box);

            //crear nuevo evento en todos los chat_heads
            //habilitar inputs de mensajes cuando clickea un tab
            $("a.chat_head").click(function () {
                $("#input_messages").removeClass("hidden");
            });
        }

        //crear elemento para visualizar el mensaje
        var new_message_item = $("#dummy_mensaje_admin").clone();
        new_message_item.removeClass("hidden");
        new_message_item.find(".cuerpo").html(mensaje[1]);

        //agregar nuevo elemento de mensaje al cuerpo del chat
        $("#" + mensaje[0]).append(new_message_item);
    }
});
