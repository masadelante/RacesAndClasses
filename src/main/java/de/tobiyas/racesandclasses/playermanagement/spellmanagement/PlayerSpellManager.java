package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait;

public class PlayerSpellManager {
	
	/**
	 * The Player this container belongs.
	 */
	private final String playerName;
	
	/**
	 * The {@link ManaManager} that the player has.
	 */
	protected final ManaManager manaManager;
	
	
	/**
	 * The Spell list of the Player.
	 */
	protected final RotatableList<MagicSpellTrait> spellList;
	
	
	/**
	 * Creates a SpellManager with a containing {@link ManaManager}.
	 * 
	 * @param playerName to create with
	 */
	public PlayerSpellManager(String playerName) {
		this.playerName = playerName;
		this.manaManager = new ManaManager(playerName);
		this.spellList = new RotatableList<MagicSpellTrait>();
	}
	
	
	/**
	 * Rescans the Player for changed Races and Classes to update the Mana
	 * and the Spells he can cast.
	 */
	public void rescan(){
		spellRescan();
		manaManager.rescanPlayer();
	}
	
	
	/**
	 * Rescans the Spells the player can cast
	 */
	private void spellRescan(){
		List<MagicSpellTrait> spellList = new LinkedList<MagicSpellTrait>();
		
		Set<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(playerName);
		for(Trait trait : traits){
			if(trait instanceof MagicSpellTrait){
				spellList.add((MagicSpellTrait) trait);
			}
		}
		
		this.spellList.setEntries(spellList);
	}
	
	/**
	 * Changes to the next Spell in the List.
	 * 
	 * @return the next Spell.
	 */
	public MagicSpellTrait changeToNextSpell(){
		return spellList.next();
	}
	
	
	/**
	 * Returns the {@link ManaManager} to check Spell casting or
	 * Mana Capabilities.
	 * 
	 * @return the ManaManager of the Player
	 */
	public ManaManager getManaManager(){
		return manaManager;
	}
}