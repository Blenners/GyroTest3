package com.example.gyrotest;

/**
 * Created by blenn on 07/04/2018.
 */

public class sendThread extends Thread {

    public void run(int[3] PRY ) {

        GyroWorker.getCurrentVals;
        SocketHandler.sendData(PRY);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
