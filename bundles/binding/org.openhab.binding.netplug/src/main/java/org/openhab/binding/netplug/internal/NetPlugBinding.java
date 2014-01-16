/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2013, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */
package org.openhab.binding.netplug.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Dictionary;

import org.openhab.binding.netplug.NetPlugBindingProvider;
import org.openhab.binding.netplug.NetPlugBroadcast;
import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.binding.netplug.internal.protocol.ServiceState;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service like
 * querying a Website/Device.
 * 
 * @author patrick.stricker
 * @since 1.3.0
 */
public class NetPlugBinding extends AbstractBinding<NetPlugBindingProvider>
		implements ManagedService, NetPlugChangeListener {

	private static final Logger logger = LoggerFactory
			.getLogger(NetPlugBinding.class);

	private NetPlugUdpListener listener = null;
	private Thread listenerThread = null;

	public NetPlugBinding() {
		super();
		logger.debug("new instance");
	}

	public void activate() {
		logger.debug("activate");
		listener = new NetPlugUdpListener(this);
		listenerThread = new Thread(listener);
		listenerThread.start();
	}

	public void deactivate() {
		// deallocate resources here that are no longer needed and
		// should be reset when activating this binding again
		logger.debug("deactivate");
		listenerThread.stop();
		listener.shutdown();
		listener = null;
		listenerThread = null;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
  		logger.debug("internalReceiveCommand() is called!");
		for (NetPlugBindingProvider provider : providers) {
			NetPlugBindingConfig config = provider.getConfig(itemName);
			if (config != null) {
				String data = null;
				if (command instanceof OnOffType) {
					OnOffType onOff = (OnOffType) command;
					data = "S:R" + config.getId() + "="
							+ (onOff == OnOffType.ON ? "1" : "0");
				}

				if (data != null) {
					DatagramSocket clientSocket;
					try {
						clientSocket = new DatagramSocket();
						InetAddress IPAddress = InetAddress.getByName(config
								.getIP());
						DatagramPacket sendPacket = null;
						byte[] sendData = data.getBytes();
						sendPacket = new DatagramPacket(sendData,
								sendData.length, IPAddress, config.getPort());
						clientSocket.send(sendPacket);

					} catch (SocketException e) {
						logger.error(
								"could not send command '" + data + "' to '"
										+ config.getIP() + ":"
										+ config.getPort() + ":"
										+ e.getMessage(), e);
					} catch (UnknownHostException e) {
						logger.error(
								"could not send command '" + data + "' to '"
										+ config.getIP() + ":"
										+ config.getPort() + ":"
										+ e.getMessage(), e);
					} catch (IOException e) {
						logger.error(
								"could not send command '" + data + "' to '"
										+ config.getIP() + ":"
										+ config.getPort() + ":"
										+ e.getMessage(), e);
					}
				} else {
					logger.debug("no data to send");
				}
			}
		}
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");

	}

	/**
	 * @{inheritDoc
	 */
//	@Override
	public void updated(Dictionary<String, ?> config)
			throws ConfigurationException {
		if (config != null) {

			// to override the default refresh interval one has to add a
			// parameter to openhab.cfg like
			// <bindingName>:refresh=<intervalInMs>
			// String refreshIntervalString = (String) config.get("refresh");
			// if (StringUtils.isNotBlank(refreshIntervalString)) {
			// refreshInterval = Long.parseLong(refreshIntervalString);
			// }

			// read further config parameters here ...

			// setProperlyConfigured(true);
		}
	}

//	@Override
	public void notifyChange(NetPlugBroadcast server, ServiceState service) {
		State state = null;
		if (service.getType() == NetPlugServiceType.RELAIS) {
			state = service.getState() == 1 ? OnOffType.ON : OnOffType.OFF;
		}
		else if (service.getType() == NetPlugServiceType.TEMPERATURE_SENSOR) {
			state = new DecimalType(service.getState() / 100);
		}
		// else if (service.getType() == NetPlugServiceType.DIGITAL_OUTPUT)
		// {
		//
		// Color.RGBtoHSB(r, g, b, hsbvals)
		// HSBType s = new HSBType(h, s, b)
		// }
		else {

			logger.debug("unhandled service change: " + server.getHostAddress()
					+ ":" + service.getId());
		}

		boolean handled = false;
		for (NetPlugBindingProvider provider : providers) {
			NetPlugBindingConfig config = provider
					.getConfig(server.getHostAddress(), service.getType(),
							service.getId());

			if (config != null) {
				eventPublisher.postUpdate(config.getItemName(), state);
				handled = true;
			}
		}
		if (!handled) {
			logger.debug("unhandled service change: " + server.getHostAddress()
					+ ":" + service.getId());
		}
	}
}
