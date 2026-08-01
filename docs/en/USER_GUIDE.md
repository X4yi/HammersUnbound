# Hammers Unbound — User Guide

**Version:** r1.0b5 | Minecraft Forge 1.12.2

Hammers Unbound adds two distinct heavy weapon types to Minecraft, each with their own combat philosophy, status effects, crafting station, and configurable parameters.

---

## Getting Started

### The Hammer Forge

Neither the WarHammer nor the SpikeHammer can be crafted at a standard Crafting Table. Both require the **Hammer Forge**, a dedicated crafting station exclusive to this mod.

**Recipe:**
- 1 Iron Block
- 2 Iron Ingots
- 1 Crafting Table (center)

Interact with the Hammer Forge to open its interface. From there:
- Use the **outer tabs** to switch between WarHammer and SpikeHammer.
- Use the **top selector** to choose the desired material (Wood, Stone, Iron, Gold, Diamond).
- The ingredient list updates dynamically. Click **Forge** when all materials are available.
- The interface automatically pre-selects the best material found in the inventory.

**JEI Integration:** All Hammer Forge recipes are visible in Just Enough Items under the Hammer Forge category.

---

## WarHammer

A crowd-control weapon designed to immobilize groups of enemies simultaneously.

### Critical Hit — Stun + Ground Slam

Scoring a **critical hit** (falling while attacking) triggers two effects:

**1. Stun**
The primary target receives the `hammersunbound:stun` effect:
- Movement speed is reduced to zero.
- Horizontal motion and jumping are completely blocked (gravity still applies).
- AI pathfinding and attack targeting are cleared for mobs.
- Players under stun cannot move their camera.
- The target's rotation is locked on the server to prevent visual desynchronization.

**2. Ground Slam (AoE)**
An area-of-effect shockwave expands around the primary target:
- All entities within the configured radius take damage.
- Secondary targets also receive a shorter Stun effect.
- Block-breaking particles are spawned based on the block beneath the impact point.
- The radius, damage, and stun durations scale with fall distance if the player was airborne.

### Skybreaker (Active Skill)

The **Skybreaker** is the WarHammer's active ability. It launches the player upward, then allows a powered landing.

**Activation:** Hold **Shift** + **Right-Click** while looking **upward** (camera pitched more than 45° up).

**Effect:**
- The player is launched approximately 15 blocks upward with directional momentum from the look vector.
- Fall damage from this launch is **automatically cancelled**.
- While airborne with the immunity active, the player is immune to knockback.

**Landing — Ground Slam:**
If the player is holding **Shift** when landing after a Skybreaker leap:
- A Ground Slam is triggered centered on the **player** (not a target).
- The slam radius, damage, and stun scale with the distance fallen.

**Cooldown:** Tracked per-material in the item's NBT. Displayed in the HUD as the **"S" bar**.

---

## SpikeHammer

A sustained damage weapon that builds up bleeding stacks and enables blood manipulation through the BloodPact system.

### Bleeding

**Application:**
- Attacking while **sprinting** with a fully charged swing applies Level 1 Bleeding.
- **Critical hits** always apply Bleeding regardless of sprint state.
- Successive applications increase the stack level, up to the configured maximum.

**Behavior:**
- Deals periodic **magic damage** (ignores armor).
- Higher stack levels tick **faster** but decay more rapidly.
- Each level has its own duration and tick interval, calculated from base config values.

**Visual:** Blood particle effects appear on the target with each damage tick. The bleeding level is synced to nearby players.

### BloodPact (Active Skill)

The BloodPact is the SpikeHammer's primary skill. It creates a supernatural link between the wielder and nearby enemies.

**Activation:** **Right-Click** while aiming at an enemy within range.

**What happens on activation:**
- The targeted enemy and up to 2 additional nearby enemies are **linked**.
- A timer begins counting down. Hitting linked targets extends it; being hit by them reduces it.

**Passive effects while active:**

| Effect | Details |
|---|---|
| Lifesteal | A percentage of damage dealt to linked targets is converted to healing |
| Reach | Attack reach is permanently increased by +1.0 while active |
| Repulsion Field | Non-linked entities within the field radius are continuously pushed away |
| Attraction | Linked entities are pulled toward the player each tick |
| Passive Heal | +1 HP regenerated every 2 seconds |
| Blood Vignette | A red vignette appears on screen to indicate the active state |

### Madness Meter

