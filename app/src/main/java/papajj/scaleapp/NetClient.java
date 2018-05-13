package papajj.scaleapp;

/**
 * Created by jan.papaj on 6/3/2015.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NetClient extends Thread {
    private String ip = "192.168.2.1";
    private int port = 2000;
    private long errCount = 0;
    private boolean keepRunning = true;
    private InetAddress serverAddr;
    private DatagramSocket socket;

    private boolean online = false;
    private int cnt;
    private int cmdOut;
    private int cmdIn;

    final byte PS = (byte) 0xAA;

    public boolean GetOnline() {
        return online;
    }

    public NetClient(String inIp, int inPort) {
        ip = inIp;
        port = inPort;
    }

    public void SetCmd(int cmd) {
        cmdOut = cmd;
    }

    public int GetCmd() {
        return cmdIn;
    }

    private int Sync() {
        int retVal = 0;

        try {
            byte[] msg = new byte[32];
            DatagramPacket dp = new DatagramPacket(msg, msg.length);
            socket.receive(dp);
            int crc;
            if(dp.getLength() == 16 &&
               msg[0] == PS)
            {
                int dataLen = msg[1];
                int id = msg[2];
                crc = crc16.Calculate(msg, dataLen + 3, 1);
                int crcTmp = (int)(msg[15] << 8 | msg[14]) & 0xffff;
                if(crc == crcTmp)
                {
                    cmdIn = (int)(msg[4] |
                                  msg[5] << 8 |
                                  msg[6] << 16 |
                                  msg[7] << 24);
                }
            }
            else
            {
                errCount++;
            }

            byte[] msgOut = new byte[32];
            for(int i = 0; i < msgOut.length; i++)
            {
                msgOut[i] = 0;
            }
            msgOut[0] = (byte) 0xAA;
            msgOut[1] = 13;
            msgOut[2] = 0x22;
            msgOut[3] = (byte) cnt;
            msgOut[4] = (byte) (cmdOut & 0xFF);
            msgOut[5] = (byte) ((cmdOut >> 8) & 0xFF);
            msgOut[5] = (byte) ((cmdOut >> 16) & 0xFF);
            msgOut[5] = (byte) ((cmdOut >> 24) & 0xFF);
            crc = crc16.Calculate(msgOut, 15, 1);
            msgOut[16] = (byte) (crc & 0xFF);
            msgOut[17] = (byte) ((crc >> 8) & 0xFF);

            DatagramPacket dps = new DatagramPacket(msgOut, 18, serverAddr, port);
            socket.send(dps);

            cnt++;
            if(cnt > 0xFF)
            {
                cnt = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            retVal = -1;
        }

        return retVal;
    }

    public void run() {
        int result;
        while (keepRunning) {
            try {
                online = false;
                serverAddr = InetAddress.getByName(ip);
                socket = new DatagramSocket(port);
                socket.setSoTimeout(1000);

                while (keepRunning) {
                    result = Sync();
                    if(-1 == result)
                    {
                        break;
                    }
                    else
                    {
                        online = true;
                    }
                }
                socket.close();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void Halt() {
        keepRunning = false;
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
