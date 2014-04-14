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
package de.tobiyas.racesandclasses.playermanagement.leveling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class LevelCalculatorTest {

	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
	}

	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void verifyGeneratorStringWorks_works(){
		String validGeneratorString = "({level} * {level}) * 1000";
		String invalidGeneratorString = "({level} * x * invalid * Banane / {level}) * 1000 )";
		
		assertTrue(LevelCalculator.verifyGeneratorStringWorks(validGeneratorString));
		assertFalse(LevelCalculator.verifyGeneratorStringWorks(invalidGeneratorString));
	}
	
	@Test
	public void calcMaxExpForLevel_works(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
			.thenReturn("({level} * {level} * {level}) * 1000");
		
		int level = 10;
		int expNeededForLevel10 = level * level * level * 1000;
		
		int actualValue = LevelCalculator.calcMaxExpForLevel(level);
		
		assertEquals(expNeededForLevel10, actualValue);
	}
	
	@Test
	public void calcMaxExpForLevel_bypasses_to_working_calculation_when_string_is_broken(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
			.thenReturn("Banane Banane, Affe, Affe, Banane!!! ?");
		
		int level = 10;
		int expNeededForLevel10 = level * level * 1000;
		
		int actualValue = LevelCalculator.calcMaxExpForLevel(level);
		
		assertEquals(expNeededForLevel10, actualValue);
	}
	
	@Test
	public void calculateLevelPackage_works_for_positive_levels(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("({level}) * 1000");
		
		int level = 10;
		int expNeededForLevel10 = level * 1000;
		
		assertEquals(new LevelPackage(level, expNeededForLevel10), LevelCalculator.calculateLevelPackage(level));
	}
	
	@Test
	public void calculateLevelPackage_passes_level1_package_for_negative_levels(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("({level}) * 1000");
		
		int level = -10;
		int expectedLevel = 1;
		int expNeededForLevel10 = expectedLevel * 1000;
		
		assertEquals(new LevelPackage(expectedLevel, expNeededForLevel10), LevelCalculator.calculateLevelPackage(level));
	}
	
	@Test
	public void calculatePercentageOfLevel_works(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("{level} * {level} * 42 * 1000");
		
		double expectedPecentage = 0.421337;
		
		int level = 98621;
		int currentEXP = (int) (LevelCalculator.calculateLevelPackage(level).getMaxEXP() * expectedPecentage);
		
		double actualPercentage = LevelCalculator.calculatePercentageOfLevel(level, currentEXP);
		
		assertEquals(100 * expectedPecentage, actualPercentage, 0.0001);
	}

}