The **Madness** bar (0–100) charges each time a linked target is hit:
- **+10** per hit on a linked target.
- Decays by **-5 every 2 seconds** when not attacking.

Madness scales two attribute buffs proportionally:
- Up to **+20% Movement Speed** at 100 Madness.
- Up to **+50% Attack Speed** at 100 Madness.

The current Madness value is displayed in the HUD while BloodPact is active.

### Blood Burst

Damage dealt to linked targets is accumulated internally. Every **10 seconds**, a detonation occurs:
- Each linked target takes `accumulatedDamage / 3` as magic damage.
- The player heals for a portion of the total burst damage dealt.
- A particle effect and explosion sound play on each affected target.

The burst countdown and accumulated damage are displayed in the HUD.

### AoE Sweep

While BloodPact is active, **Left-Click** performs a frontal sweep attack:
- Uses an AABB collision box centered 1.5 blocks in front of the player's eyes.
- All entities inside the box (except the primary target) take weapon damage.
- Has an internal anti-spam cooldown of **0.25 seconds**.

### Ping-Pong Skill

While BloodPact is active and an enemy is already linked, **Right-Click** toward a linked target launches the **Ping-Pong** sequence:

**Phase 1 — Ping:**
The target is launched in the player's look direction.
- If the target collides with a block mid-flight, it takes impact damage and immediately enters Phase 2.
- Otherwise, Phase 2 begins after the throw duration expires.

**Phase 2 — Pong:**
The target is pulled back rapidly toward the player.
- A "**HIT!**" alert pulses on screen.
- Hitting the target while it is returning during Phase 2 deals **+20% bonus damage** and immediately restarts the Ping-Pong cycle.
- If the target reaches the player without being hit, the sequence ends.
- If the player is struck by the returning target, the sequence is cancelled and the BloodPact timer is penalized.

---

## HUD

When holding a WarHammer or SpikeHammer, a cooldown display appears on screen.

### Cooldown Indicators

Each indicator is a 24×24 icon panel showing the held weapon with a fill overlay:

| Indicator | Color | Meaning |
|---|---|---|
| (no label) | Dark | Vanilla attack cooldown (standard Minecraft) |
| **P** | Red | Ground Slam / critical skill cooldown |
| **S** | Blue | Skybreaker leap cooldown |

A golden border replaces the dark border when a cooldown is fully charged.

### BloodPact HUD (SpikeHammer only)

While BloodPact is active, additional information appears near the cooldown panel:
- **Locura / Madness** — current Madness level (0–100)
- **Burst en / Burst in** — seconds remaining until the next Blood Burst
- **Daño Acum / Acum. Damage** — total damage accumulated for the next burst

### HUD Position

The HUD position is configurable in `config/hammersunbound/client.json`:

| Value | Position |
|---|---|
| `0` | Bottom Right |
| `1` | Bottom Center (above hotbar) |
| `2` | Bottom Left |
| `3` | Top Right |
| `4` | Top Left |
| `5` | Hidden |

---

## Materials

Both weapon types are available in five materials, each with different base statistics:

| Material | Tier | Notes |
|---|---|---|
| Wood | 1 | Lowest stats, fastest to obtain |
| Stone | 2 | — |
| Iron | 3 | Standard tier |
| Gold | 4 | High speed, low durability |
| Diamond | 5 | Highest stats |

All statistics (damage, attack speed, durability, skill cooldowns, bleeding values, BloodPact parameters) are fully configurable per-material in `config/hammersunbound/items.json`.

---

## Configuration

Configuration files are located in `config/hammersunbound/`. The in-game configuration screen is accessible from the mod list or via the **Config** button in the Changelog screen.

### items.json
Weapon statistics and ability values per material. See the [Configuration Reference](CONFIGURATION.md) for the full parameter list.

### server.json
Global server-side toggles and multipliers:
- Enable/disable Stun, AOE, Bleeding, and BloodPact independently.
- Global multipliers for damage, duration, range, and lifesteal.

### client.json
Client-side visual settings:
- Particle toggles and density multipliers for AOE, bleeding, and BloodPact visuals.
- HUD overlay position.
- UI language selection (`en` / `es`).
- Developer warning popup toggle.

---

## Changelog & Updates

The **Changelog** screen is accessible from the main menu or pause menu (if enabled in config). It displays release notes fetched from GitHub, with a sidebar listing all available versions. Language can be switched between English and Spanish directly from the sidebar.

When an update is available, a badge appears in the footer. Clicking it opens the mod's download page.
