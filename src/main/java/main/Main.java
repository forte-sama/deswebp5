package main;

import freemarker.template.Configuration;
import models.Articulo;
import models.Comentario;
import models.Etiqueta;
import models.Usuario;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import wrappers.session.AccessTypes;
import wrappers.session.Filtros;
import wrappers.session.Sesion;
import wrappers.db.*;
import wrappers.realtime.WebSocketHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {
    public static void main(String[] args) {

        //indicar gestor de websockets
        webSocket("/forte", WebSocketHandler.class);

        //indicar ruta de archivos publicos.
        staticFileLocation("/public");
        //agregar pantalla de debug. Solo en desarrollo.
        enableDebugScreen();

        //freemarker template engine
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Main.class, "/templates");

        //aplicar filtros
        Filtros.iniciarFiltros();

        //iniciar db
        DBService.test();

        //crear usuario por default si no se ha creado ya
        GestorUsuarios.getInstance().editar(new Usuario("admin","admin","admin",true,true));

        //Rutas
        get("/chat", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","chat");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            data.put("active_user",Sesion.getUsuarioActivo(request));

            return new ModelAndView(data,"chat_page.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/", (request, response) -> {
            response.redirect("/page/1");

            return "";
        });

        get("/page/:pageNumber", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","index");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            try {
                int page = Integer.parseInt(request.params("pageNumber"));

                page = Math.max(1,page);

                data.put("articulos",GestorArticulos.getInstance().find_page(page));

                if(GestorArticulos.getInstance().hasMoreArticles()) {
                    data.put("proxima_pagina", "" + (page + 1));
                }
                if(page > 1) {
                    data.put("anterior_pagina","" + (page - 1));
                }


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return new ModelAndView(data,"index.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/tag/:etiqueta/",(request, response) -> {
            String tag = request.params("etiqueta");
            response.redirect("/tag/" + tag + "/page/1");

            return "";
        });

        get("/tag/:etiqueta/page/:pageNumber", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","index");
            data.put("loggedIn",Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            try {
                int page = Integer.parseInt(request.params("pageNumber"));
                page = Math.max(1,page);

                String tag = request.params("etiqueta");

                data.put("articulos",GestorArticulos.getInstance().find_by_tag(page,tag));

                if(GestorArticulos.getInstance().hasMoreArticles()) {
                    data.put("proxima_pagina", "" + (page + 1));
                }
                if(page > 1) {
                    data.put("anterior_pagina","" + (page - 1));
                }


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return new ModelAndView(data,"index.ftl");
        }, new FreeMarkerEngine(configuration));


        get("/admin/user/list", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","list_users");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //obtener los usuarios
            data.put("usuarios", GestorUsuarios.getInstance().findAll());

            return new ModelAndView(data,"user_list.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/admin/user/delete/:username",(request, response) -> {
            String username = request.params("username");

            Usuario target = GestorUsuarios.getInstance().find(username);

            if(target != null) {
                //borrar
                if(GestorUsuarios.getInstance().eliminar(target)) {
                    target = null;
                }
            }
            //redireccionar
            response.redirect("/admin/user/list");

            return "";
        });

        get("/admin/user/edit/:username", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String username = request.params("username");
            Usuario target = GestorUsuarios.getInstance().find(username);

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/user/list");
            }
            else {
                //setear datos para llenar formulario
                data.put("username",target.getUsername());
                data.put("nombre",target.getNombre());

                if(target.isAdministrador()) {
                    data.put("esAdministrador","si");
                }
                else {
                    if (target.isAutor()) {
                        data.put("esAutor", "si");
                    }
                    else {
                        data.put("esLector", "si");
                    }
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/admin/user/edit", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String username = request.queryParams("username");
            Usuario target = GestorUsuarios.getInstance().find(username.trim());

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/user/list");
            }
            else {
                //tratar de actualizar usuario
                String password = request.queryParams("password");
                String nombre   = request.queryParams("nombre");
                boolean esAdministrador = request.queryParams("type").contentEquals("administrador");
                boolean esAutor = request.queryParams("type").contentEquals("autor") || esAdministrador;

                //actulizar usuario
                target = new Usuario(username,password,nombre,esAdministrador,esAutor);

                if(GestorUsuarios.getInstance().editar(target)) {
                    //redireccionar
                    response.redirect("/admin/user/list");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username", target.getUsername());
                    data.put("nombre", target.getNombre());
                    if (target.isAdministrador()) {
                        data.put("esAdministrador", "si");
                    }
                    else {
                        if (target.isAutor()) {
                            data.put("esAutor", "si");
                        } else {
                            data.put("esLector", "si");
                        }
                    }

                    data.put("msg_type", "error");
                    data.put("msg", "Hubo un error con el formulario. Revisa los campos.");
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));


        get("/article/new", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","new_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/article/new", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","new_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String titulo = request.queryParams("titulo");
            String cuerpo = request.queryParams("cuerpo");
            String raw_etiquetas = request.queryParams("etiquetas");

            //Crear articulo en el gestor
            Articulo nuevo = new Articulo();
            nuevo.setTitulo(titulo);
            nuevo.setCuerpo(cuerpo);
            nuevo.setAutor(GestorUsuarios.getInstance().find(Sesion.getUsuarioActivo(request)));
            nuevo.setFecha(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
            boolean exito = GestorArticulos.getInstance().crear(nuevo);

            if(exito) {
                //si se creo el articulo, crear las etiquetas
                Set<String> etiquetas = GestorEtiquetas.parsearEtiquetas(raw_etiquetas);
                for(String str : etiquetas) {
                    Etiqueta e = new Etiqueta();
                    e.setEtiqueta(str);
                    e.setArticulo(nuevo);

                    GestorEtiquetas.getInstance().crear(e);
                }

                //redireccionar a vista con mis articulos
                response.redirect("/");
            }
            else {
                data.put("titulo",titulo);
                data.put("cuerpo",cuerpo);
                data.put("etiquetas",raw_etiquetas);

                data.put("msg_type","error");
                data.put("msg","Hubo un error en el formulario");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/article/edit/:articulo_id", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String raw_id = request.params("articulo_id");
            Articulo articulo = null;

            try {
                Long long_id = Long.parseLong(raw_id);
                articulo = GestorArticulos.getInstance().find(long_id);
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }

            if (articulo != null) {
                data.put("id",articulo.getId());
                data.put("cuerpo",articulo.getCuerpo());
                data.put("titulo",articulo.getTitulo());
                data.put("etiquetas",GestorEtiquetas.joinListEtiquetdas(articulo.etiquetas()));
            }
            else {
                response.redirect("/");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/article/edit", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //obtener datos del form y del usuario activo
            String raw_id = request.queryParams("id");
            long long_id = -1;
            boolean exito = true;

            String titulo = request.queryParams("titulo");
            String cuerpo = request.queryParams("cuerpo");
            String raw_etiquetas = request.queryParams("etiquetas");

            try {
                long_id = Long.parseLong(raw_id.trim());

                Articulo ar = GestorArticulos.getInstance().find(long_id);
                ar.setTitulo(titulo);
                ar.setCuerpo(cuerpo);
                exito = GestorArticulos.getInstance().editar(ar);
                exito = exito && GestorEtiquetas.getInstance().crearEtiquetasByArticle(ar,raw_etiquetas);
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXITO
                e.printStackTrace();
            }

            if(exito) {
                response.redirect("/");
            }
            else {
                data.put("id",long_id);
                data.put("titulo",titulo);
                data.put("cuerpo",cuerpo);
//                data.put("etiquetas",_GestorEtiquetas.cargarEtiquetas(long_id));

                data.put("msg_type","error");
                data.put("msg","Hubo un error con el formulario.");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/article/delete/:article_id", (request, response) -> {
            String raw_id = request.params("article_id");

            try {
                long long_id = Long.parseLong(raw_id);

                Articulo articulo = GestorArticulos.getInstance().find(long_id);

                if(articulo != null) {
                    GestorArticulos.getInstance().eliminar(articulo);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            response.redirect("/");

            return "";
        });

        get("/article/view/:article_id", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","view_article");
            data.put("loggedIn",Sesion.isLoggedIn(request));
            data.put("currentUser",Sesion.getUsuarioActivo(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);
            if(esAdmin) {
                data.put("isAdmin","si");
            }

            String raw_article_id = request.params("article_id");
            boolean exito = false;

            try {
                long long_id = Long.parseLong(raw_article_id);

                Articulo articulo = GestorArticulos.getInstance().find(long_id);

                if(articulo != null) {
                    data.put("articulo", articulo);
                    data.put("comentarios",GestorComentarios.getInstance().findByArticle(articulo));


                    data.put("num_likes",GestorComentarios.getInstance().likeCount(articulo) + "");

                    exito = true;
                }
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }

            if(!exito) {
                response.redirect("/");
            }

            return new ModelAndView(data,"view_article.ftl");
        }, new FreeMarkerEngine(configuration));


        post("/comment/new", (request, response) -> {
            if(!Sesion.isLoggedIn(request)) {
                response.redirect("/");
            }

            String username        = Sesion.getUsuarioActivo(request);
            String cuerpo_com      = request.queryParams("comentario");
            String raw_articulo_id = request.queryParams("articulo_id");
            String voto            = request.queryParams("voto");

            boolean exito = false;

            try {
                long long_articulo_id = Long.parseLong(raw_articulo_id);

                Comentario comentario = new Comentario();
                comentario.setArticulo(GestorArticulos.getInstance().find(long_articulo_id));
                comentario.setComentario(cuerpo_com);
                comentario.setAutor(GestorUsuarios.getInstance().find(username));
                boolean esVotoBueno = (voto != null && voto.contentEquals("bueno"));
                comentario.setVoto(esVotoBueno);

                GestorComentarios.getInstance().crear(comentario);
                exito = true;
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }

            if(exito) {
                response.redirect("/article/view/" + raw_articulo_id);
            }
            else {
                response.redirect("/");
            }

            return "";
        });

        get("/comment/like/:comentario/:articulo", (request, response) -> {
            if(!Sesion.isLoggedIn(request)) {
                response.redirect("/");
            }

            String username          = Sesion.getUsuarioActivo(request);
            String raw_comentario_id = request.params("comentario");
            String articulo_id       = request.params("articulo");

            try {
                Long comentario_id = Long.parseLong(raw_comentario_id);

                Usuario usuario = GestorUsuarios.getInstance().find(username);
                Comentario comentario = GestorComentarios.getInstance().find(comentario_id);

                GestorLikesComentarios.getInstance().darLike(usuario, comentario);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            response.redirect("/article/view/" + articulo_id.trim());
            return "";
        });

        get("/comment/delete/:article_id/:comment_id", (request, response) -> {
            String raw_articulo_id   = request.params("article_id");

            long articulo_id = Long.parseLong(raw_articulo_id);
            Articulo ar = GestorArticulos.getInstance().find(articulo_id);

            boolean esAdministrador = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            boolean esAutor = false;
            if(ar != null) {
                esAutor = esAutor && ar.getAutor().getUsername() == Sesion.getUsuarioActivo(request);
            }

            if(esAutor || esAdministrador) {
                String comentario_id = request.params("comment_id");

                try {
                    long long_comentario_id = Long.parseLong(comentario_id);

                    Comentario comentario = GestorComentarios.getInstance().find(long_comentario_id);
                    GestorComentarios.getInstance().eliminar(comentario);
                } catch (NumberFormatException e) {
                    //TODO CAMBIAR MENSAJE DE EXCEPCION
                    e.printStackTrace();
                }
            }

            response.redirect("/article/view/" + articulo_id);

            return "";
        });


        get("/logout",(request, response) -> {
            WebSocketHandler.desconectar(Sesion.getUsuarioActivo(request));

            Sesion.cerrar(request);

            response.redirect("/");

            return "";
        });

        get("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"login.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            if(!request.queryParams("submit").isEmpty()) {
                //obtener datos de quien desea iniciar sesion
                String username = request.queryParams("username");
                String password = request.queryParams("password");

                if(GestorUsuarios.getInstance().credencialesValidas(username,password)) {
                    Usuario user = GestorUsuarios.getInstance().find(username);
                    //iniciar sesion
                    Sesion.iniciar(request,user);

                    //redireccionar con estado de exito
                    response.redirect("/");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username",username);

                    data.put("msg_type","error");
                    data.put("msg","No se pudo iniciar sesion. Username/password no coinciden.");
                }
            }

            return new ModelAndView(data,"login.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/user/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/user/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //si el request llego desde el formulario
            if(!request.queryParams("submit").isEmpty()) {
                //obtener datos de nuevo usuario
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String nombre   = request.queryParams("nombre");
                //no es administrador by default
                boolean esAutor = request.queryParams("type").contentEquals("autor"); //1 : autor, 0 : lector

                //crear nueva instancia
                Usuario newUser = new Usuario(username,password,nombre,false,esAutor);

                //persistir nueva instancia, en caso de ser valida
                if(GestorUsuarios.getInstance().crear(newUser)) {
                    //redireccionar con mensaje de exito
                    response.redirect("/");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username",newUser.getUsername());
                    data.put("nombre",newUser.getNombre());
                    if(newUser.isAutor()) {
                        data.put("esAutor","si");
                    }
                    else {
                        data.put("esLector","si");
                    }

                    data.put("msg_type","error");
                    data.put("msg","No se pudo crear usuario. Revisar datos del formulario.");
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));
    }
}