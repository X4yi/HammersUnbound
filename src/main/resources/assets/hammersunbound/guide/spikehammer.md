[EN]
# SpikeHammer

A sustained damage weapon that builds up bleeding stacks and enables blood manipulation through the BloodPact system.

## Bleeding

**Application:**
- Attacking while **sprinting** with a fully charged swing applies Level 1 Bleeding.
- **Critical hits** always apply Bleeding regardless of sprint state.
- Successive applications increase the stack level, up to the configured maximum.

**Behavior:**
- Deals periodic **magic damage** (ignores armor).
- Higher stack levels tick **faster** but decay more rapidly.
- Each level has its own duration and tick interval, calculated from base config values.

**Visual:** Blood particle effects appear on the target with each damage tick. The bleeding level is synced to nearby players.

## BloodPact (Active Skill)

The BloodPact is the SpikeHammer's primary skill. It creates a supernatural link between the wielder and nearby enemies.

**Activation:** **Right-Click** while aiming at an enemy within range.

**What happens on activation:**
- The targeted enemy and up to 2 additional nearby enemies are **linked**.
- A timer begins counting down. Hitting linked targets extends it; being hit by them reduces it.

**Passive effects while active:**
- **Lifesteal:** A percentage of damage dealt to linked targets is converted to healing.
- **Reach:** Attack reach is permanently increased by +1.0 while active.
- **Repulsion Field:** Non-linked entities within the field radius are continuously pushed away.
- **Attraction:** Linked entities are pulled toward the player each tick.
- **Passive Heal:** +1 HP regenerated every 2 seconds.

## Madness Meter

The **Madness** bar (0-100) charges each time a linked target is hit:
- **+10** per hit on a linked target.
- Decays by **-5 every 2 seconds** when not attacking.

Madness scales two attribute buffs proportionally:
- Up to **+20% Movement Speed** at 100 Madness.
- Up to **+50% Attack Speed** at 100 Madness.

The current Madness value is displayed in the HUD while BloodPact is active.

## Blood Burst

Damage dealt to linked targets is accumulated internally. Every **10 seconds**, a detonation occurs:
- Each linked target takes `accumulatedDamage / 3` as magic damage.
- The player heals for a portion of the total burst damage dealt.
- A particle effect and explosion sound play on each affected target.

## AoE Sweep

While BloodPact is active, **Left-Click** performs a frontal sweep attack:
- Uses an AABB collision box centered 1.5 blocks in front of the player's eyes.
- All entities inside the box (except the primary target) take weapon damage.
- Has an internal anti-spam cooldown of **0.25 seconds**.

## Ping-Pong Skill

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
[/EN]
[ES]
# SpikeHammer

Arma de dano sostenido que acumula pilas de sangrado y permite la manipulacion de sangre a traves del sistema BloodPact.

## Sangrado (Bleeding)

**Aplicacion:**
- Atacar mientras se **corre** con un swing completamente cargado aplica Nivel 1 de Sangrado.
- Los **golpes criticos** siempre aplican Sangrado independientemente del estado de sprint.
- Las aplicaciones sucesivas incrementan el nivel de pila, hasta el maximo configurado.

**Comportamiento:**
- Inflige dano periodico de tipo **magico** (ignora la armadura).
- Los niveles de pila mas altos hacen ticks **mas rapido** pero decaen mas rapidamente.
- Cada nivel tiene su propia duracion e intervalo de tick, calculados desde los valores de configuracion base.

**Visual:** Particulas de sangre aparecen en el objetivo con cada tick de dano. El nivel de sangrado se sincroniza con los jugadores cercanos.

## BloodPact (Habilidad Activa)

El BloodPact es la habilidad principal del SpikeHammer. Crea un vinculo sobrenatural entre el portador y los enemigos cercanos.

**Activacion:** **Clic Derecho** apuntando a un enemigo dentro del rango.

**Que ocurre al activarlo:**
- El enemigo objetivo y hasta 2 enemigos cercanos adicionales quedan **vinculados**.
- Un temporizador comienza a contar hacia atras. Golpear objetivos vinculados lo extiende; recibir golpes de ellos lo reduce.

**Efectos pasivos mientras esta activo:**
- **Robo de vida:** Un porcentaje del dano infligido a objetivos vinculados se convierte en curacion.
- **Alcance:** El alcance de ataque aumenta permanentemente en +1.0 mientras esta activo.
- **Campo de Repulsion:** Las entidades no vinculadas dentro del radio del campo son empujadas continuamente.
- **Atraccion:** Las entidades vinculadas son atraidas hacia el jugador en cada tick.
- **Curacion Pasiva:** +1 HP regenerado cada 2 segundos.

## Medidor de Locura (Madness)

La barra de **Locura** (0-100) se carga cada vez que se golpea un objetivo vinculado:
- **+10** por golpe en un objetivo vinculado.
- Decae **-5 cada 2 segundos** cuando no se esta atacando.

La Locura escala dos potenciadores de atributos de forma proporcional:
- Hasta **+20% de Velocidad de Movimiento** a 100 de Locura.
- Hasta **+50% de Velocidad de Ataque** a 100 de Locura.

El valor actual de Locura se muestra en el HUD mientras el BloodPact esta activo.

## Blood Burst

El dano infligido a objetivos vinculados se acumula internamente. Cada **10 segundos** se produce una detonacion:
- Cada objetivo vinculado recibe `dano_acumulado / 3` como dano magico.
- El jugador se cura por una parte del dano total del burst.
- Un efecto de particulas y sonido de explosion ocurren en cada objetivo afectado.

## Barrido AoE

Mientras el BloodPact esta activo, el **Clic Izquierdo** ejecuta un ataque de barrido frontal:
- Utiliza una caja de colision AABB centrada 1.5 bloques frente a los ojos del jugador.
- Todas las entidades dentro de la caja (excepto el objetivo principal) reciben dano del arma.
- Tiene un enfriamiento anti-spam interno de **0.25 segundos**.

## Habilidad Ping-Pong

Mientras el BloodPact esta activo y ya hay un enemigo vinculado, el **Clic Derecho** hacia un objetivo vinculado inicia la secuencia **Ping-Pong**:

**Fase 1 — Ping:**
El objetivo es lanzado en la direccion de vision del jugador.
- Si el objetivo colisiona con un bloque durante el vuelo, recibe dano de impacto y entra inmediatamente en la Fase 2.
- En caso contrario, la Fase 2 comienza al expirar la duracion del lanzamiento.

**Fase 2 — Pong:**
El objetivo es atraido rapidamente de vuelta hacia el jugador.
- Una alerta de "**¡GOLPEA!**" pulsa en pantalla.
- Golpear al objetivo mientras regresa durante la Fase 2 inflige **+20% de dano adicional** y reinicia inmediatamente el ciclo Ping-Pong.
- Si el objetivo alcanza al jugador sin ser golpeado, la secuencia termina.
- Si el jugador recibe un golpe del objetivo que regresa, la secuencia se cancela y el temporizador del BloodPact es penalizado.
[/ES]
