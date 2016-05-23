package me.stevenkin.http.mineserver.core.util;

import me.stevenkin.http.mineserver.core.container.HttpHandle;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by wjg on 16-4-29.
 */
public class ClassUtil {

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
