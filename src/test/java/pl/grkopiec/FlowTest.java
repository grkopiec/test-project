package pl.grkopiec;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import pl.grkopiec.domains.Addresses;
import pl.grkopiec.spring.config.RootConfig;
import pl.grkopiec.tcp.TCPServer;
import pl.grkopiec.udp.UDPServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlowTest {
	@Autowired
	private TCPServer tcpServer;
	@Autowired
	private UDPServer udpServer;
	
	@Test
	public void test1_startServers() {
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	tcpServer.start(4444);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					udpServer.start(5555);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	@Test
	public void test2_testRests() throws InterruptedException {
		final String clientHttp = "http://localhost:8080/UDP/addresses";
		final String serverHttp = "http://localhost:8080/UDP/udp-server";
		final String serverTcp = "tcp://localhost:4444/";
		Addresses addresses = new Addresses();
		addresses.setHttp(serverHttp);
		addresses.setTcp(serverTcp);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(clientHttp, addresses, Void.class);
		//wait on server close
		Thread.sleep(500);
	}
}
