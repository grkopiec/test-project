package pl.grkopiec.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.springframework.stereotype.Component;

@Component
public class UDPServer {
    private DatagramSocket socket;
    
    public void start(int serverPort) throws IOException {
        socket = new DatagramSocket(serverPort);
        boolean running = true;
        
        while (running) {
            byte[] buf = new byte[100];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
             
            InetAddress address = packet.getAddress();
            int clientPort = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, clientPort);
            String received = new String(packet.getData(), 0, packet.getLength());
             
            if (received.equals("end")) {
                running = false;
                continue;
            }
			socket.send(packet);
        }
        socket.close();
    }
}
