package io.github.flailofthelord.scubahelmet;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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
		registerTasks();
		registerCommand();

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
		utils.console("&bRemoving recipe...");
		server.resetRecipes();

		utils.console("&3Goodbye!");
	}

	public void reload() {
		utils.nl();
		utils.console("&7reloading ScubaHelmet...");

		HandlerList.unregisterAll(this);
		server.getScheduler().cancelTasks(this);

		onDisable();
		utils.nl();
		onEnable();
	}

	public void registerCommand() {
		for (String cmd : getDescription().getCommands().keySet()) {
			getCommand(cmd).setExecutor(this);
			getCommand(cmd).setTabCompleter(this);
		}
	}

	public void registerTasks() {
		server.getPluginManager().registerEvents(new HelmetEquip(), this);

		cancelTasks();
		new ScubaController().run(1);
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return new ScubaCommands().command(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> tab = new ArrayList<>();
		if (!command.getName().equalsIgnoreCase("scubahelmet")) {
			return null;
		}

		switch (args.length) {
		case 1:
			tab.add("get");
			tab.add("give");
			tab.add("reload");
			tab.add("on");
			tab.add("off");
			break;
		case 2:
			if (args[1].equalsIgnoreCase("give")) {
				for (Player player : server.getOnlinePlayers()) {
					tab.add(player.getName());
				}

			}

		}

		for (String s : tab.toArray(new String[] {})) {
			if (!s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				tab.remove(s);
			}
		}

		return tab;
	}

}
