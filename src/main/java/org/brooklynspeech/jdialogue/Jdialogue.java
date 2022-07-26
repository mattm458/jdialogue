package org.brooklynspeech.jdialogue;

import org.brooklynspeech.server.Server;

public class Jdialogue {

    public static void main(String[] args) {
        try {
            final Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
