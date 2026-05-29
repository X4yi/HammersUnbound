# Guia para modpacks

Hammers Unbound esta bastante bien preparado para packs porque no encierra todo el balance en codigo. Aun asi, conviene tocarlo con criterio: stun, AOE y robo de vida pueden romper progresion si se dejan demasiado altos.

## Archivos que deberias revisar

```text
config/hammersunbound/items.json
config/hammersunbound/server.json
config/hammersunbound/client.json
```

Para packs, normalmente el orden sano es:

1. Ajustar `server.json` para reglas globales.
2. Ajustar `items.json` para progresion por material.
3. Ajustar recetas si los martillos entran demasiado pronto.
4. Probar bosses, dungeons y PvP si aplica.

## Recomendaciones rapidas

### Vanilla+

- Deja `attackSpeed` bajo: `0.8` para martillos pesados.
- Baja un poco `aoeDamage` si los jugadores consiguen Iron/Diamond temprano.
- Mantiene Blood Pact activo, pero no subas mucho `drainPercent`.

### Expert pack

- Sube costos de receta.
- Reduce `aoeRadius` temprano.
- Haz que Diamond sea realmente un salto de tier.
- Considera desactivar Gold si no quieres armas rapidas con efectos fuertes.

### PvP

Stun en PvP puede ser muy molesto. Recomendacion:

```json
"warhammer": {
  "stunDurationMultiplier": 0.25,
  "enableAOE": true,
  "enableStun": true
}
```

Si el servidor es muy competitivo, directamente desactiva stun.

### Bosses y mobs fuertes

El mod no trae una blacklist de bosses todavia. Si tu pack tiene bosses delicados:

- Baja `stunDurationSeconds`.
- Baja `aoeStunDurationSeconds`.
- Baja `bleedingDamageMultiplier`.
- Evita `maxLevels` muy altos en SpikeHammer.

## Recetas

Todas las recetas usan ore dictionary:

- `plankWood`
- `cobblestone`
- `ingotIron`
- `ingotGold`
- `gemDiamond`
- `stickWood`

Esto es bueno para compatibilidad, pero el patron actual es igual para WarHammer y SpikeHammer.

Si quieres mejor identidad:

- WarHammer: usa bloques, plates o componentes pesados.
- SpikeHammer: usa flint, nuggets, spikes o componentes cortantes.

## Rendimiento

El punto mas sensible son particulas y entidades con bleeding activo.

Para maquinas modestas:

```json
"aoeParticles": {
  "aoeEnabled": true,
  "aoeParticleCountMultiplier": 0.6,
  "aoeParticleDensityMultiplier": 0.6,
  "aoeParticleHeightMultiplier": 0.8
}
```

Para servidores, la config cliente no reduce carga del servidor, pero reduce carga visual del jugador. Para bajar carga real, reduce:

- `aoeRadius`
- `maxLevels`
- `tickIntervalSeconds` demasiado bajos
- cantidad de jugadores usando SpikeHammer en granjas

## Compatibilidad con progresion

El mod no integra directamente GameStages, Reskillable o CraftTweaker en codigo. Para packs 1.12.2, lo normal seria:

- Quitar recetas default con scripts.
- Agregar recetas nuevas por stage.
- Bloquear acceso con mods externos.
- Usar `items.json` para ajustar los numeros finales.

## Preset sugerido: menos control, mas arma

Si quieres que los martillos sean armas fuertes pero no herramientas para cancelar todo:

```json
"warhammer": {
  "stunDurationMultiplier": 0.5,
  "enableAOE": true,
  "enableStun": true
}
```

Y en `items.json`, usa stuns cortos:

```json
"stunDurationSeconds": 0.3,
"aoeStunDurationSeconds": 0.75
```

## Preset sugerido: bajo ruido visual

```json
"aoeParticleCountMultiplier": 0.5,
"aoeParticleDensityMultiplier": 0.5,
"aoeParticleHeightMultiplier": 0.7
```

No se ve tan espectacular, pero ayuda cuando hay muchos impactos seguidos.

## Cosas a vigilar

- Blood Pact con `drainPercent` alto puede convertir peleas largas en sustain gratis.
- Bleeding con `tickIntervalSeconds` muy bajo puede borrar mobs con mucha vida.
- Stun largo puede trivializar jefes o mobs de mods.
- AOE grande puede causar friendly fire si los jugadores pelean cerca de aliados no protegidos.
