package pl.grkopiec.spring.controllers;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.components.ConnectionsProperties;
import pl.grkopiec.domains.Addresses;
import pl.grkopiec.services.SendingService;

@RestController
public class RestContrller {
	private static final Logger logger = LogManager.getLogger(RestContrller.class);
	
	@Autowired
	private SendingService sendingService;
	@Autowired
	private ConnectionsProperties connectionsProperties;
	
	@RequestMapping(path = "/addresses", method = RequestMethod.POST)
	public ResponseEntity<Void> addresses(@RequestBody Addresses addresses) throws UnknownHostException, IOException {
		logger.debug("Starting application");
		reciveHttpUDP(addresses);
		sendAndReciveUDP();
		
		sendingService.startTransmition();
	    
//	    ByteArrayOutputStream recivedFullArray = new ByteArrayOutputStream();
//	    for (int i = 0; i < resp1.length; i = i + 20) {
//	    	byte[] chunk = Arrays.copyOfRange(resp1, i, i + 20);
//	    	byte[] udpRecivedData = uDPClient.sendEcho(chunk);
//	    	recivedFullArray.write(udpRecivedData);
//	    }
//	    byte[] recFullArr = recivedFullArray.toByteArray();
//	    System.out.println(new String(recFullArr));
//	    
//	    uDPClient.sendEcho("end".getBytes());
		
		ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);
		return responseEntity;
	}
	
	//for test purposes
	@RequestMapping(path = "/udp-server", method = RequestMethod.POST)
	public ResponseEntity<Addresses> udpClient(@RequestBody Addresses addresses) {
		String serverUDPUrl = "udp://localhost:5555/";
		Addresses serverUdp = new Addresses();
		serverUdp.setUdp(serverUDPUrl);
		
		ResponseEntity<Addresses> responseEntity = new ResponseEntity<>(serverUdp, HttpStatus.OK);
		return responseEntity;
	}
	
	private void reciveHttpUDP(Addresses addresses) {
		logger.debug("Recived http: " + addresses.getHttp());
		logger.debug("Recived tcp: " + addresses.getTcp());
		connectionsProperties.getServers().setHttp(addresses.getHttp());
		connectionsProperties.getServers().setTcp(addresses.getTcp());
	}
	
	private void sendAndReciveUDP() {
		String serverHttp = connectionsProperties.getServers().getHttp();
		String clientUDP = connectionsProperties.getClients().getUdp();
		Addresses sendUDPAddress = new Addresses();
		sendUDPAddress.setTcp(clientUDP);
		
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("Sending udp: " + clientUDP);
		Addresses recivedUDPAddress = restTemplate.postForObject(serverHttp, sendUDPAddress, Addresses.class);
		
		String serverUDP = recivedUDPAddress.getUdp();
		logger.debug("Recived udp: " + serverUDP);
		connectionsProperties.getServers().setUdp(serverUDP);
	}
}