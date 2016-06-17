<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="glyphicon glyphicon-menu-hamburger"></span>
            </button>
            <a class="navbar-brand" href="/">
                <span class="glyphicon glyphicon-fire"></span>
            </a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-left">
                <li <#if action == "index">class="active"</#if>><a href="/">Home</a></li>
                <#if (action == "index" || action == "view_article") && action != "chat">
                <#--<#include "chat_box.ftl">-->
                <script type="text/javascript" src="/js/chat_user.js"></script>
                <li class="bg-primary"><a id="chat_init" href="" data-toggle="modal" data-target="#chat_box">Contactar</a></li>
                </#if>
                <#if loggedIn?? && isAutor?? && isAutor == true>
                <li <#if action == "new_article">class="active"</#if>><a href="/article/new">Crear Articulo</a></li>
                </#if>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <#if !loggedIn?? || loggedIn == true>
                <li><a href="/logout">Cerrar Sesion</a></li>
                <#else>
                <#if action != "login">
                <li><a href="/login">Login</a></li>
                </#if>
                <#if action != "register">
                <li><a href="/user/register">Register</a></li>
                </#if>
                </#if>
                <#--TODO SOLO PRESENTAR DROPDOWN CUANDO SEA UN ADMIN -->
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button">Admin <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li role="separator" class="divider"></li>
                        <li><a href="/chat">Atender contactos</a></li>
                        <li><a href="/admin/user/list">Ver Usuarios</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div id="chat_box" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <input type="text" id="chat_user" />
                <button class="btn btn-warning" id="connect_as">Conectar</button>
            </div>
            <div id="chat_box_body" class="modal-body">
                <#-- Aqui se va a almacenar cada mensaje -->
            </div>
            <div class="modal-footer">
                <div class="row">
                    <div class="col-md-9">
                        <input type="text" id="new_message" class="" style="width: 100%;"/>
                        <br>
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-success" id="send_message">Enviar Mensaje</button>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div id="dummy_mensaje_admin" class="row hidden">
    <div class="col col-md-6 pull-left">
        <h3 class="pull-left">
            <span class="label label-primary cuerpo">
            </span>
        </h3>
    </div>
</div>
<div id="dummy_mensaje_lector" class="row hidden">
    <div class="col col-md-6 pull-right">
        <h3 class="pull-right">
            <span class="label label-default cuerpo">
            </span>
        </h3>
    </div>
</div>