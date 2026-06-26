# Configuration Reference: Hammers Unbound

Configurations are located in:

```text
config/hammersunbound/
```

---

## Configuration Files

| File | Scope | Purpose |
| --- | --- | --- |
| `items.json` | Balance | Weapon statistics (damage, speed, durability) and base values per material. |
| `server.json` | Server | Mechanics toggles and global gameplay multipliers. |
| `client.json` | Client | Particles, UI HUD overlay position, language, and warnings. |

---

## `items.json`

Holds properties for each material (`wood`, `stone`, `iron`, `gold`, `diamond`) for both `warhammer` and `spikehammer` types.

### `warhammer` Properties
*   **`baseDamage`** *(float)*: Base weapon damage.
*   **`attackSpeed`** *(float)*: Attack speed value.
*   **`durability`** *(int)*: Maximum durability.
*   **`skillCooldownSeconds`** *(float)*: Cooldown of critical Ground Slam.
*   **`abilities`**:
    *   **`stunDurationSeconds`** *(float)*: Primary Stun duration.
    *   **`stunAmplifier`** *(int)*: Stun potion level.
    *   **`aoeRadius`** *(float)*: Slam radius.
    *   **`aoeDamage`** *(float)*: Slam damage.
    *   **`aoeStunDurationSeconds`** *(float)*: Secondary Stun duration.
    *   **`aoeStunAmplifier`** *(int)*: Secondary Stun potion level.

### `spikehammer` Properties
*   **`baseDamage`**, **`attackSpeed`**, **`durability`**, **`skillCooldownSeconds`**: Same as WarHammer.
*   **`bleeding`**:
    *   **`maxLevels`** *(int)*: Max bleed stacks.
    *   **`baseDurationSeconds`** *(float)*: Bleed stack duration.
    *   **`damagePerLevel`** *(float)*: Bleed tick damage.
    *   **`tickIntervalSeconds`** *(float)*: Seconds between bleed ticks.
    *   **`decaySeconds`** *(float)*: Stack decay duration.
*   **`bloodPact`**:
    *   **`range`** *(float)*: Maximum link activation range.
    *   **`drainPercent`** *(float)*: Percentage of damage converted to lifesteal (0.0 to 1.0).
    *   **`tetherBreakDistance`** *(float)*: Distance at which the link breaks.
    *   **`maxTargets`** *(int)*: Max linked targets (capped at 3).
    *   **`fieldRadius`** *(float)*: Repulsion field radius.
    *   **`repulsionForce`** *(float)*: Repulsion physics force.
    *   **`attractionForce`** *(float)*: Attraction pull force.
    *   **`baseDurationSeconds`** *(float)*: Pact base duration.
    *   **`hitBonusSeconds`** *(float)*: Time added to pact duration on hit.
    *   **`damagePenaltySeconds`** *(float)*: Time deducted from pact duration when hit.
    *   **`aoeAttackSize`** *(float)*: Frontal sweep AABB collision box size.

---

## `server.json`

Global settings synchronized on the server.

### `warhammer`
*   **`stunDurationMultiplier`** *(float, default: 1.0)*: Multiplier for primary and secondary stun durations.
*   **`enableAOE`** *(boolean, default: true)*: Toggles ground slam damage.
*   **`enableStun`** *(boolean, default: true)*: Toggles stun potion application.
*   **`serverAoeParticleSyncDistance`** *(double, default: 64.0)*: Range for syncing impact particles.

### `spikehammer`
*   **`bleedingDamageMultiplier`** *(float, default: 1.0)*: Multiplies tick damage.
*   **`bleedingDurationMultiplier`** *(float, default: 1.0)*: Multiplies stack duration.
*   **`bloodPactRangeMultiplier`** *(float, default: 1.0)*: Multiplies link and break distances.
*   **`bloodPactDrainMultiplier`** *(float, default: 1.0)*: Multiplies lifesteal healing.
*   **`enableBleeding`** *(boolean, default: true)*: Toggles bleeding mechanics.
*   **`enableBloodPact`** *(boolean, default: true)*: Toggles link ability.

---

## `client.json`

Local configuration for client rendering.

### `aoeParticles`
*   **`aoeEnabled`** *(boolean, default: true)*: Toggles slam particles.
*   **`aoeParticleCountMultiplier`** *(float, default: 1.0)*: Slams particle count.
*   **`aoeParticleDensityMultiplier`** *(float, default: 1.0)*: Concentrates particles.
*   **`aoeHeightMultiplier`** *(float, default: 1.0)*: Vertical height and speed of particles.

### `combatVisuals`
*   **`bloodPactEnabled`** *(boolean, default: true)*: Toggles visual link tethers.
*   **`bloodPactParticleCount`** *(int, default: 5)*: Orbiting blood particle density.
*   **`bleedingParticleEnabled`** *(boolean, default: true)*: Toggles bleeding splashes.

### `ui`
*   **`uiOverlayPosition`** *(int, default: 0)*: HUD Position (`0`: Bottom Left, `1`: Bottom Right, `2`: Top Left, `3`: Top Right, `4`: Hidden).
*   **`showDevWarning`** *(boolean, default: true)*: Toggles dev popup warnings.
*   **`showChangelogButton`** *(boolean, default: true)*: Toggles UI changelog.
*   **`language`** *(string, default: "es")*: Preferred GUI language.
