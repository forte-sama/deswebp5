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
        <div class="col col-md-8 col-md-offset-2">
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
                    <div class=" col-md-4">
                    <#-- Chat heads -->
                        <ul role="tablist" id="chat_heads">
                            <li id="dummy_chat_tab" class="chat_tab hidden">
                                <span class="label label-default">
                                    <a href="" class="chat_head" role="tab" data-toggle="tab"></a>
                                </span>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-8">
                        <!-- Chats -->
                        <div class="row">
                            <div class="tab-content" id="chat_bodies">
                                <div role="tabpanel" class="tab-pane hidden" id="dummy_chat_tab_box"></div>
                            </div>
                        </div>
                        <div class="row hidden" id="input_messages">
                            <div class="col-md-5">
                                <input type="text" id="new_message_autor" class="" style="width: 100%;"/>
                                <br>
                            </div>
                            <div class="col-md-3">
                                <button class="btn btn-success" id="send_message_autor">Enviar Mensaje</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>