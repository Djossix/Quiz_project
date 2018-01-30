package server;

import java.io.IOException;
import java.net.*;


public class GameServer {

    public static void main(String [] args) throws IOException {

        int port = 8088;

        //Create a socket on server-side.
        ServerSocket server = new ServerSocket(port);
        while(true) {

            try {

                System.out.println("Listening for client..");

                //Accepts client when a connection request is made.
                //Create object of GamePlay.
                //Create thread and start thread.
                Socket connectedPlayer = server.accept();
                GamePlay gp = new GamePlay(connectedPlayer);
                Thread t = new Thread((Runnable) gp);
                t.start();

            }catch (IOException e) {

            }
        }
    }
}