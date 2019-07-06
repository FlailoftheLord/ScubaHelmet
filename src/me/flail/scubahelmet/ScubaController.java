package me.flail.scubahelmet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.flailofthelord.scubahelmet.tools.Logger;
import me.flail.oldscubahelmet.Helmet.HelmetUseEvent;
import me.flail.scubahelmet.helmet.ScubaItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ScubaController extends Logger {

	public ScubaController() {

	}

	/**
	 * @param interval
	 *                     Run interval in seconds.
	 */
	public void run(int interval) {
		plugin.cancelTasks();

		new Runnable().runTaskTimer(plugin, 64L, interval * 20L);
	}

	private class Runnable extends BukkitRunnable {

		@Override
		public void run() {

			for (Player player : Bukkit.getOnlinePlayers()) {

				if (player.hasPermission("scubahelmet.use")) {
					ItemStack item = player.getInventory().getHelmet();

					if (hasTag(item, "scuba-helmet")) {
						Material headBlock = player.getWorld().getBlockAt(player.getLocation().add(0, 1, 0)).getType();

						if (headBlock == Material.WATER) {
							ScubaItem scubaHelmet = ScubaItem.fromItem(item);

							if (hasTag(item, "durability")) {
								String displayType = plugin.config.get("DurabilityDisplay", "NONE").toString()
										.replaceAll("[^a-zA-Z\\_]", "");
								String displayText = plugin.config.get("DurabilityMessage").toString();
								int durability = scubaHelmet.getDurability();
								int maxDurability = scubaHelmet.getMaxDurability();

								scubaHelmet.setDurability(durability - 1);
								durability = scubaHelmet.getDurability();

								displayText = displayText.replace("{player}", player.getName())
										.replace("{current}", durability + "").replace("{max}", maxDurability + "");


								switch (displayType.toUpperCase()) {
								case "ACTION_BAR":
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(displayText));

									break;
								case "BOSS_BAR":
									new HelmetUseEvent().durabilityBar(player, displayText, BarColor.BLUE, BarStyle.SEGMENTED_12,
											maxDurability, durability);

								}

							}

							String[] effectList = getTag(item, "effects").split("-");
							int effectDuration = Integer.parseInt(getTag(item, "effect-duration"));
							int effectPower = Integer.parseInt(getTag(item, "effect-power"));
							boolean effectParticles = Boolean.getBoolean(getTag(item, "effect-particles"));

							for (String e : effectList) {
								PotionEffectType effect = PotionEffectType.getByName(e.toUpperCase());
								if (effect != null) {
									player.addPotionEffect(
											new PotionEffect(effect, effectDuration, effectPower - 1, effectParticles));

								}

							}

							continue;
						}


						NamespacedKey nKey = new NamespacedKey(plugin,
								plugin.namespacedKey.getKey() + "-" + player.getName());
						BossBar bar = plugin.server.getBossBar(nKey);

						if (bar != null) {
							bar.removePlayer(player);
						}

					}

				}

			}

		}

	}

}
