package uk.co.tggl.pluckerpluck.multiinv.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import uk.co.tggl.pluckerpluck.multiinv.MIYamlFiles;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIEnderchestInventory;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIInventory;
import uk.co.tggl.pluckerpluck.multiinv.listener.MIPlayerListener;
import uk.co.tggl.pluckerpluck.multiinv.player.MIPlayerFile;

/**
 * Created by IntelliJ IDEA. User: Pluckerpluck Date: 19/12/11 Time: 22:58 To change this template use File | Settings | File Templates.
 */
public class MICommand {

	MultiInv plugin;

	public MICommand(MultiInv plugin) {
		this.plugin = plugin;
	}

	public static void command(String[] strings, CommandSender sender, MultiInv plugin) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		if(strings.length > 0) {
			String command = strings[0];

			// Check to see if the player has the permission to run this command.
			if(player != null && !player.hasPermission("multiinv." + command.toLowerCase())) {
				return;
			}

			// Populate a new args array
			String[] args = new String[strings.length - 1];
			for(int i = 1; i < strings.length; i++) {
				args[i - 1] = strings[i];
			}
			if(command.equalsIgnoreCase("report")) {
				if(plugin.dreport != null) {
					LinkedList<String> customdata = new LinkedList<String>();
					customdata.add("MultiInv Custom Data");
					customdata.add("================================");
					customdata.add("-----------config.yml-----------");
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(plugin.getDataFolder() + File.separator + "config.yml"));
						String line = null;
						while ((line = reader.readLine()) != null) {
							if(line.startsWith("  password:")) {
								line = "  password: NOTSHOWN";
							}
							customdata.add(line);
						}
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}finally {
						if(reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
							}
							reader = null;
						}
					}
					customdata.add("-----------groups.yml-----------");
					try {
						reader = new BufferedReader(new FileReader(plugin.getDataFolder() + File.separator + "groups.yml"));
						String line = null;
						while ((line = reader.readLine()) != null) {
							customdata.add(line);
						}
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}finally {
						if(reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
							}
						}
					}
					plugin.dreport.createReport(sender, customdata);
				}else {
					sender.sendMessage(ChatColor.RED + "In order to generate a debug report you need the plugin DebugReport!");
				}
			}else if(command.equalsIgnoreCase("reload")) {
				MIYamlFiles.loadConfig();
				MIYamlFiles.loadGroups();
				sender.sendMessage(ChatColor.DARK_GREEN + "MultiInv configs reloaded!");
			} else if(command.equalsIgnoreCase("import")) {
				sender.sendMessage(ChatColor.GOLD + "Please wait as we import all the player files.");
				FlatToMysqlImportThread ithread = new FlatToMysqlImportThread(sender, plugin);
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ithread);
			} else if(command.equalsIgnoreCase("mvimport")) {
				sender.sendMessage(ChatColor.GOLD + "Please wait as we import all the player files from Multiverse-Inventories.");
				MultiverseImportThread ithread = new MultiverseImportThread(sender, plugin);
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ithread);
			} else if(command.equalsIgnoreCase("mcimport")) {
				sender.sendMessage(ChatColor.GOLD + "Please wait as we import all the player files from Minecraft.");
				String defaultgroup = MIPlayerListener.getGroup(Bukkit.getWorlds().get(0).getName());
				MCImportThread ithread = new MCImportThread(sender, plugin, defaultgroup);
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ithread);
			} else if(command.equalsIgnoreCase("mcexport")) {
				sender.sendMessage(ChatColor.GOLD + "Please wait as we export all the player files to Minecraft.");
				String defaultgroup = MIPlayerListener.getGroup(Bukkit.getWorlds().get(0).getName());
				MCExportThread ithread = new MCExportThread(sender, plugin, defaultgroup);
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ithread);
			}
		}else {
			if(sender.hasPermission("multiinv.import")) {
				sender.sendMessage(ChatColor.GOLD + "Import Commands:");
				sender.sendMessage(ChatColor.GOLD + "/multiinv import" + ChatColor.AQUA + " - Import from fat file to mySQL");
				sender.sendMessage(ChatColor.GOLD + "/multiinv mvimport" + ChatColor.AQUA + " - Imports Multiverse-Inventories into MultiInv");
				sender.sendMessage(ChatColor.GOLD + "/multiinv miimport" + ChatColor.AQUA + " - Imports WorldInventories into MultiInv");
			}
			if(sender.hasPermission("multiinv.reload")) {
				sender.sendMessage(ChatColor.GOLD + "/multiinv reload" + ChatColor.AQUA + " - Reloads config files.");
			}
		}
	}
}
