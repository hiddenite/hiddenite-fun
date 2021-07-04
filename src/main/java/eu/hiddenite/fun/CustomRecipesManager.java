package eu.hiddenite.fun;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CustomRecipesManager implements Listener {
    public CustomRecipesManager(FunPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        Recipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "spore_blossom"), new ItemStack(Material.SPORE_BLOSSOM))
                .addIngredient(Material.GRASS)
                .addIngredient(Material.AZALEA)
                .addIngredient(Material.FLOWERING_AZALEA);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!e.getRecipe().getResult().getType().equals(Material.SPORE_BLOSSOM)) {
            return;
        }
        if (!player.hasPermission("hiddenite.fun.spore_blossom")) {
            e.setCancelled(true);
        }
    }
}
