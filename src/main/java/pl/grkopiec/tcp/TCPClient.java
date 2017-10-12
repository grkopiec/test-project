package pl.grkopiec.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

@Component
public class TCPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private DataInputStream in;
 
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new DataInputStream(clientSocket.getInputStream());
    }
 
    public byte[] sendMessage(String msg) throws IOException {
        out.println(msg);
        
        int frameLength = in.readInt();
        if (frameLength == -1) {
        	return null;
        } else {
        	byte[] frame = new byte[frameLength];
        	in.readFully(frame);
        	return frame;
        }
    }
 
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
