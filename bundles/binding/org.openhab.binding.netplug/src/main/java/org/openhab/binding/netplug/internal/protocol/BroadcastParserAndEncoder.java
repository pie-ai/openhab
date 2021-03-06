package org.openhab.binding.netplug.internal.protocol;

import org.openhab.binding.netplug.NetPlugBroadcast;
import org.openhab.binding.netplug.NetPlugCommand;
import org.openhab.binding.netplug.NetPlugMessage;
import org.openhab.binding.netplug.internal.protocol.impl.NetPlugBroadcastImpl;
import org.openhab.binding.netplug.internal.protocol.impl.NetPlugCommandImpl;
import org.openhab.binding.netplug.internal.protocol.impl.ServiceStateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastParserAndEncoder {
	private static final String NET_PLUG_BROADCAST = "NetPlugV1|B:";
	private static final String NET_PLUG_SET = "NetPlugV1|S:";
	private static Logger LOG = LoggerFactory
			.getLogger(BroadcastParserAndEncoder.class);

	public String encode(NetPlugBroadcast broadcast) {
		StringBuilder buf = new StringBuilder(NET_PLUG_BROADCAST);
		for (int i = 0; i < broadcast.getServices().size(); i++) {
			ServiceState state = broadcast.getServices().get(i);
			if (i > 0) {
				buf.append(",");
			}
			buf.append(state.getType().getIdentifier()).append(state.getId())
					.append("=").append(state.getState());
		}
		return buf.toString();
	}

	public String encode(NetPlugCommand command) {
		StringBuilder buf = new StringBuilder(NET_PLUG_SET);
		for (int i = 0; i < command.getServices().size(); i++) {
			ServiceState state = command.getServices().get(i);
			if (i > 0) {
				buf.append(",");
			}
			buf.append(state.getType().getIdentifier()).append(state.getId())
					.append("=").append(state.getState());
		}
		return buf.toString();
	}

	private void parse(NetPlugMessage message, String content) {
		String[] services = content.split(",");
		for (String serviceString : services) {
			// serviceString = R0=1 D2=FF
			
			// TODO hier schlägt das parsen fehl!
			String typeString = serviceString.substring(0, 1);
			String[] typeIdAndState = serviceString.substring(1).split("=");
			String idString = typeIdAndState[0];
			String stateAsString = "-" + typeIdAndState[1] + "-";
			String stateAsString2 = stateAsString.substring(1,
					stateAsString.length() - 1).trim();
			// stateAsString = 0-FF
			long stateAsLong = Long.decode("#" + stateAsString2);
			int state = (int) stateAsLong;
			int id = Integer.parseInt(idString);

			NetPlugServiceType type = NetPlugServiceType
					.forIdentifier(typeString);

			if (type != null) {
				message.getServices().add(ServiceStateFactory.create(type, id, state));
			} else {
				LOG.error("unhandled service: '" + serviceString + "'");
			}
		}
	}

	public NetPlugBroadcast parseBroadcast(final String broadcast,
			final String hostName, final String hostAddress, int port) {
		// NetPlugV1|B:SR0=0,SR1=0                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
		if (!broadcast.startsWith(NET_PLUG_BROADCAST)) {
			LOG.error("invalid broadcast: '" + broadcast + "'");
			return null;
		}

		String content = broadcast.substring(NET_PLUG_BROADCAST.length());

		NetPlugBroadcastImpl result = new NetPlugBroadcastImpl();
		result.setHostName(hostName);
		result.setPort(port);
		result.setHostAddress(hostAddress);
		parse(result, content);
		return result;
	}

	public NetPlugCommand parseCommand(final String command,
			final String hostName, final String hostAddress, int port) {
		// NetPlugV1|S:SR0=0,SR1=0                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
		if (!command.startsWith(NET_PLUG_SET)) {
			LOG.error("invalid command: '" + command + "'");
			return null;
		}

		String content = command.substring(NET_PLUG_SET.length());

		NetPlugCommandImpl result = new NetPlugCommandImpl();
		result.setHostName(hostName);
		result.setPort(port);
		result.setHostAddress(hostAddress);
		parse(result, content);
		return result;
	}
}
