/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.InvisibleTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker.FriendDetectEvent;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.player.PlayerUtils;

public class InvisibleTrait extends AbstractContinousCostMagicSpellTrait  {

	/**
	 * If it should abort when damage is received.
	 */
	private boolean abortOnReceiveDamage = false;
	
	/**
	 * Abort if do damage.
	 */
	private boolean abortOnDoDamage = false;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class, EntityTargetEvent.class})
	@Override
	public void generalInit() {}

	
	
	@Override
	protected TraitResults otherEventTriggered(EventWrapper eventWrapper, TraitResults result) {
		if(eventWrapper.getEvent() instanceof EntityTargetEvent){
			Entity target = ((EntityTargetEvent) eventWrapper.getEvent()).getTarget();
			if(target instanceof Player){
				Player player = (Player)target;
				if(activePlayersSchedulerMap.containsKey(player.getName())){
					((EntityTargetEvent) eventWrapper.getEvent()).setCancelled(true);
					return TraitResults.False();
				}
			}
		}
		
		return super.otherEventTriggered(eventWrapper, result);
	}

	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		if(!abortOnReceiveDamage) return;
		if(event.isCancelled()) return;
		if(event instanceof FriendDetectEvent) return;
		
		//If player has active and is breaking!
		if(event.getEntity().getType() == EntityType.PLAYER){
			RaCPlayer pl = RaCPlayerManager.get().getPlayer(event.getEntity().getUniqueId());
			//Is active -> Deactivate!
			if(isActivated(pl)) deactivate(pl);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		if(!abortOnDoDamage) return;
		if(event.isCancelled()) return;
		if(event instanceof FriendDetectEvent) return;
		
		//If player has active and is breaking!
		if(event.getDamager().getType() == EntityType.PLAYER){
			RaCPlayer pl = RaCPlayerManager.get().getPlayer(event.getDamager().getUniqueId());
			//Is active -> Deactivate!
			if(isActivated(pl)) deactivate(pl);
		}
	}
	

	@Override
	public String getName() {
		return "InvisibleTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		return "duration: " + time + " seconds. Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="InvisibleTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof InvisibleTrait)) return false;
		
		InvisibleTrait otherTrait = (InvisibleTrait) trait;
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int othertime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		return time > othertime;
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait makes you invisible.");
		return helpList;
	}


	@Override
	protected boolean activateIntern(RaCPlayer player) {	
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		time = modifyToPlayer(player, time, "duration");
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_invisible_toggle, 
				"duration", String.valueOf(time));
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 10, 1);
		
		setInvisibleToEverything(player);
		return true;
	}
	
	
	private void setInvisibleToEverything(RaCPlayer player){
		if(!player.isOnline()) return;
		
		for(Player otherPlayer : PlayerUtils.getOnlinePlayers()){
			otherPlayer.hidePlayer(player.getPlayer());
		}
		
		//Canceling targeting of nearby entities.
		for(Entity entity : player.getPlayer().getNearbyEntities(100, 100, 100)){
			if(!(entity instanceof Creature)) continue;
			Creature creature = (Creature) entity;
			if(creature.getTarget() == player){
				creature.setTarget(null);
			}
		}
	}
	
	
	private void setVisibleAgain(RaCPlayer player){
		if(!player.isOnline()) return;
		
		for(Player otherPlayer : PlayerUtils.getOnlinePlayers()){
			otherPlayer.showPlayer(player.getPlayer());
		}
	}
	
	
	@Override
	protected boolean deactivateIntern(RaCPlayer player){
		setVisibleAgain(player);
		player.sendTranslatedMessage(Keys.trait_faded, "name", getDisplayName());
		return true;
	}


	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(classToExpect=Boolean.class, fieldName="removeOnReceiveDamage", optional=true),
			@TraitConfigurationField(classToExpect=Boolean.class, fieldName="removeOnDoDamage", optional=true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.abortOnReceiveDamage = configMap.getAsBool("removeOnReceiveDamage", false);
		this.abortOnDoDamage = configMap.getAsBool("removeOnDoDamage", false);
	}

	@Override
	protected boolean tickInternal(RaCPlayer player) {
		setInvisibleToEverything(player); //just to be sure...
		return true;
	}
}
