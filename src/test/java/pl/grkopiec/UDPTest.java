package pl.grkopiec;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.grkopiec.udp.UDPClient;
import pl.grkopiec.udp.UDPServer;
import pl.grkopiec.utils.StringUtils;

public class UDPTest {
    UDPClient client;
    
    @Before
    public void setup() throws IOException{
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	new UDPServer().start(5555);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
        
        client = new UDPClient();
    }
 
    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() throws IOException {
    	byte[] echo = client.sendEcho(StringUtils.chunkFrame);
        Assert.assertArrayEquals(StringUtils.chunkFrame, echo);
        echo = client.sendEcho(StringUtils.chunkFrame);
        Assert.assertArrayEquals(StringUtils.chunkFrame, echo);
    }
 
    @After
    public void tearDown() throws IOException {
        client.sendEcho(StringUtils.endFrame);
        client.close();
    }
}
