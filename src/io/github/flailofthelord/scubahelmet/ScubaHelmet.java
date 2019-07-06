package io.github.flailofthelord.scubahelmet;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.flailofthelord.scubahelmet.tools.Logger;
import me.flail.oldscubahelmet.ScubaCommands;
import me.flail.oldscubahelmet.Helmet.Helmet;
import me.flail.oldscubahelmet.Helmet.HelmetEquip;
import me.flail.oldscubahelmet.Helmet.HelmetRecipe;
import me.flail.scubahelmet.ScubaController;

public class ScubaHelmet extends JavaPlugin {

	public ConsoleCommandSender console = Bukkit.getConsoleSender();
	public String version = getDescription().getVersion();
	public Server server = this.getServer();

	public FileConfiguration config;
	public NamespacedKey namespacedKey = new NamespacedKey(this, "ScubaHelmet");

	private Logger utils;

	@Override
	public void onEnable() {
		utils = new Logger();

		// Save the files
		saveDefaultConfig();
		config = getConfig();

		// Register events and commands
		server.getPluginManager().registerEvents(new HelmetEquip(), this);

		cancelTasks();
		new ScubaController().run(32);

		this.getCommand("scubahelmet").setExecutor(new ScubaCommands());

		// Friendly console spam :>
		String serverType = server.getVersion();
		String serverVersion = server.getBukkitVersion();

		utils.console("&3=====o====O====o=====");
		utils.console("&9 ScubaHelmet &7v" + version);
		utils.console("&2   by FlailoftheLord.");
		utils.console("&b  Breathe safely underwater? ... i think not >:)");
		utils.console("&3=====o====O====o=====");
		utils.console("&8ScubaHelmet running under " + serverType + " version " + serverVersion);

		registerRecipes();

	}

	@Override
	public void onDisable() {
		utils.console("&bShutting down tasks...");
		cancelTasks();

		utils.console("&3Goodbye!");
	}

	public void cancelTasks() {
		server.getScheduler().cancelTasks(this);
	}

	public void registerRecipes() {

		Iterator<Recipe> sR = server.recipeIterator();

		HelmetRecipe recipeHandler = new HelmetRecipe();

		while (sR.hasNext()) {
			Recipe r;

			try {
				r = sR.next();
			} catch (ConcurrentModificationException e) {
				return;
			}

			if (r.getResult().equals(Helmet.helmet())) {

				sR.remove();

				recipeHandler.helmetRecipe();
				utils.console("&3Recipe successfully updated!");
				return;
			}

		}

		recipeHandler.helmetRecipe();

	}

}
