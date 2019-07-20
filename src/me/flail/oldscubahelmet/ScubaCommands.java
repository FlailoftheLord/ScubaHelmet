package me.flail.oldscubahelmet;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.flailofthelord.scubahelmet.ScubaHelmet;
import io.github.flailofthelord.scubahelmet.tools.Logger;
import me.flail.oldscubahelmet.Helmet.Helmet;
import me.flail.oldscubahelmet.Helmet.HelmetItem;
import me.flail.scubahelmet.ScubaController;

public class ScubaCommands extends Logger {

	private ScubaHelmet plugin = ScubaHelmet.getPlugin(ScubaHelmet.class);

	private Server server = plugin.getServer();

	public boolean command(CommandSender sender, Command command, String label, String[] args) {

		FileConfiguration config = plugin.getConfig();
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		String version = plugin.getDescription().getVersion();

		String cmd = command.getName().toLowerCase();

		String reloadMessage = chat(config.getString("ReloadMessage"));
		String noPermission = chat(config.getString("NoPermissionMessage"));
		String helmetEquipped = chat(config.getString("HelmetEquippedMessage"));

		String usage = chat("{prefix} &3proper command usage&8: &7/scubahelmet [get:give:on:off:reset:reload]");
		String usageGive = chat("{prefix} &3proper command usage&8: &7/scubahelmet give [player]");
		String defaultMessage = chat("{prefix} &3ScubaHelmet &7v" + version + " &2by FlailoftheLord.");

		if (cmd.equalsIgnoreCase("scubahelmet")) {

			if ((sender instanceof Player)) {
				Player player = (Player) sender;

				if (player.hasPermission("scubahelmet.command")) {

					if (args.length == 1) {

						if (args[0].equalsIgnoreCase("reload")) {

							if (player.hasPermission("scubahelmet.command.reload")) {

								long reloadStart = System.currentTimeMillis();

								console.sendMessage(chat("Shutting down tasks..."));
								server.getScheduler().cancelTasks(plugin);
								console.sendMessage(chat("Reloading plugin..."));

								plugin.reloadConfig();
								plugin.config = plugin.getConfig();

								plugin.cancelTasks();
								new ScubaController().run(1);

								console.sendMessage(reloadMessage
										+ chat(" &8(&7took " + (System.currentTimeMillis() - reloadStart) + " ms&8)", player));
								player.sendMessage(chat(reloadMessage));

							} else {
								player.sendMessage(noPermission);
							}

						} else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("on")
								|| args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset")) {
							if (player.hasPermission("scubahelmet.command.get")) {
								new HelmetItem().helmet(player);
							} else {
								player.sendMessage(noPermission);
							}

						} else {
							player.sendMessage(usage);
						}

					} else if (args.length == 2) {

						if (args[0].equalsIgnoreCase("give")) {

							boolean unknownPlayer = false;

							for (Player p : Bukkit.getOnlinePlayers()) {
								String pName = p.getName();

								if (args[1].equalsIgnoreCase(pName)) {
									Player target = p;
									new HelmetItem().helmet(target);
									unknownPlayer = false;
									break;
								} else {
									unknownPlayer = true;
								}
							}

							if (unknownPlayer) {
								player.sendMessage(chat("&cCould not recognize the username " + args[1]
										+ " make sure it is the name of a player who is online."));
							}

						} else {
							player.sendMessage(usageGive);
						}

					} else {

						if (player.hasPermission("scubahelmet.use")) {

							ItemStack pHand = player.getInventory().getItemInMainHand();

							boolean hasHelmetInHand = new Helmet().hasHelmetInHand(player);

							if (hasHelmetInHand) {
								player.getInventory().setHelmet(pHand);
								player.getInventory().setItemInMainHand(null);
								player.sendMessage(chat(helmetEquipped, player));
							} else {
								player.sendMessage(chat(defaultMessage, player));
							}

						} else {

							player.sendMessage(noPermission);

						}

					}

				} else {
					player.sendMessage(noPermission);
				}

				return true;
			}

			console.sendMessage(defaultMessage);
			console.sendMessage(chat("{prefix} &cThis command can only be used ingame by a real player... :("));
		}

		return true;
	}

}
