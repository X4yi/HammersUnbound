# Ideas - Sesion 1

Proyecto revisado: Hammers Unbound, Minecraft 1.12.2, Forge 14.23.5.2847.

## Plan de implementacion de ideas

1. Priorizar primero mejoras de configuracion, documentacion y compatibilidad con modpacks, porque dan mas valor con menos riesgo.
2. Despues mejorar feedback visual/sonoro y lectura de combate, porque el mod ya tiene mecanicas con identidad propia: stun, AOE, bleeding y Blood Pact.
3. Luego introducir variantes nuevas de gameplay usando la misma arquitectura JSON actual, evitando hardcode y manteniendo cada martillo configurable.
4. Finalmente trabajar rendimiento, validacion y herramientas de balance para que el mod aguante servidores y modpacks grandes.

## Lectura rapida del estado actual

- El mod tiene dos familias principales: WarHammer y SpikeHammer.
- WarHammer se centra en criticos con stun, AOE, sonido de impacto y particulas.
- SpikeHammer se centra en bleeding por critico/sprint y Blood Pact por click derecho.
- Hay configuracion externa para stats y habilidades en `items.json`, `server.json` y `client.json`.
- Existe GUI de configuracion, lector de changelog, popup de advertencia de desarrollo y sincronizacion de particulas/efectos.
- Las recetas usan ore dictionary, buena base para modpacks.
- La documentacion markdown principal parece incompleta o vacia en algunos archivos, mientras que los HTML ya existen.

## Ideas de gameplay

### 1. Identidad mecanica mas clara por familia

**Idea:** convertir WarHammer y SpikeHammer en dos estilos muy distintos:

- WarHammer: control de masas, interrupcion, posicionamiento, "romper formaciones".
- SpikeHammer: duelos largos, desgaste, riesgo/recompensa y sustain mediante Blood Pact.

**Justificacion:** ahora ambas familias comparten stats base parecidos y recetas identicas. Las habilidades existen, pero el usuario puede sentir que son "dos martillos fuertes" sin una decision tactica clara.

**Implementacion sugerida:**

- Añadir al tooltip una linea de rol: `Control / AOE` o `Bleeding / Pact`.
- Dar a WarHammer un pequeño knockback radial configurable.
- Dar a SpikeHammer bonus contra objetivos ya sangrando, pero menos AOE o nada de AOE.
- Mantener todo en JSON por material.

**Prioridad:** alta.

### 2. Carga pesada para WarHammer

**Idea:** si el jugador mantiene sneak durante 0.5-1.0 segundos antes de atacar, el siguiente critico del WarHammer hace un "Ground Break": mas radio, menos dano directo, mas stun corto.

**Justificacion:** WarHammer ya tiene AOE en critico. Una carga manual haria que el jugador prepare golpes importantes en vez de depender solo de criticos vanilla.

**Configuracion:**

- `chargeRequiredTicks`
- `chargedAoeRadiusMultiplier`
- `chargedDamageMultiplier`
- `chargedStunDurationMultiplier`
- `consumeExtraDurability`

**Prioridad:** media.

### 3. Blood Pact con decisiones, no solo robo de vida

**Idea:** Blood Pact podria tener estados:

- `Linked`: el pacto esta activo.
- `Hungry`: si no golpeas al objetivo cada cierto tiempo, el pacto drena un poco al jugador.
- `Feast`: si rematas al objetivo pactado, recuperas vida o reduces cooldown.

**Justificacion:** ahora Blood Pact cura al golpear y drena periodicamente al objetivo. Si se agrega una penalizacion ligera por no presionar, pasa a ser una mecanica de riesgo/recompensa mas memorable.

**Cuidado de balance:** que sea desactivable por config para modpacks casuales.

**Prioridad:** media.

### 4. Sangrado con interacciones contra armadura

**Idea:** bleeding podria tener una opcion de ignorar un porcentaje configurable de armadura solo en niveles altos.

**Justificacion:** en 1.12.2 muchos modpacks tienen mobs con armadura o vida inflada. El sangrado escala mejor si tiene una herramienta limitada contra defensas altas.

**Configuracion:**

- `armorBypassAtLevel`
- `armorBypassPercent`
- `bossBypassMultiplier`

**Prioridad:** media.

### 5. Reacciones por tipo de objetivo

**Idea:** permitir modificadores por categoria:

- Undead: menos bleeding, mas stun.
- Arthropods: mas bleeding.
- Bosses: duracion reducida pero inmune a stun completo.
- Tameables: ya hay proteccion para mascotas domesticadas en AOE; extender esto a Blood Pact y bleeding.

