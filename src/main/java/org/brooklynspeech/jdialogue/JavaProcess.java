package org.brooklynspeech.jdialogue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JavaProcess {
    private JavaProcess() {
    }

    public static <T> Process exec(Class<T> c, List<String> args) throws IOException,
            InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = c.getName();

        List<String> command = new LinkedList<String>();
        command.add(javaBin);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        if (args != null) {
            command.addAll(args);
        }

        ProcessBuilder builder = new ProcessBuilder(command);

        Process process = builder.inheritIO().start();
        return process;
    }

}
