package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class FrontServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialiser le framework au démarrage
        AnnotationReader.init();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());
        
        out.println("<html><head><title>Framework Routing Info</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".controller { background: #e9ecef; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        out.println(".method { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #007bff; }");
        out.println(".url { color: #28a745; font-weight: bold; }");
        out.println(".not-found { background: #f8d7da; padding: 20px; border-radius: 8px; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<h1>🔍 Framework - Informations de Routage</h1>");
        out.println("<p><strong>URL demandée:</strong> " + path + "</p>");
        
        // Rechercher le mapping
        MappingInfo mapping = AnnotationReader.findMappingByUrl(path);
        
        if (mapping.isFound()) {
            // Afficher les informations du contrôleur et de ses méthodes
            displayControllerInfo(mapping, out);
        } else {
            out.println("<div class='not-found'>");
            out.println("<h3>❌ Aucun contrôleur trouvé</h3>");
            out.println("<p>Aucun contrôleur mappé pour l'URL: <strong>" + path + "</strong></p>");
            out.println("</div>");
        }
        
        out.println("</body></html>");
    }
    
    /**
     * Affiche les informations du contrôleur et de ses méthodes @GetMapping
     */
    private void displayControllerInfo(MappingInfo mapping, PrintWriter out) {
        Class<?> controllerClass = mapping.getControllerClass();
        
        out.println("<div class='controller'>");
        out.println("<h2>🎯 Contrôleur: " + controllerClass.getSimpleName() + "</h2>");
        out.println("<p><strong>Classe complète:</strong> " + controllerClass.getName() + "</p>");
        
        // Afficher l'annotation @Controller si elle existe
        Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
        if (controllerAnnotation != null && !controllerAnnotation.value().isEmpty()) {
            out.println("<p><strong>@Controller value:</strong> " + controllerAnnotation.value() + "</p>");
        }
        
        out.println("<h3>📋 Méthodes @GetMapping:</h3>");
        
        // Lister toutes les méthodes avec @GetMapping
        Method[] methods = controllerClass.getDeclaredMethods();
        boolean hasGetMapping = false;
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                hasGetMapping = true;
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                
                out.println("<div class='method'>");
                out.println("<p><strong>URL:</strong> <span class='url'>" + getMapping.value() + "</span></p>");
                out.println("<p><strong>Méthode:</strong> " + method.getName() + "()</p>");
                out.println("<p><strong>Type retour:</strong> " + method.getReturnType().getSimpleName() + "</p>");
                
                // Afficher les paramètres
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length > 0) {
                    out.println("<p><strong>Paramètres:</strong> ");
                    for (int i = 0; i < paramTypes.length; i++) {
                        out.print(paramTypes[i].getSimpleName());
                        if (i < paramTypes.length - 1) out.print(", ");
                    }
                    out.println("</p>");
                }
                out.println("</div>");
            }
        }
        
        if (!hasGetMapping) {
            out.println("<p>Aucune méthode avec @GetMapping dans ce contrôleur.</p>");
        }
        
        out.println("</div>");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        service(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        service(request, response);
    }
}