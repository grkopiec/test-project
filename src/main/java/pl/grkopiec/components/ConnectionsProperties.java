package pl.grkopiec.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import pl.grkopiec.domains.Addresses;

@Component
public class ConnectionsProperties {
	private static final Logger logger = LogManager.getLogger(ConnectionsProperties.class);
	
	private Addresses servers;
	private Addresses clients;
	
	public ConnectionsProperties() {
		logger.debug("Initializing application connections properties");
		clients = new Addresses();
		clients.setHttp("http://localhost:8080/UDP/udp-client");
		clients.setTcp("tcp://localhost:4444/");
		clients.setUdp("udp://localhost:5555/");
		servers = new Addresses();
	}

	public Addresses getServers() {
		return servers;
	}

	public void setServers(Addresses servers) {
		this.servers = servers;
	}

	public Addresses getClients() {
		return clients;
	}

	public void setClients(Addresses clients) {
		this.clients = clients;
	}
}
