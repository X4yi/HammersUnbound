# Change Testing - version test1

## Version: r1.0b2

### Test 1 - Popup aparece en primera ejecucion
Problema solucionado:
El usuario no recibia aviso visible de que el mod esta en desarrollo.

Como comprobarlo:
1. Borra o reinicia `config/hammersunbound/client.json`.
2. Inicia el juego con el mod activo.
3. En el `MainMenu`, verifica que aparece el popup de aviso.

### Test 2 - Boton Salir solo afecta la sesion actual
Problema solucionado:
Cerrar el aviso sin aceptar no debe persistir la preferencia.

Como comprobarlo:
1. Con el popup abierto, pulsa `Salir`.
2. Verifica que se cierra.
3. Cierra el juego y vuelve a abrirlo.
4. Verifica que el popup vuelve a mostrarse en el `MainMenu`.

### Test 3 - Boton Ok persiste no mostrar de nuevo
Problema solucionado:
Faltaba la persistencia de preferencia cliente para ocultar el aviso de forma permanente.

Como comprobarlo:
1. Con el popup abierto, pulsa `Ok`.
2. Cierra el juego y vuelve a abrirlo.
3. Verifica que el popup ya no aparece en el `MainMenu`.
4. Abre `config/hammersunbound/client.json` y confirma `ui.showDevWarning=false`.

### Test 4 - Enlace a GitHub Issues abre navegador
Problema solucionado:
El usuario necesitaba acceso directo para reportar errores.

Como comprobarlo:
1. Abre el popup en `MainMenu`.
2. Haz click en el enlace de GitHub Issues.
3. Verifica que el sistema abre el navegador predeterminado en la URL de issues del proyecto.

### Test 5 - Build con Forge 2864 en ForgeGradle 2.3
Problema solucionado:
El build fallaba porque `forge-1.12.2-14.23.5.2864-userdev.jar` ya no está disponible en el Maven oficial de Forge (404). Solo existe el `installer.jar` que lo contiene embebido.

Soluciones aplicadas (3 fixes):
- **Fix A**: Extraído `userdev.jar` del installer de 2864 y colocado en el caché local de Gradle.
- **Fix B**: Cambiado `mappings = 'stable_39'` → `mappings = 'snapshot_20171003'` en build.gradle (el código fuente usa nombres snapshot, no stable).
- **Fix C**: Eliminado bloque `sourceSets.each` que causaba `duplicate entry` en `reobfJar` al mezclar classes y resources en el mismo directorio.

Como comprobarlo:
1. Ejecuta `./gradlew build` — debe descargar dependencias y compilar sin errores.
2. Verifica que se genera `build/libs/hammersunbound-1.0b2.jar`.
3. Verifica que `build/libs/hammersunbound-1.0b2-sources.jar` también se genera.

### Test 6 - Particulas AOE visibles con multiplicadores 1.0x
Problema solucionado:
Las particulas del AOE eran demasiado escasas con la configuracion por defecto `1.0x`, haciendo que algunos impactos se vieran casi vacios.

Como comprobarlo:
1. Abre `config/hammersunbound/client.json`.
2. Confirma que `aoeParticleCountMultiplier`, `aoeParticleDensityMultiplier` y `aoeParticleHeightMultiplier` estan en `1.0`.
3. Entra al juego y realiza un critico con un WarHammer.
4. Verifica que el impacto AOE genera una nube/ring de particulas claramente visible, con mayor cantidad, densidad y altura que antes.

### Test 7 - Boton Changelog estilizado en MainMenu
Problema solucionado:
El boton `Changelog` del `MainMenu` usaba apariencia vanilla y no seguia el estilo visual de las GUIs del mod.

Como comprobarlo:
1. Inicia el juego hasta el `MainMenu`.
2. Verifica que el boton `Changelog` aparece en la esquina superior izquierda con fondo oscuro, borde, acento verde y texto estilizado.
3. Haz click en el boton.
4. Verifica que abre la pantalla de changelog del mod.

### Test 8 - Attack Speed positivo en GUI y config nueva
Problema solucionado:
El slider de `Attack Speed` mostraba valores negativos de atributo vanilla, lo que era confuso para usuarios y pack makers.

Como comprobarlo:
1. Abre la GUI de configuracion del mod.
2. En cualquier WarHammer o SpikeHammer, revisa el campo `Attack Speed`.
3. Verifica que el valor mostrado es positivo, por ejemplo `0.8` o `1.2`.
4. Guarda la configuracion.
5. Abre `config/hammersunbound/items.json` y confirma que `attackSpeed` se guarda como valor positivo.
6. En el juego, revisa el tooltip del martillo y confirma que coincide con el valor positivo configurado.

### Test 9 - Cooldowns y timers configurables en segundos
Problema solucionado:
Los cooldowns y timers se configuraban en ticks, obligando al usuario a convertir manualmente valores como `40` ticks a `2s`.

Como comprobarlo:
1. Abre la GUI de configuracion del mod.
2. Verifica que `Skill Cooldown`, `Stun Duration`, `AOE Stun Duration`, `Bleed Duration`, `Bleed Tick Interval`, `Bleed Decay` y `Pact Drain Interval` muestran `(s)`.
3. Cambia `Skill Cooldown` a `2.0s` y guarda.
4. Abre `config/hammersunbound/items.json`.
5. Confirma que se guarda como `skillCooldownSeconds: 2.0` y no como `40` ticks.
6. En combate, verifica que el cooldown dura aproximadamente 2 segundos.

### Test 10 - Configuracion cliente separada por categoria real
Problema solucionado:
La GUI agrupaba opciones de UI y visuales de combate dentro de `Particles`, aunque no todas eran particulas.

Como comprobarlo:
1. Abre la GUI de configuracion del mod.
2. En la seccion `Client`, verifica que existen subsecciones separadas: `AOE Particles`, `Combat Visuals` y `UI`.
3. Guarda la configuracion.
4. Abre `config/hammersunbound/client.json`.
5. Verifica que las opciones se guardan en `aoeParticles`, `combatVisuals` y `ui`.

### Test 11 - Boton Changelog pequeno y configurable
Problema solucionado:
El boton de `Changelog` del `MainMenu` ocupaba demasiado espacio y no podia ocultarse desde la configuracion cliente.

Como comprobarlo:
1. Inicia el juego hasta el `MainMenu`.
2. Verifica que el boton ahora es mas pequeno y angosto, mostrando `HU Log`.
3. Abre la GUI de configuracion del mod.
4. En `Client > UI`, desactiva `Main Menu Changelog Button` y guarda.
5. Vuelve al `MainMenu` y verifica que el boton ya no aparece.
6. Abre `config/hammersunbound/client.json` y confirma `ui.showChangelogButton=false`.
