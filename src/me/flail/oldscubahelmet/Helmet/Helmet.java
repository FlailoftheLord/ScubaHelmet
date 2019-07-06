package me.flail.oldscubahelmet.Helmet;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.flailofthelord.scubahelmet.tools.Logger;

public class Helmet extends Logger {

	public static ItemStack helmet() {

		return new HelmetItem().helmetItem();
	}

	public boolean isHelmet(ItemStack item) {
		return this.hasTag(item, "scuba-helmet");
	}

	public boolean hasHelmet(Player player) {

		if (player.getInventory().getHelmet() != null) {

			ItemStack item = new ItemStack(player.getInventory().getHelmet());

			return isHelmet(item);
		}

		return false;
	}

	public boolean hasHelmetOnCursor(Player player) {

		if (player.getItemOnCursor() != null) {
			ItemStack cursorItem = new ItemStack(player.getItemOnCursor());

			return isHelmet(cursorItem);
		}

		return false;
	}

	public boolean hasHelmetInHand(Player player) {

		if (player.getInventory().getItemInMainHand() != null) {

			ItemStack handItem = new ItemStack(player.getInventory().getItemInMainHand());

			return isHelmet(handItem);
		}

		return false;
	}

}
