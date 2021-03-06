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
package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class PlayerLostEXPEvent extends LevelEvent implements Cancellable{

private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * indicates if the Event is canceled
	 */
	private boolean cancled = false;
	
	
	/**
	 * The EXP lost
	 */
	private int exp;
	
	
	/**
	 * Creates the Event with the Player loses the EXP
	 * and the EXP lost.
	 * 
	 * @param offlinePlayer
	 * @param exp
	 */
	public PlayerLostEXPEvent(RaCPlayer player, int exp) {
		super(player);
		
		this.exp = exp;
	}


	/**
	 * @return the exp
	 */
	public int getExp() {
		return exp;
	}


	/**
	 * @param exp the exp to set
	 */
	public void setExp(int exp) {
		this.exp = exp;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }


	@Override
	public boolean isCancelled() {
		return cancled;
	}


	@Override
	public void setCancelled(boolean cancel) {
		this.cancled = cancel;
	}

}
