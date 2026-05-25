##  instruccones pre promt.
 - lee @context.md
 - crea plan antes de implementacion. los implement plans deben estar redactados en español...
 - si algo queda ambiguo hazme varias preguntas abiertass (con sugerencias y recomendados  + justificacion)
 - tambien dime qué problema solucionaste en el archivo @md/changetesting-version-test1.md (enumera version y numerro de test (varios por testing) ) y como comprobarlo (en el archivo).

## promt
"audit"

## plan

Objetivo
- Añadir una ventana emergente (popup) en el `MainMenu` que se muestre la primera vez que el jugador abre el juego con el mod `hammersunbound`.

Comportamiento esperado
- El popup muestra una advertencia: el mod está en desarrollo e inestable para modpacks, con un enlace a GitHub Issues.
- Botones: `Ok` — marca una preferencia persistente y el popup no vuelve a mostrarse; `Salir` — cierra el popup sólo para la sesión actual (volverá a mostrarse la próxima vez que se inicie el juego con el mod).
- El popup debe usar el sistema de GUI propio del mod para mantener consistencia visual.

Requisitos funcionales
- Detectar "primera ejecución" del mod en una instalación concreta (cliente).
- Persistir la preferencia "no mostrar de nuevo" en la configuración cliente del mod.
- Mostrar el popup en el `MainMenu` del cliente, sin bloquear otros subsistemas.
- Incluir traducciones/strings para el texto y el enlace (soporte para al menos `en` y `es`).

Diseño técnico (alto nivel)

Contexto del proyecto (resumen de lectura)
- El mod ya tiene un sistema de configuración gestionado por `ConfigManager` y una configuración cliente en `src/main/java/com/x4yi/hammersunbound/config/ClientConfig.java` que se carga desde `config/hammersunbound/client.json` via `ConfigManager.loadClient()`.
- El lector de changelogs está implementado en `GuiChangelogScreen` y contiene utilidades para parsear y renderizar Markdown (métodos `getChangelogLines(...)` y `drawWrappedMarkdown(...)`) que debemos reutilizar para el popup (respeta tags por idioma como `[ES]...[/ES]`).
- La inicialización cliente registra `HammerClientHandler` en el `ClientProxy`; es el lugar natural para añadir un listener `GuiOpenEvent` que detecte cuando se abre `GuiMainMenu` y muestre el popup.

Diseño técnico (ajustado)
- Persistencia: añadir una propiedad en la configuración cliente gestionada por `ClientConfig` (p.ej. `public static boolean showDevWarning = true`) y leer/escribirla mediante `ConfigManager.loadClient()` y `ConfigManager.save()`.
- Fuente del contenido: crear `assets/hammersunbound/dev/popup.md` en `src/main/resources` y usar la misma convención de localización dentro del archivo (`[ES]...[/ES]`, `[EN]...[/EN]`).
- Parser/renderer: reutilizar `GuiChangelogScreen.getChangelogLines(...)` para extraer la sección del idioma y `GuiChangelogScreen.drawWrappedMarkdown(...)` (o extraer ambos a una utilidad compartida `MarkdownRenderer`) para renderizar el texto dentro del popup manteniendo el estilo.
- Hook en MainMenu: registrar un `GuiOpenEvent` en `HammerClientHandler` (o crear `MainMenuHandler`) que, al detectar `GuiMainMenu`, abra `DevWarningPopup` si `ClientConfig.showDevWarning == true` y si no se ha mostrado ya en la sesión actual.
- Comportamiento de botones: `Ok` → poner `ClientConfig.showDevWarning = false`; llamar a `ConfigManager.save()` (es síncrono y rápido) y cerrar el `GuiScreen`. `Salir` → simplemente cerrar el `GuiScreen` sin persistir.

Pasos de implementación (secuenciales y concretos)
1. Crear recurso `src/main/resources/assets/hammersunbound/dev/popup.md` con bloques por idioma.  
2. Añadir `public static boolean showDevWarning = true;` a `src/main/java/com/x4yi/hammersunbound/config/ClientConfig.java` y leer el valor en `load()` desde `ConfigManager.loadClient()` (buscar/crear la sección `ui` o `meta`).  
3. Modificar `ConfigManager.createDefaultClient()` para incluir el valor por defecto `ui.showDevWarning=true`, y actualizar `ConfigManager.save()` para escribir `ui.showDevWarning` en el JSON.
4. Implementar la clase GUI `src/main/java/com/x4yi/hammersunbound/client/gui/DevWarningPopup.java` extendiendo `GuiBaseScreen`, reutilizando la lógica de `GuiChangelogScreen` para renderizar el Markdown (copiar/extraer `getChangelogLines` + `drawWrappedMarkdown`).  
5. Añadir un listener a `HammerClientHandler` que capture `net.minecraftforge.client.event.GuiOpenEvent` y cuando la GUI abierta sea `net.minecraft.client.gui.GuiMainMenu` muestre `DevWarningPopup` si `ClientConfig.showDevWarning` es `true` y la sesión no la ha mostrado aún.
6. Implementar la apertura de enlaces: el parser debe detectar links en la MD (si ya no hay soporte, renderizar la URL como texto y al hacer click usar `java.awt.Desktop.getDesktop().browse(new URI(url))`).  
7. Probar manualmente los casos (ver sección "Comprobación" actualizada abajo).
8. Añadir entrada en `src/main/resources/assets/hammersunbound/dev/popup.md` y registrar los tests en `md/changetesting-version-test1.md`.

