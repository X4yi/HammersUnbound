# Configuracion

La configuracion vive en:

```text
config/hammersunbound/
```

Se genera automaticamente al iniciar el juego con el mod.

## Archivos

| Archivo | Para que sirve |
| --- | --- |
| `items.json` | Stats y habilidades de cada martillo/material |
| `server.json` | Toggles y multiplicadores de gameplay |
| `client.json` | Particulas, visuales y UI |

## `items.json`

Este archivo controla el balance fino.

Estructura general:

```json
{
  "warhammer": {
    "materials": {
      "iron": {
        "baseDamage": 9.0,
        "attackSpeed": 0.8,
        "durability": 251,
        "skillCooldownSeconds": 7.0,
        "abilities": {
          "stunDurationSeconds": 1.0,
          "stunAmplifier": 4,
          "aoeRadius": 2.5,
          "aoeDamage": 6.75,
          "aoeStunDurationSeconds": 2.5,
          "aoeStunAmplifier": 1
        }
      }
    }
  }
}
```

### Campos comunes

| Campo | Tipo | Explicacion |
| --- | --- | --- |
| `baseDamage` | numero | Dano base del arma |
| `attackSpeed` | numero positivo | Velocidad visible en tooltip/GUI |
| `durability` | entero | Durabilidad maxima |
| `skillCooldownSeconds` | numero | Cooldown de habilidad en segundos |

### WarHammer abilities

| Campo | Tipo | Explicacion |
| --- | --- | --- |
| `stunDurationSeconds` | numero | Stun al objetivo principal |
| `stunAmplifier` | entero | Potencia del stun |
| `aoeRadius` | numero | Radio del dano en area |
| `aoeDamage` | numero | Dano aplicado a objetivos cercanos |
| `aoeStunDurationSeconds` | numero | Duracion de stun para objetivos AOE |
| `aoeStunAmplifier` | entero | Potencia del stun AOE |

### SpikeHammer bleeding

```json
"bleeding": {
  "maxLevels": 5,
  "baseDurationSeconds": 5.0,
  "damagePerLevel": 2.0,
  "tickIntervalSeconds": 0.8,
  "decaySeconds": 10.0
}
```

| Campo | Explicacion |
| --- | --- |
| `maxLevels` | Nivel maximo acumulable |
| `baseDurationSeconds` | Duracion base antes de bajar de nivel |
| `damagePerLevel` | Base para calcular dano por nivel |
| `tickIntervalSeconds` | Intervalo base entre danos |
| `decaySeconds` | Valor guardado para compatibilidad/estado del efecto |

### SpikeHammer Blood Pact

```json
"bloodPact": {
  "range": 12.0,
  "drainPercent": 0.25,
  "tetherBreakDistance": 18.0,
  "drainIntervalSeconds": 0.4
}
```

| Campo | Explicacion |
| --- | --- |
| `range` | Distancia para activar pacto |
| `drainPercent` | Porcentaje usado para curar al jugador |
| `tetherBreakDistance` | Distancia maxima antes de romper el pacto |
| `drainIntervalSeconds` | Cada cuanto drena vida |

## `server.json`

Controla reglas globales del servidor.

```json
{
  "warhammer": {
    "stunDurationMultiplier": 1.0,
    "enableAOE": true,
    "enableStun": true
  },
  "spikehammer": {
    "bleedingDamageMultiplier": 1.0,
    "bleedingDurationMultiplier": 1.0,
    "bloodPactRangeMultiplier": 1.0,
    "bloodPactDrainMultiplier": 1.0,
    "enableBleeding": true,
    "enableBloodPact": true
  }
}
```

Usa este archivo para balance rapido. Usa `items.json` cuando quieras cambiar material por material.

## `client.json`

La config cliente esta separada por categoria real:

```json
{
  "aoeParticles": {
    "aoeEnabled": true,
    "aoeParticleCountMultiplier": 1.0,
    "aoeParticleDensityMultiplier": 1.0,
    "aoeParticleHeightMultiplier": 1.0
  },
  "combatVisuals": {
    "bloodPactEnabled": true,
    "bloodPactParticleCount": 5,
    "bleedingParticleEnabled": true
  },
  "ui": {
    "uiOverlayPosition": 0,
    "showDevWarning": true,
    "showChangelogButton": true,
    "language": "es"
  }
}
```

### Posiciones de UI

| Valor | Posicion |
| ---: | --- |
| 0 | Bottom Right |
| 1 | Bottom Center |
| 2 | Bottom Left |
| 3 | Top Right |
| 4 | Top Left |

### Opciones de UI

| Campo | Tipo | Default | Explicacion |
| --- | --- | --- | --- |
| `uiOverlayPosition` | entero | `0` | Posicion del overlay de habilidad |
| `showDevWarning` | boolean | `true` | Muestra el aviso de desarrollo en el Main Menu |
| `showChangelogButton` | boolean | `true` | Muestra el boton pequeno de Changelog en el Main Menu |
| `language` | texto | `es` | Idioma usado por pantallas que soportan seleccion ES/EN |

## Compatibilidad con configs viejas

El mod todavia entiende campos antiguos:

- `attackSpeed` negativo.
- `skillCooldown` en ticks.
- `stunDuration` y `aoeStunDuration` en ticks.
- `baseDuration`, `tickInterval`, `decayTicks` y `drainInterval` en ticks.
- Config cliente antigua agrupada en `particles`.

Cuando guardas desde la GUI actual, el mod escribe el formato nuevo.

## Recargar config

El mod registra un comando de configuracion. En servidor, usa:

```text
/hammersunbound reload
```

Si algo no cambia al instante, reinicia mundo/cliente. Minecraft 1.12.2
