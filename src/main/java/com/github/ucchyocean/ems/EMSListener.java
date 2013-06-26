/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;

/**
 * リスナークラス
 * @author ucchy
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
        ArrayList<ItemStack> kit =
                EnchantMobSpawner.config.getKit(profile);
        ArrayList<PotionEffect> effect =
                EnchantMobSpawner.config.getEffect(profile);
        LivingEntity le = event.getEntity();

        if ( kit != null ) {
            // 装備品を設定
            le.getEquipment().setItemInHand(kit.get(0));
            le.getEquipment().setHelmet(kit.get(1));
            le.getEquipment().setChestplate(kit.get(2));
            le.getEquipment().setLeggings(kit.get(3));
            le.getEquipment().setBoots(kit.get(4));

            // 装備品をドロップしないように設定
            le.getEquipment().setItemInHandDropChance(0);
            le.getEquipment().setHelmetDropChance(0);
            le.getEquipment().setChestplateDropChance(0);
            le.getEquipment().setLeggingsDropChance(0);
            le.getEquipment().setBootsDropChance(0);
        }

        if ( effect != null ) {
            // エフェクトを設定
            le.addPotionEffects(effect);
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

        // カスタムアイテムでなければ用は無い
        if ( !item.hasItemMeta() ||  !item.getItemMeta().hasDisplayName() ) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner)event.getBlockPlaced().getState();
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        // カスタムデータを埋め込む
        if ( name.startsWith("EnchantMobSpawner-") ) {
            String displayName = item.getItemMeta().getDisplayName();
            String profile = displayName.substring(displayName.indexOf("-") + 1);
            MetadataValue value = new FixedMetadataValue(
                    EnchantMobSpawner.instance, profile);
            spawner.setMetadata("EMSProfile", value);
            EntityType type = EnchantMobSpawner.config.getEntityType(profile);
            int delay = EnchantMobSpawner.config.getDelay(profile);

            spawner.setSpawnedType(type);
            spawner.setDelay(delay);
            spawner.update();

        } else if ( name.startsWith("Spawner-") ) {
            String displayName = item.getItemMeta().getDisplayName();
            String profile = displayName.substring(displayName.indexOf("-") + 1);
            EntityType type = EntityType.fromName(profile);

            spawner.setSpawnedType(type);
            spawner.update();
        }
    }

    /**
     * Blockを除去したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        // スポナーでなければ用は無い
        if ( event.getBlock().getType() != Material.MOB_SPAWNER ) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner)event.getBlock().getState();

        // メタデータを除去しておく
        if ( spawner.hasMetadata("EMSProfile") ) {
            spawner.removeMetadata("EMSProfile", EnchantMobSpawner.instance);
        }
    }

    /**
     * 指定したLocationから一番近いCreatureSpawnerのメタデータを取得する
     * @param location スポナーを探す基点
     * @return 一番近いスポナーから取得したメタデータ
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

        // 見つからなかったり、見つかったがメタデータが無い場合はnullを返して終了
        if ( spawner == null || !spawner.hasMetadata("EMSProfile") ) {
            return null;
        }

        // メタデータを取得して終了
        List<MetadataValue> value = spawner.getMetadata("EMSProfile");
        return value.get(0).asString();
    }
}
