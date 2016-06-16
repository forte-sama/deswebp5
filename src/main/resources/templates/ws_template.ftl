<!DOCTYPE html>
<html lang="en">
<head>
    <link type="text/css" rel="stylesheet" href="/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="/css/custom.css">
    <meta charset="UTF-8">
    <title>Title</title>
    <script type="text/javascript" src="/js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col col-md-12">
            <div class="well well-lg">
                <ul id="respuestas_server">
                </ul>

                <input type="text" name="texto_send" />

                <button id="boton_send">Enviar info</button>
            </div>
        </div>
    </div>
</div>
<script>
    //abriendo el websocket
    var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/forte");

    //indicando eventos:
    webSocket.onmessage = function(data) {
        recibirInformacionServidor(data);
    };
    webSocket.onopen = function(e) {
        console.log("Conectado - status " + this.readyState);
    };
    webSocket.onclose = function(e) {
        console.log("Desconectado - status " + this.readyState);
        alert("Conexión Terminada");
    };

    $(document).ready(function() {
        console.info("Iniciando Ejemplo WebServices");

        $("#boton_send").click(function() {
            webSocket.send($("input[name=texto_send]").val());
        });
    });

    function recibirInformacionServidor(mensaje) {
        console.log("Recibiendo del servidor: " + mensaje.data);

        $("#respuestas_server").append(mensaje.data);
    }
</script>
</body>
</html>