package de.tobiyas.races.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.util.consts.PermissionNode;

public class CommandExecutor_RaceHeal implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_RaceHeal(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("raceheal").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /raceheal.");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(args.length == 0){
			if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)){
				if(sender instanceof Player){
					Player player = (Player) sender;
					HealthManager.getHealthManager().resetHealth(player.getName());
					player.sendMessage(ChatColor.GREEN + "You have been healed.");
				}else sender.sendMessage(ChatColor.RED + "You have to be a Player to use this command.");
			}else return true;
		}
		
		if(args.length == 1){
			if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healOther)){
				Player other = Bukkit.getPlayer(args[0]);
				if(other != null){
					Player player = (Player) sender;
					HealthManager.getHealthManager().resetHealth(other.getName());
					other.sendMessage(ChatColor.GREEN + "You have been healed from: " + ChatColor.LIGHT_PURPLE + player.getName());
					player.sendMessage(ChatColor.GREEN + "You have healed: " + ChatColor.LIGHT_PURPLE +  other.getName());
				}else sender.sendMessage(ChatColor.RED + "You have to be a Player to use this command.");
			}else return true;
		}
		
		if(args.length > 1){
			sender.sendMessage(ChatColor.RED + "Wrong usage. Use: /raceheal [PlayerName]");
			return true;
		}
		
		return true;
	}

}