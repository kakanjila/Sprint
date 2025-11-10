package com.framework.servlet;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    
    private String basePackage;
    
    public void loadConfiguration() {
        if (basePackage != null) return;
        
        Properties props = new Properties();
        
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            if (input != null) {
                props.load(input);
                basePackage = props.getProperty("base.package", "com.framework.servlet");
                System.out.println("Package de base: " + basePackage);
            } else {
                // FORCER le package correct
                basePackage = "com.framework.servlet";
                System.out.println("Fichier config.properties non trouvé, utilisation package: " + basePackage);
            }
        } catch (Exception e) {
            // FORCER le package correct en cas d'erreur
            basePackage = "com.framework.servlet";
            System.out.println("Erreur config, utilisation package: " + basePackage);
        }
    }
    
    public String getBasePackage() {
        if (basePackage == null) {
            loadConfiguration();
        }
        return basePackage;
    }
}