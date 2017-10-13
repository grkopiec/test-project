package pl.grkopiec.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

import pl.grkopiec.utils.StringUtils;

@Component
public class TCPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;
 
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
        	System.out.println(inputLine);
            if ("getFrame".equals(inputLine)) {
            	for (int i = 0; i < 20; i++) {
            		System.out.println(i);
	            	out.write(StringUtils.frame);
	            	out.flush();
            	}
            	out.write(StringUtils.endTransmitionFrame);
            	out.flush();
            } else {
            	out.write(StringUtils.endTransmitionFrame);
            	break;
            }
        }
    }
 
    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
