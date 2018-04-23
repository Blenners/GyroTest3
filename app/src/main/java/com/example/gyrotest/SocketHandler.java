package com.example.gyrotest;

import java.io.*;
import java.net.*;

import static java.lang.Thread.sleep;

public class SocketHandler {

    private static int[] orientationVals = new int[3]; // Initialise variables to be sent to drone
    public static Thread thread = new Thread(new Runnable() { // Creates this as a thread

        @Override
        public void run() {
            try {

                    //TODO read in inputs from text box but keep as default
                    String hostName = "192.168.0.58"; // IP address of the Raspberry Pi 58 20 for laptop
                    int portNumber = 8888; // Port that the server is hosting

                    try (
                            Socket droneSocket = new Socket(hostName, portNumber); // Creates the socket
                            PrintWriter out = // Creates channel to write data to the server
                                    new PrintWriter(droneSocket.getOutputStream(), true);

                            /*BufferedReader in = // Creates channel to read data from server (not used)
                                    new BufferedReader( // Kept for potential future development
                                            new InputStreamReader(droneSocket.getInputStream()));
                            BufferedReader stdIn =
                                    new BufferedReader(
                                            new InputStreamReader(System.in))*/
                    ) {


            while ((orientationVals = GyroWorker.LimitedValsPRA) != null) { // When there are values available
                out.println(orientationVals[0] + "," + orientationVals[1] + "," + orientationVals[2]); // Send them to the server
                sleep(2000); // Do this every xxx milliseconds
            }
                    } catch (UnknownHostException e) { // Error handling
                        System.err.println("Don't know about host " + hostName);
                        System.exit(1);
                    } catch (IOException e) {
                        System.err.println("Couldn't get I/O for the connection to " +
                                hostName);
                        System.exit(1);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

            }

    });

    /*public boolean isConnected() {
        if (droneSocket == null){
            return false;
        }
        else if (socket.isConnected()){
            return true;
        }
        else {
            socket = null;
            return false;
        }
    }*/

    public static void connect() throws IOException { // Called from main when button pressed

        thread.start(); // Starts thread

    }

    /*public static void sendData(int[] vals, PrintWriter pw){

        pw.println(vals);
        //pw.println("Tree");
        System.out.println(vals);

    }*/
}