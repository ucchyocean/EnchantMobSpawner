/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author ucchy
 * EnchantMobSpawnerのコンフィグクラス
 */
public class EMSConfig {

    private ArrayList<String> profileNames;
    private FileConfiguration config;

    /**
     * config.yml の読み出し処理
     * @param plugin プラグインインスタンス
     * @param jarFile JarFileのオブジェクト（JavaPlugin.getJarFile()）
     */
    public void reloadConfig() {

        EnchantMobSpawner plugin = EnchantMobSpawner.instance;
        File jarFile = EnchantMobSpawner.instance.getJarFile();

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(jarFile, configFile, "config_ja.yml", false);
        }

        plugin.reloadConfig();
        config = plugin.getConfig();

        profileNames = new ArrayList<String>();
        Iterator<String> i = config.getValues(false).keySet().iterator();
        while ( i.hasNext() ) {
            String profile = i.next();
            String type = config.getString(profile + ".mob");
            if ( isValidEntityType(type) ) {
                profileNames.add(profile);
            }
        }
    }

    /**
     * 指定の文字列は、EntityTypeとして指定可能な内容かどうかを確認する
     * @param value 検査する文字列
     * @return EntityTypeとして指定可能かどうか
     */
    private boolean isValidEntityType(String value) {

        if ( value == null ) {
            return false;
        }

        EntityType type = EntityType.fromName(value);
        return (type != null);
    }

    /**
     * コンフィグから指定したパスの値を取得する
     * @param path パス
     * @return 設定値、存在しない場合はnullとなる
     */
    public String getStringValue(String path) {
        return config.getString(path);
    }

    /**
     * コンフィグから指定されたプロファイルのMOBタイプを取得する
     * @param profile プロファイル
     * @return MOBタイプ
     */
    public EntityType getEntityType(String profile) {

        String str = config.getString(profile + ".mob");
        return EntityType.fromName(str);
    }

    /**
     * コンフィグから指定されたプロファイルのキットを取得する
     * @param profile プロファイル
     * @return キット
     */
    public ArrayList<ItemStack> getKit(String profile) {

        String str = config.getString(profile + ".kit");
        if ( str == null ) {
            return null;
        }

        return KitParser.parseClassItemData(str);
    }

    /**
     * ロードされているプロファイル名の一覧を返します。
     * @return プロファイル名の一覧
     */
    public ArrayList<String> getProfileNames() {
        return profileNames;
    }
}
