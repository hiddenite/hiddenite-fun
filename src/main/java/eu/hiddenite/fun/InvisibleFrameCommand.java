package eu.hiddenite.fun;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InvisibleFrameCommand implements CommandExecutor, TabCompleter {
    private final String lookAtFrame;

    public InvisibleFrameCommand(FileConfiguration configuration) {
        lookAtFrame = configuration.getString("messages.iframe.look-at-frame");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String alias,
                             String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        Block block = player.getTargetBlockExact(6);
        BlockFace face = player.getTargetBlockFace(6);
        if (block == null || face == null) {
            player.sendMessage(lookAtFrame);
            return true;
        }

        Collection<ItemFrame> frames = block.getWorld().getNearbyEntitiesByType(
                ItemFrame.class,
                block.getLocation().add(0.5, 0.5, 0.5),
                0.6);

        ItemFrame selectedFrame = null;
        for (ItemFrame frame : frames) {
            if (frame.getAttachedFace() == face.getOppositeFace()) {
                selectedFrame = frame;
                break;
            }
        }

        if (selectedFrame == null) {
            player.sendMessage(lookAtFrame);
            return true;
        }

        selectedFrame.setVisible(!selectedFrame.isVisible());
        selectedFrame.getWorld().spawnParticle(
                Particle.ENTITY_EFFECT,
                selectedFrame.getLocation(),
                8, Color.fromRGB(120, 255, 160));

        selectedFrame.getWorld().playSound(selectedFrame.getLocation(), Sound.BLOCK_LANTERN_BREAK, 0.5f, 0.8f);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {
        return Collections.emptyList();
    }
}
