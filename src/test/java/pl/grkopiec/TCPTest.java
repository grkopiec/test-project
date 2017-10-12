package pl.grkopiec;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.grkopiec.tcp.TCPClient;
import pl.grkopiec.tcp.TCPServer;
import pl.grkopiec.utils.StringUtils;

public class TCPTest {
	TCPClient client;
	
	@Before
	public void setup() throws UnknownHostException, IOException, InterruptedException {
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	new TCPServer().start(4444);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
	    client = new TCPClient();
	    client.startConnection("localhost", 4444);
	}
	
	@Test
	public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws UnknownHostException, IOException {
	    byte[] resp1 = client.sendMessage("getFrame");
	    byte[] resp2 = client.sendMessage("getFrame");
	    byte[] resp3 = client.sendMessage("getFrame");
	    byte[] resp4 = client.sendMessage(".");
	     
	    Assert.assertArrayEquals(StringUtils.frame, resp1);
	    Assert.assertArrayEquals(StringUtils.frame, resp2);
	    Assert.assertArrayEquals(StringUtils.frame, resp3);
	    Assert.assertArrayEquals(null, resp4);
	}
	
	@After
	public void tearDown() throws IOException {
	    client.stopConnection();
	}
}
