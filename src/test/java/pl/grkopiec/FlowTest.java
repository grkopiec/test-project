package pl.grkopiec;

import java.net.SocketException;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.domains.Addresses;

public class FlowTest {
	@Test
	public void testFlow() throws SocketException {
		final String clientHttp = "http://localhost:8080/UDP/addresses";
		final String serverHttp = "http://localhost:8080/UDP/udp-server";
		final String serverTcp = "tcp://localhost:4444/";
		RestTemplate restTemplate = new RestTemplate();
		Addresses addresses = new Addresses();
		addresses.setHttp(serverHttp);
		addresses.setTcp(serverTcp);
		
//		new Thread(new Runnable() {
//			public void run() {
//		        try {
//		        	new TCPServer().start(4444);
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}).start();
//		new Thread(new Runnable() {
//			public void run() {
//				try {
//			    	new UDPServer().start(5555);
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}).start();
		
		restTemplate.postForObject(clientHttp, addresses, Void.class);
	}
}
