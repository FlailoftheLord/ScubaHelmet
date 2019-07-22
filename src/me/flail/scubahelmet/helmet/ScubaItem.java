package me.flail.scubahelmet.helmet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.flailofthelord.scubahelmet.tools.Logger;

public class ScubaItem extends Logger {

	private static ItemStack item = null;

	public ScubaItem() {

		create();
	}

	public static ScubaItem fromItem(ItemStack baseItem) {
		item = baseItem;

		return new ScubaItem();
	}

	private void create() {
		String name = chat(plugin.config.get("HelmetName", "&3ScubaHelmet").toString());
		String typeID = plugin.config.get("HelmetType", "GLASS").toString().toUpperCase();

		Material type = Material.matchMaterial(typeID);
		List<String> helmetLore = plugin.config.getStringList("HelmetLore");
		List<String> lore = new ArrayList<>();

		if (typeID.contains("PLAYER_HEAD=")) {
			String headOwner = typeID.split("=")[1];
			type = Material.PLAYER_HEAD;

			item = new ItemStack(type);

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(headOwner)));
			item.setItemMeta(meta);
		}

		if (item == null) {
			item = new ItemStack(type);
		}

		addTag("scuba-helmet", "yes");
		ItemMeta meta = itemMeta();

		meta.setDisplayName(name);

		for (String line : helmetLore) {
			lore.add(chat(line));
		}
		lore.add(" ");

		meta.setLore(lore);

		if (plugin.config.getBoolean("EnchantedGlow", false)) {
			meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		}

		item.setItemMeta(meta);

		setActiveEffects(plugin.config.getStringList("HelmetEffects"));
		addTag("effect-duration", plugin.config.getInt("EffectDuraion", 12) + "");
		addTag("effect-power", plugin.config.getInt("EffectPower", 1) + "");
		addTag("effect-particles", plugin.config.getBoolean("HideEffectParticles", true) + "");

		if (plugin.config.getBoolean("HelmetHasDurability", true) && !hasTag("durability")) {
			int durability = plugin.config.getInt("HelmetDurability", 320);
			if (durability < 1) {
				durability = 320;
			}

			setDurability(durability);
			addTag("max-durability", durability + "");
		}

	}

	public ItemStack item() {
		return item;
	}

	public ItemMeta itemMeta() {
		return item.getItemMeta();
	}

	public int getDurability() {
		return hasTag("durability") ? Integer.parseInt(getTag("durability")) : -1;
	}

	public int getMaxDurability() {
		return hasTag("durability") ? Integer.parseInt(getTag("max-durability")) : 0;
	}

	public void setActiveEffects(List<String> effectList) {
		String value = "";
		for (String s : effectList) {
			value = value + s.toUpperCase() + "-";
		}

		removeTag("effects");
		addTag("effects", value.substring(0, value.length() - 1));
	}

	public ScubaItem setDurability(long durability) {
		List<String> lore = new ArrayList<>();
		removeTag("durability");
		addTag("durability", durability + "");

		ItemMeta meta = itemMeta();

		if (meta.hasLore()) {
			lore =meta.getLore();
		}
		String durabilityDisplay = lore.get(lore.size() - 1);

		// Check if it's actually displaying durability, then reset it.
		if (durabilityDisplay.contains("durability:")) {

			durabilityDisplay = chat("&8durability: " + durability);
			lore.remove(lore.size() - 1);
		}

		lore.add(durabilityDisplay);

		meta.setLore(lore);
		item.setItemMeta(meta);

		return this;
	}

	public void addTag(String key, String value) {
		item = addTag(item, key, value);
	}

	public void removeTag(String key) {
		item = removeTag(item, key);
	}

	public String getTag(String key) {
		return getTag(item, key);
	}

	public boolean hasTag(String key) {
		return hasTag(item, key);
	}

}
