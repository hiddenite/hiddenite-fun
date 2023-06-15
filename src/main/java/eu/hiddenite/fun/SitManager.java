package eu.hiddenite.fun;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Collections;
import java.util.List;

public class SitManager implements CommandExecutor, TabCompleter, Listener {
    NamespacedKey key;

    public SitManager(FunPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        key = new NamespacedKey(plugin, "hiddenite_sit");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String alias,
                             @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -1.70, 0), EntityType.ARMOR_STAND);
        armorStand.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
        armorStand.setVisible(false);
        armorStand.setPassenger(player);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        return Collections.emptyList();
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getDismounted().getType() != EntityType.ARMOR_STAND) {
            return;
        }

        if (event.getDismounted().getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0) == (byte) 0) {
            return;
        }

        event.getDismounted().remove();
        player.teleport(player.getLocation().add(0, 1, 0));
    }

}
