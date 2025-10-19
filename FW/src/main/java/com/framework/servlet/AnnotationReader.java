package com.framework.servlet;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Set;

public class AnnotationReader {
    public static void main(String[] args) {
        System.out.println("Recherche de toutes les classes utilisant @MyAnnotation...");
        
        try {
            // Utiliser Reflections pour scanner le package
            Reflections reflections = new Reflections("com.framework.servlet");
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(MyAnnotation.class);
            
            if (annotatedClasses.isEmpty()) {
                System.out.println("Aucune classe trouvée avec @MyAnnotation");
            } else {
                System.out.println("Classes trouvées avec @MyAnnotation:");
                for (Class<?> clazz : annotatedClasses) {
                    MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
                    System.out.println(" - " + clazz.getSimpleName() + " : " + annotation.URL());
                    
                    // Optionnel: Vérifier aussi les méthodes annotées
                    Arrays.stream(clazz.getDeclaredMethods())
                          .filter(method -> method.isAnnotationPresent(MyAnnotation.class))
                          .forEach(method -> {
                              MyAnnotation methodAnnotation = method.getAnnotation(MyAnnotation.class);
                              System.out.println("   ↳ Méthode " + method.getName() + " : " + methodAnnotation.URL());
                          });
                }
            }
            
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            System.out.println("Assurez-vous d'avoir la librairie Reflections dans votre classpath");
        }
    }
}