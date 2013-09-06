package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;

public class TraitWithNoAnnotations extends AbstractBasicTrait {

	@Override
	public void importTrait() {
	}

	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "Invalid";
	}

	@Override
	public String getPrettyConfiguration() {
		return "Nothing";
	}

	@Override
	public void setConfiguration(Map<String, String> configMap) {
	}

	@Override
	public boolean modify(Event event) {
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

}
