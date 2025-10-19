package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.print.DocFlavor.URL;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@MyAnnotation(URL = "https://api.example.com/products")
public class FrontServlet extends HttpServlet {

    @Override
    @MyAnnotation(URL = "ejter")
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        // Vérifier si une ressource existe (par exemple, un fichier de configuration)
        String uri = request.getRequestURI();
        java.net.URL resourceUrl = getServletContext().getResource(uri);
        if (resourceUrl != null) {
            // Ressource trouvée, utiliser son URL
            out.println("Ressource trouvée: " + resourceUrl.toString());
            System.out.println("Ressource trouvée: " + resourceUrl.toString());
        } else {
            // Aucune ressource, utiliser getRequestURL()
            out.println("URL demandée: " + request.getRequestURL().toString());
            System.out.println("URL demandée: " + request.getRequestURL().toString());
        }
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