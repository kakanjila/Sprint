package com.framework.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class UrlMappingRegistry {
    
    private Map<String, MappingInfo> urlMappings;
    private boolean initialized;
    
    public UrlMappingRegistry() {
        this.urlMappings = new HashMap<>();
        this.initialized = false;
    }
    
    /**
     * Construit le registre des URLs à partir des classes scannées
     */
    public void buildRegistry(List<Class<?>> classes) {
        if (initialized) {
            System.out.println("Registre déjà initialisé.");
            return;
        }
        
        urlMappings.clear();
        int urlCount = 0;
        
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    String url = mapping.value();
                    urlMappings.put(url, new MappingInfo(clazz, method, url));
                    urlCount++;
                    System.out.println("✓ Mapping: " + url + " → " + clazz.getSimpleName() + "." + method.getName());
                }
            }
        }
        
        initialized = true;
        System.out.println("Registre construit: " + urlCount + " URL(s) mappée(s).");
    }
    
    public MappingInfo findByUrl(String url) {
        return urlMappings.get(url);
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public int size() {
        return urlMappings.size();
    }
    
    // Pour récupérer toutes les mappings (optionnel)
    public Map<String, MappingInfo> getMappings() {
        return new HashMap<>(urlMappings);
    }
}