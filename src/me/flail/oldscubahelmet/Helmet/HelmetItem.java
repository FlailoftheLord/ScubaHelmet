package me.flail.oldscubahelmet.Helmet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.flailofthelord.scubahelmet.ScubaHelmet;
import io.github.flailofthelord.scubahelmet.tools.Logger;

public class HelmetItem extends Logger {
	private static ScubaHelmet plugin = ScubaHelmet.getPlugin(ScubaHelmet.class);

	static FileConfiguration config = plugin.getConfig();

	public ItemStack helmetItem() {

		String helmetName = chatColor(config.getString("HelmetName", "&3ScubaHelmet"));
		String helmetType = config.getString("HelmetType", "GLASS");
		boolean isGlowing = config.getBoolean("EnchantedGlow", false);

		ItemStack sHelm = new ItemStack(Material.getMaterial(helmetType));
		ItemMeta hMeta = sHelm.getItemMeta();

		ArrayList<String> lore = new ArrayList<>();
		List<String> hLore = config.getStringList("HelmetLore");

		for (String l : hLore) {
			lore.add(ChatColor.translateAlternateColorCodes('&', l));
		}

		boolean hasDurability = config.getBoolean("HelmetHasDurability", true);
		if (hasDurability) {
			int durability = config.getInt("HelmetDurability", 300);

			sHelm = addTag(sHelm, "durability", durability + "");
			sHelm = addTag(sHelm, "max-durability", durability + "");

			lore.add(" ");
			lore.add(chat("&8durability: " + durability));
		}



		hMeta.setDisplayName(helmetName);

		if (isGlowing) {
			hMeta.addEnchant(Enchantment.MENDING, 1, true);
			hMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		hMeta.setLore(lore);

		sHelm.setItemMeta(hMeta);

		sHelm = addTag(sHelm, "scuba-helmet", "true");

		return sHelm;

	}

	public void helmet(Player player) {

		FileConfiguration config = plugin.getConfig();

		String helmetReset = config.getString("HelmetResetMessage");
		String helmetEquipped = config.getString("HelmetEquippedMessage");
		String emptySlotMessage = config.getString("EmptySlotErrorMessage");

		ItemStack helmet = new ItemStack(helmetItem());

		boolean hasHelmet = new Helmet().hasHelmet(player);
		boolean hasHelmetInHand = new Helmet().hasHelmetInHand(player);

		if (player.hasPermission("scubahelmet.use")) {

			if (hasHelmet) {

				player.getInventory().setHelmet(null);
				player.sendMessage(chat(helmetReset, player));

			} else {

				if (player.getInventory().getHelmet() == null) {

					if (hasHelmetInHand) {
						player.getInventory().setHelmet(player.getInventory().getItemInMainHand());
						player.getInventory().setItemInMainHand(null);
					} else {
						player.getInventory().setHelmet(helmet);
					}
					player.sendMessage(chat(helmetEquipped, player));
				} else {
					player.sendMessage(chat(emptySlotMessage, player));
				}

			}

		}

	}

}
