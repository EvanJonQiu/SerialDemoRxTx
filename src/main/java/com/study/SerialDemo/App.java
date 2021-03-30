package com.study.SerialDemo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TooManyListenersException;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * Hello world!
 *
 */
public class App implements Runnable {
    DataInputStream in;
    private boolean checkLoop = false;
    
    final SerialWrapper serialWrapper = new SerialWrapper();
    final SerialPort serialPort = serialWrapper.openComPort("COM1", 115200, 8, 1, 0);
    
    public void run() {
        BufferedReader into = new BufferedReader(new InputStreamReader(System.in));
        String read;
        System.out.println("Welcome...");
        
        try {
            serialWrapper.setSerialPortListener(serialPort, new SerialPortEventListener() {
                public void serialEvent(SerialPortEvent arg0) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                        byte[] bytes = serialWrapper.readData(serialPort);
                        System.out.println("收到的数据长度："+bytes.length);
                        System.out.println("收到的数据："+ new String(bytes));
                    }
                }
            });
        } catch (TooManyListenersException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        while(!checkLoop) {
            try {
                System.out.println("running1");
                read = into.readLine();
                System.out.println(read);
                if(read.equals(".bye"))
                {
                    checkLoop = true;
                }
                Thread.sleep(500);
            } catch (IOException e) {
                System.out.println(e);System.out.println("running2");
            } catch (InterruptedException ie) {
                System.out.println(ie);System.out.println("running3");
            }
        }
        System.out.println("running4");
        serialWrapper.closeComPort(serialPort);
    }
    
    public static void main( String[] args ) {
        App main = new App();
        Thread t1 = new Thread(main);
        t1.start();
        
        System.out.println("end");
    }
}
