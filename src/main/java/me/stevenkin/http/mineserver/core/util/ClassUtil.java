package me.stevenkin.http.mineserver.core.util;

import com.google.common.base.Strings;
import me.stevenkin.http.mineserver.core.annotation.Controller;
import me.stevenkin.http.mineserver.core.container.HttpHandle;

import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.File;

/**
 * Created by wjg on 16-4-29.
 */
public class ClassUtil {
    /*public static List<Class<? extends HttpHandle>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<? extends HttpHandle>> classList = new ArrayList<Class<? extends HttpHandle>>();
        try {
            System.out.println("ClassUtil");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            System.out.println("classLoader "+classLoader);
            getProjectPath();
            Enumeration<URL> urls = classLoader.getResources(packageName.replaceAll("\\.", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                System.out.println("url "+url);
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        System.out.println("packagePath "+packagePath);
                        System.out.println("packageName "+packageName);
                        addClassByAnnotation(classList, packagePath, packageName, annotationClass);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            System.out.println("jarEntryName "+jarEntryName);
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                Class<? extends HttpHandle> cls = (Class<? extends HttpHandle>) Class.forName(className);
                                if (cls.isAnnotationPresent(annotationClass)) {
                                    classList.add(cls);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    private static File[] getClassFiles(String packagePath) {
        return new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
    }

    private static String getClassName(String packageName, String fileName) {
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        if (!Strings.isNullOrEmpty(packageName)) {
            className = packageName + "." + className;
        }
        return className;
    }

    private static String getSubPackagePath(String packagePath, String filePath) {
        String subPackagePath = filePath;
        if (!Strings.isNullOrEmpty(packagePath)) {
            subPackagePath = packagePath + "/" + subPackagePath;
        }
        return subPackagePath;
    }

    private static String getSubPackageName(String packageName, String filePath) {
        String subPackageName = filePath;
        if (!Strings.isNullOrEmpty(packageName)) {
            subPackageName = packageName + "." + subPackageName;
        }
        return subPackageName;
    }

    private static void addClassByAnnotation(List<Class<? extends HttpHandle>> classList, String packagePath, String packageName, Class<? extends Annotation> annotationClass) {
        try {
            File[] files = getClassFiles(packagePath);
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        String className = getClassName(packageName, fileName);
                        Class<? extends HttpHandle> cls = (Class<? extends HttpHandle>) Class.forName(className);
                        if (cls.isAnnotationPresent(annotationClass)&& HttpHandle.class.isAssignableFrom(cls) && !HttpHandle.class.equals(cls)) {
                            classList.add(cls);
                        }
                    } else {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClassByAnnotation(classList, subPackagePath, subPackageName, annotationClass);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static List<Class<? extends HttpHandle>> getClassesByAnnotation(Class<? extends Annotation> annotationClass) {

        java.net.URL url = ClassUtil.class .getProtectionDomain().getCodeSource().getLocation();
        String filePath = null ;
        try {
            filePath = java.net.URLDecoder.decode (url.getPath(), "utf-8");
            System.out.println("filePath"+filePath);
            List<Class<? extends HttpHandle>> classList = getClasssFromJarFile(filePath,annotationClass);
            System.out.println("classList size"+classList.size());
            return classList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Class<? extends HttpHandle>> getClasssFromJarFile(String jarPaht, Class<? extends Annotation> annotationClass){
        List<Class<? extends HttpHandle>> clazzs = new ArrayList<Class<? extends HttpHandle>>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = (JarEntry) ee.nextElement();
            String jarEntryName = entry.getName();
            if(jarEntryName.endsWith(".class")) {
                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                Class cls = null;
                try {
                    cls = Class.forName(className);
                    if(cls.isAnnotationPresent(annotationClass) && HttpHandle.class.isAssignableFrom(cls) && !HttpHandle.class.equals(cls)) {
                        clazzs.add((Class<? extends HttpHandle>) cls);
                    }
                } catch (Throwable e) {
                }
            }
        }

        return clazzs;
    }
}
