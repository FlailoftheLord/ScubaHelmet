package me.flail.oldscubahelmet.Helmet;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.flailofthelord.scubahelmet.tools.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class HelmetUseEvent extends Logger implements Runnable {

	public NamespacedKey nKey = new NamespacedKey(plugin, plugin.namespacedKey.getKey());

	public BossBar durabilityBar(Player player, String msg, BarColor color, BarStyle style, double max, double durability) {

		String pName = player.getName();

		nKey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + pName);

		BossBar bar = plugin.server.getBossBar(nKey);

		if (bar == null) {
			bar = plugin.server.createBossBar(nKey, msg, color, style);

			bar.removePlayer(player);
			bar.addPlayer(player);

		} else if (bar != null) {
			bar.removePlayer(player);
			bar.setTitle(msg);

			bar.addPlayer(player);
		}

		double progress = durability / max;

		if ((progress >= 0.0) && (progress <= 1.0)) {
			bar.setProgress(progress);

		}
		return bar;
	}

	@Override
	public void run() {

		FileConfiguration config = plugin.getConfig();

		for (Player player : Bukkit.getOnlinePlayers()) {

			boolean hasHelmet = new Helmet().hasHelmet(player);

			if (hasHelmet) {

				String pName = player.getName();

				ItemStack pHelm = new ItemStack(player.getInventory().getHelmet());

				Block pBlock = player.getLocation().add(0, 1, 0).getBlock();

				if (pBlock != null) {

					String pWater = pBlock.getType().toString();

					boolean hasDurability = hasTag(pHelm, "durability");

					if (pWater.equalsIgnoreCase("WATER") || pWater.equalsIgnoreCase("STATIONARY_WATER")
							|| player.isSwimming()) {

						int duration = (config.getInt("EffectDuration") + 1) * 20;
						int power = config.getInt("EffectPower") - 1;

						boolean showParticles = config.getBoolean("HideEffectParticles");

						List<String> potionEffects = config.getStringList("HelmetEffects");

						if ((potionEffects != null) && !potionEffects.isEmpty()) {

							for (String s : potionEffects) {

								if (s.equalsIgnoreCase("")) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration,
											power, showParticles));
									break;
								} else {

									String effect = s.toUpperCase();
									PotionEffectType isValidEffect = PotionEffectType.getByName(effect);

									if (isValidEffect != null) {
										player.removePotionEffect(isValidEffect);

										player.addPotionEffect(
												new PotionEffect(isValidEffect, duration, power, showParticles));
									}

								}

							}

						} else {
							player.addPotionEffect(
									new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, power, showParticles));

						}

						if (hasDurability && !(player.getGameMode().equals(GameMode.CREATIVE))) {
							nKey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + pName);

							ItemMeta pItemMeta = pHelm.getItemMeta();
							List<String> helmetLore = pItemMeta.getLore();

							int durabilityLine = helmetLore.size() - 1;

							int maxDurability = Integer.parseInt(this.getTag(pHelm, "max-durability"));
							int currentDurability = Integer.parseInt(this.getTag(pHelm, "durability"));

							int durabilityLossRate = config.getInt("DurabilityLossRate");

							String durabilityType = config.getString("DurabilityDisplay");
							String durabilityMessage = config.getString("DurabilityMessage");

							String msg = chat(durabilityMessage.replace("{current}", currentDurability + "")
									.replace("{max}", maxDurability + ""), player);

							if (currentDurability < 1) {
								player.getInventory().setHelmet(null);
								player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 1);
								player.sendMessage(chat("{prefix} &7Your ScubaHelmet has broken.", player));

								try {
									plugin.server.getBossBar(nKey)
									.removeAll();
									plugin.server.removeBossBar(nKey);
								} catch (Exception e) {
								}

								break;
							}

							int newDurability = currentDurability - durabilityLossRate;

							String newDurabilityLore = chat("&8durability: " + newDurability + 1, player);

							helmetLore.set(durabilityLine, newDurabilityLore);

							pItemMeta.setLore(helmetLore);
							pHelm.setItemMeta(pItemMeta);

							pHelm = removeTag(pHelm, "durability");
							pHelm = addTag(pHelm, "durability", newDurability + "");

							player.getInventory().setHelmet(pHelm);
							player.updateInventory();

							switch (durabilityType.toLowerCase()) {

							case "bossbar":

								BarColor barColor = BarColor.BLUE;
								BarStyle barStyle = BarStyle.SEGMENTED_12;

								durabilityBar(player, msg, barColor, barStyle, maxDurability, newDurability);

								break;
							case "action_bar":
								TextComponent actionBarMessage;

								actionBarMessage = new TextComponent(msg);

								player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
								break;
							case "none":
								break;

							}

						}

					} else {

						if (hasDurability) {

							try {
								plugin.server.getBossBar(nKey).removePlayer(player);
								plugin.server.removeBossBar(nKey);
							} catch (Exception e) {
								// plugin.console.sendMessage("o");
							}

						}

					}

				}

			}

		} // for Loop

	}

}
