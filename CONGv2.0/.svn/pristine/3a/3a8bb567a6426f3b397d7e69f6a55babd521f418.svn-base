/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.reflection;

import edu.ucsc.leeps.fire.FIREClientInterface;
import edu.ucsc.leeps.fire.FIREServerInterface;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.logging.LogEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author jpettit
 */
public class ClassFinder {

    private static Class serverClass, clientClass, clientInterfaceClass, configClass;
    private static Set<Class> logEventClasses;
    private static byte[] jarBytes;

    /**
     * Windows prefers ; to separate lists.
     */
    public static void initialize() {
        logEventClasses = new HashSet<Class>();
        for (String pathItem : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (pathItem.contains("jar")) {
                searchClassesInPackageFromJar("", pathItem, new FIREClassChecker());
            } else {
                searchClassesInPackageFromFile("", pathItem, new FIREClassChecker());
            }
        }
        Class[] ifaces = clientClass.getInterfaces();
        for (Class iface : ifaces) {
            if (iface != FIREClientInterface.class) {
                clientInterfaceClass = iface;
                break;
            }
        }
        assert serverClass != null;
        assert clientClass != null;
        assert clientInterfaceClass != null;
        assert configClass != null;
    }

    public static Object newServer() {
        try {
            return serverClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object newClient() {
        try {
            return clientClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object newConfig() {
        try {
            return configClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Class getClientInterfaceClass() {
        return clientInterfaceClass;
    }

    public static Set<Class> getLogEventClasses() {
        return logEventClasses;
    }

    public static Set<Class> getImplementingClasses(Class iface) {
        InterfaceClassChecker checker = new InterfaceClassChecker(iface);
        for (String pathItem : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (pathItem.contains("jar")) {
                searchClassesInPackageFromJar("", pathItem, checker);
            } else {
                searchClassesInPackageFromFile("", pathItem, checker);
            }
        }
        return checker.classes;
    }

    private static void searchClassesInPackageFromJar(String packageName, String jarFilePath, ClassChecker checker) {
        try {
            JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFilePath));
            JarEntry entry = jarInputStream.getNextJarEntry();
            while (entry != null) {
                if (entry.getName().endsWith(".class")) {
                    checker.found(jarFilePath, entry.getName().replace("/", "."));
                }
                entry = jarInputStream.getNextJarEntry();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void searchClassesInPackageFromFile(String packagePath, String filePath, ClassChecker checker) {
        File directory = new File(filePath);
        if (!directory.exists()) {
            return;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                if (!packagePath.equals("")) {
                    searchClassesInPackageFromFile(packagePath + "." + file.getName(), file.getAbsolutePath(), checker);
                } else {
                    searchClassesInPackageFromFile(packagePath + file.getName(), file.getAbsolutePath(), checker);
                }
            } else {
                if (file.getName().endsWith(".class")) {
                    String fullyQualifiedPath = packagePath + "." + file.getName();
                    checker.found(filePath, fullyQualifiedPath);
                }
            }
        }
    }

    private static void loadJarBytes(String jarPath) {
        File jarFile = new File(jarPath);
        if (!jarFile.getName().endsWith(".jar")) {
            System.err.println("ClassFinder cannot load jar bytes");
            return;
        }
        try {
            FileInputStream is = new FileInputStream(jarFile);
            jarBytes = new byte[(int) jarFile.length()];
            is.read(jarBytes);
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] getJar() {
        return jarBytes;
    }

    private static interface ClassChecker {

        public void found(String source, String fullyQualifiedPath);
    }

    private static class FIREClassChecker implements ClassChecker {

        public void found(String source, String fullyQualifiedPath) {
            assert fullyQualifiedPath.endsWith(".class");
            fullyQualifiedPath = fullyQualifiedPath.replace(".class", "");
            if (!fullyQualifiedPath.contains("fire")) {
                return;
            }
            try {
                Class clazz = ClassLoader.getSystemClassLoader().loadClass(fullyQualifiedPath);
                if (!clazz.isInterface()) {
                    Class[] interfaces = clazz.getInterfaces();
                    for (Class iface : interfaces) {
                        if (iface.equals(FIREServerInterface.class)) {
                            loadJarBytes(source);
                            serverClass = clazz;
                        } else if (iface.equals(FIREClientInterface.class)) {
                            clientClass = clazz;
                        } else if (iface.equals(LogEvent.class)) {
                            logEventClasses.add(clazz);
                        }
                    }
                    Class superClass = clazz.getSuperclass();
                    if (superClass != null) {
                        if (clazz.getSuperclass().equals(BaseConfig.class)) {
                            configClass = clazz;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class InterfaceClassChecker implements ClassChecker {

        private Class iface;
        public Set<Class> classes;

        public InterfaceClassChecker(Class iface) {
            this.iface = iface;
            this.classes = new HashSet<Class>();
        }

        public void found(String source, String fullyQualifiedPath) {
            assert fullyQualifiedPath.endsWith(".class");
            fullyQualifiedPath = fullyQualifiedPath.replace(".class", "");
            if (!fullyQualifiedPath.contains("fire")) {
                return;
            }
            try {
                Class clazz = ClassLoader.getSystemClassLoader().loadClass(fullyQualifiedPath);
                Class[] interfaces = clazz.getInterfaces();
                for (Class implemented : interfaces) {
                    if (implemented.equals(iface)) {
                        classes.add(clazz);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setConfigClass(Class configClass) {
        ClassFinder.configClass = configClass;
    }
}
