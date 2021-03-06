package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import org.bukkit.World;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;

public class TimeModifier extends AbstractModifier {

	private final int minTime;
	private final int maxTime;
	
	
	
	public TimeModifier(int minTime, int maxTime, double modifier, String toModify) {
		super(modifier, toModify);
	
		this.minTime = minTime;
		this.maxTime = maxTime;
	}


	@Override
	public boolean canBeApplied(String toModify, RaCPlayer player) {
		if(!super.canBeApplied(toModify, player)) return false;
		
		World world = player.getWorld();
		long currentTime = world.getTime();
		int hour = (int) (currentTime / 1000);
		
		return hour >= minTime && hour <= maxTime;
	}

	
	public static TimeModifier generate(String descriptor, double modifier, String toModify){
		String[] split = descriptor.split("-");
		if(split.length != 2) return null;
		
		try{
			int minTime = Integer.parseInt(split[0]);
			int maxTime = Integer.parseInt(split[1]);
			
			return new TimeModifier(minTime, maxTime, modifier, toModify);
		}catch(NumberFormatException exp){
			//not parseable.
			return null;
		}
	}
}
