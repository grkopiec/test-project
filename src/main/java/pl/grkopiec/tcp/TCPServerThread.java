package pl.grkopiec.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import pl.grkopiec.utils.StringUtils;

public class TCPServerThread extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;
	
	public TCPServerThread(ServerSocket serverSocket, Socket clientSocket) {
		this.serverSocket = serverSocket;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
        try {
			out = new DataOutputStream(clientSocket.getOutputStream());
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            if ("getFrame".equals(inputLine)) {
	            	for (int i = 0; i < 20; i++) {
		            	out.write(StringUtils.frame);
		            	out.flush();
	            	}
	            	out.write(StringUtils.endTransmitionFrame);
	            	out.flush();
	            	stopConnection();
	            	//after sent all data wait 500ms and stop server
	            	Thread.sleep(500);
	                stopServer();
	            } else {
	            	out.write(StringUtils.endTransmitionFrame);
	            	stopConnection();
	            }
	        }
		} catch (IOException | InterruptedException exception) {
			new RuntimeException(exception);
		}
	}
	
	private void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
	}
	 
    private void stopServer() throws IOException {
    	serverSocket.close();
    }
}
