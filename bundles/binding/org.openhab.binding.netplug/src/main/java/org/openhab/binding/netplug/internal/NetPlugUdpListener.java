package org.openhab.binding.netplug.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import org.openhab.binding.netplug.NetPlugBroadcast;
import org.openhab.binding.netplug.internal.protocol.BroadcastParserAndEncoder;
import org.openhab.binding.netplug.internal.protocol.ServerPool;
import org.openhab.binding.netplug.internal.protocol.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetPlugUdpListener implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(NetPlugUdpListener.class);
	private ServerPool serverPool = new ServerPool();
	private NetPlugChangeListener listener = null;
	private DatagramSocket serverSocket = null;

	public void shutdown() {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	public NetPlugUdpListener(NetPlugChangeListener listener) {
		super();
		this.listener = listener;
	}

	public void run() {
		try {
			BroadcastParserAndEncoder parser = new BroadcastParserAndEncoder();
			serverSocket = new DatagramSocket(10666);
			serverSocket.setBroadcast(true);
			byte[] receiveData = new byte[1024];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				serverSocket.receive(receivePacket);

				InetAddress ip = receivePacket.getAddress();
//				logger.debug("receivePacket.getSocketAddress():"
//						+ receivePacket.getSocketAddress().toString());
//				logger.debug("ip:" + ip);

				String hostName = ip.getCanonicalHostName();
				String hostAddress = ip.getHostAddress();

				int port = receivePacket.getPort();
				String sentence = new String(receivePacket.getData());

				NetPlugBroadcast server = null;
				try {
					server = parser.parseBroadcast(sentence, hostName,
							hostAddress, port);
				} catch (NumberFormatException nfe) {
					logger.error(
							"could not parse '" + sentence + "':"
									+ nfe.getMessage(), nfe);
					nfe.printStackTrace();
				}

				if (server != null) {
					List<ServiceState> services = serverPool
							.updateServer(server);
					if (services.size() > 0) {
						for (ServiceState service : services) {
							listener.notifyChange(server, service);
						}
					} else {
						// nothing happened
					}
				}

			}
		} catch (IOException ioe) {
			logger.error("could not listen to udp:" + ioe.getMessage(), ioe);
		} catch (Throwable t) {
			logger.error("could not listen to udp:" + t.getMessage(), t);
		} finally {
			shutdown();
			logger.info("stopped");
		}
	}

}
