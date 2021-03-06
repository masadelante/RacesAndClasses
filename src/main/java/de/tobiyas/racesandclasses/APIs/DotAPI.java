package de.tobiyas.racesandclasses.APIs;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotBuilder;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotContainer;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotManager;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DamageType;

public class DotAPI {

	
	
	private static DotManager m(){
		return RacesAndClasses.getPlugin().getDotManager();
	}
	
	
	
	/**
	 * Dots an Entity.
	 * 
	 * @param entity to Dot
	 * @param builder the builder for the Dot.
	 * 
	 * @return if it worked.
	 */
	public static boolean addDot(LivingEntity target, DotBuilder builder){
		if(target == null || builder == null) return false;
		return m().dotEntity(target, builder);
	}
	
	
	/**
	 * Gets all Dots of the entity.
	 * @param entity to use.
	 * @return all Dots.
	 */
	public static Collection<DotContainer> getAllDots(LivingEntity target){
		if(target == null) return new HashSet<>();
		return m().getAllDotsOnEntity(target);
	}
	
	
	/**
	 * Removes all Poison effects from the entity.
	 * 
	 * @param entity to un poison.
	 * @return true if worked, false otherwise.
	 */
	public static int removeAllDots(LivingEntity entity){
		if(entity == null) return 0;
		return m().removeAllDots(entity);
	}
	
	/**
	 * Removes all dots with that name.
	 * 
	 * @param entity to clear.
	 * @param name to remove.
	 * 
	 * @return true if worked, false otherwise.
	 */
	public static int removeDot(LivingEntity entity, String name){
		if(entity == null || name == null || name.isEmpty()) return 0;
		return m().removeAllDots(entity, name);
	}
	
	/**
	 * Removes all dots with that name.
	 * 
	 * @param entity to clear.
	 * @param name to remove.
	 * 
	 * @return true if worked, false otherwise.
	 */
	public static int removeDots(LivingEntity entity, DamageType type){
		if(entity == null || type == null) return 0;
		return m().removeAllDots(entity, type);
	}
}
