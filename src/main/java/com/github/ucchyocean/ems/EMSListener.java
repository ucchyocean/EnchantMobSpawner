/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * @author ucchy
 * リスナークラス
 */
public class EMSListener implements Listener {

    /**
     * MOBがスポーンしたときに呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        // スポナーから生まれた物でなければ対象外
        if ( event.getSpawnReason() != SpawnReason.SPAWNER ) {
            return;
        }

        // 近隣のスポナーからプロファイルを取得する
        // 取得したプロファイルがnullなら、普通のスポナーなので対象外
        String profile = getProfileFromNearestSpawner( event.getLocation() );
        if ( profile == null ) {
            return;
        }

        // プロファイル名からキットを取得し、装備に設定していく
        ArrayList<ItemStack> kit = EnchantMobSpawner.config.getKit(profile);
        if ( kit.size() >= 5 ) {
            LivingEntity le = event.getEntity();
            le.getEquipment().setItemInHand(kit.get(0));
            le.getEquipment().setHelmet(kit.get(1));
            le.getEquipment().setChestplate(kit.get(2));
            le.getEquipment().setLeggings(kit.get(3));
            le.getEquipment().setBoots(kit.get(4));
        }
    }

    /**
     * Blockを設置したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        // スポナーでなければ用は無い
        if ( event.getBlockPlaced().getType() != Material.MOB_SPAWNER ) {
            return;
        }

        ItemStack item = event.getItemInHand();
        CreatureSpawner spawner = (CreatureSpawner)event.getBlockPlaced().getState();

        // メタデータが設定されているアイテムだったら、カスタムデータを埋め込む
        if ( item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().contains("EnchantMobSpawner-") ) {
            String displayName = item.getItemMeta().getDisplayName();
            String profile = displayName.substring(displayName.indexOf("-") + 1);
            MetadataValue value = new FixedMetadataValue(
                    EnchantMobSpawner.instance, profile);
            spawner.setMetadata("EMSProfile", value);
            EntityType type = EnchantMobSpawner.config.getEntityType(profile);

            spawner.setSpawnedType(type);
            spawner.update();

        } else if ( item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().contains("Spawner-") ) {
            String displayName = item.getItemMeta().getDisplayName();
            String profile = displayName.substring(displayName.indexOf("-") + 1);
            EntityType type = EntityType.fromName(profile);

            spawner.setSpawnedType(type);
            spawner.update();
        }
    }

    /**
     * 指定したLocationから一番近いCreatureSpawnerのメタタグを取得する
     * @param location スポナーを探す基点
     * @return 一番近いスポナーから取得したメタタグ
     */
    private String getProfileFromNearestSpawner(Location location) {

        // 周囲 9x9x9 立方 のブロックを全て取得する
        ArrayList<Block> blocks = new ArrayList<Block>();
        for ( int x=-4; x<=4; x++ ) {
            for ( int y=-4; y<=4; y++ ) {
                for ( int z=-4; z<=4; z++ ) {
                    blocks.add( location.getBlock().getRelative(x, y, z));
                }
            }
        }

        // 一番距離の近いスポナーを探す
        Block spawner = null;
        double distance = 999.9;
        for ( Block block : blocks ) {
            if ( block.getType() == Material.MOB_SPAWNER ) {
                if ( distance > location.distance(block.getLocation()) ) {
                    distance = location.distance(block.getLocation());
                    spawner = block;
                }
            }
        }

        if ( spawner == null || !spawner.hasMetadata("EMSProfile") ) {
            return null;
        }

        List<MetadataValue> value = spawner.getMetadata("EMSProfile");
        return value.get(0).asString();
    }
}
