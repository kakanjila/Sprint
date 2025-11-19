package com.framework.servlet;

@Controller("products")
public class Produit {
    
    @GetMapping("/products")
    public String listProducts() {
        return "<h2>Liste des produits</h2>" +
               "<ul>" +
               "<li>Produit 1 - 19.99€</li>" +
               "<li>Produit 2 - 29.99€</li>" +
               "<li>Produit 3 - 39.99€</li>" +
               "</ul>";
    }
    
    @GetMapping("/products/details")
    public String productDetails() {
        return "<h2>Détails du produit</h2>" +
               "<p><strong>Nom:</strong> iPhone 15</p>" +
               "<p><strong>Prix:</strong> 999€</p>" +
               "<p><strong>Description:</strong> Smartphone haut de gamme</p>";
    }
    
    @GetMapping("/products/add")
    public String addProduct() {
        return "<h2>Ajouter un produit</h2>" +
               "<form method='post'>" +
               "<label>Nom: <input type='text' name='name'></label><br>" +
               "<label>Prix: <input type='number' name='price'></label><br>" +
               "<button type='submit'>Ajouter</button>" +
               "</form>";
    }
    
    @GetMapping("/products/count")
    public Integer productCount() {
        return 42; 
    }
    
    @GetMapping("/products/void")
    public void voidMethod() {
    }

    @GetMapping("/products/void2")
    public String vue() {
        return "a.jsp";
    }
}