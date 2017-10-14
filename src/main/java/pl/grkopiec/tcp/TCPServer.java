package pl.grkopiec.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

@Component
public class TCPServer {
    public void start(int port) throws IOException {
    	ServerSocket serverSocket = null;
    	Socket clientSocket = null;
    	
    	serverSocket = new ServerSocket(port);
        while (true) {
        	clientSocket = serverSocket.accept();
        	new TCPServerThread(serverSocket, clientSocket).start();;
        }
    }
}
