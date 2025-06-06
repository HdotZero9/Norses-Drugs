# DrugsV2 PlaceholderAPI Integration

This plugin provides optional integration with [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/). When PlaceholderAPI is installed, you can use various placeholders to display information about drug tolerances and effectiveness.

## Requirements
- PlaceholderAPI installed on your server
- DrugsV2 plugin

## Available Placeholders

### Basic Tolerance
```
%drugs_<drugname>%
```
Shows the current tolerance level for the specified drug.
- Example: `%drugs_heroin%` might return `3`

### Maximum Tolerance
```
%drugs_<drugname>_max%
```
Shows the maximum possible tolerance level for the specified drug.
- Example: `%drugs_blue_ice_max%` might return `5`

### Drug Effectiveness
```
%drugs_<drugname>_effectiveness%
```
Shows the current effectiveness percentage for the specified drug based on tolerance level.
- Example: `%drugs_cocaine_effectiveness%` might return `75.0`

## Usage Examples

### Chat Format
```yaml
chat-format: "&7[&c%drugs_heroin%/%drugs_heroin_max%&7] &f%player_name%"
```

### Scoreboard
```yaml
scoreboard:
  title: "&6Drug Stats"
  lines:
    - "&fHeroin Tolerance: &c%drugs_heroin%/%drugs_heroin_max%"
    - "&fEffectiveness: &a%drugs_heroin_effectiveness%%"
```

### Hologram Example (HolographicDisplays)
```yaml
holograms:
  - "&6Current Drug Status"
  - "&fTolerance: &c%drugs_blue_ice%"
  - "&fMax Tolerance: &c%drugs_blue_ice_max%"
  - "&fEffectiveness: &a%drugs_blue_ice_effectiveness%%"
```

## Notes
- All placeholders are case-insensitive
- Works with all registered drugs, including custom drugs
- Returns empty string if the player is offline
- Effectiveness is displayed as a percentage with one decimal place
- All placeholders update in real-time as tolerance levels change

## Custom Drug Support
The placeholders automatically work with any custom drugs you've added to the system. Just use the same format with your custom drug ID:
```
%drugs_<custom_drug_id>%
%drugs_<custom_drug_id>_max%
%drugs_<custom_drug_id>_effectiveness%
```

## Troubleshooting
1. Make sure PlaceholderAPI is installed
2. Verify the drug name exists in your configuration
3. Check that you're using the correct placeholder format
4. Ensure the player is online when using the placeholders 