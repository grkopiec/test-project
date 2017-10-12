package pl.grkopiec.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

@Component
public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
 
    public UDPClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }
 
    public byte[] sendEcho(byte[] buf) throws IOException {
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, address, 5555);
        socket.send(packet);
        socket.receive(packet);
        return packet.getData();
    }
 
    public void close() {
        socket.close();
    }	
}
