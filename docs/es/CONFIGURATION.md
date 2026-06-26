# Referencia de Configuración: Hammers Unbound

Los archivos de configuración se encuentran en:

```text
config/hammersunbound/
```

---

## Archivos de Configuración

| Archivo | Ámbito | Propósito |
| --- | --- | --- |
| `items.json` | Balance | Estadísticas de armas (daño, velocidad, durabilidad) y valores base por material. |
| `server.json` | Servidor | Toggles de mecánicas y multiplicadores globales de juego. |
| `client.json` | Cliente | Partículas, posición de la interfaz de HUD, idioma y advertencias. |

---

## `items.json`

Contiene las propiedades de cada material (`wood`, `stone`, `iron`, `gold`, `diamond`) para los tipos `warhammer` y `spikehammer`.

### Propiedades de `warhammer`
*   **`baseDamage`** *(float)*: Daño base de ataque.
*   **`attackSpeed`** *(float)*: Velocidad de ataque.
*   **`durability`** *(int)*: Durabilidad máxima.
*   **`skillCooldownSeconds`** *(float)*: Cooldown del impacto terrestre (crítico).
*   **`abilities`**:
    *   **`stunDurationSeconds`** *(float)*: Duración del aturdimiento principal.
    *   **`stunAmplifier`** *(int)*: Nivel del efecto del aturdimiento principal.
    *   **`aoeRadius`** *(float)*: Radio del golpe terrestre.
    *   **`aoeDamage`** *(float)*: Daño del golpe terrestre.
    *   **`aoeStunDurationSeconds`** *(float)*: Duración del aturdimiento secundario.
    *   **`aoeStunAmplifier`** *(int)*: Nivel del aturdimiento secundario.

### Propiedades de `spikehammer`
*   **`baseDamage`**, **`attackSpeed`**, **`durability`**, **`skillCooldownSeconds`**: Mismo comportamiento que el WarHammer.
*   **`bleeding`**:
    *   **`maxLevels`** *(int)*: Límite máximo de stacks de sangrado.
    *   **`baseDurationSeconds`** *(float)*: Duración de los stacks.
    *   **`damagePerLevel`** *(float)*: Daño periódico por nivel de sangrado.
    *   **`tickIntervalSeconds`** *(float)*: Intervalo en segundos entre daños de sangrado.
    *   **`decaySeconds`** *(float)*: Duración del decaimiento de stacks.
*   **`bloodPact`**:
    *   **`range`** *(float)*: Rango máximo para iniciar el enlace del pacto.
    *   **`drainPercent`** *(float)*: Porcentaje del daño convertido a curación (0.0 a 1.0).
    *   **`tetherBreakDistance`** *(float)*: Distancia de ruptura del enlace.
    *   **`maxTargets`** *(int)*: Límite de objetivos enlazados (máximo 3).
    *   **`fieldRadius`** *(float)*: Radio del campo de repulsión.
    *   **`repulsionForce`** *(float)*: Fuerza física de repulsión.
    *   **`attractionForce`** *(float)*: Fuerza de atracción física.
    *   **`baseDurationSeconds`** *(float)*: Duración base del pacto.
    *   **`hitBonusSeconds`** *(float)*: Tiempo añadido al pacto tras un golpe.
    *   **`damagePenaltySeconds`** *(float)*: Tiempo descontado del pacto al recibir daño de un pactado.
    *   **`aoeAttackSize`** *(float)*: Tamaño de la caja de colisión AABB para barrido frontal.

---

## `server.json`

Opciones lógicas y multiplicadores sincronizados a nivel de servidor.

### `warhammer`
*   **`stunDurationMultiplier`** *(float, defecto: 1.0)*: Multiplicador de duración del aturdimiento principal y secundario.
*   **`enableAOE`** *(boolean, defecto: true)*: Activa o desactiva el impacto terrestre.
*   **`enableStun`** *(boolean, defecto: true)*: Activa o desactiva el efecto de aturdimiento.
*   **`serverAoeParticleSyncDistance`** *(double, defecto: 64.0)*: Rango de sincronización de partículas de impacto.

### `spikehammer`
*   **`bleedingDamageMultiplier`** *(float, defecto: 1.0)*: Multiplica el daño por stack de sangrado.
*   **`bleedingDurationMultiplier`** *(float, defecto: 1.0)*: Multiplica la duración de los stacks de sangrado.
*   **`bloodPactRangeMultiplier`** *(float, defecto: 1.0)*: Multiplica el rango de enlace y ruptura.
*   **`bloodPactDrainMultiplier`** *(float, defecto: 1.0)*: Multiplica el robo de vida.
*   **`enableBleeding`** *(boolean, defecto: true)*: Activa o desactiva el sangrado.
*   **`enableBloodPact`** *(boolean, defecto: true)*: Activa o desactiva el pacto de sangre.

---

## `client.json`

Ajustes del lado del cliente para renderizado visual.

### `aoeParticles`
*   **`aoeEnabled`** *(boolean, defecto: true)*: Habilita partículas de impacto terrestre.
*   **`aoeParticleCountMultiplier`** *(float, defecto: 1.0)*: Cantidad de partículas generadas.
*   **`aoeParticleDensityMultiplier`** *(float, defecto: 1.0)*: Concentración de partículas.
*   **`aoeHeightMultiplier`** *(float, defecto: 1.0)*: Altura y velocidad vertical de las partículas.

### `combatVisuals`
*   **`bloodPactEnabled`** *(boolean, defecto: true)*: Activa los hilos visuales del pacto.
*   **`bloodPactParticleCount`** *(int, defecto: 5)*: Densidad de partículas de sangre orbitantes.
*   **`bleedingParticleEnabled`** *(boolean, defecto: true)*: Habilita salpicaduras de sangrado.

### `ui`
*   **`uiOverlayPosition`** *(int, defecto: 0)*: Posición de HUD (`0`: Abajo Izquierda, `1`: Abajo Derecha, `2`: Arriba Izquierda, `3`: Arriba Derecha, `4`: Oculto).
*   **`showDevWarning`** *(boolean, defecto: true)*: Habilita advertencias emergentes.
*   **`showChangelogButton`** *(boolean, defecto: true)*: Muestra el botón de changelog en el menú.
*   **`language`** *(string, defecto: "es")*: Idioma de las interfaces.
