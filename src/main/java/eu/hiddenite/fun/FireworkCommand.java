package eu.hiddenite.fun;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FireworkCommand implements CommandExecutor, TabCompleter {
    private final String fireworkUsage;
    private final String fireworkInfo;
    private final String fireworkSmallInfo;
    private final String noFireworkUsagesLeft;

    private final HashMap<UUID, Integer> fireworkCounter = new HashMap<>();
    private final HashMap<UUID, Long> fireworkTimer = new HashMap<>();

    private final int fireworkMaxStack;
    private final int fireworkRefill;
    private final int fireworkRefillTime;
    private final int fireworkAfterReboot;

    public FireworkCommand(FileConfiguration configuration) {
        fireworkUsage = configuration.getString("messages.firework.firework-usage");
        fireworkInfo = configuration.getString("messages.firework.firework-info");
        fireworkSmallInfo = configuration.getString("messages.firework.firework-small-info");
        noFireworkUsagesLeft = configuration.getString("messages.firework.no-firework-usages-left");

        fireworkMaxStack = configuration.getInt("firework.firework-max-stack");
        fireworkRefill = configuration.getInt("firework.firework-refill");
        fireworkRefillTime = configuration.getInt("firework.firework-refill-time");
        fireworkAfterReboot = configuration.getInt("firework.firework-after-reboot");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String alias,
                             @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        UUID uuid = player.getUniqueId();
        if (!fireworkCounter.containsKey(uuid)) fireworkCounter.put(uuid, fireworkAfterReboot);
        if (!fireworkTimer.containsKey(uuid)) fireworkTimer.put(uuid, new Date().getTime());

        long actualTime = new Date().getTime();
        if (fireworkTimer.get(uuid) + fireworkRefillTime * (long)1000 <= actualTime) {
            int refillCount = (int)((actualTime - fireworkTimer.get(uuid)) / (fireworkRefillTime * 1000));

            int fireworkUsesAfterRefill = fireworkCounter.get(uuid) + (refillCount * fireworkRefill);
            fireworkCounter.replace(uuid, Math.min(16, fireworkUsesAfterRefill));

            fireworkTimer.replace(uuid, fireworkTimer.get(uuid) + (refillCount * fireworkRefillTime * (long)1000));
        }

        if (args.length > 0) {
            if (args[0].equals("info")) {
                long remainingTime = fireworkRefillTime - ((new Date().getTime() - fireworkTimer.get(uuid)) / 1000);

                String message = fireworkInfo
                    .replace("{USAGES_LEFT}", fireworkCounter.get(uuid).toString())
                    .replace("{MAX_USAGES}", String.valueOf(fireworkMaxStack))
                    .replace("{S1}", fireworkCounter.get(uuid) > 1 ? "s" : "")
                    .replace("{NEXT_FIREWORK_TIME}", String.valueOf(remainingTime))
                    .replace("{S2}", remainingTime > 1 ? "s" : "");
                player.sendMessage(message);
            } else player.sendMessage(fireworkUsage);
        } else {
            if (fireworkCounter.get(uuid) <= 0) player.sendMessage(noFireworkUsagesLeft);
            else {
                fireworkCounter.replace(uuid, fireworkCounter.get(uuid)-1);

                Location playerLoc = player.getLocation();

                Random random = new Random();
                if (random.nextInt(100) == 0) {
                    for (Location loc : getLocationsForEasterEgg(playerLoc)) {
                        spawnRandomFirework(loc);
                    }
                } else spawnRandomFirework(playerLoc);

                String message = fireworkSmallInfo
                    .replace("{USAGES_LEFT}", fireworkCounter.get(uuid).toString())
                    .replace("{MAX_USAGES}", String.valueOf(fireworkMaxStack))
                    .replace("{S}", fireworkCounter.get(uuid) > 1 ? "s" : "");
                player.sendMessage(message);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        ArrayList<String> options = new ArrayList<>();
        options.add("info");

        return options;
    }

    private void spawnRandomFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        Random random = new Random();

        for (int i = 0; i<random.nextInt(2)+1; i++) {
            fireworkMeta.addEffect(FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getRandomColor()).withFade(getRandomColor()).with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]).trail(random.nextBoolean()).build());
        }

        fireworkMeta.setPower(new Random().nextInt(2)+1);

        firework.setFireworkMeta(fireworkMeta);
    }

    private ArrayList<Location> getLocationsForEasterEgg(Location location) {
        ArrayList<Location> locations = new ArrayList<>();

        Random random = new Random();
        for (int i=0; i<10; i++) {
            Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

            loc.add((double)random.nextInt(10)-5, 0, (double)random.nextInt(10)-5);

            locations.add(loc);
        }

        return locations;
    }

    private Color getRandomColor() {
        int random = new Random().nextInt(17) + 1;

        switch (random) {
            case 1 -> {
                return Color.AQUA;
            }
            case 2 -> {
                return Color.BLACK;
            }
            case 3 -> {
                return Color.BLUE;
            }
            case 4 -> {
                return Color.FUCHSIA;
            }
            case 5 -> {
                return Color.GRAY;
            }
            case 6 -> {
                return Color.GREEN;
            }
            case 7 -> {
                return Color.LIME;
            }
            case 8 -> {
                return Color.MAROON;
            }
            case 9 -> {
                return Color.NAVY;
            }
            case 10 -> {
                return Color.OLIVE;
            }
            case 11 -> {
                return Color.ORANGE;
            }
            case 12 -> {
                return Color.PURPLE;
            }
            case 13 -> {
                return Color.RED;
            }
            case 14 -> {
                return Color.SILVER;
            }
            case 15 -> {
                return Color.TEAL;
            }
            case 16 -> {
                return Color.WHITE;
            }
            case 17 -> {
                return Color.YELLOW;
            }
        }

        return Color.WHITE;
    }
}
