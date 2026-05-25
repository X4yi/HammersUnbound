[ES]
# Hammers Unbound r1.0b2

## Corrección de Errores
- **Validación de Críticos:** Se ha corregido la lógica de golpes críticos de los martillos. Ahora requieren un ataque completamente cargado y se valida que el jugador no esté corriendo, solucionando el problema donde no se aplicaban correctamente los efectos AoE y el Stun del Warhammer.
- **Bleeding del Spikehammer:** El efecto de sangrado del Spikehammer ahora se aplica tanto en ataques críticos como en ataques en sprint, siempre y cuando el golpe esté cargado al máximo.
- **Sincronización de Partículas de Sangrado:** Las partículas del efecto de sangrado ahora aparecen exactamente cuando el objetivo recibe el daño, en lugar de aparecer de forma aleatoria, mejorando la respuesta visual del combate.
- **Bypass de Inmunidad:** El daño de sangrado ya no se ve interrumpido por los ticks de inmunidad de otras fuentes de daño. El Spikehammer se vuelve mucho más mortífero.

## Cambios en Interfaz (UI/GUI)
- **Cooldowns Limpios:** Se han eliminado el porcentaje de cooldown y el texto "RDY" de la interfaz rápida en el juego para mantener el visual más minimalista y estético.
- **Nuevas Animaciones:** Las interfaces de Configuración y Changelog ahora cuentan con una animación de aparición (scale pop) suave, dándoles un toque más premium.
- **Mejoras en el Scroll:** Se introdujo interpolación para lograr un deslizamiento fluido (smooth scroll) en las interfaces. Además, se solucionó un problema visual al expandir/contraer categorías en la configuración.
- **Nuevo aviso de desarrollo:** Al entrar al menu principal aparece un popup informativo con acceso directo a GitHub Issues. Si pulsas `Ok`, no vuelve a mostrarse; si pulsas `Salir`, solo se oculta durante la sesion actual.
[/ES]

[EN]
# Hammers Unbound r1.0b2

## Bug Fixes
- **Critical Hit Validation:** Fixed hammer critical hit logic. They now require a fully charged attack and validate that the player is not sprinting, fixing the issue where AoE and Stun effects were not applying correctly for the Warhammer.
- **Spikehammer Bleeding:** The Spikehammer's bleeding effect now applies on both critical hits and sprint attacks, as long as the hit is fully charged.
- **Bleeding Particle Sync:** Blood effect particles now spawn exactly when the target takes damage, rather than randomly, vastly improving visual combat feedback.
- **Immunity Bypass:** Bleeding damage is no longer interrupted by immunity ticks from other damage sources. The Spikehammer just got a lot more lethal.

## UI/GUI Changes
- **Cleaner Cooldowns:** Removed the cooldown percentage and the "RDY" text from the quick in-game UI to keep a minimalist and aesthetic look.
- **New Animations:** The Configuration and Changelog GUIs now feature a smooth pop-in animation on open, giving them a much more premium feel.
- **Scroll Improvements:** Added interpolation for smooth scrolling across GUIs. Additionally, fixed an interaction issue when expanding/collapsing config categories.
- **New development warning:** A notice popup now appears on the main menu with direct access to GitHub Issues. Press `Ok` to hide it permanently, or `Salir` to hide it only for the current session.
[/EN]