**Justificacion:** mejora compatibilidad con aventura/RPG y evita que bosses queden trivializados por stuns largos.

**Prioridad:** alta para modpacks.

### 6. Finishers configurables

**Idea:** cuando un objetivo tiene bleeding maximo o esta stunned, un golpe plenamente cargado puede ejecutar un efecto final:

- WarHammer: impacto radial visual fuerte, rompe escudos o empuja.
- SpikeHammer: consume stacks de bleeding para dano instantaneo y cura menor.

**Justificacion:** da cierre a las mecanicas acumulativas y crea momentos reconocibles para el usuario.

**Prioridad:** baja/media.

## Ideas para usuarios y UX

### 7. Tooltips completos pero limpios

**Idea:** tooltips con modo normal y avanzado:

- Normal: dano, velocidad, durabilidad, rol y habilidad principal.
- Avanzado con Shift: radio AOE, stun, sangrado, Blood Pact, cooldown.

**Justificacion:** ya se muestran dano, velocidad y durabilidad. Falta explicar por que elegir un martillo u otro sin abrir archivos JSON.

**Prioridad:** alta.

### 8. Feedback de cooldown y habilidad lista

**Idea:** cuando el WarHammer sale de cooldown o Blood Pact se rompe, mostrar particula breve, sonido leve o mensaje en actionbar.

**Justificacion:** el jugador necesita saber por que un critico no activo AOE/stun o por que se corto el pacto.

**Prioridad:** alta.

### 9. Guia dentro del juego

**Idea:** agregar un boton "Guide" en la GUI junto a Changelog/Config, con paginas simples:

- WarHammer
- SpikeHammer
- Configuracion
- Modpacks
- Problemas comunes

**Justificacion:** el mod ya tiene GUI propia y lector de changelog. Una guia pequena evita depender solo de docs externas.

**Prioridad:** media.

### 10. Idiomas reales ES/EN

**Idea:** completar localizacion con `es_es.lang` y mover strings hardcodeadas de GUI/tooltips a lang keys.

**Justificacion:** la GUI tiene textos en ingles y el proyecto se documenta en espanol. Para usuarios, servidores y modpacks internacionales, conviene que todo sea traducible.

**Prioridad:** alta.

## Ideas para modpacks

### 11. Presets de balance

**Idea:** incluir presets JSON opcionales:

- `vanilla_plus`
- `expert`
- `rpg_mobs`
- `low_particles`
- `pvp_safe`

**Justificacion:** un pack maker no siempre quiere tunear cada numero. Presets aceleran adopcion y reducen configuraciones rotas.

**Prioridad:** alta.

### 12. Integracion CraftTweaker / ZenScript

**Idea:** exponer comandos o hooks para cambiar stats, activar/desactivar materiales y ajustar recetas desde CraftTweaker.

**Justificacion:** en 1.12.2 CraftTweaker es casi estandar en modpacks grandes. Aunque ya hay JSON, muchos pack makers prefieren centralizar balance en scripts.

**Prioridad:** media/alta.

### 13. Compatibilidad con GameStages / Reskillable

**Idea:** permitir bloquear martillos o habilidades por stage/skill si esos mods estan presentes.

**Justificacion:** los martillos tienen mucha identidad de progresion. En packs RPG/expert, desbloquear Blood Pact o Ground Break por progresion seria muy util.

**Prioridad:** media.

### 14. Lista negra/blanca de entidades y dimensiones

**Idea:** configurar entidades inmunes, dimensiones deshabilitadas o modificadores por dimension.

**Justificacion:** algunos packs tienen bosses, dimensiones de eventos o mobs scriptados que pueden romperse con stun, AOE o drain.

**Prioridad:** alta.

### 15. Recetas diferenciadas

**Idea:** separar receta de WarHammer y SpikeHammer para que no usen exactamente el mismo patron.

**Justificacion:** ahora ambas familias usan `LLL/LLL/ S `. Eso reduce identidad y puede confundir. SpikeHammer podria requerir flint/iron nugget/spikes; WarHammer podria usar bloques/placas.

**Prioridad:** alta.

## Ideas de configuracion y escalabilidad

### 16. Validacion fuerte de JSON

**Idea:** validar rangos antes de aplicar configs:

- No permitir radios negativos.
- Limitar multiplicadores absurdos.
- Asegurar `tickInterval >= 1`.
- Reportar claves desconocidas con warning.

**Justificacion:** el mod renombra configs corruptas, pero tambien conviene proteger contra JSON valido con numeros peligrosos para servidores.

**Prioridad:** alta.

### 17. Habilidades por lista extensible

**Idea:** pasar de campos fijos a una lista de habilidades por material:

