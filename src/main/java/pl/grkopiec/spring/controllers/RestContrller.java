package pl.grkopiec.spring.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.domains.Addresses;
import pl.grkopiec.tcp.TCPClient;
import pl.grkopiec.udp.UDPClient;

@RestController
public class RestContrller {
	@Autowired
	private UDPClient uDPClient;
	@Autowired
	private TCPClient tCPClient;
	
	@RequestMapping(path = "/addresses", method = RequestMethod.POST)
	public ResponseEntity<Void> addresses(@RequestBody Addresses addresses) throws UnknownHostException, IOException {
		final String uri = "http://localhost:8080/UDP/udp-client";
		RestTemplate restTemplate = new RestTemplate();
		
		Addresses udpAddress = new Addresses();
		udpAddress.setUdp("tcp://localhost:5555/");
		udpAddress = restTemplate.postForObject(uri, addresses, Addresses.class);
		addresses.setUdp(udpAddress.getUdp());
		
		System.out.println(addresses.getTcp());
		String tcpIp = StringUtils.substringBetween(addresses.getTcp(), "//", ":");
		Integer tcpPort = Integer.parseInt(StringUtils.substringBetween(addresses.getTcp(), tcpIp + ":", "/"));
		tCPClient.startConnection(tcpIp, tcpPort);
	    byte[] resp1 = tCPClient.sendMessage("getFrame");
	    System.out.println(new String(resp1));
	    tCPClient.stopConnection();
	    
	    ByteArrayOutputStream recivedFullArray = new ByteArrayOutputStream();
	    for (int i = 0; i < resp1.length; i = i + 20) {
	    	byte[] chunk = Arrays.copyOfRange(resp1, i, i + 20);
	    	byte[] udpRecivedData = uDPClient.sendEcho(chunk);
	    	recivedFullArray.write(udpRecivedData);
	    }
	    byte[] recFullArr = recivedFullArray.toByteArray();
	    System.out.println(new String(recFullArr));
	    
	    uDPClient.sendEcho("end".getBytes());
		
		ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);
		return responseEntity;
	}
	
	//for test purposes
	@RequestMapping(path = "/udp-client", method = RequestMethod.POST)
	public ResponseEntity<Addresses> udpClient(@RequestBody Addresses addresses) {
		ResponseEntity<Addresses> responseEntity = new ResponseEntity<>(addresses, HttpStatus.OK);
		return responseEntity;
	}
}