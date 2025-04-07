package com.drugs;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class AchievementManager {

    // Master list of achievement keys and their display info
    public enum DrugAchievement {
        FIRST_DOSE("first-dose", "&aFirst Dose", "&7Use your first drug"),
        CONNOISSEUR("connoisseur", "&bChem Connoisseur", "&7Try every drug at least once"),
        RISKY_BUSINESS("risky", "&6Risky Business", "&7Use a drug while at max tolerance"),
        CLEAN_SLATE("clean-slate", "&eClean Slate", "&7Let a maxed drug decay to 0"),
        MAXED_THREE("maxed-three", "&dI Can Stop Anytime", "&7Hit max tolerance on 3 drugs");

        public final String key;
        public final String title;
        public final String description;

        DrugAchievement(String key, String title, String description) {
            this.key = key;
            this.title = title;
            this.description = description;
        }

        public static DrugAchievement getByKey(String key) {
            for (DrugAchievement a : values()) {
                if (a.key.equalsIgnoreCase(key)) return a;
            }
            return null;
        }

        public static List<DrugAchievement> getAll() {
            return Arrays.asList(values());
        }
    }

    public static void grant(Player player, DrugAchievement achievement) {
        PlayerAchievementData data = new PlayerAchievementData(player.getUniqueId());

        if (data.hasAchievement(achievement.key)) return;

        data.grantAchievement(achievement.key);

        // Fancy unlock message
        String msg = ChatColor.translateAlternateColorCodes('&',
                "&6[Drugs] &aAchievement Unlocked: " + achievement.title);
        player.sendMessage(msg);

        // Sound effect
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

        // Firework celebration
        Firework fw = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.AQUA)
                .withFade(Color.WHITE)
                .with(Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }

    public static boolean has(Player player, DrugAchievement achievement) {
        PlayerAchievementData data = new PlayerAchievementData(player.getUniqueId());
        return data.hasAchievement(achievement.key);
    }

    public static Set<String> getUnlocked(Player player) {
        PlayerAchievementData data = new PlayerAchievementData(player.getUniqueId());
        return data.getUnlockedAchievements();
    }
}
