package pl.grkopiec;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.grkopiec.spring.config.RootConfig;
import pl.grkopiec.udp.UDPClient;
import pl.grkopiec.udp.UDPServer;
import pl.grkopiec.utils.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
public class UDPTest {
	@Autowired
	private UDPServer server;
	@Autowired
	private UDPClient client;
    
    @Before
    public void setup() throws IOException{
		new Thread(new Runnable() {
			public void run() {
		        try {
		        	server.start(5555);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
    }
 
    @Test
    public void test1_sendEcho() throws IOException {
    	byte[] echo = client.sendEcho(StringUtils.chunkFrame);
        Assert.assertArrayEquals(StringUtils.chunkFrame, echo);
        echo = client.sendEcho(StringUtils.chunkFrame);
        Assert.assertArrayEquals(StringUtils.chunkFrame, echo);
    }
 
    @After
    public void closeServer() throws IOException {
        client.sendEcho(StringUtils.endFrame);
        client.close();
    }
}
