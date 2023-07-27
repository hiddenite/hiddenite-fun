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
        Objects.requireNonNull(getCommand("firework")).setExecutor(new FireworkCommand(getConfig()));

        if (getConfig().getBoolean("heads.enabled")) {
            new HeadOnDeathManager(this);
        }

        if (getConfig().getBoolean("custom-recipes.enabled")) {
            new CustomRecipesManager(this);
        }
    }
}
