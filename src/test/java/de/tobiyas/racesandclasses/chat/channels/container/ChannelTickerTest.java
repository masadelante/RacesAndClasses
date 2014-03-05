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
package de.tobiyas.racesandclasses.chat.channels.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class ChannelTickerTest {

	private ChannelTicker sut;
	private int tastNr = 42;
	
	private ChannelContainer mockContainer = Mockito.mock(ChannelContainer.class);
	
	@Before
	public void mockStatics(){
		ChannelTicker.ticker = null;
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
				
		BukkitScheduler scheduler = Bukkit.getScheduler();
		Mockito.when(scheduler.scheduleSyncRepeatingTask(Mockito.any(RacesAndClasses.class), Mockito.any(Runnable.class), 
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(tastNr);
		
		ChannelTicker.init();
		sut = ChannelTicker.ticker;
		
		scheduler = Bukkit.getScheduler();
		Mockito.verify(scheduler, Mockito.times(1)).scheduleSyncRepeatingTask(Mockito.any(RacesAndClasses.class), Mockito.any(Runnable.class), 
				Mockito.anyInt(), Mockito.anyInt());
	}
	
	@After
	public void tearDownMocks(){
		ChannelTicker.disable();
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void registering_channel_registers_channel(){
		ChannelTicker.registerChannel(mockContainer);
		assertTrue(sut.channels.contains(mockContainer));
	}
	
	@Test
	public void removing_registered_channel_removes_channel(){
		ChannelTicker.registerChannel(mockContainer);
		assertTrue(sut.channels.contains(mockContainer));
		
		ChannelTicker.unregisterChannel(mockContainer);
		assertFalse(sut.channels.contains(mockContainer));
	}
	
	@Test
	public void removing_not_present_channel_removes_nothing(){
		int sizeBefore = sut.channels.size();
		assertFalse(sut.channels.contains(mockContainer));

		ChannelTicker.unregisterChannel(mockContainer);
		assertFalse(sut.channels.contains(mockContainer));
		
		int sizeAfter = sut.channels.size();
		
		assertEquals(sizeBefore, sizeAfter);
	}
	
	@Test
	public void disable_clears_everything_stuff(){
		ChannelTicker.registerChannel(mockContainer);
		
		ChannelTicker.disable();
		
		assertTrue(sut.channels.size() == 0);
		assertTrue(ChannelTicker.ticker == null);
		
		BukkitScheduler mockScheduler = Bukkit.getScheduler();
		Mockito.verify(mockScheduler, Mockito.times(1)).cancelTask(tastNr);
	}
	
	@Test
	public void running_ticks_container(){
		ChannelTicker.registerChannel(mockContainer);
		
		sut.run();
		
		Mockito.verify(mockContainer, Mockito.times(1)).tick();
	}
	
}
