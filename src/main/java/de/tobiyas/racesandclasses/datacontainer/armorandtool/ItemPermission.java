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
package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class ItemPermission implements AbstractItemPermission {

	/**
	 * The Item to check against
	 */
	private final Material material;
	
	
	/**
	 * Creates an Item Permission for a specific Item.
	 * 
	 * @param material to check
	 */
	public ItemPermission(Material material) {
		this.material = material;
	}

	
	@Override
	public boolean hasPermission(ItemStack item) {
		return item.getType() == material;
	}

	
	@Override
	public boolean isAlreadyRegistered(ItemQuality quality) {
		return false;
	}
	
	@Override
	public String toString(){
		return material.name();
	}

}
