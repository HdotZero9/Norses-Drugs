package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles performance optimizations for the plugin
 */
public class PerformanceOptimizer {

    // Use ConcurrentHashMap for thread safety without excessive locking
    private static final Map<UUID, Map<String, Integer>> toleranceCache = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastCacheSyncTime = new ConcurrentHashMap<>();
    
    // Cache expiry time in milliseconds (5 minutes)
    private static final long CACHE_EXPIRY_TIME = 5 * 60 * 1000;
    
    // Batch size for processing players
    private static final int BATCH_SIZE = 10;
    
    /**
     * Initializes the performance optimizer
     */
    public static void initialize() {
        // Start cache cleanup task
        startCacheCleanupTask();
        
        // Start batched processing task
        startBatchedProcessingTask();
        
        Bukkit.getLogger().info("[DrugsV2] Performance optimizer initialized");
    }
    
    /**
     * Starts a task to clean up expired cache entries
     */
    private static void startCacheCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                
                // Remove expired cache entries
                lastCacheSyncTime.entrySet().removeIf(entry -> {
                    if (now - entry.getValue() > CACHE_EXPIRY_TIME) {
                        toleranceCache.remove(entry.getKey());
                        return true;
                    }
                    return false;
                });
            }
        }.runTaskTimerAsynchronously(DrugsV2.getInstance(), 20 * 60, 20 * 60); // Run every minute
    }
    
    /**
     * Starts a task to process players in batches
     */
    private static void startBatchedProcessingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Process tolerance decay in batches
                processBatchedToleranceDecay();
            }
        }.runTaskTimer(DrugsV2.getInstance(), 20 * 5, 20 * 5); // Run every 5 seconds
    }
    
    /**
     * Process tolerance decay in batches to avoid lag spikes
     */
    private static void processBatchedToleranceDecay() {
        // Get all online players
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        
        // Process in batches
        for (int i = 0; i < players.length; i += BATCH_SIZE) {
            final int startIndex = i;
            
            // Process this batch after a delay based on batch number
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int j = startIndex; j < startIndex + BATCH_SIZE && j < players.length; j++) {
                        ToleranceTracker.processDecay(players[j]);
                    }
                }
            }.runTaskLater(DrugsV2.getInstance(), (i / BATCH_SIZE) * 2); // 2 tick delay between batches
        }
    }
    
    /**
     * Gets cached tolerance data for a player
     * 
     * @param uuid The player UUID
     * @return The tolerance data, or null if not cached
     */
    public static Map<String, Integer> getCachedToleranceData(UUID uuid) {
        return toleranceCache.get(uuid);
    }
    
    /**
     * Updates the tolerance cache for a player
     * 
     * @param uuid The player UUID
     * @param toleranceData The tolerance data to cache
     */
    public static void updateToleranceCache(UUID uuid, Map<String, Integer> toleranceData) {
        toleranceCache.put(uuid, new HashMap<>(toleranceData)); // Store a copy to prevent modification
        lastCacheSyncTime.put(uuid, System.currentTimeMillis());
    }
    
    /**
     * Invalidates the tolerance cache for a player
     * 
     * @param uuid The player UUID
     */
    public static void invalidateToleranceCache(UUID uuid) {
        toleranceCache.remove(uuid);
        lastCacheSyncTime.remove(uuid);
    }
    
    /**
     * Checks if a player's tolerance data is cached
     * 
     * @param uuid The player UUID
     * @return true if cached, false otherwise
     */
    public static boolean isToleranceCached(UUID uuid) {
        return toleranceCache.containsKey(uuid);
    }
    
    /**
     * Checks if a player's cache is expired
     * 
     * @param uuid The player UUID
     * @return true if expired, false otherwise
     */
    public static boolean isCacheExpired(UUID uuid) {
        Long lastSync = lastCacheSyncTime.get(uuid);
        if (lastSync == null) {
            return true;
        }
        
        return System.currentTimeMillis() - lastSync > CACHE_EXPIRY_TIME;
    }
} 