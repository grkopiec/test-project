package pl.grkopiec;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.grkopiec.spring.config.RootConfig;
import pl.grkopiec.tcp.TCPClient;
import pl.grkopiec.tcp.TCPServer;
import pl.grkopiec.utils.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TCPTest {
	@Autowired
	private TCPServer server;
	@Autowired
	private TCPClient client;
	
	@Before
	public void setup() throws UnknownHostException, IOException, InterruptedException {
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	server.start(4444);
				} catch (IOException exception) {
					throw new RuntimeException(exception);
				}
			}
		}).start();
		
	    client.startConnection("localhost", 4444);
	}
	
	@Test
	public void test1_testInitMessageAndCloseServer() throws UnknownHostException, IOException {
		client.sendInitMessage();
		DataInputStream tcpInputStream = client.getInputStream();
		byte[] frame = new byte[100];
		for (int i = 0; i < 20; i++) {
    		tcpInputStream.read(frame);
    		Assert.assertArrayEquals(StringUtils.frame, frame);
		}
		tcpInputStream = client.getInputStream();
		frame = new byte[StringUtils.endTransmitionFrame.length];
		tcpInputStream.read(frame);
		Assert.assertArrayEquals(StringUtils.endTransmitionFrame, frame);
	}
	
	@Test(expected = ConnectException.class)
	public void test2_openConnectionException() throws ConnectException, IOException, InterruptedException {
		//wait on server close
		Thread.sleep(500);
		client.newConnectionMessage("localhost", 4444, new byte[20]);
	}
}
