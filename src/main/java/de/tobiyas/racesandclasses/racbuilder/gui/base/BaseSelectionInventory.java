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
package de.tobiyas.racesandclasses.racbuilder.gui.base;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.racbuilder.gui.holdermanager.ClassSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.holdermanager.RaceSelectionInterface;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;

public class BaseSelectionInventory extends BasicSelectionInterface {

	/**
	 * The Item representing the Races
	 */
	private final ItemStack raceSelectionStack;
	
	/**
	 * The Item representing the Classes
	 */
	private final ItemStack classSelectionStack;
	
	
	public BaseSelectionInventory(Player player, RacesAndClasses plugin) {
		super(player, null, "Controls", "Select what to edit", plugin);
		
		raceSelectionStack = generateItem(Material.SKULL_ITEM, ChatColor.RED + "Races",
				ChatColor.LIGHT_PURPLE + "Edit your Races here");

		classSelectionStack = generateItem(Material.SKULL_ITEM, ChatColor.RED + "Classes",
				ChatColor.LIGHT_PURPLE + "Edit your Classes here");
		
		selectionInventory.setItem(3, raceSelectionStack);
		selectionInventory.setItem(4, classSelectionStack);
	}

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	
	@Override
	protected void onAcceptPressed() {
		performSave();
	}

	
	
	/**
	 * Saves the made changes to the files.
	 */
	protected void performSave(){
		//TODO implement me
	}

	
	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		RacesAndClasses plugin = (RacesAndClasses) this.plugin;
		
		if(item.equals(this.classSelectionStack)){			
			openNewView(new ClassSelectionInterface(player, this, plugin.getClassManager(), plugin));
			return;
		}

		if(item.equals(this.raceSelectionStack)){
			openNewView(new RaceSelectionInterface(player, this, plugin.getRaceManager(), plugin));
			return;
		}
	}

	
	@Override
	protected void onControlItemPressed(ItemStack item) {
	}
}
