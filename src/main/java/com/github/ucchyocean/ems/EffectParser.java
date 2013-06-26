/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ems;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * ポーションエフェクト情報の解析クラス
 * @author ucchy
 */
public class EffectParser {

    private static final int DEFAULT_DURATION = 1000000;
    private static final String REGEX_EFFECT_PATTERN = "([0-9]+)-([0-9]+)";
    private static Pattern pattern;

    /**
     * Classのエフェクトデータ文字列を解析し、PotionEffect配列に変換する。
     * @param data 解析元の文字列　例）"1-2,2-2,3-1"
     * @return PotionEffectの配列
     */
    protected static ArrayList<PotionEffect> parseEffectData(String data) {

        if ( pattern == null ) {
            pattern = Pattern.compile(REGEX_EFFECT_PATTERN);
        }

        ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {

            Matcher matcher = pattern.matcher(array[i]);

            if ( matcher.matches() ) {

                int id = Integer.parseInt(matcher.group(1));
                int level = Integer.parseInt(matcher.group(2));

                PotionEffectType type = PotionEffectType.getById(id);
                if ( type == null ) {
                    continue;
                }

                PotionEffect applyEffect = new PotionEffect(
                        type, DEFAULT_DURATION, level);
                if ( applyEffect != null ) {
                    effects.add(applyEffect);
                }

            } else {

                EnchantMobSpawner.logger.severe(
                        "指定された形式 " + array[i] + " が正しく解析できません。");
                return null;
            }
        }

        return effects;
    }
}
