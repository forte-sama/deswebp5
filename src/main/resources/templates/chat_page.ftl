<!DOCTYPE html>
<html lang="en">
<head>
    <link type="text/css" rel="stylesheet" href="/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="/css/custom.css">
    <meta charset="UTF-8">
    <title>Title</title>
    <script type="text/javascript" src="/js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/chat_admin.js"></script>
</head>
<body>
<#include "nav.ftl">
<div class="container">
    <div class="row">
        <div class="col col-md-12">
            <div class="well well-lg">
                <div class="row">
                    <div class=" col-md-12">
                    <#if active_user??>
                        <span id="id" class="hidden" value="${active_user}"></span>
                    </#if>
                        <button class="btn btn-danger" id="iniciador" href="#">Establecer/Restablecer chat</button>
                        <hr />
                    </div>
                </div>
                <div class="row">
                    <#--<div class=" col-md-4">-->
                    <#--&lt;#&ndash; Chat heads &ndash;&gt;-->
                        <#--<ul role="tablist" class="chat_heads">-->
                            <#--<li>-->
                                <#--<a href="#home" class="chat_head" role="tab" data-toggle="tab">Home</a>-->
                            <#--</li>-->
                            <#--<li>-->
                                <#--<a href="#profile" class="chat_head" role="tab" data-toggle="tab">Profile</a>-->
                            <#--</li>-->
                            <#--<li>-->
                                <#--<a href="#messages" class="chat_head" role="tab" data-toggle="tab">Messages</a>-->
                            <#--</li>-->
                            <#--<li>-->
                                <#--<a href="#settings" class="chat_head" role="tab" data-toggle="tab">Settings</a>-->
                            <#--</li>-->
                        <#--</ul>-->
                    <#--</div>-->
                    <#--<div class="col-md-8">-->
                        <#--<!-- Chats &ndash;&gt;-->
                        <#--<div class="tab-content">-->
                            <#--<div role="tabpanel" class="tab-pane" id="home">tab 1</div>-->
                            <#--<div role="tabpanel" class="tab-pane" id="profile">tab 2</div>-->
                            <#--<div role="tabpanel" class="tab-pane" id="messages">tab 3</div>-->
                            <#--<div role="tabpanel" class="tab-pane" id="settings">tab 4</div>-->
                        <#--</div>-->
                    <#--</div>-->
                    <div class="col col-md-12">
                        <input type="text" id="target" />
                        <input type="text" id="new_message_autor" />
                        <button class="btn btn-success" id="send_message_autor">Enviar Mensaje</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>