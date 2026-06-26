[ES]
# Hammers Unbound r1.0b3

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
