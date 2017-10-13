package pl.grkopiec.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import pl.grkopiec.services.SendingService;
import pl.grkopiec.utils.StringUtils;

@Component
public class TCPClient {
	private static final Logger logger = LogManager.getLogger(TCPClient.class);
    private Socket clientSocket;
    private PrintWriter out;
    private DataInputStream in;
 
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
    	logger.debug("Establishing connction via TCP with server using ip: " + ip + " and port: " + port);
        clientSocket = new Socket(ip, port);
        logger.info("Established connction via TCP with server using ip: " + ip + " and port: " + port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new DataInputStream(clientSocket.getInputStream());
    }
 
    public void sendInitMessage() throws IOException {
    	logger.debug("Sending init message via TCP to server");
    	out.println(StringUtils.initTcpMessage);
    	logger.info("Sent init message via TCP to server");
    }
    
    public DataInputStream getInputStream() {
    	return in;
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
