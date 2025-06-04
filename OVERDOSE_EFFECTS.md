# Configuring Overdose Effects

This guide explains how to configure custom overdose effects in Norse's Drugs plugin.

## Overview

The overdose system allows server owners to create unique consequences when players overdose on drugs, moving beyond the simple "instant death" approach. You can configure different effects for each drug, create staged progression based on overdose count, or even set up random effect pools.

## Configuration File

All overdose settings are stored in `plugins/DrugsV2/overdose.yml`. The file is organized into several sections:

```yaml
settings:     # Global settings
default:      # Default effects (fallback)
drug_specific: # Drug-specific effects
staged:       # Staged progression effects
random_effects: # Random effect pools
```

## Global Settings

```yaml
settings:
  enabled: true                # Enable/disable the entire overdose system
  threshold: 3                 # How many attempts before triggering effects
  track-per-drug: true         # Track overdose attempts per drug or globally
  attempt-expiry: 30           # How long overdose attempts are remembered (minutes)
  broadcast-messages: true     # Whether to broadcast overdose messages
```

## Effect Types

You can configure various effect types:

### Death Effect
Kills the player with an optional message.

```yaml
- type: "death"
  message: "&4%player% died from a %drug% overdose!"
```

### Potion Effects
Applies potion effects to the player.

```yaml
- type: "effects"
  potion_effects:
    - effect: "WEAKNESS"
      duration: 2400  # In ticks (20 ticks = 1 second)
      amplifier: 2    # Level of effect (0 = level 1)
    - effect: "SLOW"
      duration: 1200
      amplifier: 1
```

### Damage Effect
Deals damage to the player.

```yaml
- type: "damage"
  amount: 16
  message: "&cYour heart can't handle the stimulants!"
```

### Message Effect
Sends a message to the player or broadcasts it.

```yaml
- type: "message"
  text: "&cYou overdosed and feel terrible!"
  broadcast: false  # Set to true to broadcast to all players
```

### Sound Effect
Plays a sound at the player's location.

```yaml
- type: "sound"
  sound: "ENTITY_PLAYER_HURT"
  volume: 1.0
  pitch: 1.0
```

### Command Effect
Executes a command as the player or console.

```yaml
- type: "command"
  command: "effect give %player% minecraft:nausea 30 1"
  as_console: true  # Set to false to run as the player
```

## Variables

You can use these variables in messages and commands:

- `%player%` - The player's name
- `%drug%` - The drug ID that caused the overdose

## Drug-Specific Effects

Configure unique effects for specific drugs:

```yaml
drug_specific:
  fent:
    effects:
      - type: "death"
        message: "&4%player% died from a Fent overdose!"
      - type: "message"
        text: "&cYou overdosed on Fent and died!"
        broadcast: false

  blue_crystal:
    effects:
      - type: "effects"
        potion_effects:
          - effect: "WEAKNESS"
            duration: 2400
            amplifier: 2
      - type: "damage"
        amount: 16
        message: "&cYour heart can't handle the stimulants!"
```

## Staged Progression

Configure different effects based on how many times a player has overdosed:

```yaml
staged:
  enabled: true
  first:  # First overdose
    effects:
      - type: "message"
        text: "&eYou feel sick from the overdose, but survive."
      - type: "effects"
        potion_effects:
          - effect: "CONFUSION"
            duration: 600
            amplifier: 0
  
  second:  # Second overdose
    effects:
      - type: "message"
        text: "&cYou barely survive another overdose!"
      - type: "damage"
        amount: 10
  
  third:  # Third overdose
    effects:
      - type: "death"
        message: "&4%player% couldn't handle another overdose and died!"
```

## Random Effect Pools

Configure a pool of effects where one is randomly selected:

```yaml
random_effects:
  enabled: true
  effects:
    - type: "effects"
      potion_effects:
        - effect: "BLINDNESS"
          duration: 600
          amplifier: 1
    - type: "damage"
      amount: 15
    - type: "death"
      message: "&4Bad luck! %player% died from an overdose!"
```

## Default Effects

These effects are used if no drug-specific, staged, or random effects apply:

```yaml
default:
  effects:
    - type: "death"
      message: "&4%player% died from a drug overdose!"
    - type: "message"
      text: "&cYou overdosed and died!"
      broadcast: false
```

## Commands

- `/drugs overdose reload` - Reload overdose configuration (requires `drugs.admin.overdose` permission)
- `/drugs overdose reset <player>` - Reset a player's overdose counts (requires `drugs.admin.overdose` permission)

## Example: Creating a Custom Overdose Effect

Here's an example of a custom overdose effect for a drug called "nightmare":

```yaml
drug_specific:
  nightmare:
    effects:
      - type: "message"
        text: "&5&lYour mind is consumed by darkness..."
        broadcast: true
      - type: "effects"
        potion_effects:
          - effect: "BLINDNESS"
            duration: 1200
            amplifier: 0
          - effect: "CONFUSION"
            duration: 1200
            amplifier: 2
      - type: "sound"
        sound: "ENTITY_ENDERMAN_SCREAM"
        volume: 1.0
        pitch: 0.5
      - type: "command"
        command: "title %player% title {\"text\":\"NIGHTMARE\",\"color\":\"dark_purple\",\"bold\":true}"
        as_console: true
```

This will:
1. Broadcast a message about the player's mind being consumed
2. Apply Blindness and Confusion effects
3. Play the Enderman scream sound
4. Display a title saying "NIGHTMARE" to the player

## Valid Potion Effects

Here are some common potion effect types you can use:
- `BLINDNESS`
- `CONFUSION` (Nausea)
- `DAMAGE_RESISTANCE`
- `FAST_DIGGING` (Haste)
- `FIRE_RESISTANCE`
- `HARM` (Instant Damage)
- `HEAL` (Instant Health)
- `HUNGER`
- `INCREASE_DAMAGE` (Strength)
- `INVISIBILITY`
- `JUMP`
- `LEVITATION`
- `NIGHT_VISION`
- `POISON`
- `REGENERATION`
- `SLOW` (Slowness)
- `SLOW_DIGGING` (Mining Fatigue)
- `SPEED`
- `WATER_BREATHING`
- `WEAKNESS`
- `WITHER`

## Valid Sound Effects

Some common sound effects:
- `ENTITY_PLAYER_HURT`
- `ENTITY_PLAYER_DEATH`
- `ENTITY_WITHER_DEATH`
- `ENTITY_ENDERMAN_SCREAM`
- `ENTITY_GHAST_SCREAM`
- `BLOCK_GLASS_BREAK`
- `ENTITY_LIGHTNING_BOLT_THUNDER`
- `ENTITY_GENERIC_EXPLODE`
- `BLOCK_NOTE_BLOCK_PLING` 