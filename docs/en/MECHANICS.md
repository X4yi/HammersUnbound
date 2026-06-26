# Technical Specifications & Formulas

Comprehensive formulas, attribute modifiers, physics calculations, and packet specifications for **Hammers Unbound**.

---

## 🩸 Bleeding Mechanics

Processed on entities via `IBleedingCapability`. The bleed stack level ($L$) dictates three values: tick damage, duration, and tick interval.

### 1. Tick Damage Calculation

$$\text{Damage} = \text{damagePerLevel} \times \left(0.5 + 0.25 \times (L - 1)\right)$$

### 2. Duration / Decay Calculation
When `ticksUntilDecay` reaches zero, $L$ decreases by 1 and the timer resets:

$$\text{ticksUntilDecay} = \text{baseDuration} \times \max\left(0.4, \; 2.0 - (L - 1) \times 0.3\right)$$

### 3. Damage Tick Interval Calculation
Controls the frequency of bleeding damage ticks:

$$\text{ticksUntilDamage} = \max\left(5, \; \text{configTickInterval} \times \text{multiplier}\right)$$

Where the multiplier is determined by:
*   **Level 1:** $\times 4.0$
*   **Level 2:** $\times 2.5$
*   **Level 3:** $\times 1.5$
*   **Level 4:** $\times 1.0$
*   **Level 5+:** $\max\left(0.5, \; 1.0 - (L - 4) \times 0.1\right)$ (capped at a minimum of $\times 0.5$ at Level 9)

---

## 🌀 Stun Mechanics

Applied via the `hammersunbound:stun` potion effect. Implemented server-side and client-side:

### 1. Server-Side Physics & AI Overrides
*   **Attributes:** Applies a modifier on `SharedMonsterAttributes.MOVEMENT_SPEED` of $-100\%$ (Value: `-1.0D`, Operation: `2`).
*   **Motion:** In `LivingUpdateEvent`, locks motion axes:
    *   `motionX = 0.0D`, `motionZ = 0.0D`
    *   `motionY = \min(motionY, 0.0D)` (allows falling, prevents upward vertical velocity)
*   **AI:** Triggers `living.getNavigator().clearPath()` and `living.setAttackTarget(null)`.
*   **Angle Lock:** Entity rotation angles (`rotationYaw`, `rotationPitch`, etc.) are saved in NBT and forced back on update to prevent packet desynchronization.

### 2. Client-Side Mouse Override
*   **Mouse Capture:** `StunMouseHelper` replaces the default `MouseHelper`.
*   **Interception:** Overrides `mouseXYChange()` to set raw mouse `deltaX = 0` and `deltaY = 0` when the stun potion is active, disabling camera looking.

---

## 🩸 BloodPact Mechanics

Tracked on the player using `IBloodPactCapability`.

### 1. Attribute Modifiers
UUID modifiers applied to the player proportional to their **Madness** ($M$, ranges 0–100):

*   **Movement Speed:**
    *   **UUID:** `6d7f022b-2a71-46ab-a021-e0e56b4685ff`
    *   **Equation:** $\text{Bonus} = \left(\frac{M}{100.0}\right) \times 0.20$ (Operation: `2`, up to $+20\%$).
*   **Attack Speed:**
    *   **UUID:** `a1b6a7b3-c15c-4d57-b088-348f9fa4ea88`
    *   **Equation:** $\text{Bonus} = \left(\frac{M}{100.0}\right) \times 0.50$ (Operation: `2`, up to $+50\%$).
*   **Attack Reach:**
    *   **UUID:** `2d7d8e6a-5a91-4d37-88cc-f5e94b26715f`
    *   **Equation:** $+1.0D$ constant (Operation: `0`).

### 2. Physics Fields
*   **Attraction (Linked Mobs):**
    Adds acceleration towards the player:
    
    $$\Delta v = \text{attractionForce} \times \text{Normalize}(\vec{pos}_{\text{player}} - \vec{pos}_{\text{mob}})$$
    
    If the mob is a pathfinding entity, forces a target path destination with speed `1.25D`.
*   **Repulsion (Non-Linked Mobs):**
    For mobs inside the `fieldRadius` ($R$):
    
    $$\text{force} = \left(\frac{R - \text{dist}}{R}\right) \times \text{repulsionForce}$$
    $$\Delta v_x = \text{force} \times \frac{dx}{\text{dist}}, \quad \Delta v_z = \text{force} \times \frac{dz}{\text{dist}}, \quad v_y = \max(v_y, 0.08D)$$

### 3. Burst Mechanic
Every 200 ticks (10s), detonations deal magic damage:

$$\text{Burst Damage} = \frac{\text{accumulatedDamage}}{3.0}$$

### 4. Ping-Pong Skill
*   **Fase 1 (Ping):** Target velocity set to $\vec{look}_{\text{player}} \times 0.95D$ and $v_y = 0.22D$ for 8 ticks.
*   **Fase 2 (Pong):** Target velocity set to:
    
    $$\vec{pull} = \text{Normalize}(\vec{pos}_{\text{player}} - \vec{pos}_{\text{mob}})$$
    $$v_x = \text{pull}_x \times 1.10D, \quad v_z = \text{pull}_z \times 1.10D, \quad v_y = \text{pull}_y \times 0.50D + 0.1D \quad \text{for 12 ticks.}$$

---

## ⚔️ Frontal Sweep Check (AABB Collision)

Triggers front-swept damage calculations:
1.  **AABB Center ($\vec{C}$):** Positioned $1.5D$ blocks in front of the player's eye coordinates:

$$\vec{C} = \vec{pos}_{\text{player}} + (\vec{look} \times 1.5)$$

2.  **AABB Volume:** Grows based on the configured weapon's `aoeAttackSize` ($S$):

$$\text{AABB} = [C_x - S, \; C_y - S, \; C_z - S] \times [C_x + S, \; C_y + S, \; C_z + S]$$

3.  **Anti-Spam Cooldown:** Server discards incoming `PacketSpikeHammerAoE` requests if the difference between current time and `LastSpikeHammerAoETick` is less than **5 ticks (0.25s)**.
