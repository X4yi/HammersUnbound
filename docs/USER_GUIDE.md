# Guia de usuario

Esta guia explica como se juega Hammers Unbound sin meterse demasiado en el codigo. La idea corta: son martillos pesados, lentos y con efectos fuertes. No son espadas con otro modelo.

## Que agrega el mod

Hammers Unbound agrega dos tipos de armas:

- **WarHammer**: pensado para control de grupos. Golpea fuerte, aturde y puede hacer dano en area.
- **SpikeHammer**: pensado para desgaste. Aplica sangrado y permite enlazar un objetivo con Blood Pact.

Cada familia tiene cinco materiales:

- Wood
- Stone
- Iron
- Gold
- Diamond

Los martillos aparecen en la creative tab `Hammers Unbound`.

## WarHammer

El WarHammer es el martillo pesado clasico: lento, fuerte y con impacto grande.

Cuando haces un golpe critico con un WarHammer:

1. El objetivo principal recibe stun si la opcion esta activada.
2. Las entidades cercanas reciben dano AOE si la opcion esta activada.
3. El AOE tambien puede aplicar stun secundario.
4. Se generan particulas y sonidos de impacto alrededor del objetivo.
5. El arma entra en cooldown si ese material tiene cooldown configurado.

El WarHammer se siente mejor cuando esperas el momento correcto para pegar, no cuando spameas clicks.

## SpikeHammer

El SpikeHammer es mas sucio, mas de pelea larga. No intenta controlar una zona entera como el WarHammer; intenta convertir un objetivo en una mala decision con patas.

Tiene dos mecanicas importantes:

- **Bleeding**: sangrado acumulable aplicado en golpes criticos y ataques en sprint cargados.
- **Blood Pact**: enlace por click derecho contra un objetivo al frente.

## Bleeding

Bleeding es un efecto acumulable. Cada aplicacion sube el nivel hasta el maximo del material, reinicia la duracion y hace dano periodico segun el nivel.

A niveles mas altos, el efecto pega mas seguido y se vuelve bastante peligroso. Las particulas de sangre aparecen cuando ocurre el dano, no al azar.

## Blood Pact

Blood Pact se activa con click derecho mientras tienes un SpikeHammer en mano.

Si hay una entidad viva frente a ti dentro del rango configurado:

1. El jugador queda enlazado al objetivo.
2. El objetivo recibe dano periodico.
3. El jugador se cura una parte del dano.
4. Si golpeas mientras el pacto esta activo, tambien puedes curarte segun el dano del martillo.
5. Si el objetivo muere, el jugador muere o la distancia supera el limite, el pacto se rompe.

## Stun

Stun es un efecto de control aplicado principalmente por WarHammer.

Mientras una entidad esta stunned:

- Su movimiento horizontal se bloquea.
- Su rotacion queda congelada.
- Si es un mob, se limpia su pathfinding y objetivo de ataque.
- Si es un jugador, el cliente bloquea el movimiento de camara mientras dura el efecto.

En servidores PvP o packs con bosses, conviene ajustar duraciones.

## Interfaz del mod

El mod incluye varias pantallas propias:

- **Config GUI**: permite cambiar stats, toggles y opciones visuales.
- **Changelog GUI**: muestra cambios locales y remotos cuando estan disponibles.
- **Main Menu Changelog button**: acceso rapido al changelog desde el menu principal.
- **Development warning popup**: aviso de estado de desarrollo. `Ok` lo oculta permanentemente; `Salir` solo lo cierra durante la sesion actual.

## Tecla de configuracion

El mod registra una tecla para abrir la GUI:

```text
Open Config GUI
```

Puedes cambiarla desde el menu de controles de Minecraft, dentro de la categoria `Hammers Unbound`.

## Tooltips

Los martillos muestran informacion directa:

- Damage
- Speed
- Durability actual/maxima

La velocidad se muestra como valor positivo, por ejemplo `0.8` o `1.2`, para que sea entendible. Internamente Minecraft sigue usando su atributo de attack speed, pero el usuario no tiene que lidiar con valores negativos raros.

## Configuracion rapida

Los archivos se generan en:

```text
config/hammersunbound/
```

Archivos principales:

- `items.json`: stats y habilidades por material.
- `server.json`: toggles y multiplicadores de gameplay.
- `client.json`: particulas, visuales y UI.

Los tiempos nuevos se configuran en segundos. Por ejemplo:

```json
"skillCooldownSeconds": 2.0
```

El mod conserva compatibilidad con configs viejas que usaban ticks.

## Consejos de balance

- Si el WarHammer se siente exagerado, baja `aoeDamage`, `aoeRadius` o los tiempos de stun.
- Si el SpikeHammer mata demasiado rapido, baja `damagePerLevel` o sube `tickIntervalSeconds`.
- Si hay demasiadas particulas, baja los multiplicadores de `aoeParticles` en `client.json`.
- Para servidores PvP, considera desactivar stun o reducirlo mucho.
