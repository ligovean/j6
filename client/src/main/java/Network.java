import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Socket socket;
    //private static ObjectEncoderOutputStream out;
    //private static ObjectDecoderInputStream in;

    private static DataOutputStream out;
    private static DataInputStream in;

    public static void start() {
        try {
            socket = new Socket("localhost", 8199);
            //out = new ObjectEncoderOutputStream(socket.getOutputStream());
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            System.out.println("Отправка сообщения!");
            //out.writeObject(msg);
            out.write((byte) 15);
            byte[] filenameBytes = msg.getFilename().getBytes();
            out.writeInt(filenameBytes.length);
            out.write(filenameBytes);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static byte[] readObject() throws ClassNotFoundException, IOException {
//        byte[] msg = in.read();
//        return obj;
//    }
}