```json
"abilities": [
  { "type": "stun", "duration": 20, "amplifier": 2 },
  { "type": "aoe", "radius": 2.5, "damage": 6.0 }
]
```

**Justificacion:** ahora WarHammer y SpikeHammer tienen estructuras distintas. Una lista de habilidades permitiria crear nuevos martillos sin duplicar clases.

**Prioridad:** baja/media, porque implica refactor.

### 18. Activar/desactivar materiales por config

**Idea:** cada material deberia tener `enabled`.

**Justificacion:** modpacks pueden querer solo hierro+diamante, o reemplazar madera/oro por progresion propia.

**Prioridad:** alta.

### 19. Separar config de servidor sincronizada y config local

**Idea:** documentar y reforzar que el servidor manda balance autoritativo y el cliente solo controla visual/UI.

**Justificacion:** ya existen paquetes de sync config. Hacer explicita esa frontera evita desyncs y exploits visuales.

**Prioridad:** alta.

## Ideas de rendimiento

### 20. Control de particulas por presupuesto

**Idea:** ademas de multiplicadores, agregar presupuesto maximo por segundo:

- maximo de particulas AOE por jugador/segundo.
- maximo de particulas bleeding por entidad/segundo.
- distancia menor para particulas de bajo impacto.

**Justificacion:** WarHammer envia particulas AOE a jugadores cercanos y bleeding puede dispararse en muchos mobs. En granjas o dungeons, un presupuesto evita lag visual/red.

**Prioridad:** alta.

### 21. Tick rate adaptable para efectos

**Idea:** no procesar bleeding/Blood Pact cada tick si no hace falta. Usar intervalos internos o scheduler simple.

**Justificacion:** el handler revisa entidades vivas en update. Aunque hay early returns, en servidores con muchas entidades conviene minimizar trabajo repetitivo.

**Prioridad:** media.

### 22. Cache de configs por item

**Idea:** cachear entradas de material por item y refrescarlas solo al reload.

**Justificacion:** actualmente varios getters consultan configs dinamicas por tipo/material. Funciona, pero en tooltips/atributos puede repetirse mucho.

**Prioridad:** media.

## Ideas de compatibilidad y seguridad de gameplay

### 23. PvP configurable

**Idea:** opciones separadas:

- permitir stun en jugadores.
- permitir Blood Pact en jugadores.
- permitir bleeding en jugadores.
- multiplicador PvP.

**Justificacion:** servidores PvE y PvP necesitan reglas distintas. Stun y sustain pueden ser muy fuertes en PvP.

**Prioridad:** alta.

### 24. Boss safety

**Idea:** detectar bosses por `isNonBoss`/barra de boss/mod hooks cuando sea posible y aplicar reducciones:

- stun convertido a slow.
- bleeding limitado a nivel menor.
- Blood Pact con duracion maxima.

**Justificacion:** evita romper progresion de bosses en modpacks.

**Prioridad:** alta.

### 25. Proteccion contra friendly fire ampliada

**Idea:** extender exclusion de mascotas domesticadas a:

- team scoreboard.
- owner de entidades modded si exponen owner.
- jugadores aliados por config.

**Justificacion:** el AOE ya evita mascotas vanilla domesticadas. En servidores y modpacks hay equipos, summons y pets modded.

**Prioridad:** media.

## Ideas de contenido futuro

### 26. Nuevos materiales configurables

**Idea:** soporte para materiales externos sin registrar codigo por cada uno:

- bronze
- steel
- obsidian
- nether star
- modded ingots por ore dictionary

**Justificacion:** ahora los items estan registrados manualmente para madera, piedra, hierro, oro y diamante. Para modpacks, el valor grande seria crear martillos de materiales del pack.

**Prioridad:** grande, alto valor.

### 27. Tiers de mango/cabeza

**Idea:** separar receta y stats en `headMaterial` y `handleMaterial`.

**Justificacion:** daria flexibilidad tipo Tinkers-lite sin convertirse en un sistema enorme. Ejemplo: cabeza de hierro + mango de blaze rod = mas stun pero menos durabilidad.

**Prioridad:** grande.

### 28. Encantamientos propios

**Idea:** encantamientos limitados a martillos:

- `Seismic`: mas AOE, menos dano directo.
- `Hemorrhage`: mas duracion de bleeding.
- `Ritual`: Blood Pact dura mas pero drena al jugador si falla.
- `Anchor`: stun mas fuerte contra mobs pequeños, menor contra bosses.

**Justificacion:** aumenta progresion sin agregar demasiados items.

**Prioridad:** media/grande.

## Ideas de documentacion

### 29. Completar docs markdown primero

