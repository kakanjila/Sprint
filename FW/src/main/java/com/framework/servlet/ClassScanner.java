package com.framework.servlet;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {
    
    public List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        
        System.out.println("🔍 Début du scan pour le package: " + packageName);
        
        try {
            // Scanner via ClassLoader (pour JAR)
            scanViaClassLoader(packageName, classes);
            
            // Si rien trouvé, essayer le filesystem
            if (classes.isEmpty()) {
                System.out.println("⚠️ Aucune classe trouvée via ClassLoader, essai filesystem...");
                scanViaFileSystem(packageName, classes);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du scan: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📦 " + classes.size() + " contrôleur(s) trouvé(s)");
        return classes;
    }
    
    /**
     * Scanner via ClassLoader (pour JAR)
     */
    private void scanViaClassLoader(String packageName, List<Class<?>> classes) {
        try {
            String packagePath = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            System.out.println("🔍 Recherche via ClassLoader: " + packagePath);
            
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            
            if (!resources.hasMoreElements()) {
                System.out.println("❌ Aucune ressource ClassLoader pour: " + packagePath);
                return;
            }
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println("📁 Ressource trouvée: " + resource);
                
                if (resource.getProtocol().equals("jar")) {
                    processJarResource(resource, packageName, classes);
                } else if (resource.getProtocol().equals("file")) {
                    processFileResource(resource, packageName, classes);
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur scan ClassLoader: " + e.getMessage());
        }
    }
    
    /**
     * Traiter une ressource JAR
     */
    private void processJarResource(URL jarUrl, String packageName, List<Class<?>> classes) {
        JarFile jarFile = null;
        try {
            String jarPath = jarUrl.getFile();
            
            // Nettoyer le chemin du JAR
            if (jarPath.startsWith("file:")) {
                jarPath = jarPath.substring(5);
            }
            if (jarPath.contains("!")) {
                jarPath = jarPath.substring(0, jarPath.indexOf("!"));
            }
            
            System.out.println("📦 Ouverture du JAR: " + jarPath);
            jarFile = new JarFile(jarPath);
            
            String packagePath = packageName.replace('.', '/');
            Enumeration<JarEntry> entries = jarFile.entries();
            
            int foundCount = 0;
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                
                // Vérifier si c'est un .class dans le bon package
                if (entryName.endsWith(".class") && entryName.startsWith(packagePath)) {
                    String className = entryName.replace('/', '.').replace(".class", "");
                    
                    // Ignorer les classes internes
                    if (!className.contains("$")) {
                        loadAndCheckController(className, classes);
                        foundCount++;
                    }
                }
            }
            
            System.out.println("✅ " + foundCount + " classe(s) examinée(s) dans le JAR");
            
        } catch (Exception e) {
            System.out.println("❌ Erreur traitement JAR: " + e.getMessage());
        } finally {
            if (jarFile != null) {
                try { jarFile.close(); } catch (Exception e) {}
            }
        }
    }
    
    /**
     * Traiter une ressource fichier
     */
    private void processFileResource(URL fileUrl, String packageName, List<Class<?>> classes) {
        try {
            File directory = new File(fileUrl.toURI());
            if (directory.exists()) {
                System.out.println("📁 Scan du répertoire: " + directory.getAbsolutePath());
                scanDirectory(directory, packageName, classes);
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur traitement fichier: " + e.getMessage());
        }
    }
    
    /**
     * Scanner un répertoire
     */
    private void scanDirectory(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName + "." + file.getName();
                scanDirectory(file, subPackage, classes);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                loadAndCheckController(className, classes);
            }
        }
    }
    
    /**
     * Scanner via filesystem (fallback)
     */
    private void scanViaFileSystem(String packageName, List<Class<?>> classes) {
        try {
            String path = packageName.replace('.', '/');
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL resource = cl.getResource(path);
            
            if (resource != null) {
                File directory = new File(resource.toURI());
                if (directory.exists() && directory.isDirectory()) {
                    scanDirectory(directory, packageName, classes);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur scan filesystem: " + e.getMessage());
        }
    }
    
    /**
     * Charger et vérifier si une classe est un contrôleur
     */
    private void loadAndCheckController(String className, List<Class<?>> classes) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                classes.add(clazz);
                System.out.println("✅ CONTROLEUR TROUVÉ: " + className);
                
                // Afficher les méthodes @GetMapping
                java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
                for (java.lang.reflect.Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping mapping = method.getAnnotation(GetMapping.class);
                        System.out.println("   📍 " + mapping.value() + " → " + method.getName() + "()");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ Classe non trouvée: " + className);
        } catch (NoClassDefFoundError e) {
            System.out.println("⚠️ Dépendance manquante: " + className);
        } catch (Exception e) {
            // Ignorer autres erreurs
        }
    }
}