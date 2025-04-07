package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Tracks tolerance and overdose behavior for players.
 */
public class ToleranceTracker {

    private static final Map<UUID, Map<String, Integer>> toleranceLevels = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> lastUseTimestamps = new HashMap<>();
    private static final Map<UUID, Map<String, Integer>> overdoseAttempts = new HashMap<>();
    private static final Set<String> cleanSlateProgress = new HashSet<>();

    public static void onDrugUse(Player player, String drugId) {
        UUID uuid = player.getUniqueId();
        int current = getToleranceLevel(player, drugId);
        int max = ToleranceConfigLoader.getMaxTolerance(drugId);

        if (current < max) {
            setToleranceLevel(player, drugId, current + 1);
            resetOverdoseCount(player, drugId);
        }

        updateLastUse(player, drugId);

        // Track that they reached max tolerance for Clean Slate later
        if (current + 1 >= max) {
            cleanSlateProgress.add(uuid + ":" + drugId.toLowerCase());
        }

        // Achievement: I Can Stop Anytime – max tolerance on 3+ drugs
        int maxedDrugs = 0;
        Map<String, Integer> playerTolerances = toleranceLevels.getOrDefault(uuid, Collections.emptyMap());

        for (Map.Entry<String, Integer> entry : playerTolerances.entrySet()) {
            int tolLevel = entry.getValue();
            int tolMax = ToleranceConfigLoader.getMaxTolerance(entry.getKey());
            if (tolLevel >= tolMax) {
                maxedDrugs++;
            }
        }

        if (maxedDrugs >= 3) {
            AchievementManager.grant(player, AchievementManager.DrugAchievement.MAXED_THREE);
        }
    }

    public static int getToleranceLevel(Player player, String drugId) {
        return toleranceLevels
                .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                .getOrDefault(drugId.toLowerCase(), 0);
    }

    private static void setToleranceLevel(Player player, String drugId, int value) {
        toleranceLevels.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(drugId.toLowerCase(), value);
    }

    private static void updateLastUse(Player player, String drugId) {
        lastUseTimestamps.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(drugId.toLowerCase(), System.currentTimeMillis());
    }

    public static double getEffectivenessMultiplier(Player player, String drugId) {
        int level = getToleranceLevel(player, drugId);
        return ToleranceConfigLoader.getEffectivenessMultiplier(drugId, level);
    }

    public static void startDecayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                for (UUID uuid : new HashSet<>(toleranceLevels.keySet())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;

                    Map<String, Integer> playerTolerance = toleranceLevels.get(uuid);
                    Map<String, Long> playerTimestamps = lastUseTimestamps.getOrDefault(uuid, Collections.emptyMap());

                    for (String drugId : new HashSet<>(playerTolerance.keySet())) {
                        int level = playerTolerance.get(drugId);
                        if (level <= 0) continue;

                        long lastUsed = playerTimestamps.getOrDefault(drugId, 0L);
                        int decayMinutes = ToleranceConfigLoader.getDecayMinutes(drugId);
                        long decayMillis = decayMinutes * 60_000L;

                        if (now - lastUsed >= decayMillis) {
                            int newLevel = level - 1;
                            playerTolerance.put(drugId, newLevel);
                            updateLastUse(player, drugId); // restart timer

                            // Grant Clean Slate if it decays from max to 0
                            String key = uuid + ":" + drugId.toLowerCase();
                            if (newLevel == 0 && cleanSlateProgress.contains(key)) {
                                cleanSlateProgress.remove(key);
                                AchievementManager.grant(player, AchievementManager.DrugAchievement.CLEAN_SLATE);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(DrugsV2.getInstance(), 20L, 60L * 20);
    }

    public static int incrementOverdoseCount(Player player, String drugId) {
        UUID uuid = player.getUniqueId();
        String id = drugId.toLowerCase();

        Map<String, Integer> map = overdoseAttempts.computeIfAbsent(uuid, k -> new HashMap<>());
        int current = map.getOrDefault(id, 0);
        current++;
        map.put(id, current);
        return current;
    }

    public static void resetOverdoseCount(Player player, String drugId) {
        overdoseAttempts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(drugId.toLowerCase(), 0);
    }

    public static void resetAllTolerance(Player player) {
        toleranceLevels.remove(player.getUniqueId());
        lastUseTimestamps.remove(player.getUniqueId());
        overdoseAttempts.remove(player.getUniqueId());
    }

    public static boolean isAtMaxTolerance(UUID playerId, String drugId) {
        int current = toleranceLevels
                .getOrDefault(playerId, Collections.emptyMap())
                .getOrDefault(drugId.toLowerCase(), 0);

        int max = ToleranceConfigLoader.getMaxTolerance(drugId);
        return current >= max;
    }
}
