package org.openhab.binding.netplug.internal.protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openhab.binding.netplug.NetPlugBroadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerPool {
	private static final Logger LOG = LoggerFactory.getLogger(ServerPool.class);
	private Map<String, ServerPoolEntry> servers = new HashMap<String, ServerPoolEntry>();

	public List<ServerPoolEntry> getAllServers() {
		Collection<ServerPoolEntry> allServers = servers.values();
		return new ArrayList<ServerPoolEntry>(allServers);
	}

	public List<ServiceState> updateServer(NetPlugBroadcast server) {
		if (servers.containsKey(server.getHostName())) {
			servers.get(server.getHostName()).setLastContactAt(new Date());
			NetPlugBroadcast old = servers.get(server.getHostName()).getServer();
			servers.get(server.getHostName()).setServer(server);
			List<ServiceState> changed = new LinkedList<ServiceState>();
			for (int i = 0; i < server.getServices().size(); i++) {
				if (!server.getServices().get(i)
						.equals(old.getServices().get(i))) {
					changed.add(server.getServices().get(i));
				}
			}
			return changed;
		} else {
			LOG.info("registered new server: " + server.toString());
			servers.put(server.getHostName(), new ServerPoolEntry(server));
			return server.getServices();
		}
	}

	public ServerPool() {
		super();
	}

	public ServerPoolEntry getServer(String hostName) {
		if (servers.containsKey(hostName)) {
			return servers.get(hostName);
		} else {
			return null;
		}
	}
}
