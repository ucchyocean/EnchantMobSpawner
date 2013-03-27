/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author ucchy
 * コマンドクラス
 */
public class EMSCommand implements CommandExecutor {

    private static final String PREINFO = ChatColor.AQUA.toString();
    private static final String PREERR = ChatColor.RED.toString();
    private static final String FIRST_LINE_OF_LIST =
            "===== Profile List =====";

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // 引数なしで実行された場合は何もしない
        if ( args.length <= 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {
            // ems reload の実行処理

            EnchantMobSpawner.config.reloadConfig();
            sender.sendMessage(PREINFO + "config.yml を再読み込みしました。");
            return true;

        } else if ( args[0].equalsIgnoreCase("list") ) {
            // ems list の実行処理

            sender.sendMessage(PREINFO + FIRST_LINE_OF_LIST);
            for ( String profile : EnchantMobSpawner.config.getProfileNames() ) {
                sender.sendMessage(PREINFO + profile);
            }
            return true;

        } else if ( args[0].equalsIgnoreCase("hand") ) {
            // ems hand の実行処理

            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + "このコマンドはゲーム内からのみ実行可能です。");
                return true;
            }

            Player player = (Player)sender;

            ItemStack item = player.getItemInHand();
            sender.sendMessage("アイテム情報 < " + KitParser.getItemInfo(item) + " >");
            return true;

        } else if ( args[0].equalsIgnoreCase("get") ) {
            // ems get の実行処理

            if ( args.length <= 1 ) {
                sender.sendMessage(PREERR + "プロファイル名を指定してください。");
                sender.sendMessage(PREERR + "USAGE : /" + command.getName() + " get (ProfileName)");
                return true;
            }

            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + "このコマンドはゲーム内からのみ実行可能です。");
                return true;
            }

            Player player = (Player)sender;

            String profile = args[1];
            String displayName = "";
            if ( EnchantMobSpawner.config.getProfileNames().contains(profile) ) {
                displayName = "EnchantMobSpawner-" + profile;
            } else if ( isValidEntityType(profile) ) {
                displayName = "Spawner-" + profile;
            } else {
                sender.sendMessage(PREERR + "指定されたプロファイル" + profile + "は無効です。");
                return true;
            }

            // スポナーのItemStackを作成する
            ItemStack item = new ItemStack(Material.MOB_SPAWNER, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + displayName);
            item.setItemMeta(meta);

            // インベントリに突っ込む
            ItemStack temp = player.getItemInHand();
            player.setItemInHand(item);
            player.getInventory().addItem(temp);

            return true;
        }

        return false;
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
}
