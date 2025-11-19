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
        
        // Rechercher le mapping
        MappingInfo mapping = AnnotationReader.findMappingByUrl(path);
        
        if (mapping.isFound()) {
            // Exécuter la méthode et traiter le résultat
            executeAndProcessMethod(mapping, request, response);
        } else {
            out.println("<html><head><title>Framework Routing Info</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println(".not-found { background: #f8d7da; padding: 20px; border-radius: 8px; }");
            out.println("</style>");
            out.println("</head><body>");
            
            out.println("<h1>🔍 Framework - Informations de Routage</h1>");
            out.println("<p><strong>URL demandée:</strong> " + path + "</p>");
            
            out.println("<div class='not-found'>");
            out.println("<h3>❌ Aucun contrôleur trouvé</h3>");
            out.println("<p>Aucun contrôleur mappé pour l'URL: <strong>" + path + "</strong></p>");
            out.println("</div>");
            
            out.println("</body></html>");
        }
    }
    
    /**
     * Exécute la méthode et traite le résultat
     */
    private void executeAndProcessMethod(MappingInfo mapping, HttpServletRequest request, 
                                       HttpServletResponse response) 
            throws ServletException, IOException {
        
        Class<?> controllerClass = mapping.getControllerClass();
        Method method = mapping.getMethod();
        
        try {
            // Créer une instance du contrôleur
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            
            // Exécuter la méthode
            Object result = method.invoke(controllerInstance);
            
            // Traiter le résultat selon son type
            processResult(result, request, response, mapping);
            
        } catch (Exception e) {
            handleError(e, request, response);
        }
    }
    
    /**
     * Traite le résultat de la méthode selon son type
     */
    private void processResult(Object result, HttpServletRequest request, 
                             HttpServletResponse response, MappingInfo mapping) 
            throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        
        if (result == null) {
            // Si null, afficher une page par défaut
            displayDefaultInfoPage(mapping, "La méthode a retourné: null", out);
            
        } else if (result instanceof String) {
            String resultString = (String) result;
            
            // Vérifier si c'est une vue JSP (se termine par .jsp)
            if (resultString.endsWith(".jsp")) {
                // Rediriger vers la vue JSP
                forwardToJsp(resultString, request, response);
            } else {
                // Afficher le contenu String normalement
                displayDefaultInfoPage(mapping, resultString, out);
            }
            
        } else {
            // Autres types non supportés
            displayDefaultInfoPage(mapping, 
                "<div class='error'>" +
                "<p><strong>⚠️ Type de retour non supporté</strong></p>" +
                "<p>La méthode a retourné un objet de type: <strong>" + 
                result.getClass().getSimpleName() + "</strong></p>" +
                "<p>Valeur toString(): " + result.toString() + "</p>" +
                "<p><em>Seuls String et les vues JSP (.jsp) sont supportés.</em></p>" +
                "</div>", out);
        }
    }
    
    /**
     * Redirige vers une vue JSP
     */
    private void forwardToJsp(String jspPath, HttpServletRequest request, 
                            HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Nettoyer le chemin si nécessaire
        String cleanPath = jspPath.startsWith("/") ? jspPath : "/" + jspPath;
        
        // Forward vers la JSP
        request.getRequestDispatcher(cleanPath).forward(request, response);
    }
    
    /**
     * Affiche la page d'information par défaut
     */
    private void displayDefaultInfoPage(MappingInfo mapping, String content, PrintWriter out) {
        out.println("<html><head><title>Framework Routing Info</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".controller { background: #e9ecef; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        out.println(".method { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #007bff; }");
        out.println(".url { color: #28a745; font-weight: bold; }");
        out.println(".result { background: #d1ecf1; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println(".error { background: #f8d7da; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<h1>🔍 Framework - Informations de Routage</h1>");
        
        Class<?> controllerClass = mapping.getControllerClass();
        Method method = mapping.getMethod();
        
        out.println("<div class='controller'>");
        out.println("<h2>🎯 Contrôleur: " + controllerClass.getSimpleName() + "</h2>");
        out.println("<p><strong>Classe complète:</strong> " + controllerClass.getName() + "</p>");
        out.println("<p><strong>Méthode exécutée:</strong> " + method.getName() + "()</p>");
        
        out.println("<div class='result'>");
        out.println("<h3>📊 Résultat de l'exécution:</h3>");
        out.println(content);
        out.println("</div>");
        
        out.println("</div>");
        out.println("</body></html>");
    }
    
    /**
     * Gère les erreurs d'exécution
     */
    private void handleError(Exception e, HttpServletRequest request, 
                           HttpServletResponse response) throws IOException {
        
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Erreur</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".error { background: #f8d7da; padding: 20px; border-radius: 8px; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='error'>");
        out.println("<h3>❌ Erreur lors de l'exécution</h3>");
        out.println("<p><strong>Exception:</strong> " + e.getCause().getClass().getSimpleName() + "</p>");
        out.println("<p><strong>Message:</strong> " + e.getCause().getMessage() + "</p>");
        out.println("</div>");
        
        out.println("</body></html>");
        
        e.printStackTrace();
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