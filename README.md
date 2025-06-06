# Norse's Drugs - Minecraft Drug Plugin

Norse's Drugs is a powerful, modular Minecraft plugin that adds craftable, configurable combat drugs with effects, tolerance, overdosing, and GUI support.

## 🔥 Features
- Craftable drugs with custom effects
- Tolerance system that reduces potency with repeated use
- Overdose system when abusing drugs at max tolerance
- Configurable recipes and effects (no hardcoded logic)
- GUI for browsing and previewing all drugs
- Admin tools: reload configs, purge players tolerances, list drugs
- Add your own drugs in seconds!
- **NEW!** Fully customizable achievement system with multilingual support
- **NEW!** Create your own custom achievements with different triggers and rewards
- **NEW!** Configurable overdose effects with drug-specific consequences
- **NEW!** PlaceholderAPI support for displaying drug tolerances and effectiveness

## 🧪 Default Drugs Included
- **Heroin** – Strength & Speed
- **Henbane** – Berserker burst (Strength III + Blindness)
- **Opium** – Regeneration & Resistance
- **Fent** – Quick PvP burst with high risk
- **Morphine** – Regeneration tank
- **Shroomshake** – Luck + Jump + Trippy visuals
- **Blue Crystal** – Speed + Haste (Made in an RV ;))
- **Cryozen** – Tanky, icy resistance
- **Shadowspike** – Invisibility & escape tool

## 🏆 Achievements

Players can unlock progress-based achievements by using drugs in specific ways. Use `/drugs achievements` to track them!

### Default Achievements
- **First Dose** – Use your first drug
- **Chem Connoisseur** – Try every drug at least once
- **Risky Business** – Use a drug while at max tolerance
- **I Can Stop Anytime** – Reach max tolerance on 3 different drugs
- **Clean Slate** – Let a maxed drug's tolerance decay all the way back to 0
- **Flatline** – Die from a drug overdose
- **Close Call** – Survive an overdose attempt

### Custom Achievements
You can create your own achievements by adding them to the `achievements.yml` file. Each achievement needs:
- A unique ID
- Title and description (with color code support)
- A trigger type that determines when it's granted
- Icon materials for locked and unlocked states

Available triggers include:
- `first_drug_use`: First time using any drug
- `all_drugs_used`: Using every registered drug at least once
- `use_at_max`: Using a drug while at max tolerance
- `maxed_three`: Hitting max tolerance on 3+ drugs
- `decay_full`: When a maxed drug's tolerance decays to 0
- `use_specific_drug`: Using a specific drug (requires drug_id)
- `craft_drug`: Crafting any drug
- `craft_specific`: Crafting a specific drug (requires drug_id)
- `overdose_survive`: Surviving an overdose attempt
- `overdose_death`: Dying from an overdose
- `use_count`: Using drugs a certain number of times

Example custom achievement:
```yaml
crystal-chef:
  title: "&9Crystal Chef"
  description: "&7Craft Blue Crystal for the first time"
  trigger: "craft_specific"
  drug_id: "blue_crystal"
  icon: "LAPIS_LAZULI"
  completed_icon: "LAPIS_BLOCK"
```

### Achievement Customization

You can fully customize the achievement system in the `achievement_settings.yml` file:

- Enable/disable the entire achievement system
- Enable/disable individual achievements
- Customize achievement titles and descriptions in any language
- Configure notification settings (chat messages, sounds, fireworks)

## 💀 Configurable Overdose Effects

The plugin now features a highly customizable overdose system that goes beyond simple death when players overdose. Configure in `overdose.yml`:

### Overdose Features
- **Drug-Specific Effects**: Define unique overdose consequences for each drug
- **Effect Types**: Choose from death, potion effects, damage, messages, sounds, and commands
- **Staged Progression**: Set different effects based on number of overdoses
- **Random Effect Pools**: Configure random effect selection for unpredictability
- **Threshold Control**: Set how many attempts trigger overdose effects
- **Global or Per-Drug Tracking**: Track overdose attempts globally or per drug
- **Customizable Messages**: Set custom messages with color codes and variables

Example configuration:
```yaml
drug_specific:
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

For detailed instructions on configuring overdose effects, see [`OVERDOSE_EFFECTS.md`](./OVERDOSE_EFFECTS.md).

## 🚀 Performance Optimizations

This plugin is optimized for large servers with hundreds of players:

- **Smart Caching**: Reduces database/disk operations by caching player data
- **Batched Processing**: Processes tolerance decay in small batches to prevent lag spikes
- **Concurrent Collections**: Uses thread-safe data structures for multi-threaded operations
- **Memory Management**: Automatically cleans up unused cache entries to prevent memory leaks
- **Pre-computed Values**: Stores frequently accessed values to avoid expensive operations
- **Asynchronous Tasks**: Runs non-essential tasks off the main thread

## 📦 Commands

| Command | Description |
|--------|-------------|
| `/drugs` | Opens the drug GUI |
| `/drugs help` | Shows help menu |
| `/drugs give <player> <drug> [amount]` | Gives a drug to a player (admin) |
| `/drugs purge <player>` | Resets tolerance and overdose counts for a player (admin) |
| `/drugs reload` | Reloads all configs |
| `/drugs list` | Lists all loaded drugs |
| `/drugs overdose reload` | Reloads overdose configuration (admin) |
| `/drugs overdose reset <player>` | Resets a player's overdose counts (admin) |
| `/tolerance` | View your own tolerance levels |
| `/drugs achievements` | View your personal achievement progress |

## 🔐 Permissions

| Node | Access |
|------|--------|
| `drugs.menu` | Access the /drugs GUI (default: true) |
| `drugs.give` | Use /drugs give (default: op) |
| `drugs.tolerance` | Use /tolerance |
| `drugs.admin.reload` | Use /drugs reload |
| `drugs.admin.purge` | Use /drugs purge |
| `drugs.admin.list` | Use /drugs list |
| `drugs.achievements` | Use /drugs achievements |
| `drugs.admin.achievements` | Manage achievement settings |
| `drugs.admin.overdose` | Manage overdose settings |

## ⚙️ Configuration
Edit the files in `/plugins/DrugsV2/`:
- `config.yml` – drug effects, lore, materials
- `recipes.yml` – crafting shapes + ingredients
- `tolerance.yml` – scaling + decay per drug
- `achievement_settings.yml` – achievement customization and language settings
- `achievements.yml` – define custom achievements and triggers
- `overdose.yml` – configure overdose effects and behavior

## 📘 Documentation
- [`DRUG_TEMPLATE.md`](./DRUG_TEMPLATE.md) - Learn how to add your own drugs
- [`OVERDOSE_EFFECTS.md`](./OVERDOSE_EFFECTS.md) - Configure overdose effects
- [`PLACEHOLDER_USAGE.md`](./PLACEHOLDER_USAGE.md) - PlaceholderAPI integration guide

## 📘 Add Your Own Drugs
See [`DRUG_TEMPLATE.md`](./DRUG_TEMPLATE.md)

## ✅ Supports:
- Minecraft 1.21+
- Paper, Spigot
- PlaceholderAPI

Enjoy 😉
