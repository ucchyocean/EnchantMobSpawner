/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * EnchantMobSpawnerのコンフィグクラス
 * @author ucchy
 */
public class EMSConfig {

    private FileConfiguration config;
    private ArrayList<String> profiles;
    private HashMap<String, EntityType> types;
    private HashMap<String, ArrayList<ItemStack>> kits;
    private HashMap<String, ArrayList<PotionEffect>> effects;
    private HashMap<String, Integer> delays;

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

        profiles = new ArrayList<String>();
        types = new HashMap<String, EntityType>();
        kits = new HashMap<String, ArrayList<ItemStack>>();
        effects = new HashMap<String, ArrayList<PotionEffect>>();
        delays = new HashMap<String, Integer>();

        Iterator<String> i = config.getValues(false).keySet().iterator();
        while ( i.hasNext() ) {

            String profile = i.next();
            String type = config.getString(profile + ".mob");
            if ( Utility.isValidEntityType(type) ) {

                profiles.add(profile);

                types.put(profile, EntityType.fromName(type));

                if ( config.contains(profile + ".kit") ) {
                    String kit_temp = config.getString(profile + ".kit");
                    ArrayList<ItemStack> kit =
                            KitParser.parseClassItemData(kit_temp);
                    while ( kit.size() < 5 ) {
                        // 5個に満たない場合は、5個までnullで埋める
                        kit.add(null);
                    }
                    kits.put(profile, kit);
                }

                if ( config.contains(profile + ".effect") ) {
                    String effect_temp = config.getString(profile + ".effect");
                    ArrayList<PotionEffect> effect =
                            EffectParser.parseEffectData(effect_temp);
                    effects.put(profile, effect);
                }

                if ( config.contains(profile + ".delay") ) {
                    int delay = config.getInt(profile + ".delay");
                    if ( 1 <= delay )
                        delays.put(profile, delay);
                }
            }
        }
    }

    /**
     * コンフィグから指定されたプロファイルのMOBタイプを取得する
     * @param profile プロファイル
     * @return MOBタイプ
     */
    public EntityType getEntityType(String profile) {
        return types.get(profile);
    }

    /**
     * コンフィグから指定されたプロファイルのキットを取得する
     * @param profile プロファイル
     * @return キット、該当の設定が無い場合はnull
     */
    public ArrayList<ItemStack> getKit(String profile) {
        if ( !kits.containsKey(profile) ) {
            return null;
        }
        // cloneを作って返す
        return new ArrayList<ItemStack>(kits.get(profile));
    }

    /**
     * コンフィグから指定されたプロファイルのエフェクトを取得する
     * @param profile プロファイル
     * @return エフェクト、該当の設定が無い場合はnull
     */
    public ArrayList<PotionEffect> getEffect(String profile) {
        if ( !effects.containsKey(profile) ) {
            return null;
        }
        // cloneを作って返す
        return new ArrayList<PotionEffect>(effects.get(profile));
    }

    /**
     * コンフィグから指定されたプロファイルのdelay値を取得する
     * @param profile プロファイル
     * @return delay値、該当の設定が無い場合は20
     */
    public int getDelay(String profile) {
        if ( !delays.containsKey(profile) ) {
            return 20;
        }
        return delays.get(profile);
    }

    /**
     * ロードされているプロファイル名の一覧を返します。
     * @return プロファイル名の一覧
     */
    public ArrayList<String> getProfileNames() {
        return profiles;
    }
}
