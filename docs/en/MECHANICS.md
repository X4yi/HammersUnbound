# Technical Specifications & Formulas

Comprehensive formulas, attribute modifiers, physics calculations, and packet specifications for **Hammers Unbound**.

---

##  Bleeding Mechanics

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

##  Stun Mechanics

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

##  Skybreaker Mechanics (WarHammer)

Triggered via `PacketSkybreaker` (client → server). Processed by `WarHammerItem.performSkybreaker()`.

### 1. Leap Physics

$$motionY = 2.0, \quad motionX = look_x \times 1.5, \quad motionZ = look_z \times 1.5$$

The flag `SkybreakerImmunity` is set on the entity's data, which:
*   **Cancels `LivingFallEvent`** to prevent fall damage.
*   **Cancels `LivingKnockBackEvent`** to prevent mid-air knockback.

### 2. Ground Slam on Landing
Triggered by `LivingFallEvent` when `SkybreakerImmunity` is active and the player is sneaking:

$$slamRadius = baseRadius \times 0.5 + fallDistance \times 0.25$$
$$slamDamage = baseDamage \times 0.5 + fallDistance \times 0.5$$
$$slamStun = (int)(baseStunDuration \times 0.5 + fallDistance \times 5.0)$$

### 3. Critical Hit Bonus
If the player was falling (`fallDistance > 0`) at the time of a critical hit:

$$extraRadius = fallDistance \times 0.5$$
$$extraDamage = fallDistance \times 1.0$$
$$extraStun = (int)(fallDistance \times 10)$$

---

##  BloodPact Mechanics

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

#### Phase 1 — Ping (timer: 8 ticks)
*   **Velocity:** $mob.motion = direction \times 1.5D$ (where $direction$ is the player's normalized look vector at cast time)
*   **Block Collision:** A raycast is performed each tick from the mob's center to its projected position. On a `BLOCK` hit:
    *   $impactDamage = attackDamage \times 0.8$
    *   Immediately transitions to Phase 2.

#### Phase 2 — Pong (timer: 20 ticks)
*   **Pull vector:** $\vec{pull} = \text{Normalize}(\vec{pos}_{\text{player\_eye}} - \vec{pos}_{\text{mob\_eye}})$
*   **Velocity:** $mob.motion = \vec{pull} \times 1.8D$
*   **Arrival condition:** If $dist(mob, player) < 1.5D$:
    *   $finalDamage = attackDamage \times 1.2$
    *   `cancelPingPong()`
*   **Hit during Pong (`LivingHurtEvent`):** Damage multiplied by $\times 1.5$; `startPingPong()` is called again.

---

##  Frontal Sweep Check (AABB Collision)

Triggers front-swept damage calculations:
1.  **AABB Center ($\vec{C}$):** Positioned $1.5D$ blocks in front of the player's eye coordinates:

$$\vec{C} = \vec{pos}_{\text{player}} + (\vec{look} \times 1.5)$$

2.  **AABB Volume:** Grows based on the configured weapon's `aoeAttackSize` ($S$):

$$\text{AABB} = [C_x - S, \; C_y - S, \; C_z - S] \times [C_x + S, \; C_y + S, \; C_z + S]$$

3.  **Anti-Spam Cooldown:** Server discards incoming `PacketSpikeHammerAoE` requests if the difference between current time and `LastSpikeHammerAoETick` is less than **5 ticks (0.25s)**.
