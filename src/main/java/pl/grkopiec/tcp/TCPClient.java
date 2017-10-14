package pl.grkopiec.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import pl.grkopiec.utils.StringUtils;

@Component
public class TCPClient {
	private static final Logger logger = LogManager.getLogger(TCPClient.class);
    private Socket socket;
    private PrintWriter out;
    private DataInputStream in;
 
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
    	logger.debug("Establishing connction via TCP with server using ip: " + ip + " and port: " + port);
        socket = new Socket(ip, port);
        logger.info("Established connction via TCP with server using ip: " + ip + " and port: " + port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new DataInputStream(socket.getInputStream());
    }
 
    public void sendInitMessage() throws IOException {
    	logger.debug("Sending init message via TCP to server");
    	out.println(StringUtils.initTcpMessage);
    	logger.info("Sent init message via TCP to server");
    }
    
    public DataInputStream getInputStream() {
    	return in;
    }
    
    public void newConnectionMessage(String ip, int port, byte[] message) throws ConnectException, IOException {
        Socket socket = new Socket(ip, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(message.length);
        out.write(message);
        out.flush();
        socket.close();
    }
 
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
