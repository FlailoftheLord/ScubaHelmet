package me.flail.oldscubahelmet.Helmet;

import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import io.github.flailofthelord.scubahelmet.tools.Logger;

public class HelmetEquip extends Logger implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onHelmetEquip(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		FileConfiguration config = plugin.getConfig();

		String noPermission = config.getString("NoPermissionMessage", "{prefix} &cYou don't have permission to use this!");

		boolean hasHelmet = new Helmet().hasHelmet(player);
		boolean hasHelmetOnCursor = new Helmet().hasHelmetOnCursor(player);

		ItemStack pHelm = player.getItemOnCursor();

		if (hasHelmet && event.getSlotType().equals(SlotType.ARMOR) && (event.getRawSlot() == 5)) {
			event.setCancelled(true);
		}

		// Make sure to run the task a few ticks after the event is called.
		plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

			if (event.getSlotType().equals(SlotType.ARMOR) && (event.getRawSlot() == 5)) {
				NamespacedKey nKey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + player.getName());

				if (hasHelmet) {
					BossBar durabilityBar = plugin.server.getBossBar(nKey);

					if (durabilityBar != null) {
						durabilityBar.removePlayer(player);
					}

					if (!player.hasPermission("scubahelmet.use")) {
						player.closeInventory();
						player.sendMessage(chat(noPermission, player));
					} else {
						player.setItemOnCursor(player.getInventory().getHelmet());
						player.getInventory().setHelmet(null);
					}

				} else if (hasHelmetOnCursor) {

					if (player.hasPermission("scubahelmet.use")) {

						player.getInventory().setHelmet(pHelm);
						player.setItemOnCursor(null);
						player.updateInventory();

					} else {

						event.setCancelled(true);
						player.setItemOnCursor(null);
						player.closeInventory();
						player.sendMessage(chat("{prefix} " + noPermission, player));

					}

				}

			}

		}, 1); // one smol tick :,>

	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();

		if (new Helmet().isHelmet(item)) {
			event.setCancelled(true);
		}

	}

}
