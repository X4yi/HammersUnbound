# User Guide: Hammers Unbound

Hammers Unbound introduces heavy hammer weapons with custom combat physics, status effects, and network synchronization to Minecraft 1.12.2.

---

## 🔨 WarHammer

The **WarHammer** is a crowd-control weapon designed to disable groups of targets.

### Mechanics

#### 1. Stun Effect
Critical hits apply the **Stun** effect (registry: `hammersunbound:stun`):
*   **Immobilization:** Target movement speed is reduced by 100%. Gravity applies, but horizontal movement and jumping are disabled.
*   **AI Disable:** Mobs clear their active navigation paths and target states.
*   **Camera Lock:** Players under the Stun effect cannot rotate their camera (`StunMouseHelper` intercepts raw input).
*   **Angle Locking:** Target yaw and pitch are locked on the server and enforced on the client to prevent orientation desynchronization.

#### 2. Ground Slam (AOE)
Critical hits trigger an area-of-effect impact:
*   **Damage & Stun:** Deals damage and applies Stun to secondary targets in a configured radius.
*   **Visuals:** Block-breaking particles correspond to the texture of the block below the target.

---

## 🗡️ SpikeHammer

The **SpikeHammer** focuses on damage over time, bleed stacks, and blood manipulation.

### Mechanics

#### 1. Bleeding Effect
*   **Application:** Attacks while sprinting or fully charged apply Level 1 Bleeding. Critical hits apply additional levels.
*   **Damage:** Periodic magic damage that bypasses armor. Higher stacks tick faster but decay rapidly.

#### 2. BloodPact (Enchantment/Skill)
Right-clicking a target activates the **BloodPact**:
*   **Links:** Links the player with up to 3 nearby entities.
*   **Lifesteal:** Converts a percentage of damage dealt to linked targets into health.
*   **Repulsion Field:** Continuously pushes away non-linked entities within a specific radius. Vertical lifting is applied to break ground friction.
*   **Attraction:** Linked targets are pulled towards the player and forced to navigate to their coordinates.
*   **Reach Buff:** Grants +1.0 Attack Reach while active.
*   **AoE Sweeps:** Left-clicking performs a frontal 3D sweep damage check (AABB collision box) dealing weapon damage to all front enemies (0.25s internal cooldown).

#### 3. Madness Meter
Attacking linked targets charges the **Madness** bar (0-100):
*   **Charge:** +10 per attack. Decays by -5 per second (20 ticks).
*   **Buffs:** Otorga up to +20% movement speed and +50% attack speed proportionally.

#### 4. Blood Burst
Accumulates damage dealt to linked targets. Every 10 seconds, detonations deal **1/3 of the total damage** to all linked targets.

#### 5. Ping-Pong Skill
Right-clicking towards a linked target throws them:
1.  **Ping:** Target is launched 4 blocks back in the player's look direction.
2.  **Pong:** Target is pulled back rapidly to the player.
3.  **Return Hit:** Hitting the target during return deals **+50% damage** and restarts the Ping-Pong cycle.
4.  **Penalties:** Missing targets applies a 10s cooldown. Being hit by the returning target cancels the cycle and applies a pact duration penalty.
