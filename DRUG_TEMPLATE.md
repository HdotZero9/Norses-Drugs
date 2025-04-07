# 🧪 DrugsV2 - Create Your Own Drug

## Step-by-Step

### 1. Add to `config.yml`

```yaml
mydrugid:
  material: SUGAR
  display-name: "&bMy Custom Drug"
  lore:
    - "&7Describe your drug here."
  effects:
    SPEED:
      duration: 600
      amplifier: 1
    STRENGTH:
      duration: 400
      amplifier: 0
```

### 2. Add matching entry to `recipes.yml`

```yaml
mydrugid:
  shape:
    - " S "
    - "GAG"
    - " B "
  ingredients:
    S: SUGAR
    G: GUNPOWDER
    A: APPLE
    B: BONE
```

### 3. Optional: Add to `tolerance.yml`

```yaml
mydrugid:
  max: 4
  decay-minutes: 3
  effectiveness:
    0: 1.0
    1: 0.8
    2: 0.6
    3: 0.4
    4: 0.0
```

## ✅ Then run `/drugs reload` and it's live!
