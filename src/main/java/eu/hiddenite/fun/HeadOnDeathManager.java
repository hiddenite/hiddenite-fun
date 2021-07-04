package eu.hiddenite.fun;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class HeadOnDeathManager implements Listener {
    private final FunPlugin plugin;

    private final HashMap<UUID, HashMap<UUID, Date>> previousLoots = new HashMap<>();

    private final NamespacedKey killerNamespaceKey;
    private final NamespacedKey dateNamespaceKey;

    private final String loreText;
    private final DateFormat loreDateFormat;
    private final int delayBetweenTwoIdenticalDrops;

    public HeadOnDeathManager(FunPlugin plugin) {
        this.plugin = plugin;

        killerNamespaceKey = new NamespacedKey(plugin, "head-killer");
        dateNamespaceKey = new NamespacedKey(plugin, "head-date");

        loreText = plugin.getConfig().getString("heads.lore.text");
        String loreDateFormatString = plugin.getConfig().getString("heads.lore.date-format");
        if (loreDateFormatString != null) {
            loreDateFormat = new SimpleDateFormat(loreDateFormatString);
        } else {
            loreDateFormat = new SimpleDateFormat();
        }
        delayBetweenTwoIdenticalDrops = plugin.getConfig().getInt("heads.delay-between-two-identical-drops");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.PLAYER_HEAD && event.getBlock().getType() != Material.PLAYER_WALL_HEAD) {
            return;
        }

        if (event.getItemInHand().getType() != Material.PLAYER_HEAD) {
            return;
        }

        SkullMeta itemSkull = (SkullMeta)event.getItemInHand().getItemMeta();
        if (itemSkull == null) {
            return;
        }

        String killer = itemSkull.getPersistentDataContainer().get(killerNamespaceKey, PersistentDataType.STRING);
        if (killer == null) {
            return;
        }

        Long timestamp = itemSkull.getPersistentDataContainer().get(dateNamespaceKey, PersistentDataType.LONG);
        if (timestamp == null) {
            return;
        }

        Skull blockSkull = (Skull)event.getBlock().getState();
        blockSkull.getPersistentDataContainer().set(killerNamespaceKey, PersistentDataType.STRING, killer);
        blockSkull.getPersistentDataContainer().set(dateNamespaceKey, PersistentDataType.LONG, timestamp);
        blockSkull.update();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.PLAYER_HEAD && event.getBlock().getType() != Material.PLAYER_WALL_HEAD) {
            return;
        }

        Skull blockSkull = (Skull)event.getBlock().getState();

        String killerString = blockSkull.getPersistentDataContainer().get(killerNamespaceKey, PersistentDataType.STRING);
        if (killerString == null) {
            return;
        }

        UUID killerUUID = UUID.fromString(killerString);
        OfflinePlayer killerPlayer = plugin.getServer().getOfflinePlayer(killerUUID);

        Long timestamp = blockSkull.getPersistentDataContainer().get(dateNamespaceKey, PersistentDataType.LONG);
        if (timestamp == null) {
            return;
        }

        ItemStack headItem = createPlayerHeadItem(killerPlayer, blockSkull.getOwningPlayer(), new Date(timestamp));

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), headItem);
        event.setDropItems(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer == null) {
            return;
        }
        if (player.getUniqueId().equals(killer.getUniqueId())) {
            return;
        }

        if (!previousLoots.containsKey(player.getUniqueId())) {
            previousLoots.put(player.getUniqueId(), new HashMap<>());
        }

        Date now = new Date();
        if (previousLoots.get(player.getUniqueId()).containsKey(killer.getUniqueId())) {
            Date previousIdenticalKill = previousLoots.get(player.getUniqueId()).get(killer.getUniqueId());

            long elapsedSeconds = (now.getTime() - previousIdenticalKill.getTime()) / 1000;
            if (elapsedSeconds < delayBetweenTwoIdenticalDrops) {
                return;
            }
        }

        previousLoots.get(player.getUniqueId()).put(killer.getUniqueId(), now);

        plugin.getLogger().info(killer.getName() + " looted " + player.getName() + "'s head");

        ItemStack headItem = createPlayerHeadItem(killer, player, new Date());
        event.getDrops().add(headItem);
    }

    private ItemStack createPlayerHeadItem(OfflinePlayer killer, OfflinePlayer victim, Date when) {
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skull = (SkullMeta) headItem.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(victim);

            String killerName = killer.getName() != null ? killer.getName() : "???";
            String itemLore = loreText
                    .replace("{KILLER}", killerName)
                    .replace("{DATE}", loreDateFormat.format(when));

            skull.setLore(Collections.singletonList(itemLore));
            skull.getPersistentDataContainer().set(killerNamespaceKey, PersistentDataType.STRING, killer.getUniqueId().toString());
            skull.getPersistentDataContainer().set(dateNamespaceKey, PersistentDataType.LONG, when.getTime());
            headItem.setItemMeta(skull);
        }

        return headItem;
    }
}
