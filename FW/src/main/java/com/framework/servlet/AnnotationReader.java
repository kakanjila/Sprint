package com.framework.servlet;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Set;

public class AnnotationReader {
    public static void main(String[] args) {
        System.out.println("Recherche de toutes les classes utilisant @MyAnnotation...");
        
        try {
            Reflections reflections = new Reflections("com.framework.servlet");
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(MyAnnotation.class);
            
            if (annotatedClasses.isEmpty()) {
                System.out.println("Aucune classe trouvée avec @MyAnnotation");
            } else {
                System.out.println("Classes trouvées avec @MyAnnotation:");
                for (Class<?> clazz : annotatedClasses) {
                    MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
                    System.out.println(" - " + clazz.getSimpleName());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            System.out.println("Assurez-vous d'avoir la librairie Reflections dans votre classpath");
        }
    }
}