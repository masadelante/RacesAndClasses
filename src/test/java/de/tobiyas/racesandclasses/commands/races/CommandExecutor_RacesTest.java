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
package de.tobiyas.racesandclasses.commands.races;

import static de.tobiyas.racesandclasses.translation.languages.Keys.already_are;
import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_race;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_RacesTest {

	private CommandExecutor_Race sut;
	
	private CommandSender consoleSender;
	private String consoleSenderName = "console";
	
	private Player player;
	private String playerName = "player1";
	
	
	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
	
		sut = new CommandExecutor_Race();
		sut.addObserver(RacesAndClasses.getPlugin().getTutorialManager());
		
		consoleSender = mock(CommandSender.class);
		when(consoleSender.getName()).thenReturn(consoleSenderName);
		
		player = mock(Player.class);
		when(player.getName()).thenReturn(playerName);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_enableRaces()).thenReturn(true);
	}
	
	
	@After
	public void tearDown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	@Test
	public void registering_command_works(){
		String commandName = "race";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_Race();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	//Send help
	
	private void verifyHelp(CommandSender sender, boolean permissions, boolean wrongUse){
		if(wrongUse){
			verify(sender).sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		}else{
			verify(sender).sendMessage(ChatColor.RED + "Use one of the following commands:");
		}
		
		
		verify(sender).sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "info");
		verify(sender).sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "list");

		verify(sender, permissions ? times(1) : never()).sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<racename>");
		verify(sender, permissions ? times(1) : never()).sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<racename>");
	}
	
	
	@Test
	public void test_help_with_no_permissions_from_console_sender(){
		sut.onCommand(consoleSender, null, "", new String[] {});
		
		verifyHelp(consoleSender, false, false);
	}
	
	@Test
	public void test_sending_wrong_command_sends_help_from_console_sender(){
		sut.onCommand(consoleSender, null, "", new String[] {"wrongCommand"});
		
		verifyHelp(consoleSender, false, true);
	}
	
	@Test
	public void test_help_with_permissions_from_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(any(CommandSender.class), anyString())).thenReturn(true);
		
		sut.onCommand(consoleSender, null, "", new String[] {});
		
		verifyHelp(consoleSender, true, false);
	}
	
	
	//Info command
	
	@Test
	public void test_info_with_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "info";
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(plugin_pre + race_not_exist);
	}
	
	@Test
	public void test_info_with_unknown_race_and_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "info";
		String raceToSelect = "Banane";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, raceToSelect});
		
		verify(consoleSender).sendMessage(plugin_pre + race_not_exist);
	}
	
	@Test
	public void test_info_with_classes_present_from_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "info";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getAllHolderNames()).thenReturn(raceList);
		RaceContainer raceContainer = mock(RaceContainer.class);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(consoleSenderName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(raceContainer.getName()).thenReturn(raceName);
		when(raceContainer.getTag()).thenReturn(raceName);
		when(raceContainer.getArmorString()).thenReturn("Armor");
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "Race name: " + ChatColor.LIGHT_PURPLE + raceContainer.getName());
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "Race tag: " + ChatColor.LIGHT_PURPLE + raceContainer.getTag());
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + raceContainer.getArmorString());
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
	}
	
	
	//List command
	
	@Test
	public void test_list_with_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "list";
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		verify(consoleSender).sendMessage(ChatColor.RED + "DefaultRace�e  <-- " + your_race);
	}

	@Test
	public void test_list_with_multiple_races_console_sender(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "list";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add("race1");
		raceList.add("race2");
		raceList.add("race3");
		
		List<String> copiedList = new LinkedList<String>(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(copiedList);
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		for(String race : raceList){
			verify(consoleSender).sendMessage(ChatColor.BLUE + race);
		}
		
		verify(consoleSender).sendMessage(ChatColor.RED + "DefaultRace" + ChatColor.YELLOW +"  <-- " + your_race);
	}
	
	@Test
	public void test_list_with_multiple_races_and_own_race_console_sender(){
		String command = "list";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add("race1");
		raceList.add("race2");
		raceList.add("race3");
		
		List<String> copiedList = new LinkedList<String>(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(copiedList);
		
		RaceContainer raceContainer = mock(RaceContainer.class);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(consoleSenderName)).thenReturn(raceContainer);
		when(raceContainer.getName()).thenReturn("race1");
		
		sut.onCommand(consoleSender, null, "", new String[] {command});
		
		
		verify(consoleSender).sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");

		verify(consoleSender).sendMessage(ChatColor.RED + "race1" + ChatColor.YELLOW +"  <-- " + your_race);
		
		verify(consoleSender).sendMessage(ChatColor.BLUE + "race2");
		verify(consoleSender).sendMessage(ChatColor.BLUE + "race3");
	}
	
	
	@Test
	public void test_list_with_player_triggers_observer(){
		String command = "list";
		
		sut.onCommand(player, null, "", new String[] {command});
		verify(RacesAndClasses.getPlugin().getTutorialManager()).update(any(CommandExecutor_Class.class), any(TutorialStepContainer.class));
	}
	
	//selecting
	
	@Test
	public void test_select_with_console_sender_fails(){
		String command = "select";
		String race = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, race});
		
		verify(consoleSender).sendMessage(plugin_pre + "only_players");
	}
	
	@Test
	public void test_player_opening_race_with_no_container_selection_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "select";
		List<String> raceList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "no_race_to_select");
	}
	
	@Test
	public void test_player_opening_race_selection_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "select";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer classContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(raceName);		
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "open_holder");
	}
	
	@Test
	public void test_player_opening_race_selection_fails_because_already_has_race(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "select";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer classContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(classContainer.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(classContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(classContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "already_have_race");
	}
	
	
	//Changing
	
	@Test
	public void test_change_with_console_sender_fails(){
		String command = "change";
		String race = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, race});
		
		verify(consoleSender).sendMessage(plugin_pre + "only_players");
	}
	
	@Test
	public void test_player_opening_race_changer_with_no_container_selected_screen(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "change";
		List<String> raceList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "no_race_selected");
	}
	
	
	
	@Test
	public void test_player_opening_race_with_no_changeable_race_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "no_race_to_select");
	}
	
	@Test
	public void test_player_opening_race_change_success_because_already_has_race(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect()).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "open_holder");
	}
	
	////////////////////////
	//CHAT INTERACTION    //
	////////////////////////
	
	//selecting
	
	@Test
	public void test_select_with_console_sender_fails_chat(){		
		String command = "select";
		String race = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, race});
		
		verify(consoleSender).sendMessage(plugin_pre + "only_players");
	}
	
	
	@Test
	public void test_player_selecting_race_with_no_container_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "select";
		
		List<String> raceList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "needs_1_arg");
	}
	
	@Test
	public void test_player_selecting_race_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "select";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);		
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		when(RacesAndClasses.getPlugin().getRaceManager().addPlayerToHolder(anyString(), anyString(), anyBoolean())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + race_changed_to);
	}
	
	
	@Test
	public void test_player_selecting_race_chat_fails_because_already_has_race(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "select";
		String raceName = "race1";
		
		List<String> classList = new LinkedList<String>();
		classList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);		
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(classList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + already_have_race);
	}
	
	
	@Test
	public void test_player_selecting_race_chat_fails_because_not_exist_has_class(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "select";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + "race_not_exist");
	}
	
	
	//changing

	@Test
	public void test_change_with_console_sender_fails_chat(){		
		String command = "change";
		String race = "nothing";
		
		sut.onCommand(consoleSender, null, "", new String[] {command, race});
		
		verify(consoleSender).sendMessage(plugin_pre + "only_players");
	}
	
	
	@Test
	public void test_player_changing_race_with_no_container_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		String command = "change";
		
		List<String> raceList = new LinkedList<String>();

		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		
		sut.onCommand(player, null, "", new String[] {command});
		
		verify(player).sendMessage(plugin_pre + "needs_1_arg");
	}
	
	@Test
	public void test_player_changing_race_chat(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		when(RacesAndClasses.getPlugin().getRaceManager().changePlayerHolder(anyString(), anyString(), anyBoolean())).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		String currentRace = "race2";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);
		
		RaceContainer raceContainerCurrent = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(currentRace);		
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(currentRace)).thenReturn(raceContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainerCurrent);
		
		when(RacesAndClasses.getPlugin().getRaceManager().addPlayerToHolder(anyString(), anyString(), anyBoolean())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + race_changed_to);
	}
	
	@Test
	public void test_player_changing_to_same_race_chat_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);		
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
		
		when(RacesAndClasses.getPlugin().getRaceManager().addPlayerToHolder(anyString(), anyString(), anyBoolean())).thenReturn(true);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(already_are);
	}
	
	
	@Test
	public void test_player_changing_race_chat_fails_because_has_no_race(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		
		List<String> raceList = new LinkedList<String>();
		raceList.add(raceName);

		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		when(raceContainer.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName(raceName)).thenReturn(raceContainer);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(raceList);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + no_race_selected);
	}
	
	
	@Test
	public void test_player_changing_race_chat_fails_because_not_exist_race(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(any(CommandSender.class), anyString())).thenReturn(true);
		
		String command = "change";
		String raceName = "race1";
		
		RaceContainer raceContainer = mock(RaceContainer.class, Mockito.RETURNS_DEEP_STUBS);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(raceContainer);
		
		sut.onCommand(player, null, "", new String[] {command, raceName});
		
		verify(player).sendMessage(plugin_pre + race_not_exist);
	}
	
	@Test
	public void test_player_changing_race_chat_without_permissions_fails(){
		String command = "change";
		String className = "race1";
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(player, null, "", new String[] {command, className});
		
		verify(RacesAndClasses.getPlugin().getPermissionManager()).checkPermissions(any(CommandSender.class), anyString());
	}

}