**Idea:** llenar `docs/USER_GUIDE.md`, `docs/mecanics.md` y `docs/items.md` como fuente principal, y generar HTML desde ahi si se desea.

**Justificacion:** algunos markdown parecen vacios. Mantener HTML manual y markdown vacio facilita desactualizacion.

**Prioridad:** alta.

### 30. Pagina para pack makers

**Idea:** crear `docs/PACKMAKER_GUIDE.md` con:

- archivos de config.
- ejemplos de balance.
- recetas.
- compatibilidad.
- presets recomendados.
- advertencias de PvP/bosses.

**Justificacion:** el mod ya tiene una base muy configurable. Hay que hacerla visible y facil de usar.

**Prioridad:** alta.

## Ideas visuales y de audio

### 31. Impactos segun superficie

**Idea:** WarHammer podria variar particulas/sonido segun bloque bajo el objetivo: piedra, tierra, madera, metal.

**Justificacion:** ya se consulta el bloque bajo el impacto para sonido. Se puede extender a particulas por material sin cambiar la mecanica.

**Prioridad:** media.

### 32. Indicador de Blood Pact

**Idea:** overlay pequeño con icono del pacto, distancia al objetivo y estado del vinculo.

**Justificacion:** Blood Pact es una habilidad sostenida; el jugador necesita leer si sigue conectado y cuando se va a romper.

**Prioridad:** media.

### 33. Sangrado visible por intensidad

**Idea:** particulas mas densas/color distinto segun nivel de bleeding, con opcion low-performance.

**Justificacion:** el nivel de sangrado es importante para decisiones de combate. Si se ve, el jugador no necesita memorizar golpes.

**Prioridad:** media.

## Backlog recomendado

1. Tooltips avanzados con Shift.
2. Opciones PvP/boss safety.
3. Presets de balance para modpacks.
4. Recetas diferenciadas entre WarHammer y SpikeHammer.
5. Validacion fuerte de JSON.
6. Guia markdown para usuarios y pack makers.
7. Feedback de cooldown/Blood Pact roto.
8. Presupuesto de particulas.
9. Lista negra/blanca de entidades/dimensiones.
10. Nuevos materiales configurables.

## Change testing revisado

Archivo revisado: `md/changetesting-version-test1.md`.

### Version r1.0b2 - Test 1

**Problema solucionado:** el usuario no recibia un aviso visible de que el mod esta en desarrollo.

**Como comprobarlo:** borrar o reiniciar `config/hammersunbound/client.json`, iniciar el juego con el mod activo y confirmar que aparece el popup de aviso en el `MainMenu`.

### Version r1.0b2 - Test 2

**Problema solucionado:** el boton `Salir` del aviso no debe guardar la preferencia; solo cierra el popup durante la sesion actual.

**Como comprobarlo:** abrir el popup, pulsar `Salir`, confirmar que se cierra, reiniciar el juego y verificar que el popup vuelve a aparecer en el `MainMenu`.

### Version r1.0b2 - Test 3

**Problema solucionado:** faltaba persistir la preferencia de cliente para ocultar el aviso permanentemente.

**Como comprobarlo:** pulsar `Ok`, reiniciar el juego, verificar que el popup ya no aparece y revisar que `config/hammersunbound/client.json` tenga `ui.showDevWarning=false`.

### Version r1.0b2 - Test 4

**Problema solucionado:** el usuario necesitaba acceso directo para reportar errores.

**Como comprobarlo:** abrir el popup en `MainMenu`, hacer click en el enlace de GitHub Issues y confirmar que el navegador predeterminado abre la URL de issues del proyecto.

### Version r1.0b2 - Test 5

**Problema solucionado:** el build fallaba porque `forge-1.12.2-14.23.5.2864-userdev.jar` ya no esta disponible en el Maven oficial de Forge. El test documenta tres fixes: extraer `userdev.jar` desde el installer, cambiar mappings a `snapshot_20171003` y eliminar el bloque `sourceSets.each` que causaba `duplicate entry` en `reobfJar`.

**Como comprobarlo:** ejecutar `./gradlew build` y confirmar que se generan `build/libs/hammersunbound-1.0b2.jar` y `build/libs/hammersunbound-1.0b2-sources.jar`.

## Notas de ambiguedad

- No queda claro si quieres ideas solo para r1.0b2 o para un roadmap largo; por eso separe prioridades rapidas, medias y grandes.
- No queda claro si prefieres mantener dos familias fijas o evolucionar hacia martillos configurables/modulares; deje ambas rutas.
- No queda claro si el foco principal sera singleplayer, servidores PvE o PvP; las ideas de PvP/boss safety deberian ajustarse segun ese objetivo.
