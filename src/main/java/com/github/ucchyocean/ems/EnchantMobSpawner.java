/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Enchant Mob Spawner プラグインのメインクラス
 * @author ucchy
 */
public class EnchantMobSpawner extends JavaPlugin {

    protected static EnchantMobSpawner instance;
    protected static EMSConfig config;
    protected static Logger logger;

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        instance = this;
        logger = getLogger();

        // コンフィグのロード
        config = new EMSConfig();
        config.reloadConfig();

        // リスナーの登録
        getServer().getPluginManager().registerEvents(new EMSListener(), this);

        // コマンドの登録
        getCommand("EnchantMobSpawner").setExecutor(new EMSCommand());
    }

    /**
     * JavaPlugin.getFile() を、他のクラスから呼び出すためのメソッド
     * @return JavaPlugin.getFile() の実行結果
     */
    protected File getJarFile() {
        return this.getFile();
    }
}
