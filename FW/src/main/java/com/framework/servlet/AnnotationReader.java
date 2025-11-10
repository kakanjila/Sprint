package com.framework.servlet;


import java.util.List;

public class AnnotationReader {
    
    private static final ConfigLoader configLoader = new ConfigLoader();
    private static final ClassScanner classScanner = new ClassScanner();
    private static final UrlMappingRegistry urlRegistry = new UrlMappingRegistry();
    
    /**
     * Initialise le système de mapping
     */
    public static void init() {
        if (urlRegistry.isInitialized()) {
            System.out.println("AnnotationReader déjà initialisé.");
            return;
        }
        
        System.out.println("=== Initialisation du Framework ===");
        
        // 1. Charger configuration
        configLoader.loadConfiguration();
        String basePackage = configLoader.getBasePackage();
        
        // 2. Scanner classes
        List<Class<?>> classes = classScanner.scanPackage(basePackage);
        System.out.println("Classes @Controller trouvées: " + classes.size());
        
        // 3. Construire registre
        urlRegistry.buildRegistry(classes);
        
        System.out.println("=== Framework initialisé ===\n");
    }
    
    /**
     * Recherche un mapping par URL
     */
    public static MappingInfo findMappingByUrl(String url) {
        if (!urlRegistry.isInitialized()) {
            System.out.println("Framework non initialisé, initialisation...");
            init();
        }
        
        MappingInfo info = urlRegistry.findByUrl(url);
        return info != null ? info : new MappingInfo();
    }
    
    /**
     * Méthode main pour tester en standalone
     */
    public static void main(String[] args) {
        System.out.println("🚀 Test du Framework AnnotationReader");
        init();
        
        // Test de quelques URLs
        String[] testUrls = {"/", "/employees", "/products", "/unknown"};
        
        for (String url : testUrls) {
            System.out.println("\n--- Test URL: " + url + " ---");
            MappingInfo mapping = findMappingByUrl(url);
            
            if (mapping.isFound()) {
                System.out.println("✅ Trouvé: " + mapping.getClassName() + "." + mapping.getMethodName());
            } else {
                System.out.println("❌ Non trouvé");
            }
        }
    }
}