package pl.grkopiec.services;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.grkopiec.components.ConnectionsProperties;
import pl.grkopiec.tcp.TCPClient;
import pl.grkopiec.udp.UDPClient;
import pl.grkopiec.utils.StringUtils;

@Service
public class SendingService {
	private static final Logger logger = LogManager.getLogger(SendingService.class);
	private ByteArrayOutputStream frameToSend;
	private final int maxFrameToSendSize = 100;
	
	@Autowired
	private UDPClient udpClient;
	@Autowired
	private TCPClient tcpClient;
	@Autowired
	private ConnectionsProperties connectionsProperties;
	
	public SendingService() {
		frameToSend = new ByteArrayOutputStream();
	}
	
	public void startTransmition() throws UnknownHostException, IOException {
		String tcpIp = connectionsProperties.getServerTcpIp();
		Integer tcpPort = connectionsProperties.getServerTcpPort();
		tcpClient.startConnection(tcpIp, tcpPort);
	    tcpClient.sendInitMessage();
	    DataInputStream tcpInputStream = tcpClient.getInputStream();
	    receiveFromTcpServer(tcpInputStream);
	    tcpClient.stopConnection();
	}
	
	private void receiveFromTcpServer(DataInputStream dataInputStream) throws IOException {
		logger.info("Starting reciving data from TCP server");
    	boolean end = false;
    	while (end == false) {
    		byte[] frame = new byte[100];
    		int bytesRead = dataInputStream.read(frame);
    		String frameString = new String (frame, 0, bytesRead);
    		logger.debug("Recived message from TCP server: " + frameString);
    		
    		for (int i = 0; i < frame.length; i = i + 20) {
    			byte[] chunk = Arrays.copyOfRange(frame, i, i + 20);
    			sendChunkViaUdp(chunk);
    		}
    		
    		if (frameString.equals(StringUtils.endTransmitionLabel)) {
    			logger.info("Finished reciving data from TCP server");
    			end = true;
    		}
    	}
	}
	
	private void sendChunkViaUdp(byte[] chunk) throws IOException {
		//send via udp and receive and check if is the same
		
		String frameString = new String(chunk);
		if (frameString.startsWith(StringUtils.endTransmitionLabel)) {
			//send frame via tcp
			return;
		}
		
		Integer frameToSendSize = frameToSend.size();
		if (frameToSendSize >= maxFrameToSendSize) {
			//send frame via tcp
		}
		
		frameToSend.write(chunk);
	}
}
