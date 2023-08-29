package net.isksss.mc.minegacha;

import org.bukkit.plugin.java.JavaPlugin;

public final class MineGacha extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("プラグインを有効にしました。");
        this.getCommand("gacha").setExecutor(new GachaCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
