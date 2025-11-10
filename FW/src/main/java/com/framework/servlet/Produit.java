package com.framework.servlet;



@Controller("products")  // Ajout de la value
public class Produit {
    
    @GetMapping("/products")
    public void listProducts() {
        // Cette méthode ne sera PAS exécutée, seulement affichée
    }
    
    @GetMapping("/products/details")
    public void productDetails() {
        // Méthode mappée à /products/details
    }
    
    @GetMapping("/products/add")
    public void addProduct() {
        // Méthode mappée à /products/add
    }
}