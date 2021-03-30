package com.study.SerialDemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialWrapper {
    
    public ArrayList<String> findSystemAllComPort() {
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<String>();
        
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }
    
    public SerialPort openComPort(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        CommPort commPort = null;
        
        if (portName == null || "".equals(portName)) {
            List<String> comPortList = findSystemAllComPort();
            if (comPortList != null && comPortList.size() > 0) {
                portName = comPortList.get(0);
            }
        }
        
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            
            commPort = portIdentifier.open(portName, 2000);
            
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort)commPort;
                serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                return serialPort;
            } else {
                // open port failed.
                System.out.println("open port failed");
            }
            
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
            if (commPort != null) {
                commPort.close();
            }
        }

        return null;
    }

    public void closeComPort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
        }
    }
    
    public void setSerialPortListener(SerialPort serialPort, SerialPortEventListener listener) throws TooManyListenersException {
        if (serialPort != null) {
            serialPort.addEventListener(listener);
            
            serialPort.notifyOnDataAvailable(true);
            
            serialPort.notifyOnBreakInterrupt(true);
        }
    }
    
    public final byte[] readData(SerialPort serialPort) {
        InputStream is = null;
        
        byte[] bytes = null;
        try {
            is = serialPort.getInputStream();
            int buffLength = is.available();
            
            while (buffLength > 0) {
                bytes = new byte[buffLength];
                is.read(bytes);
                buffLength = is.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return bytes;
    }
}
