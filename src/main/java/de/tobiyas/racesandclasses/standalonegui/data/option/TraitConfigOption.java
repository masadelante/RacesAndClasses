package de.tobiyas.racesandclasses.standalonegui.data.option;

import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.NeedsSave;

public interface TraitConfigOption extends Comparable<TraitConfigOption>, NeedsSave {
	
	
	/**
	 * Returns true if it is optional
	 * 
	 * @return true if optional.
	 */
	public boolean isOptional();
	

	/**
	 * Returns the Name of the Option.
	 * 
	 * @return name of the Option.
	 */
	public String getName();
	
	
	/**
	 * Returns the option type.
	 * 
	 * @return the type of the option.
	 */
	public OptionType getOptionType();
	
	
	/**
	 * Returns a set of options to set.
	 * 
	 * @return the set of options.
	 */
	public Set<String> options();	

	
	/**
	 * Sets the options to show.
	 * 
	 * @param options the set of options to set.
	 * 
	 * @return this for chaining.
	 */
	public TraitConfigOption setOptions(Set<String> options);

	
	/**
	 * Sets the options to show.
	 * 
	 * @param options the set of options to set.
	 * 
	 * @return this for chaining.
	 */
	public TraitConfigOption setOptions(String... options);
	
	
	
	/**
	 * If the Value passed is acceptable for the Options.
	 * 
	 * @param value to check
	 * 
	 * @return true if acceptable, false if not.
	 */
	public boolean isAcceptable(String value);
	
	
	/**
	 * Selected a value.
	 * 
	 * @param value to select.
	 */
	public void valueSelected(String value);
	
	
	/**
	 * Returns the current Selection.
	 * 
	 * @return the current Selection.
	 */
	public String getCurrentSelection();


	/**
	 * If the Value is created or not.
	 * 
	 * @return true if created
	 */
	public boolean isCreated();


	/**
	 * Sets the value to created.
	 * 
	 * @param created if it is created.
	 */
	public void setCreated(boolean created);
	
	/**
	 * Notifies that the value should be resettet.
	 */
	public void reset();

	/**
	 * Notifies that it has been saved.
	 */
	public void notifySaved();

	
}
