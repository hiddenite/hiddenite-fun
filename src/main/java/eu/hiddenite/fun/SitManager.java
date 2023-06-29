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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Collections;
import java.util.List;

public class SitManager implements CommandExecutor, TabCompleter, Listener {
    private final FunPlugin plugin;
    private final NamespacedKey key;

    public SitManager(FunPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "hiddenite_seat");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String alias,
                             @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (player.getVehicle() != null) {
            if (player.getVehicle().getType() == EntityType.ARMOR_STAND && player.getVehicle().getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 1) == (byte) 1) {
                plugin.getLogger().info("[Sit] Ejecting " + player.getName());
                player.getVehicle().removePassenger(player);
            }
            return true;
        }

        if (!player.isOnGround()) {
            return true;
        }

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -1.70, 0), EntityType.ARMOR_STAND);
        armorStand.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.addPassenger(player);

        plugin.getLogger().info("[Sit] Spawned an ArmorStand for " + player.getName());
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

        plugin.getLogger().info("[Sit] Destroying the ArmorStand of " + player.getName());
        event.getDismounted().remove();
        player.teleport(player.getLocation().add(0, 1, 0));
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType() != EntityType.ARMOR_STAND) {
                continue;
            }
            if (entity.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0) == (byte) 0) {
                continue;
            }

            plugin.getLogger().info("[Sit] Removing a stranded ArmorStand: " + entity);
            entity.remove();
        }
    }

}
