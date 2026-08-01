# Hammers Unbound — Guía de Usuario

**Versión:** r1.0b5 | Minecraft Forge 1.12.2

Hammers Unbound añade dos tipos de armas pesadas a Minecraft, cada una con su propia filosofía de combate, efectos de estado, estación de crafteo y parámetros configurables.

---

## Primeros Pasos

### La Hammer Forge

Ni el WarHammer ni el SpikeHammer pueden fabricarse en una Mesa de Crafteo estándar. Ambos requieren la **Hammer Forge**, una estación de crafteo exclusiva de este mod.

**Receta:**
- 1 Bloque de Hierro
- 2 Lingotes de Hierro
- 1 Mesa de Crafteo (centro)

Al interactuar con la Hammer Forge se abre su interfaz. Desde allí:
- Las **pestañas exteriores** permiten cambiar entre WarHammer y SpikeHammer.
- El **selector superior** permite elegir el material deseado (Madera, Piedra, Hierro, Oro, Diamante).
- La lista de ingredientes se actualiza dinámicamente. Hacer clic en **Forjar** cuando todos los materiales estén disponibles.
- La interfaz preselecciona automáticamente el mejor material encontrado en el inventario.

**Integración con JEI:** Todas las recetas de la Hammer Forge son visibles en Just Enough Items bajo la categoría Hammer Forge.

---

## WarHammer

Arma de control de masas diseñada para inmovilizar grupos de enemigos simultáneamente.

### Golpe Crítico — Aturdimiento + Ground Slam

Asestar un **golpe crítico** (atacar mientras se cae) activa dos efectos:

**1. Aturdimiento (Stun)**
El objetivo primario recibe el efecto `hammersunbound:stun`:
- La velocidad de movimiento se reduce a cero.
- El movimiento horizontal y el salto quedan completamente bloqueados (la gravedad sigue aplicando).
- La ruta de navegación y el objetivo de ataque de los mobs se eliminan.
- Los jugadores bajo el efecto de aturdimiento no pueden mover la cámara.
- La rotación del objetivo queda bloqueada en el servidor para evitar desincronización visual.

**2. Ground Slam (Área de Efecto)**
Una onda de choque se expande desde el objetivo primario:
- Todas las entidades dentro del radio configurado reciben daño.
- Los objetivos secundarios también reciben un efecto de Aturdimiento de menor duración.
- Partículas de bloques rotos aparecen en función del bloque bajo el punto de impacto.
- El radio, el daño y la duración del aturdimiento escalan con la distancia de caída si el jugador estaba en el aire.

### Skybreaker (Habilidad Activa)

El **Skybreaker** es la habilidad activa del WarHammer. Lanza al jugador hacia arriba y permite una caída potenciada.

**Activación:** Mantener **Shift** + **Clic Derecho** mirando **hacia arriba** (cámara inclinada más de 45° hacia arriba).

**Efecto:**
- El jugador es lanzado aproximadamente 15 bloques hacia arriba con impulso direccional desde el vector de visión.
- El daño por caída de este lanzamiento se **cancela automáticamente**.
- Mientras la inmunidad está activa en el aire, el jugador es inmune al empuje (knockback).

**Aterrizaje — Ground Slam:**
Si el jugador mantiene **Shift** al aterrizar tras un salto Skybreaker:
- Se activa un Ground Slam centrado en el **jugador** (no en un objetivo).
- El radio del slam, el daño y el aturdimiento escalan con la distancia caída.

**Enfriamiento (Cooldown):** Se rastrea por material en el NBT del ítem. Se muestra en el HUD como la **barra "S"**.

---

## SpikeHammer

Arma de daño sostenido que acumula pilas de sangrado y permite la manipulación de sangre a través del sistema BloodPact.

### Sangrado (Bleeding)

**Aplicación:**
- Atacar mientras se **corre** con un swing completamente cargado aplica Nivel 1 de Sangrado.
- Los **golpes críticos** siempre aplican Sangrado independientemente del estado de sprint.
- Las aplicaciones sucesivas incrementan el nivel de pila, hasta el máximo configurado.

**Comportamiento:**
- Inflige daño periódico de tipo **mágico** (ignora la armadura).
- Los niveles de pila más altos hacen ticks **más rápido** pero decaen más rápidamente.
- Cada nivel tiene su propia duración e intervalo de tick, calculados desde los valores de configuración base.

**Visual:** Partículas de sangre aparecen en el objetivo con cada tick de daño. El nivel de sangrado se sincroniza con los jugadores cercanos.

### BloodPact (Habilidad Activa)

El BloodPact es la habilidad principal del SpikeHammer. Crea un vínculo sobrenatural entre el portador y los enemigos cercanos.

**Activación:** **Clic Derecho** apuntando a un enemigo dentro del rango.

**Qué ocurre al activarlo:**
- El enemigo objetivo y hasta 2 enemigos cercanos adicionales quedan **vinculados**.
- Un temporizador comienza a contar hacia atrás. Golpear objetivos vinculados lo extiende; recibir golpes de ellos lo reduce.

**Efectos pasivos mientras está activo:**

| Efecto | Detalles |
|---|---|
| Robo de vida | Un porcentaje del daño infligido a objetivos vinculados se convierte en curación |
| Alcance | El alcance de ataque aumenta permanentemente en +1.0 mientras está activo |
| Campo de Repulsión | Las entidades no vinculadas dentro del radio del campo son empujadas continuamente |
| Atracción | Las entidades vinculadas son atraídas hacia el jugador en cada tick |
| Curación Pasiva | +1 HP regenerado cada 2 segundos |
| Viñeta de Sangre | Una viñeta roja aparece en pantalla para indicar el estado activo |

### Medidor de Locura (Madness)

