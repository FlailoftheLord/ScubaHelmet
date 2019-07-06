package io.github.flailofthelord.scubahelmet.tools;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.flailofthelord.scubahelmet.ScubaHelmet;

public class BaseUtilities {
	protected ScubaHelmet plugin = ScubaHelmet.getPlugin(ScubaHelmet.class);

	protected ItemStack addTag(ItemStack item, String key, String tag) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + key);

		meta.getPersistentDataContainer().set(nkey, PersistentDataType.STRING, tag);

		item.setItemMeta(meta);
		return item;
	}

	protected ItemStack removeTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + key);

		meta.getPersistentDataContainer().remove(nkey);

		item.setItemMeta(meta);
		return item;
	}

	protected String getTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		NamespacedKey nkey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + key);

		if (hasTag(item, key)) {
			return meta.getPersistentDataContainer().get(nkey, PersistentDataType.STRING);
		}

		return "null";
	}

	protected boolean hasTag(ItemStack item, String key) {
		if ((item != null) && item.hasItemMeta()) {

			ItemMeta meta = item.getItemMeta();
			NamespacedKey nkey = new NamespacedKey(plugin, plugin.namespacedKey.getKey() + "-" + key);

			return meta.getPersistentDataContainer().has(nkey, PersistentDataType.STRING);
		}
		return false;
	}

}
