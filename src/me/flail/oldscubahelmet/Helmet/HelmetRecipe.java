package me.flail.oldscubahelmet.Helmet;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.flailofthelord.scubahelmet.ScubaHelmet;
import io.github.flailofthelord.scubahelmet.tools.Logger;

public class HelmetRecipe extends Logger {

	private ScubaHelmet plugin;

	public void helmetRecipe() {

		plugin = JavaPlugin.getPlugin(ScubaHelmet.class);

		FileConfiguration config = plugin.getConfig();

		ItemStack sHelm = Helmet.helmet();

		ShapedRecipe helmet = new ShapedRecipe(plugin.namespacedKey, sHelm);

		String line1 = config.getString("HelmetRecipe.Shape.1");
		String line2 = config.getString("HelmetRecipe.Shape.2");
		String line3 = config.getString("HelmetRecipe.Shape.3");

		helmet.shape(line1, line2, line3);

		String[] materialKeys = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };

		for (String key : materialKeys) {
			String materialName = config.getString("HelmetRecipe.Material." + key);

			if (line1.contains(key) || line2.contains(key) || line3.contains(key)) {

				Material material = Material.matchMaterial(materialName);

				if ((material == null) || material.equals(null)) {
					plugin.console.sendMessage(chat("Warning! Invalid Item Type for Recipe Material: '" + key + "'"));
					continue;
				}

				helmet.setIngredient(key.charAt(0), material);
			}

		}

		try {
			if (plugin.server.getRecipesFor(sHelm).isEmpty()) {
				plugin.getServer().addRecipe(helmet);
				plugin.console.sendMessage(chat("Recipe successfully registered!"));
			}

		} catch (Throwable t) {

		}

	}

}
