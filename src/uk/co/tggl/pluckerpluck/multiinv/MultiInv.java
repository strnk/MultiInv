package uk.co.tggl.pluckerpluck.multiinv;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tggl.pluckerpluck.multiinv.command.MICommand;
import uk.co.tggl.pluckerpluck.multiinv.listener.MIPlayerListener;
import uk.co.tggl.pluckerpluck.multiinv.logger.MILogger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Pluckerpluck
 * Date: 17/12/11
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class MultiInv extends JavaPlugin {

    // Initialize logger (auto implements enable/disable messages to console)
    public static MILogger log;

    // Listeners
    MIPlayerListener playerListener;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        // Initialize Logger
        log = new MILogger();

        // Get the description file containing plugin information
        PluginDescriptionFile pdfFile = this.getDescription();

        // Load yaml files
        MIYamlFiles.loadConfig();
        MIYamlFiles.loadGroups();
        MIYamlFiles.loadPlayerLogoutWorlds();
        
        //An easy way to set the default logging levels
        if(MIYamlFiles.config.contains("loglevel")) {
        	try {
            	log.setLogLevel(MILogger.Level.valueOf(MIYamlFiles.config.getString("loglevel").toUpperCase()));
        	}catch(Exception e) {
        		log.warning("Log level value invalid! Valid values are: NONE, SEVERE, WARNING, INFO and DEBUG.");
        		log.warning("Setting log level to INFO.");
            	log.setLogLevel(MILogger.Level.INFO);
        	}
        }else {
        	//Set a sane level for logging
        	log.setLogLevel(MILogger.Level.INFO);
        }

        // Initialize listeners
        playerListener = new MIPlayerListener(this);

        // Register required events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);

    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        MICommand.command(args, sender);
        return true;
    }

}