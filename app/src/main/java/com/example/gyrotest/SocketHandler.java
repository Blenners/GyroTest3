package com.example.gyrotest;

import java.io.*;
import java.net.*;

import static java.lang.Thread.sleep;

public class SocketHandler {

    private static int[] orientationVals = new int[3];
    public static PrintWriter printWriter;
    public static BufferedReader bufferedReaderIn;
    public static BufferedReader bufferedReaderStdIn;
    public static Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            try {

                    String hostName = "192.168.0.60"; //args[0];
                    int portNumber = 8888;//Integer.parseInt(args[1]);

                    try (
                            Socket droneSocket = new Socket(hostName, portNumber);
                            PrintWriter out =
                                    new PrintWriter(droneSocket.getOutputStream(), true);

                            BufferedReader in =
                                    new BufferedReader(
                                            new InputStreamReader(droneSocket.getInputStream()));
                            BufferedReader stdIn =
                                    new BufferedReader(
                                            new InputStreamReader(System.in))
                    ) {
                        printWriter = out;
                        bufferedReaderIn = in;
                        bufferedReaderStdIn = stdIn;

                        //TODO sort separate sending code

                        printWriter.println("Test Message");
            while ((orientationVals = GyroWorker.AdjustedValsPRA) != null) {

                out.println(orientationVals[0] + "," + orientationVals[1] + "," + orientationVals[2]);
                //out.println(orientationVals);
                //System.out.println("--------------");
                //System.out.println(orientationVals);
                sleep(2000);
            }
                    } catch (UnknownHostException e) {
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

    public static void connect() throws IOException {

            thread.start();

        //TODO read in inputs from text box but keep as default

    }

    public static void sendData(int[] vals, PrintWriter pw){

        pw.println(vals);
        //pw.println("Tree");
        System.out.println(vals);

    }
}