Notas de integración y decisiones recomendadas
- Usar la convención `[ES]...[/ES]` y `[EN]...[/EN]` dentro de `popup.md` para reutilizar la función `getChangelogLines(...)`.  
- Guardar la preferencia en la sección `ui` del JSON cliente para mantener separación de responsabilidades (ej: `client.json` -> `{ "particles": {...}, "ui": { "showDevWarning": true } }`).
- Mostrar el popup sólo cuando `GuiMainMenu` se abra por primera vez en la sesión para evitar spam si el usuario abre/cierra repetidamente el menú.


Pasos de implementación (secuenciales)
1. Definir los strings/ID de localización necesarios y añadirlos a las tablas de idioma del mod (texto del título, cuerpo, botones, enlace).  
2. Diseñar componente GUI: crear la clase `DevWarningPopup` que use el sistema de GUI propio del mod (título, cuerpo con enlace clicable, dos botones).  
3. Añadir la comprobación en el flujo de inicialización del `MainMenu` para instanciar y mostrar `DevWarningPopup` si corresponde.  
4. Implementar persistencia: leer/escribir la bandera `showDevWarning` en la configuración cliente del mod (asegurar guardado sin bloquear hilo principal).  
5. Manejar eventos de los botones: `Ok` → persistir `false` y cerrar; `Salir` → cerrar sin persistir.  
6. Añadir tests manuales y pasos de verificación (ver sección "Comprobación").  
7. Actualizar documentación y registrar el cambio en el archivo de testing correspondiente (`md/changetesting-version-test1.md`) con versión y número(s) de prueba.

Comprobación / Criterios de aceptación
- En una instalación limpia del mod, al abrir el juego y llegar al `MainMenu`, el popup aparece exactamente una vez hasta que se pulsa `Ok`.  
- Si se pulsa `Salir`, el popup se cierra, pero al reiniciar el juego vuelve a aparecer hasta que se pulsa `Ok`.  
- Si se pulsa `Ok`, la preferencia persiste y en posteriores inicios el popup NO aparece.  
- El popup usa los estilos y componentes del sistema GUI del mod (ver comparativa visual).  
- El enlace a GitHub Issues es clicable y abre la URL en el navegador (cliente).

Casos de prueba manuales (rápida guía)
- Test 1: Instalación nueva — iniciar juego → verificar que aparece popup.  
- Test 2: Pulsar `Salir` — cerrar popup → reiniciar juego → verificar que vuelve a aparecer.  
- Test 3: Pulsar `Ok` — cerrar popup → reiniciar juego → verificar que ya no aparece.  
- Test 4: Comprobar archivo de configuración (`config/hammersunbound/client.json`) contiene `showDevWarning=false` tras `Ok`.

Preguntas abiertas (por favor responde)
- ¿Dónde prefieres almacenar la bandera persistente: en el archivo JSON de `config/` del mod (`config/hammersunbound/client.json`) o usando el sistema de `Configuration`/Forge si ya lo usan? en ClientConfig
- ¿Quieres que el texto del popup incluya un botón/link directo que abra el navegador en la URL de Issues, o que copie la URL al portapapeles? que abra el navegador.  
- ¿Deseas soporte de localización para más idiomas desde la primera implementación (por ejemplo `es` + `en`), o implementamos sólo el idioma base y añadimos traducciones después? si, en un mismo .md siguiendo la logica de nuestro parser de Markdown 
Archivos y símbolos a modificar (estimación)
- GUI: `src/main/java/com/x4yi/.../gui/DevWarningPopup.java` (nueva clase).  
- Integración MainMenu: `src/main/java/com/x4yi/.../client/MainMenuHandler.java` (o la clase que inicializa el menú).  
- Configuración: lectura/escritura en el manejador de configuración del mod (ej: `ClientConfig.java`).  
- Localización: ficheros `assets/hammersunbound/dev/popup.md`
Registro de cambios y testing

Fin del plan.