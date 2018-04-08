package com.example.gyrotest;

import java.io.*;
import java.net.*;

public class SocketHandler {
    public static void connect() throws IOException {

        //TODO read in inputs from text box but keep as default
        String hostName = "192.168.0.60"; //args[0];
        int portNumber = 8888;//Integer.parseInt(args[1]);

        try
                (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        )
        {
            //TODO sort separate sending code
            /*String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }*/
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    public static void sendData(int[] vals){

        out.println(vals);

    }
}