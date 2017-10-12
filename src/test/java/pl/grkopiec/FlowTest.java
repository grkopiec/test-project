package pl.grkopiec;

import java.io.IOException;
import java.net.SocketException;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.domains.Addresses;
import pl.grkopiec.tcp.TCPServer;
import pl.grkopiec.udp.UDPServer;

public class FlowTest {
	@Test
	public void testFlow() throws SocketException {
		final String uri = "http://localhost:8080/UDP/addresses";
		RestTemplate restTemplate = new RestTemplate();
		Addresses addresses = new Addresses();
		addresses.setHttp("http://localhost:8080/test");
		addresses.setTcp("tcp://localhost:4444/");
		
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	new TCPServer().start(4444);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
			    	new UDPServer().start(5555);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		restTemplate.postForObject(uri, addresses, Void.class);
	}
}
