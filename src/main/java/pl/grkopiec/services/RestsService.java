package pl.grkopiec.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.components.ConnectionsProperties;
import pl.grkopiec.domains.Addresses;

@Service
public class RestsService {
	private static final Logger logger = LogManager.getLogger(RestsService.class);
	
	@Autowired
	private ConnectionsProperties connectionsProperties;
	
	public void reciveHttpUDP(Addresses addresses) {
		logger.debug("Recived http: " + addresses.getHttp());
		logger.debug("Recived tcp: " + addresses.getTcp());
		connectionsProperties.getServers().setHttp(addresses.getHttp());
		connectionsProperties.getServers().setTcp(addresses.getTcp());
	}
	
	public void sendAndReciveUDP() {
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
	
	public void closedServerInformation() {
		RestTemplate restTemplate = new RestTemplate();
		final String serverHttp = "http://localhost:8080/UDP/closed-server";
		logger.debug("Sending information about closed server");
		restTemplate.postForObject(serverHttp, null, Addresses.class);
		logger.debug("Sent information about closed server");
	}
}
