/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.commands.debug;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.racbuilder.gui.base.BaseSelectionInventory;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.inventorymenu.stats.StringSelectionInterface;

public class CommandExecutor_Edit implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_Edit() {
		plugin = RacesAndClasses.getPlugin();

		String command = "racedit";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.racEdit)) return true;

		
		if(args.length > 0 && args[0].equalsIgnoreCase("test")){
			player.openInventory(new StringSelectionInterface(player, null, new HashMap<String, Object>(), "null", plugin));
			return true;
		}
		
		
		LanguageAPI.sendTranslatedMessage(sender, open_holder);
		player.openInventory(new BaseSelectionInventory(player, plugin));
		
		return true;
	}

}
