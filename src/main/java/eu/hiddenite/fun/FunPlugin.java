package eu.hiddenite.fun;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class FunPlugin extends JavaPlugin {
    @Override
    public void onEnable()  {
        saveDefaultConfig();

        Objects.requireNonNull(getCommand("hat")).setExecutor(new HatCommand(getConfig()));
        Objects.requireNonNull(getCommand("iframe")).setExecutor(new InvisibleFrameCommand(getConfig()));
        Objects.requireNonNull(getCommand("sit")).setExecutor(new SitManager(this));

        if (getConfig().getBoolean("heads.enabled")) {
            HeadOnDeathManager headOnDeathManager = new HeadOnDeathManager(this);
            Objects.requireNonNull(getCommand("give-head")).setExecutor(headOnDeathManager);
        }
    }
}
