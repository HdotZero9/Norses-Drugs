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

## 📦 Commands

| Command | Description |
|--------|-------------|
| `/drugs` | Opens the drug GUI |
| `/drugs help` | Shows help menu |
| `/drugs give <player> <drug> [amount]` | Gives a drug to a player (admin) |
| `/drugs purge <player>` | Resets tolerance for a player (admin) |
| `/drugs reload` | Reloads all configs |
| `/drugs list` | Lists all loaded drugs |
| `/tolerance` | View your own tolerance levels |

## 🔐 Permissions

| Node | Access |
|------|--------|
| `drugs.menu` | Access the /drugs GUI (default: true) |
| `drugs.give` | Use /drugs give (default: op) |
| `drugs.tolerance` | Use /tolerance |
| `drugs.admin.reload` | Use /drugs reload |
| `drugs.admin.purge` | Use /drugs purge |
| `drugs.admin.list` | Use /drugs list |

## ⚙️ Configuration
Edit the files in `/plugins/DrugsV2/`:
- `config.yml` – drug effects, lore, materials
- `recipes.yml` – crafting shapes + ingredients
- `tolerance.yml` – scaling + decay per drug

## 📘 Add Your Own Drugs
See [`DRUG_TEMPLATE.md`](./DRUG_TEMPLATE.md)

## ✅ Supports:
- Minecraft 1.21+
- Paper, Spigot

Enjoy 😉
