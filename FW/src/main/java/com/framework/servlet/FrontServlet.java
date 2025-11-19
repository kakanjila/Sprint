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
        out.println(".result { background: #d1ecf1; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println(".error { background: #f8d7da; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<h1>🔍 Framework - Informations de Routage</h1>");
        out.println("<p><strong>URL demandée:</strong> " + path + "</p>");
        
        // Rechercher le mapping
        MappingInfo mapping = AnnotationReader.findMappingByUrl(path);
        
        if (mapping.isFound()) {
            // Exécuter la méthode et afficher le résultat
            executeAndDisplayMethod(mapping, out, request, response);
        } else {
            out.println("<div class='not-found'>");
            out.println("<h3>❌ Aucun contrôleur trouvé</h3>");
            out.println("<p>Aucun contrôleur mappé pour l'URL: <strong>" + path + "</strong></p>");
            out.println("</div>");
        }
        
        out.println("</body></html>");
    }
    
    /**
     * Exécute la méthode et affiche le résultat
     */
    private void executeAndDisplayMethod(MappingInfo mapping, PrintWriter out, 
                                       HttpServletRequest request, HttpServletResponse response) {
        Class<?> controllerClass = mapping.getControllerClass();
        Method method = mapping.getMethod();
        
        out.println("<div class='controller'>");
        out.println("<h2>🎯 Contrôleur: " + controllerClass.getSimpleName() + "</h2>");
        out.println("<p><strong>Classe complète:</strong> " + controllerClass.getName() + "</p>");
        out.println("<p><strong>Méthode exécutée:</strong> " + method.getName() + "()</p>");
        
        try {
            // Créer une instance du contrôleur
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            
            // Exécuter la méthode
            Object result = method.invoke(controllerInstance);
            
            // Afficher le résultat
            out.println("<div class='result'>");
            out.println("<h3>📊 Résultat de l'exécution:</h3>");
            
            if (result == null) {
                out.println("<p><em>La méthode a retourné: null</em></p>");
            } else if (result instanceof String) {
                out.println("<p><strong>Contenu retourné (String):</strong></p>");
                out.println("<div style='background: white; padding: 10px; border: 1px solid #ccc;'>");
                out.println(result.toString());
                out.println("</div>");
            } else {
                out.println("<div class='error'>");
                out.println("<p><strong>⚠️ Type de retour non supporté</strong></p>");
                out.println("<p>La méthode a retourné un objet de type: <strong>" + 
                           result.getClass().getSimpleName() + "</strong></p>");
                out.println("<p>Valeur toString(): " + result.toString() + "</p>");
                out.println("<p><em>Seul le type String est actuellement supporté pour l'affichage.</em></p>");
                out.println("</div>");
            }
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<h3>❌ Erreur lors de l'exécution</h3>");
            out.println("<p><strong>Exception:</strong> " + e.getCause().getClass().getSimpleName() + "</p>");
            out.println("<p><strong>Message:</strong> " + e.getCause().getMessage() + "</p>");
            out.println("</div>");
            e.printStackTrace();
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