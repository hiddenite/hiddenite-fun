package eu.hiddenite;

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
import java.util.logging.Logger;

public class InvisibleFrameCommand implements CommandExecutor, TabCompleter {
    private String lookAtFrame;

    Logger logger = Logger.getLogger("invisible-frame");

    public InvisibleFrameCommand(FileConfiguration configuration) {
        lookAtFrame = configuration.getString("messages.iframe.look-at-frame");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String alias,
                             String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

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

        selectedFrame.setVisible(false);
        selectedFrame.getWorld().spawnParticle(
                Particle.SPELL_MOB,
                selectedFrame.getLocation(),
                8, 0.1, 0, 0.1);

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
