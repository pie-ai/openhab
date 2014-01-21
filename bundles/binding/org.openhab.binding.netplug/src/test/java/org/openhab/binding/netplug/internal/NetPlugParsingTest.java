package org.openhab.binding.netplug.internal;

import static org.testng.Assert.assertNotNull;

import org.openhab.binding.netplug.internal.protocol.BroadcastParserAndEncoder;
import org.openhab.binding.netplug.internal.protocol.impl.ButtonPush;
import org.openhab.binding.netplug.internal.protocol.impl.NetPlugCommandImpl;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.testng.annotations.Test;
public class NetPlugParsingTest {
	@Test
	public void udp() throws BindingConfigParseException
	{
		NetPlugBindingConfig cfg = NetPlugBindingConfig.parse("test", "udp://192.168.0.1:10666/T0");
		assertNotNull(cfg);
	}
	
	
	@Test
	public void tcp() throws BindingConfigParseException
	{
		NetPlugBindingConfig cfg = NetPlugBindingConfig.parse("test", "http://192.168.178.21:80/netplug/|007:008:P0");
		assertNotNull(cfg);
		ButtonPush btn = new ButtonPush(0);
		NetPlugCommandImpl message = new NetPlugCommandImpl(btn);
		BroadcastParserAndEncoder encode = new BroadcastParserAndEncoder();
		NetPlugBinding.handleCommand(cfg, encode.encode(message));
	}
	
	
	@Test
	public void usb()
	{
		
	}
}
