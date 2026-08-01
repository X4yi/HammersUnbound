[EN]
# WarHammer

A crowd-control weapon designed to immobilize groups of enemies simultaneously.

## Critical Hit — Stun + Ground Slam

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

## Skybreaker (Active Skill)

The **Skybreaker** is the WarHammer's active ability. It launches the player upward, then allows a powered landing.

**Activation:** Hold **Shift** + **Right-Click** while looking **upward** (camera pitched more than 45 degrees up).

**Effect:**
- The player is launched approximately 15 blocks upward with directional momentum from the look vector.
- Fall damage from this launch is **automatically cancelled**.
- While airborne with the immunity active, the player is immune to knockback.

**Landing — Ground Slam:**
If the player is holding **Shift** when landing after a Skybreaker leap:
- A Ground Slam is triggered centered on the **player** (not a target).
- The slam radius, damage, and stun scale with the distance fallen.

**Cooldown:** Tracked per-material in the item's NBT. Displayed in the HUD as the **"S" bar**.
[/EN]
[ES]
# WarHammer

Arma de control de masas disenada para inmovilizar grupos de enemigos simultaneamente.

## Golpe Critico — Aturdimiento + Ground Slam

Asestar un **golpe critico** (atacar mientras se cae) activa dos efectos:

**1. Aturdimiento (Stun)**
El objetivo primario recibe el efecto `hammersunbound:stun`:
- La velocidad de movimiento se reduce a cero.
- El movimiento horizontal y el salto quedan completamente bloqueados (la gravedad sigue aplicando).
- La ruta de navegacion y el objetivo de ataque de los mobs se eliminan.
- Los jugadores bajo el efecto de aturdimiento no pueden mover la camara.
- La rotacion del objetivo queda bloqueada en el servidor para evitar desincronizacion visual.

**2. Ground Slam (Area de Efecto)**
Una onda de choque se expande desde el objetivo primario:
- Todas las entidades dentro del radio configurado reciben dano.
- Los objetivos secundarios tambien reciben un efecto de Aturdimiento de menor duracion.
- Particulas de bloques rotos aparecen en funcion del bloque bajo el punto de impacto.
- El radio, el dano y la duracion del aturdimiento escalan con la distancia de caida si el jugador estaba en el aire.

## Skybreaker (Habilidad Activa)

El **Skybreaker** es la habilidad activa del WarHammer. Lanza al jugador hacia arriba y permite una caida potenciada.

**Activacion:** Mantener **Shift** + **Clic Derecho** mirando **hacia arriba** (camara inclinada mas de 45 grados hacia arriba).

**Efecto:**
- El jugador es lanzado aproximadamente 15 bloques hacia arriba con impulso direccional desde el vector de vision.
- El dano por caida de este lanzamiento se **cancela automaticamente**.
- Mientras la inmunidad esta activa en el aire, el jugador es inmune al empuje (knockback).

**Aterrizaje — Ground Slam:**
Si el jugador mantiene **Shift** al aterrizar tras un salto Skybreaker:
- Se activa un Ground Slam centrado en el **jugador** (no en un objetivo).
- El radio del slam, el dano y el aturdimiento escalan con la distancia caida.

**Enfriamiento (Cooldown):** Se rastrea por material en el NBT del item. Se muestra en el HUD como la **barra "S"**.
[/ES]
