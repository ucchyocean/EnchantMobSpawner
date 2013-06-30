/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * スポナーのプロファイル情報をファイルに保存するクラス
 * @author ucchy
 */
public class EMSLocationData {

    private static final String FILE_NAME = "data.yml";
    
    private File file;
    private YamlConfiguration config;

    /**
     * コンストラクタ
     */
    public EMSLocationData() {

        file = new File(
                EnchantMobSpawner.instance.getDataFolder(),
                FILE_NAME);

        if ( !file.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * キーからプロファイル名を取得する。未設定ならnullになる。
     * @param key キー
     * @return プロファイル名
     */
    public String get(String key) {

        // 取得する前にリロードする
        config = YamlConfiguration.loadConfiguration(file);

        return config.getString(key);
    }
    
    /**
     * 場所からプロファイル名を取得する。未設定ならnullになる。
     * @param loc 場所
     * @return プロファイル名
     */
    public String get(Location loc) {
        return get(convertLocToKey(loc));
    }

    /**
     * 場所とプロファイル名のセットを保存する
     * @param loc 場所
     * @param value プロファイル名
     */
    public void set(Location loc, String value) {

        String key = convertLocToKey(loc);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 現在の設定ファイル内のキー一覧を取得する
     * @return キー一覧
     */
    public Set<String> keys() {

        // 取得する前にリロードする
        config = YamlConfiguration.loadConfiguration(file);
        
        return config.getKeys(false);
    }

    /**
     * 現在の設定ファイル内の場所一覧を取得する
     * @return 場所一覧
     */
    public HashSet<Location> keyLocations() {

        HashSet<Location> results = new HashSet<Location>();

        // 取得する前にリロードする
        config = YamlConfiguration.loadConfiguration(file);

        for ( String key : config.getKeys(false) ) {
            Location loc = convertKeyToLoc(key);
            if ( loc != null ) {
                results.add(loc);
            }
        }

        return results;
    }
    
    /**
     * 設定ファイル内のプロファイル情報を再設定する
     */
    public void setProfiles() {
        
        HashSet<Location> locs = keyLocations();
        
        for ( Location l : locs ) {
            Block block = l.getBlock();
            if ( block.getType() != Material.MOB_SPAWNER ) {
                set(l, null);
                continue;
            }
            String profile = get(l);
            CreatureSpawner spawner = (CreatureSpawner)block.getState();
            MetadataValue value = new FixedMetadataValue(
                    EnchantMobSpawner.instance, profile);
            spawner.setMetadata("EMSProfile", value);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * キーを場所に変換する
     * @param key キー
     * @return 場所
     */
    private Location convertKeyToLoc(String key) {
        
        String[] array = key.split(",");
        if ( array.length < 4 ) {
            return null;
        }
        if ( !array[1].matches("-?[0-9]+") ||
                !array[2].matches("-?[0-9]+") ||
                !array[3].matches("-?[0-9]+") ) {
            return null;
        }
        World world = Bukkit.getWorld(array[0]);
        if ( world == null ) {
            return null;
        }
        int x = Integer.parseInt(array[1]);
        int y = Integer.parseInt(array[2]);
        int z = Integer.parseInt(array[3]);
        return new Location(world, x, y, z);
    }
    
    /**
     * 場所をキーに変換する
     * @param location 場所
     * @return キー
     */
    private String convertLocToKey(Location location) {
        
        return String.format("%s,%d,%d,%d", 
                location.getWorld().getName(),
                location.getBlockX(), 
                location.getBlockY(), 
                location.getBlockZ() );
    }
}
