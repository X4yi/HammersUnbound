# Hammers Unbound r1.0b4

[ES]
## Resumen de Cambios
Esta versión trae un rediseño importante a la sensación de la mecánica del "Blood Pact", además de múltiples arreglos visuales y de rendimiento para la primera y tercera persona.

### Mejoras Visuales y de Renderizado
- **Cordón de Carne (Tether) mejorado:** 
  - Ahora es más delgado y su respuesta física a los movimientos de los enemigos es mucho más rápida y precisa
- **Partículas Dinámicas:** Las partículas de sangre orbitando al jugador ahora escalan dinámicamente con el daño acumulado (1 partícula por cada 2 puntos de daño).
- **Overlay de Pantalla:** El filtro rojizo de sangre ahora es visible correctamente en ambas perspectivas (1ra y 3ra persona).

### Rediseño de Mecánicas (Blood Pact)
- **Retraso de Impacto "Burst":** El estallido final de daño ya no es instantáneo. Ahora las partículas acumuladas viajan hacia los enemigos como misiles durante 1 segundo antes de detonar el daño, aportándole mucho más "peso" al impacto.
- **Regeneración de Vida Constante:** Mientras el Blood Pact esté activo, el jugador regenera 1 punto de salud cada 40 ticks (2 segundos).
- **Curación por Burst:** Al ejecutar el burst final, el jugador se curará un 33% (1/3) del daño total infligido a todos los objetivos.
- **Retorno Prematuro:** Si el último objetivo atado muere antes de que explote el burst, el pacto no se desperdicia. Todo el daño acumulado se devuelve íntegro como curación al jugador, y las partículas se animan regresando hacia él en lugar de desaparecer.

[/ES]

[EN]
## Changelog Summary
This version brings a major overhaul to the "Blood Pact" mechanics' game feel, alongside multiple visual and performance fixes for both 1st and 3rd person views.

### Visual & Rendering Improvements
- **Tether Improvements:**
  - The tether is now thinner and its physics response is much faster and more accurate
- **Dynamic Particles:** Orbiting blood particles now scale dynamically based on accumulated damage (1 particle per 2 points of damage).
- **Screen Overlay:** The red blood vignette overlay is now properly visible in both perspectives (1st and 3rd person).

### Mechanics Overhaul (Blood Pact)
- **Delayed Burst Impact:** The final burst damage is no longer instantaneous. Accumulated particles now travel towards enemies like homing missiles for 1 second before detonating, adding significant "weight" to the impact.
- **Passive Regeneration:** While the Blood Pact is active, the player regenerates 1 HP every 40 ticks (2 seconds).
- **Burst Healing:** Executing the final burst heals the player for 33% (1/3) of the total damage dealt to all targets.
- **Premature Return:** If the last tethered target dies before the burst triggers, the pact isn't wasted. All accumulated damage is directly given back as healing to the player, and particles animate flying back to the player instead of disappearing.

[/EN]