La barra de **Locura** (0–100) se carga cada vez que se golpea un objetivo vinculado:
- **+10** por golpe en un objetivo vinculado.
- Decae **-5 cada 2 segundos** cuando no se está atacando.

La Locura escala dos potenciadores de atributos de forma proporcional:
- Hasta **+20% de Velocidad de Movimiento** a 100 de Locura.
- Hasta **+50% de Velocidad de Ataque** a 100 de Locura.

El valor actual de Locura se muestra en el HUD mientras el BloodPact está activo.

### Blood Burst

El daño infligido a objetivos vinculados se acumula internamente. Cada **10 segundos** se produce una detonación:
- Cada objetivo vinculado recibe `daño_acumulado / 3` como daño mágico.
- El jugador se cura por una parte del daño total del burst.
- Un efecto de partículas y sonido de explosión ocurren en cada objetivo afectado.

El contador del burst y el daño acumulado se muestran en el HUD.

### Barrido AoE

Mientras el BloodPact está activo, el **Clic Izquierdo** ejecuta un ataque de barrido frontal:
- Utiliza una caja de colisión AABB centrada 1.5 bloques frente a los ojos del jugador.
- Todas las entidades dentro de la caja (excepto el objetivo principal) reciben daño del arma.
- Tiene un enfriamiento anti-spam interno de **0.25 segundos**.

### Habilidad Ping-Pong

Mientras el BloodPact está activo y ya hay un enemigo vinculado, el **Clic Derecho** hacia un objetivo vinculado inicia la secuencia **Ping-Pong**:

**Fase 1 — Ping:**
El objetivo es lanzado en la dirección de visión del jugador.
- Si el objetivo colisiona con un bloque durante el vuelo, recibe daño de impacto y entra inmediatamente en la Fase 2.
- En caso contrario, la Fase 2 comienza al expirar la duración del lanzamiento.

**Fase 2 — Pong:**
El objetivo es atraído rápidamente de vuelta hacia el jugador.
- Una alerta de "**¡GOLPEA!**" pulsa en pantalla.
- Golpear al objetivo mientras regresa durante la Fase 2 inflige **+20% de daño adicional** y reinicia inmediatamente el ciclo Ping-Pong.
- Si el objetivo alcanza al jugador sin ser golpeado, la secuencia termina.
- Si el jugador recibe un golpe del objetivo que regresa, la secuencia se cancela y el temporizador del BloodPact es penalizado.

---

## HUD

Al sostener un WarHammer o SpikeHammer, aparece una pantalla de cooldown en pantalla.

### Indicadores de Cooldown

Cada indicador es un panel de ícono de 24×24 que muestra el arma sostenida con una superposición de relleno:

| Indicador | Color | Significado |
|---|---|---|
| (sin etiqueta) | Oscuro | Cooldown de ataque vanilla (Minecraft estándar) |
| **P** | Rojo | Cooldown del Ground Slam / habilidad crítica |
| **S** | Azul | Cooldown del salto Skybreaker |

Un borde dorado reemplaza el borde oscuro cuando el cooldown está completamente cargado.

### HUD del BloodPact (solo SpikeHammer)

Mientras el BloodPact está activo, información adicional aparece cerca del panel de cooldown:
- **Locura** — nivel actual de Locura (0–100)
- **Burst en** — segundos restantes hasta el próximo Blood Burst
- **Daño Acum** — daño total acumulado para el próximo burst

### Posición del HUD

La posición del HUD es configurable en `config/hammersunbound/client.json`:

| Valor | Posición |
|---|---|
| `0` | Parte inferior derecha |
| `1` | Parte inferior central (sobre la barra de acceso rápido) |
| `2` | Parte inferior izquierda |
| `3` | Parte superior derecha |
| `4` | Parte superior izquierda |
| `5` | Oculto |

---

## Materiales

Ambos tipos de armas están disponibles en cinco materiales, cada uno con estadísticas base diferentes:

| Material | Nivel | Notas |
|---|---|---|
| Madera | 1 | Estadísticas más bajas, más fácil de obtener |
| Piedra | 2 | — |
| Hierro | 3 | Nivel estándar |
| Oro | 4 | Alta velocidad, baja durabilidad |
| Diamante | 5 | Estadísticas más altas |

Todas las estadísticas (daño, velocidad de ataque, durabilidad, cooldowns de habilidades, valores de sangrado, parámetros del BloodPact) son completamente configurables por material en `config/hammersunbound/items.json`.

---

## Configuración

Los archivos de configuración se encuentran en `config/hammersunbound/`. La pantalla de configuración en el juego es accesible desde la lista de mods o mediante el botón **Config** en la pantalla de Changelog.

### items.json
Estadísticas de armas y valores de habilidades por material. Ver la [Referencia de Configuración](CONFIGURATION.md) para la lista completa de parámetros.

### server.json
Toggles globales del servidor y multiplicadores:
- Habilitar/deshabilitar Aturdimiento, AoE, Sangrado y BloodPact de forma independiente.
- Multiplicadores globales para daño, duración, rango y robo de vida.

### client.json
Ajustes visuales del cliente:
- Toggles de partículas y multiplicadores de densidad para los visuales de AoE, sangrado y BloodPact.
- Posición del overlay del HUD.
- Selección de idioma de la interfaz (`en` / `es`).
- Toggle del popup de aviso de desarrollador.

---

## Changelog y Actualizaciones

La pantalla de **Changelog** es accesible desde el menú principal o el menú de pausa (si está habilitado en la config). Muestra las notas de versión obtenidas de GitHub, con una barra lateral que lista todas las versiones disponibles. El idioma puede cambiarse entre inglés y español directamente desde la barra lateral.

Cuando hay una actualización disponible, aparece un badge en el pie de página. Hacer clic en él abre la página de descarga del mod.
