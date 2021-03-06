package org.openhab.binding.netplug.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import org.openhab.binding.netplug.NetPlugCommand;
import org.openhab.binding.netplug.internal.protocol.BroadcastParserAndEncoder;
import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.binding.netplug.internal.protocol.ServiceState;
import org.openhab.binding.netplug.internal.protocol.impl.NetPlugBroadcastImpl;
import org.openhab.binding.netplug.internal.protocol.impl.ServiceStateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetPlugTestServer implements Runnable {
	private static final Logger LOG = LoggerFactory
			.getLogger(NetPlugTestServer.class);
	private DatagramSocket serverSocket = null;
	private NetPlugBroadcastImpl state = null;
	private int port;
	private long lastBroadcast = 0;
	private BroadcastParserAndEncoder parserAndEncoder = new BroadcastParserAndEncoder();

	private NetPlugTestServer(int port, ServiceState... services)
			throws IOException {
		state = new NetPlugBroadcastImpl();
		state.addServices(services);
		this.port = port;
	}

	private void broadcast() throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("255.255.255.255");

		String broadcast = parserAndEncoder.encode(state);
		LOG.debug("broadcasting:" + broadcast);
		byte[] sendData = broadcast.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, 10666);
		clientSocket.send(sendPacket);
		lastBroadcast = new Date().getTime();
	}

	public static void main(String[] args) throws IOException {
		NetPlugTestServer server = new NetPlugTestServer(11000,
				ServiceStateFactory.create(NetPlugServiceType.RELAIS, 0, 0),
				ServiceStateFactory.create(NetPlugServiceType.RELAIS, 1, 1));

		server.parserAndEncoder.parseBroadcast("NetPlugV1|B:D0=08,R0=1 ",
				"test-host", "test-host-address", 10666);

		Thread listenerThread = new Thread(server);
		listenerThread.start();
	}

	public void shutdown() {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	public NetPlugTestServer() {
		super();
	}

	public void run() {
		try {
			broadcast();
			BroadcastParserAndEncoder parser = new BroadcastParserAndEncoder();
			serverSocket = new DatagramSocket(port);
			serverSocket.setBroadcast(true);
			byte[] receiveData = new byte[1024];
			while (true) {

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				serverSocket.receive(receivePacket);

				InetAddress ip = receivePacket.getAddress();
				LOG.debug("receivePacket.getSocketAddress():"
						+ receivePacket.getSocketAddress().toString());
				LOG.debug("ip:" + ip);

				String hostName = ip.getCanonicalHostName();
				String hostAddress = ip.getHostAddress();

				int port = receivePacket.getPort();
				String sentence = new String(receivePacket.getData());

				LOG.debug("trying to parse:" + sentence);

				NetPlugCommand command = null;
				try {
					command = parser.parseCommand(sentence, hostName,
							hostAddress, port);
					LOG.debug("parsed command:" + command.toString());
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}

				// if (server != null) {
				// List<ServiceState> services = serverPool
				// .updateServer(server);
				// if (services.size() > 0) {
				// // for (ServiceState service : services) {
				// // listener.notifyChange(server, service);
				// // }
				// } else {
				// // nothing happened
				// }
				// }

			}
		} catch (IOException ioe) {
			LOG.error("could not listen to udp:" + ioe.getMessage(), ioe);
		} catch (Throwable t) {
			LOG.error("could not listen to udp:" + t.getMessage(), t);
		} finally {
			shutdown();
			LOG.debug("stopped");
		}
	}

}
