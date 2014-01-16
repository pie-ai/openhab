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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openhab.binding.netplug.NetPlugBindingProvider;
import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author patrick.stricker
 * @since 1.3.0
 */
public class NetPlugGenericBindingProvider extends
		AbstractGenericBindingProvider implements NetPlugBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "netplug";
	}

	/**
	 * @{inheritDoc Switch NetworkPlug0 "Steckdose 0" (NetworkPlug)
	 *              {netplug="192.168.178.44:0"} bindingConfig =
	 *              192.168.178.44:0
	 * 
	 */
//	@Override
	public void validateItemType(Item item, String bindingConfig)
			throws BindingConfigParseException {
		// if (!(item instanceof SwitchItem || item instanceof DimmerItem)) {
		// throw new BindingConfigParseException("item '" + item.getName()
		// + "' is of type '" + item.getClass().getSimpleName()
		// +
		// "', only Switch- and DimmerItems are allowed - please check your *.items configuration");
		// }

		if (item instanceof SwitchItem) {
			// Relais
			NetPlugBindingConfig cfg = NetPlugBindingConfig.parse(
					item.getName(), bindingConfig);

			if (cfg.getType() != NetPlugServiceType.RELAIS) {
				throw new BindingConfigParseException(
						"not a valid switch, bindingConfiguration:"
								+ cfg.toString());
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		String[] bindings = bindingConfig.split(",");
		for (String binding : bindings) {
			NetPlugBindingConfig cfg = NetPlugBindingConfig.parse(
					item.getName(), binding);
			addBindingConfig(item, cfg);
		}

	}

//	@Override
	public NetPlugBindingConfig getConfig(String hostAddress,
			NetPlugServiceType type, int serviceId) {
		if (bindingConfigs.containsKey(type.name())) {
			Map<String, NetPlugBindingConfig> netPlugBindingConfigsForType = (Map<String, NetPlugBindingConfig>) bindingConfigs
					.get(type.name());
			for (String name : netPlugBindingConfigsForType.keySet()) {
				NetPlugBindingConfig config = netPlugBindingConfigsForType
						.get(name);
				if (config.getIP().equals(hostAddress)) {
					if (config.getId() == serviceId) {
						return config;
					}
				}

			}
		}
		return null;
	}

//	@Override
	public NetPlugBindingConfig getConfig(String itemName) {
		if (bindingConfigs.containsKey(itemName)) {
			return (NetPlugBindingConfig) bindingConfigs.get(itemName);
		}
		return null;
	}
}
