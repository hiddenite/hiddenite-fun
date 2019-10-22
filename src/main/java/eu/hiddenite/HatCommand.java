package eu.hiddenite;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class HatCommand implements CommandExecutor, TabCompleter {
    private String alreadySomethingInHelmetSlot;
    private String nothingInMainHand;

    public HatCommand(FileConfiguration configuration) {
        alreadySomethingInHelmetSlot = configuration.getString("messages.already-something-in-helmet-slot");
        nothingInMainHand = configuration.getString("messages.nothing-in-main-hand");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        if (player.getInventory().getHelmet() == null) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getAmount() > 0) {
                ItemStack copy = itemInHand.clone();
                copy.setAmount(1);
                player.getInventory().setHelmet(copy);
                if (player.getInventory().getHelmet() != null) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
            } else {
                player.sendMessage(nothingInMainHand);
            }
        } else {
            player.sendMessage(alreadySomethingInHelmetSlot);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
