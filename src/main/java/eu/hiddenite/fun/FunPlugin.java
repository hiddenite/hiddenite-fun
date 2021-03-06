package eu.hiddenite.fun;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class FunPlugin extends JavaPlugin {
    @Override
    public void onEnable()  {
        saveDefaultConfig();

        PluginCommand hatCommand = getCommand("hat");
        if (hatCommand != null) {
            hatCommand.setExecutor(new HatCommand(getConfig()));
        }

        PluginCommand invisibleFrameCommand = getCommand("iframe");
        if (invisibleFrameCommand != null) {
            invisibleFrameCommand.setExecutor(new InvisibleFrameCommand(getConfig()));
        }
    }
}
