package de.tobiyas.racesandclasses.commands.chat.channels;

import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.configuration.member.MemberConfig;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class CommandExecutor_Channel extends Observable implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_Channel(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("channel").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /channel.");
		}
		
		TutorialManager.registerObserver(this);
		this.setChanged();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			sender.sendMessage(ChatColor.RED + "Channels are disabled.");
			return true;
		}
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be used by Players.");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 0){
			postHelp(player);
			return true;
		}
		
		String channelCommand = args[0];
		
		if(channelCommand.equalsIgnoreCase("info")){
			String channelName = "";
			if(args.length == 2)
				channelName = args[1];
			
			postChannelInfo(player, channelName);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("list")){
			listChannels(player);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("change") || channelCommand.equalsIgnoreCase("post") || channelCommand.equalsIgnoreCase("switch")){
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use the command like this:" + 
									ChatColor.LIGHT_PURPLE + "/channel change <channelname>");
				return true;
			}

			String changeTo = args[1];
			changeChannel(player, changeTo);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("join")){
			if(args.length == 1 || args.length > 3){
				player.sendMessage(ChatColor.RED + "The command: " + ChatColor.LIGHT_PURPLE + "/channel join <channelname> [password]" + 
									ChatColor.RED + " needs a channelname. And optionaly a password");
				return true;
			}
			String channelName = args[1];
			String password = "";
			if(args.length == 3)
				password = args[2];
			
			joinChannel(player, channelName, password);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("leave")){
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel leave <channelname>");
				return true;
			}
			String channelName = args[1];
			leaveChannel(player, channelName);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("create")){
			if(args.length == 1 || args.length > 4){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel create <channelname> [channelType] [password]");
				return true;
			}
			String channelName = args[1];
			String channelType = "PublicChannel";
			String channelPassword = "";
			
			if(args.length > 2)
				channelType = args[2];
			
			if(args.length > 3)
				channelPassword = args[3];
			
			createChannel(player, channelName, channelType, channelPassword);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("ban")){
			if(!(args.length == 3 ||  args.length == 4)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel ban <channelname> <playername> [time in sec]");
				return true;
			}
			
			int time = Integer.MAX_VALUE;
			try{
				time = Integer.valueOf(args[3]);
			}catch(Exception e){
			}
			
			ChannelManager.GetInstance().banPlayer(player, args[2], args[1], time);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("unban")){
			if(!(args.length == 3)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel unban <channelname> <playername>");
				return true;
			}
			
			ChannelManager.GetInstance().unbanPlayer(player, args[2], args[1]);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("mute")){
			if(!(args.length == 3 ||  args.length == 4)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel mute <channelname> <playername> [time in sec]");
				return true;
			}
			
			int time = Integer.MAX_VALUE;
			try{
				time = Integer.valueOf(args[3]);
			}catch(Exception e){
			}
			
			ChannelManager.GetInstance().mutePlayer(player, args[2], args[1], time);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("unmute")){
			if(!(args.length == 3)){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel unmute <channelname> <playername>");
				return true;
			}
			
			ChannelManager.GetInstance().unmutePlayer(player, args[2], args[1]);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("edit")){
			if(args.length != 4){
				player.sendMessage(ChatColor.RED + "Wrong usage. Use: /channel edit <channelname> <channelproperty> <newValue>");
				return true;
			}
			String channel = args[1];
			String property = args[2];
			String newValue = args[3];
			
			channelEdit(player, channel, property, newValue);
			return true;
		}
		
		
		postHelp(player);
		return true;
	}
	
	private void postHelp(Player player){
		player.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "info " + ChatColor.AQUA + "[channelname]");
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "list");
		
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "<post/change/switch> " + ChatColor.YELLOW + "<channelname>");
		
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "join " + ChatColor.YELLOW + "<channelname> " + 
							ChatColor.AQUA + "[password]");
		
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "leave " + ChatColor.YELLOW + "<channelname> ");
		
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "create " + ChatColor.YELLOW + "<channelname> " +
						   ChatColor.AQUA + "[channeltype] [password]");
		
		player.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "edit " + ChatColor.YELLOW + "<channelname> " +
							ChatColor.AQUA + "<property> <newValue>");
	}
	
	private void postChannelInfo(Player player, String channel){
		if(channel == ""){
			channel = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName()).getCurrentChannel();
		}
		
		player.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + " Channel Information: " + 
							ChatColor.AQUA + channel + ChatColor.YELLOW + " =====");
		ChannelManager.GetInstance().postChannelInfo(player, channel);
	}
	
	private void listChannels(Player player){
		player.sendMessage(ChatColor.YELLOW + "======" + ChatColor.RED + "Channel-List:" + ChatColor.YELLOW + "=====");
		player.sendMessage(ChatColor.YELLOW + "HINT: Format is: " + ChatColor.BLUE + "ChannelName: " + ChatColor.AQUA + "ChannelLevel");
		for(ChannelLevel level : ChannelLevel.values())
			for(String channel : ChannelManager.GetInstance().listAllPublicChannels()){
				if(ChannelManager.GetInstance().getChannelLevel(channel) == level){
					String addition = "";
					if(ChannelManager.GetInstance().isMember(player.getName(), channel))
						addition =  ChatColor.YELLOW + "   <-[Joined]";
					player.sendMessage(ChatColor.BLUE + channel + ": " + ChatColor.AQUA + level.name() + addition);
				}
			}
		
		this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.channels, 1));
		this.setChanged();
	}
	
	private void joinChannel(Player player, String channelName, String password){
		ChannelLevel level = ChannelManager.GetInstance().getChannelLevel(channelName);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
			return;
		}
		
		ChannelManager.GetInstance().joinChannel(player, channelName, password, true);
	}
	
	private void leaveChannel(Player player, String channelName){
		ChannelLevel level = ChannelManager.GetInstance().getChannelLevel(channelName);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
			return;
		}
		
		ChannelManager.GetInstance().leaveChannel(player, channelName, true);
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		if(channelName.equalsIgnoreCase(config.getCurrentChannel())){
				config.setValue(MemberConfig.chatChannel, "Global");
		}
			
	}
	
	private void createChannel(Player player, String channelName, String channelType, String channelPassword){
		ChannelLevel channelLevel = getChannelLevel(channelType);
		if(channelLevel == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Channel Level could not be recognized: " + ChatColor.LIGHT_PURPLE + channelType);
			return;
		}
		
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel){
			player.sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + channelLevel);
			return;
		}
		
		if(channelLevel == ChannelLevel.PasswordChannel && 
		   !plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePassword))
			return;
		
		if(channelLevel == ChannelLevel.PublicChannel && 
		   !plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePublic))
			return;
		
		if(channelLevel == ChannelLevel.PrivateChannel && 
			!plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePrivate))
			return;
		
		if(channelLevel != ChannelLevel.PasswordChannel && channelPassword != "")
			player.sendMessage(ChatColor.YELLOW + "[INFO] You try to create a non-password channel with a password. The password will be ignored.");
		
		
		if(ChannelManager.GetInstance().getChannelLevel(channelName) != ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "This channel already exisists.");
			return;
		}
		
		ChannelManager.GetInstance().registerChannel(channelLevel, channelName, channelPassword, player);
	}
	
	private void changeChannel(Player player, String changeTo){
		ChannelLevel level = ChannelManager.GetInstance().getChannelLevel(changeTo);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + changeTo);
			return;
		}
		
		if(level != ChannelLevel.LocalChannel){
			if(!ChannelManager.GetInstance().isMember(player.getName(), changeTo)){
				player.sendMessage(ChatColor.RED + "You are no member of: " + ChatColor.LIGHT_PURPLE + changeTo);
				return;
			}
		}
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		if(config == null){
			player.sendMessage(ChatColor.RED + "Something gone wrong with your config. Try relogging or ask an Admin.");
			return;
		}
		
		config.setValue(MemberConfig.chatChannel, changeTo);
		player.sendMessage(ChatColor.GREEN + "You now write in the channel: " + ChatColor.AQUA + changeTo);
		
		if(changeTo.equalsIgnoreCase("tutorial")){
			this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.channels, 4));
			this.setChanged();
		}
	}
	
	private ChannelLevel getChannelLevel(String channelType){
		for(ChannelLevel level : ChannelLevel.values()){
			if(level.name().equalsIgnoreCase(channelType))
				return level;
		}
		
		return ChannelLevel.NONE;
	}
	
	private void channelEdit(Player player, String channel, String property, String newValue){
		ChannelLevel level = ChannelManager.GetInstance().getChannelLevel(channel);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channel);
			return;
		}
		
		if(level == ChannelLevel.GlobalChannel || level == ChannelLevel.WorldChannel){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelEdit)){
				return;
			}
		}
		
		if(level == ChannelLevel.RaceChannel){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelEdit)){
				return;
			}
		}
		
		ChannelManager.GetInstance().editChannel(player, channel, property, newValue);
	}
	
}