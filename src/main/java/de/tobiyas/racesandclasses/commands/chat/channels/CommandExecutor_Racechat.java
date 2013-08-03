/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.chat.channels;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;


public class CommandExecutor_Racechat implements CommandExecutor {
	private RacesAndClasses plugin;

	public CommandExecutor_Racechat(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racechat").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racechat.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			sender.sendMessage(ChatColor.RED + "RaceChat is not active.");
			return true;
		}
		
		Player player = (Player) sender;		
		AbstractTraitHolder container = RaceManager.getInstance().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = RaceManager.getInstance().getDefaultHolder();
		if(container == null || container == stdContainer){
			player.sendMessage(ChatColor.RED + "You have no race selected.");
			return true;
		}
		
		String message = "";
		for(String snippet : args){
			message += snippet + " ";
		}

		ChannelManager.GetInstance().broadcastMessageToChannel(container.getName(), player, message);
		return true;
	}
}