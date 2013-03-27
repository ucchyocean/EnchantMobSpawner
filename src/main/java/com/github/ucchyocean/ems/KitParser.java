/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author ucchy
 *
 */
public class KitParser {

    private static final String REGEX_ITEM_PATTERN =
            "([0-9]+)((?:\\^[0-9]+-[0-9]+)*)(?:@([0-9]+))?(?:\\$([0-9A-Fa-f]{6}))?";
    private static final String REGEX_ENCHANT_PATTERN = "\\^([0-9]+)-([0-9]+)";

    private static Pattern pattern;
    private static Pattern patternEnchant;

    /**
     * Classのアイテムデータ文字列を解析し、ItemStack配列に変換する。
     * @param data 解析元の文字列　例）"44:64,44@2:64,281:10"
     * @return ItemStackの配列
     */
    protected static ArrayList<ItemStack> parseClassItemData(String data) {

        if ( pattern == null ) {
            pattern = Pattern.compile(REGEX_ITEM_PATTERN);
            patternEnchant = Pattern.compile(REGEX_ENCHANT_PATTERN);
        }

        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {

            Matcher matcher = pattern.matcher(array[i]);

            if ( matcher.matches() ) {

                int item = 0;
                short damage = 0;
                HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
                Color color = null;

                item = Integer.parseInt(matcher.group(1));
                if ( matcher.group(3) != null ) {
                    damage = Short.parseShort(matcher.group(3));
                }

                // item id が0なら、nullを設定して次へ進む
                if ( item == 0 ) {
                    items.add(null);
                    continue;
                }

                // Materialの取得をして、正しいIDが指定されたかどうかを確認する
                Material m = Material.getMaterial(item);
                if (m == null) {
                    EnchantMobSpawner.logger.severe(
                            "指定されたItemID " + item + " が見つかりません。");
                    return null;
                }

                // 指定エンチャントの解析
                Matcher matcherEnchant = patternEnchant.matcher(matcher.group(2));
                while ( matcherEnchant.find() ) {
                    int enchantID = Integer.parseInt(matcherEnchant.group(1));
                    int enchantLevel = Integer.parseInt(matcherEnchant.group(2));
                    enchants.put(enchantID, enchantLevel);
                }

                // 指定カラーの解析
                if ( matcher.group(4) != null ) {
                    String colorID = matcher.group(4);
                    int red = Integer.decode( "0x" + colorID.substring(0, 2) );
                    int green = Integer.decode( "0x" + colorID.substring(2, 4) );
                    int blue = Integer.decode( "0x" + colorID.substring(4, 6) );
                    color = Color.fromRGB(red, green, blue);
                }

                items.add(getEnchantedItem(item, enchants, damage, color));

            } else {

                EnchantMobSpawner.logger.severe(
                        "指定された形式 " + array[i] + " が正しく解析できません。");
                return null;
            }
        }

        return items;
    }

    /**
     * ItemStackインスタンスを返す
     * @param item 配布するアイテムのID
     * @param amount 配布するアイテムの数量
     * @param damage 配布するアイテムのダメージ値（指定しない場合は0にする）
     * @return ItemStackインスタンス
     */
    private static ItemStack getItemStack(int item, int amount, short damage) {
        if ( damage > 0 )
            return new ItemStack(item, amount, damage);
        else
            return new ItemStack(item, amount);
    }

    /**
     * エンチャント付きのItemStackインスタンスを返す
     * @param item 配布するアイテムのID
     * @param enchants 付与するエンチャントIDと、そのレベルのセット
     * @return ItemStackインスタンス
     */
    private static ItemStack getEnchantedItem(int item, HashMap<Integer, Integer> enchants, short damage, Color color) {

        ItemStack i = getItemStack(item, 1, damage);

        Set<Integer> keys = enchants.keySet();
        for ( int eid : keys ) {
            int level = enchants.get(eid);
            Enchantment ench = new EnchantmentWrapper(eid);
            if ( level < ench.getStartLevel() ) {
                level = ench.getStartLevel();
            } else if ( level > 1000 ) {
                level = 1000;
            }
            i.addUnsafeEnchantment(ench, level);
        }

        if ( color != null && 298 <= item && item <= 301 ) {
            LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
            lam.setColor(color);
            i.setItemMeta(lam);
        }

        return i;
    }

    /**
     * アイテムの情報を文字列にして返します。
     * @param item アイテム
     * @return アイテムの文字列表現
     */
    protected static String getItemInfo(ItemStack item) {

        StringBuilder message = new StringBuilder();

        String material = item.getType().toString();
        int itemID = item.getTypeId();
        int amount = item.getAmount();
        short durability = item.getDurability();
        String color = null;
        if ( 298 <= itemID && itemID <= 301 ) {
            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            Color colorTemp = lam.getColor();
            if ( colorTemp != null ) {
                color = convertColorToString(colorTemp);
            }
        }

        message.append(material + " : " + itemID);
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        Set<Enchantment> keys = enchants.keySet();
        for ( Enchantment e : keys ) {
            message.append("^" + e.getId() + "-" + enchants.get(e));
        }
        if ( durability > 1 ) {
            message.append("@" + durability);
        }
        if ( color != null ) {
            message.append("$" + color);
        }
        if ( amount > 1 ) {
            message.append(":" + amount);
        }

        return message.toString();
    }

    /**
     * Colorを文字列表現に変換します。
     * @param color Color
     * @return 文字列表現
     */
    private static String convertColorToString(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("%02x%02x%02x", red, green, blue).toUpperCase();
    }
}
