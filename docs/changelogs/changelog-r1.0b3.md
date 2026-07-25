[ES]
# Hammers Unbound r1.0b3

## Habilidad Especial: Pacto de Sangre (BloodPact)
- **Vínculo de Sangre:** Ahora puedes enlazar tu energía vital a un grupo de hasta 3 enemigos simultáneamente.
- **Efectos del Pacto:** Mientras el pacto esté activo, recuperas salud al golpear a tus objetivos pactados (Robo de Vida). Además, un campo de fuerza a tu alrededor repele a cualquier enemigo ajeno al pacto para mantener limpia tu área de combate.
- **Barra de Locura (Madness):** Golpear a los objetivos pactados llena un nuevo medidor de Locura en tu pantalla, otorgándote grandes bufos de velocidad de movimiento y velocidad de ataque.
- **Explosión Sanguínea (Burst):** Al agotarse el temporizador de 10 segundos, el pacto detona infligiendo daño explosivo equivalente a un tercio del daño total acumulado a los objetivos enlazados.
- **Ataque Frontal (AoE Sweeps):** Al atacar con el SpikeHammer en la mano durante el pacto, realizarás un barrido frontal automático en área que daña a múltiples enemigos en frente de ti.

## Habilidad Especial: Ping-Pong
- **Lanzamiento y Retorno:** Haz click derecho hacia un objetivo pactado para lanzarlo por los aires alejado de ti (Ping) y luego atraerlo magnéticamente de vuelta (Pong).
- **Golpe de Retorno:** Si golpeas al enemigo justo en el momento en que regresa hacia ti, le infligirás un **+50% de daño adicional** y reiniciarás el lanzamiento de forma inmediata.

## Correcciones y Mejoras
- **Carga de Configuración Segura:** Se optimizó la inicialización del mod eliminando llamadas de lectura de disco en la carga de clases estáticas, previniendo crashes raros al abrir el juego.
- **Rango del Pacto Corregido:** Se solucionó el problema por el cual el enlace de sangre se rompía de inmediato si se usaban multiplicadores de rango altos.
- **Aturdimiento Consistente:** El multiplicador de duración de stun del servidor ahora también escala correctamente el tiempo de aturdimiento de los enemigos golpeados en área con el WarHammer.
- **Navegador Web Asíncrono:** Corregido el problema donde el juego se congelaba al intentar abrir enlaces web (como reportar errores en GitHub) en sistemas Linux.
- **Límites de Seguridad en GUI:** Los deslizadores de multiplicadores en la configuración dentro del juego ahora se limitan a un mínimo de 0.1 para evitar anular por completo los daños o efectos de forma no deseada.

## Configuración y Traducción
- **Menú de Configuración Completo:** Se añadieron todas las opciones que hacían falta en la interfaz dentro del juego, incluyendo la densidad de partículas del pacto de sangre y el rango de sincronización en red.
- **Traducción Completa:** Ahora toda la interfaz de configuración (secciones, botones y títulos) está traducida y se adapta al idioma seleccionado.
[/ES]

[EN]
# Hammers Unbound r1.0b3

## Special Ability: BloodPact
- **Blood Tether:** Link your life force with up to 3 nearby enemies simultaneously.
- **Pact Effects:** While active, hitting linked targets restores your health (lifesteal). Additionally, a repulsion field around you pushes away non-linked enemies to clear your combat space.
- **Madness Meter:** Hitting linked targets charges the Madness gauge, granting movement and attack speed boosts.
- **Blood Burst:** Every 10 seconds, the pact detonates, dealing explosive magic damage equal to 1/3 of the total accumulated damage to all linked targets.
- **Frontal AoE Sweeps:** Swing the SpikeHammer during the pact to trigger a frontal area-of-effect check, damaging multiple targets in front of you.

## Special Ability: Ping-Pong
- **Throw and Pull:** Right-click a linked target to launch them back (Ping) and then pull them back to you (Pong).
- **Return Strike:** Hit the returning target at the right moment to deal **+50% bonus damage** and immediately restart the Ping-Pong cycle.

## Fixes and Improvements
- **Safe Config Loading:** Optimized mod initialization by removing static disk I/O calls on class load, preventing JVM startup crashes.
- **Tether Break Fix:** Fixed an issue where the BloodPact link would immediately break if using high range multipliers.
- **Consistent Stun:** The server's stun duration multiplier now correctly scales secondary targets' stun duration during a WarHammer critical ground slam.
- **Async Web Browsing:** Fixed game lockups when opening links (such as GitHub Issues) on Linux platforms.
- **GUI Slider Safety:** Added a minimum boundary of 0.1 to multiplier sliders in the config GUI to prevent accidentally disabling combat stats or effects.

## Config & Localization
- **Complete Config Menu:** Added all missing settings to the in-game GUI, including blood pact particle density and network sync ranges.
- **Full Localization:** The entire configuration menu (tabs, button labels, and header titles) is now fully translated and localized.
[/EN]
