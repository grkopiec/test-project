package pl.grkopiec.services;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;
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
	private static int counter = 0;
	
	@Autowired
	private UDPClient udpClient;
	@Autowired
	private TCPClient tcpClient;
	@Autowired
	private ConnectionsProperties connectionsProperties;
	@Autowired
	private RestsService restService;
	
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
    			if (concatFrame(chunk) == false) {
    				break;
    			}
    		}
    		
    		if (frameString.equals(StringUtils.endTransmitionLabel)) {
    			logger.info("Finished reciving data from TCP server");
    			end = true;
    		}
    	}
	}
	
	private boolean concatFrame(byte[] chunk) throws IOException {
		String frameString = new String(chunk);
		if (frameString.startsWith(StringUtils.endTransmitionLabel)) {
			logger.info("Tcp server has been closed itself");
			restService.closedServerInformation();
			return false;
		}
		
		chunk = sendChunkViaUdp(chunk);
		
		Integer frameToSendSize = frameToSend.size();
		if (frameToSendSize >= maxFrameToSendSize) {
			String tcpIp = connectionsProperties.getServerTcpIp();
			Integer tcpPort = connectionsProperties.getServerTcpPort();
			byte[] frameToSendArray = frameToSend.toByteArray();
			logger.debug("Sending via tcp concated frame: " + new String(frameToSendArray));
			try {
				tcpClient.newConnectionMessage(tcpIp, tcpPort, frameToSendArray);
				logger.debug("Sent via tcp concated frame");
			} catch (ConnectException exception) {
				logger.warn("Cannot send concated frame via tcp because server has been closed");
			}
		}
		
		frameToSend.write(chunk);
		return true;
	}
	
	private byte[] sendChunkViaUdp(byte[] chunk) throws IOException {
		counter++;
		logger.debug("Actual udp sequence counter equals: " + counter);
		
		ByteArrayOutputStream chunkWithSequnceNumber = new ByteArrayOutputStream();
		chunkWithSequnceNumber.write(chunk);
		String sequenceNumberString = String.valueOf(counter);
		sequenceNumberString = org.apache.commons.lang3.StringUtils.rightPad(sequenceNumberString, 4);
		byte[] sequenceNumber = sequenceNumberString.getBytes();
		chunkWithSequnceNumber.write(sequenceNumber);
		
		byte[] frameToSend = chunkWithSequnceNumber.toByteArray();
		logger.debug("Sending message to udp server");
		byte[] frameRecived = udpClient.sendEcho(frameToSend);
		logger.debug("Recived message from udp server: " + new String(frameToSend));
		
		if (Arrays.equals(frameToSend, frameRecived) == false) {
			logger.warn("Recived frame: " + frameRecived + " is not equal with sent frame: " + frameToSend);
		}
		
		byte[] recivedTruncateSequence = Arrays.copyOf(frameRecived, 20);
		return recivedTruncateSequence;
	}
}
