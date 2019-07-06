package me.flail.scubahelmet.helmet;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		Material type = Material.matchMaterial(plugin.config.get("HelmetType", "GLASS").toString().replaceAll("[a-zA-Z\\_]", ""));
		List<String> lore = plugin.config.getStringList("HelmetLore");

		if (item == null) {
			item = new ItemStack(type);
		}
		addTag("scuba-helmet", "yes");
		ItemMeta meta = itemMeta();

		meta.setDisplayName(name);

		for (String line : lore) {
			line = chat(line);
		}
		lore.add(" ");

		meta.setLore(lore);


		if (plugin.config.getBoolean("HelmetHasDurability", true)) {
			int durability = plugin.config.getInt("HelmetDurability", 320);

			setDurability(durability);
			addTag("max-durability", durability + "");
		}

		if (plugin.config.getBoolean("EnchantedGlow", false)) {
			meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		}

		setActiveEffects(plugin.config.getStringList("HelmetEffects"));
		addTag("effect-duration", plugin.config.getInt("EffectDuraion", 12) + "");
		addTag("effect-power", plugin.config.getInt("EffectPower", 1) + "");
		addTag("effect-particles", plugin.config.getBoolean("HideEffectParticles", true) + "");

	}

	public ItemStack item() {
		return item;
	}

	public ItemMeta itemMeta() {
		return item.getItemMeta();
	}

	public int getDurability() {
		return hasTag("durability") ? Integer.parseInt(getTag("durability")) : 0;
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
		addTag("effects", value.substring(0, value.length() - 2));
	}

	public void setDurability(int durability) {
		List<String> lore = itemMeta().getLore();
		String durabilityDisplay = lore.get(lore.size() - 1);

		removeTag("durability");
		addTag("durability", durability + "");

		if (durabilityDisplay.contains("durability:")) {
			durabilityDisplay = chat("&8durability: " + durability);
		}

		lore.remove(lore.size() - 1);
		lore.add(durabilityDisplay);

		ItemMeta meta = itemMeta();
		meta.setLore(lore);

		item.setItemMeta(meta);

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
