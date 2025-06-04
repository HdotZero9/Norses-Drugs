package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles configurable overdose effects
 */
public class OverdoseEffectManager {

    private static boolean enabled = true;
    private static int threshold = 3;
    private static boolean trackPerDrug = true;
    private static int attemptExpiry = 30;
    private static boolean broadcastMessages = true;
    
    private static boolean stagedEnabled = false;
    private static boolean randomEnabled = false;
    
    // Store overdose effects
    private static final List<OverdoseEffect> defaultEffects = new ArrayList<>();
    private static final Map<String, List<OverdoseEffect>> drugSpecificEffects = new ConcurrentHashMap<>();
    private static final Map<String, List<OverdoseEffect>> stagedEffects = new ConcurrentHashMap<>();
    private static final List<OverdoseEffect> randomEffects = new ArrayList<>();
    
    // Track player overdose counts for staged effects
    private static final Map<UUID, Integer> globalOverdoseCounts = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, Integer>> drugOverdoseCounts = new ConcurrentHashMap<>();
    
    /**
     * Loads overdose configuration
     */
    public static void load(File dataFolder) {
        File file = new File(dataFolder, "overdose.yml");
        
        // Create default file if it doesn't exist
        if (!file.exists()) {
            DrugsV2.getInstance().saveResource("overdose.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Clear existing data
        defaultEffects.clear();
        drugSpecificEffects.clear();
        stagedEffects.clear();
        randomEffects.clear();
        
        // Load global settings
        ConfigurationSection settingsSection = config.getConfigurationSection("settings");
        if (settingsSection != null) {
            enabled = settingsSection.getBoolean("enabled", true);
            threshold = settingsSection.getInt("threshold", 3);
            trackPerDrug = settingsSection.getBoolean("track-per-drug", true);
            attemptExpiry = settingsSection.getInt("attempt-expiry", 30);
            broadcastMessages = settingsSection.getBoolean("broadcast-messages", true);
        }
        
        // Load default effects
        ConfigurationSection defaultSection = config.getConfigurationSection("default");
        if (defaultSection != null) {
            defaultEffects.addAll(loadEffectList(defaultSection.getConfigurationSection("effects")));
        }
        
        // Load drug-specific effects
        ConfigurationSection drugSpecificSection = config.getConfigurationSection("drug_specific");
        if (drugSpecificSection != null) {
            for (String drugId : drugSpecificSection.getKeys(false)) {
                ConfigurationSection drugSection = drugSpecificSection.getConfigurationSection(drugId);
                if (drugSection != null) {
                    drugSpecificEffects.put(drugId.toLowerCase(), 
                            loadEffectList(drugSection.getConfigurationSection("effects")));
                }
            }
        }
        
        // Load staged effects
        ConfigurationSection stagedSection = config.getConfigurationSection("staged");
        if (stagedSection != null) {
            stagedEnabled = stagedSection.getBoolean("enabled", false);
            
            for (String stage : stagedSection.getKeys(false)) {
                if (stage.equals("enabled")) continue;
                
                ConfigurationSection stageSection = stagedSection.getConfigurationSection(stage);
                if (stageSection != null) {
                    stagedEffects.put(stage.toLowerCase(), 
                            loadEffectList(stageSection.getConfigurationSection("effects")));
                }
            }
        }
        
        // Load random effects
        ConfigurationSection randomSection = config.getConfigurationSection("random_effects");
        if (randomSection != null) {
            randomEnabled = randomSection.getBoolean("enabled", false);
            randomEffects.addAll(loadEffectList(randomSection.getConfigurationSection("effects")));
        }
        
        Bukkit.getLogger().info("[DrugsV2] Loaded overdose effects configuration. Enabled: " + enabled);
    }
    
    /**
     * Helper method to load a list of effects from a configuration section
     */
    private static List<OverdoseEffect> loadEffectList(ConfigurationSection section) {
        List<OverdoseEffect> effects = new ArrayList<>();
        
        if (section == null) return effects;
        
        for (String key : section.getKeys(false)) {
            ConfigurationSection effectSection = section.getConfigurationSection(key);
            if (effectSection == null) continue;
            
            String type = effectSection.getString("type", "").toLowerCase();
            
            switch (type) {
                case "death":
                    effects.add(new DeathEffect(effectSection.getString("message", "")));
                    break;
                    
                case "effects":
                    List<PotionEffect> potionEffects = new ArrayList<>();
                    ConfigurationSection potionSection = effectSection.getConfigurationSection("potion_effects");
                    if (potionSection != null) {
                        for (String potionKey : potionSection.getKeys(false)) {
                            ConfigurationSection effectConfig = potionSection.getConfigurationSection(potionKey);
                            if (effectConfig == null) continue;
                            
                            String effectName = effectConfig.getString("effect", "");
                            int duration = effectConfig.getInt("duration", 200);
                            int amplifier = effectConfig.getInt("amplifier", 0);
                            
                            try {
                                PotionEffectType effectType = PotionEffectType.getByName(effectName);
                                if (effectType != null) {
                                    potionEffects.add(new PotionEffect(effectType, duration, amplifier));
                                }
                            } catch (Exception e) {
                                Bukkit.getLogger().warning("[DrugsV2] Invalid potion effect: " + effectName);
                            }
                        }
                    }
                    effects.add(new PotionEffectsEffect(potionEffects));
                    break;
                    
                case "damage":
                    double amount = effectSection.getDouble("amount", 10);
                    String message = effectSection.getString("message", "");
                    effects.add(new DamageEffect(amount, message));
                    break;
                    
                case "message":
                    String text = effectSection.getString("text", "");
                    boolean broadcast = effectSection.getBoolean("broadcast", false);
                    effects.add(new MessageEffect(text, broadcast));
                    break;
                    
                case "sound":
                    String soundName = effectSection.getString("sound", "ENTITY_PLAYER_HURT");
                    float volume = (float) effectSection.getDouble("volume", 1.0);
                    float pitch = (float) effectSection.getDouble("pitch", 1.0);
                    
                    try {
                        Sound sound = Sound.valueOf(soundName);
                        effects.add(new SoundEffect(sound, volume, pitch));
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("[DrugsV2] Invalid sound: " + soundName);
                    }
                    break;
                    
                case "command":
                    String command = effectSection.getString("command", "");
                    boolean asConsole = effectSection.getBoolean("as_console", true);
                    effects.add(new CommandEffect(command, asConsole));
                    break;
            }
        }
        
        return effects;
    }
    
    /**
     * Processes an overdose for a player
     * 
     * @param player The player who overdosed
     * @param drugId The drug that caused the overdose
     * @param attemptCount How many times they've tried to use at max tolerance
     * @return true if the player should die (for backward compatibility)
     */
    public static boolean processOverdose(Player player, String drugId, int attemptCount) {
        if (!enabled) {
            // If disabled, use the old behavior (kill on 3+ attempts)
            return attemptCount >= threshold;
        }
        
        // Track overdose counts for staged effects
        UUID uuid = player.getUniqueId();
        
        // Update global count
        int globalCount = globalOverdoseCounts.getOrDefault(uuid, 0) + 1;
        globalOverdoseCounts.put(uuid, globalCount);
        
        // Update drug-specific count
        Map<String, Integer> drugCounts = drugOverdoseCounts.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        int drugCount = drugCounts.getOrDefault(drugId.toLowerCase(), 0) + 1;
        drugCounts.put(drugId.toLowerCase(), drugCount);
        
        // Check if we should apply effects yet
        if (attemptCount < threshold) {
            return false;
        }
        
        // Determine which effects to apply
        List<OverdoseEffect> effectsToApply = new ArrayList<>();
        
        // Check for drug-specific effects first
        if (drugSpecificEffects.containsKey(drugId.toLowerCase())) {
            effectsToApply.addAll(drugSpecificEffects.get(drugId.toLowerCase()));
        } 
        // Check for staged effects if enabled
        else if (stagedEnabled) {
            int relevantCount = trackPerDrug ? drugCount : globalCount;
            String stage;
            
            if (relevantCount == 1) {
                stage = "first";
            } else if (relevantCount == 2) {
                stage = "second";
            } else {
                stage = "third";
            }
            
            if (stagedEffects.containsKey(stage)) {
                effectsToApply.addAll(stagedEffects.get(stage));
            }
        }
        // Check for random effects if enabled
        else if (randomEnabled && !randomEffects.isEmpty()) {
            // Pick a random effect
            effectsToApply.add(randomEffects.get(new Random().nextInt(randomEffects.size())));
        }
        // Fall back to default effects
        else if (!defaultEffects.isEmpty()) {
            effectsToApply.addAll(defaultEffects);
        }
        // If no effects defined, use old behavior
        else {
            return true;
        }
        
        // Apply all effects
        boolean shouldDie = false;
        for (OverdoseEffect effect : effectsToApply) {
            if (effect.apply(player, drugId)) {
                shouldDie = true;
            }
        }
        
        return shouldDie;
    }
    
    /**
     * Resets overdose counts for a player
     */
    public static void resetOverdoseCounts(Player player) {
        UUID uuid = player.getUniqueId();
        globalOverdoseCounts.remove(uuid);
        drugOverdoseCounts.remove(uuid);
    }
    
    /**
     * Base interface for overdose effects
     */
    private interface OverdoseEffect {
        /**
         * Applies the effect to a player
         * @param player The player
         * @param drugId The drug ID
         * @return true if this effect should kill the player
         */
        boolean apply(Player player, String drugId);
    }
    
    /**
     * Effect that kills the player
     */
    private static class DeathEffect implements OverdoseEffect {
        private final String message;
        
        public DeathEffect(String message) {
            this.message = message;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            if (broadcastMessages && !message.isEmpty()) {
                String formattedMsg = ChatColor.translateAlternateColorCodes('&', 
                        message.replace("%player%", player.getName())
                               .replace("%drug%", drugId));
                Bukkit.broadcastMessage(formattedMsg);
            }
            
            player.setHealth(0);
            return true;
        }
    }
    
    /**
     * Effect that applies potion effects
     */
    private static class PotionEffectsEffect implements OverdoseEffect {
        private final List<PotionEffect> effects;
        
        public PotionEffectsEffect(List<PotionEffect> effects) {
            this.effects = effects;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            for (PotionEffect effect : effects) {
                player.addPotionEffect(effect);
            }
            return false;
        }
    }
    
    /**
     * Effect that damages the player
     */
    private static class DamageEffect implements OverdoseEffect {
        private final double amount;
        private final String message;
        
        public DamageEffect(double amount, String message) {
            this.amount = amount;
            this.message = message;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            if (!message.isEmpty()) {
                String formattedMsg = ChatColor.translateAlternateColorCodes('&', 
                        message.replace("%player%", player.getName())
                               .replace("%drug%", drugId));
                player.sendMessage(formattedMsg);
            }
            
            player.damage(amount);
            return false;
        }
    }
    
    /**
     * Effect that sends a message
     */
    private static class MessageEffect implements OverdoseEffect {
        private final String text;
        private final boolean broadcast;
        
        public MessageEffect(String text, boolean broadcast) {
            this.text = text;
            this.broadcast = broadcast;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            String formattedMsg = ChatColor.translateAlternateColorCodes('&', 
                    text.replace("%player%", player.getName())
                         .replace("%drug%", drugId));
            
            if (broadcast) {
                Bukkit.broadcastMessage(formattedMsg);
            } else {
                player.sendMessage(formattedMsg);
            }
            
            return false;
        }
    }
    
    /**
     * Effect that plays a sound
     */
    private static class SoundEffect implements OverdoseEffect {
        private final Sound sound;
        private final float volume;
        private final float pitch;
        
        public SoundEffect(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            player.playSound(player.getLocation(), sound, volume, pitch);
            return false;
        }
    }
    
    /**
     * Effect that executes a command
     */
    private static class CommandEffect implements OverdoseEffect {
        private final String command;
        private final boolean asConsole;
        
        public CommandEffect(String command, boolean asConsole) {
            this.command = command;
            this.asConsole = asConsole;
        }
        
        @Override
        public boolean apply(Player player, String drugId) {
            String formattedCmd = command.replace("%player%", player.getName())
                                        .replace("%drug%", drugId);
            
            if (asConsole) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd);
            } else {
                player.performCommand(formattedCmd);
            }
            
            return false;
        }
    }
} 