# Mecanicas

Este documento describe como funcionan las mecanicas reales del mod. No es una promesa de diseno futuro: es lo que el proyecto hace ahora mismo
## Flujo general de combate

Los martillos heredan de una base comun (`ItemHammer`) que controla:

- Dano de ataque.
- Velocidad de ataque.
- Durabilidad.
- Tooltip.
- Busqueda de entidades cercanas para efectos AOE.

Cada familia implementa su propia logica:

- `WarHammerItem`: stun, AOE, sonidos y particulas.
- `SpikeHammerItem`: bleeding y Blood Pact.

## Criticos y ataques cargados

El mod no aplica todo en cualquier click.

Para WarHammer:

- Las habilidades fuertes se disparan en golpe critico.
- Si el martillo tiene cooldown activo, no vuelve a disparar habilidad.
- El cooldown se aplica por item usando el cooldown tracker del jugador.

Para SpikeHammer:

- Bleeding se aplica en criticos.
- Tambien puede aplicarse en ataques en sprint si el ataque esta cargado.

La intencion es que el jugador espere el golpe, no que gane spameando.

## WarHammer

Cuando el WarHammer activa su habilidad:

1. Lee el material actual desde config.
2. Si `warhammerEnableStun` esta activo, aplica stun al objetivo principal.
3. Si `warhammerEnableAOE` esta activo, busca entidades vivas en el radio configurado.
4. Aplica dano AOE a esas entidades.
5. Si el stun AOE tiene duracion mayor a 0, lo aplica tambien a los objetivos secundarios.
6. Genera sonidos y manda paquete de particulas AOE a jugadores cercanos.

### AOE

El AOE usa una caja alrededor del objetivo principal. Se excluyen:

- El objetivo principal.
- El atacante.
- Jugadores en creativo.
- Mascotas tameadas cuyo dueno sea el atacante.

Esto evita parte del friendly fire accidental, especialmente con mascotas vanilla.

## Stun

En entidades:

- Cancela movimiento horizontal.
- Bloquea rotacion.
- Si la entidad es un mob, limpia navegacion y target.
- Evita que el mob siga persiguiendo mientras dura el efecto.

En cliente:

- Si el jugador local esta stunned, se bloquea el movimiento de camara.
- Tambien se reemplaza el mouse helper para evitar que la camara "escape" del stun.

## SpikeHammer

SpikeHammer tiene dos sistemas: bleeding y Blood Pact.

### Bleeding

Bleeding vive en cada entidad afectada.

Cada entidad viva puede tener:

- Nivel actual.
- Ticks hasta bajar de nivel.
- Ticks hasta el siguiente dano.
- Config usada por el efecto activo.

Cuando se aplica bleeding:

1. Sube el nivel si todavia no llego al maximo.
2. Guarda la config del material que lo aplico.
3. Reinicia duracion y timer de dano.
4. Sincroniza el nivel con clientes que trackean la entidad.

### Escalado de bleeding

El daño por nivel usa esta idea:

```text
nivel 1 = 50% del damagePerLevel
nivel 2 = 75%
nivel 3 = 100%
nivel 4 = 125%
niveles altos = sigue escalando
```

La duracion baja con niveles mas altos. El intervalo de dano tambien baja, asi que niveles altos pegan mas seguido.

## Blood Pact

en desarrollo...

## Configuracion servidor-cliente

El servidor es autoritativo para gameplay:

- Daño.
- Duraciones.
- Toggles de WarHammer/SpikeHammer.
- Multiplicadores.

El cliente controla visuales:

- Particulas AOE.
- Visual de Blood Pact.
- Particulas de bleeding.
- Posicion de UI.
- Idioma de changelog/UI donde aplica.

## Compatibilidad con configs viejas

El mod acepta algunos nombres antiguos