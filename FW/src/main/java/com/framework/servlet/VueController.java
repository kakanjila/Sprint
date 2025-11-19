package com.framework.servlet;

@Controller
public class VueController {
    
    @GetMapping("/home")
    public String homePage() {
        return "a.jsp"; // Retourne le nom de la vue JSP
    }
    
    @GetMapping("/products")
    public String productsPage() {
        return "a.jsp"; // Retourne une vue JSP
    }
    
    @GetMapping("/contact")
    public String contactPage() {
        return "a.jsp"; // Retourne une vue JSP
    }
    
    @GetMapping("/mixed")
    public String mixedExample() {
        // Selon une condition, on peut retourner soit du HTML soit une vue JSP
        if (Math.random() > 0.5) {
            return "<h2>Contenu HTML direct</h2><p>Ceci est du HTML généré directement</p>";
        } else {
            return "a.jsp"; // Vue JSP alternative
        }
    }
    
    @GetMapping("/admin")
    public String adminPage() {
        // Retourne une vue JSP avec chemin dans un sous-dossier
        return "a.jsp";
    }
}