# Hammers Unbound r1.0b5

[ES]
## Resumen de Cambios
Esta versión introduce una estación de crafteo completamente nueva exclusiva para los martillos del mod, con una interfaz interactiva de estilo oscuro, integración con JEI y varias mejoras de usabilidad.

### Dependencias (Importante)
- **Nueva Librería X4UI:** Todo el sistema de menús e interfaces gráficas del mod ha sido extraído a su propia librería base llamada **X4UI**. 
- **Requisito:** A partir de esta versión, es estrictamente necesario descargar e instalar el mod X4UI junto a Hammers Unbound para poder jugar. [Descargar X4UI r1.0b1](https://github.com/X4yi/X4UI/releases/latest)

### Nueva Mesa de Forja (Hammer Forge)
- **Bloque Exclusivo:** Los Warhammers y Spikehammers ya no se fabrican en la mesa de crafteo Vanilla. Ahora requieren su propia estación especializada: la **Hammer Forge**.
- **Receta de Crafteo:** Puedes fabricar la mesa utilizando 1 bloque de hierro, 2 lingotes de hierro y 1 mesa de trabajo en el centro.

### Interfaz de Usuario (UI) y Experiencia
- **Diseño Personalizado:** La Hammer Forge cuenta con una interfaz de estética oscura.
- **Pestañas y Selectores:** La navegación se realiza mediante pestañas exteriores para cambiar entre el tipo de martillo, y un selector horizontal en la parte superior que muestra los distintos materiales disponibles.
- **Lista de Ingredientes Inteligente (Flow Layout):** La receta muestra los ítems necesarios en un formato de cuadrícula adaptable. Para ahorrar espacio y mantener un diseño limpio, solo se muestran los íconos y la cantidad requerida (puedes ver el nombre del ítem al pasar el cursor por encima).
- **Inventario Interactivo Completo:** El inventario del jugador está integrado nativamente en el fondo de la interfaz, permitiendo arrastrar, soltar y organizar ítems de manera fluida y sin cerrar la mesa de forja.
- **Autoselección:** Al cambiar de tipo de martillo, la interfaz buscará automáticamente en tu inventario para preseleccionar un material del cual tengas suficientes recursos.
- **Corrección de Escala (Hammer Forge):** Resolución base ajustada a 210x220 para evitar desbordamientos y cortes en escalas de GUI superiores (Escala 4).

### Nueva Guía In-Game y Mejoras de Interfaz
- **Manual Integrado:** Se añadió un completo manual interactivo dentro del juego. Ahora puedes consultar mecánicas, características de los martillos y detalles de configuración sin salir de tu partida.
- **Accesibilidad:** La nueva guía es accesible directamente desde botones dedicados en la pantalla de Configuración y en el Changelog.
- **Selector de Idioma Dinámico:** El botón para cambiar de idioma (Inglés/Español) ha sido rediseñado a una moderna "pastilla" deslizable en el borde exterior de la pantalla, reemplazando a las antiguas banderas estáticas.

### Menú de Configuración
- **Restablecimiento Individual y de Categoría:** Añadido un botón "R" en cada opción, y en el encabezado de cada sección, para restaurar los valores predeterminados de fábrica de forma aislada o grupal.
- **Precisión Numérica:** Los ajustes tipo slider ahora incorporan un campo de texto interactivo para ingresar valores exactos.
- **Corrección de Bug (Enteros):** Se solucionó un fallo técnico en el controlador de la interfaz que impedía leer y escribir ajustes de tipo entero (como la Durabilidad), permitiendo ahora configurarlos con normalidad.


### Integración con Mods
- **Soporte para Just Enough Items (JEI):** Todas las recetas de la Hammer Forge ahora pueden ser consultadas directamente desde JEI, con su propia categoría personalizada.


### Mejoras al Skybreaker (Warhammer)
- **Salto:** Al mirar hacia arriba, usar Shift + Clic Derecho impulsa al jugador 15 bloques hacia arriba (cancela el daño por caída).
- **Knockback:** Inmunidad al empuje enemigo mientras se cae tras usar la habilidad.
- **Impacto (Ground Slam):** Aterrizar manteniendo Shift genera daño en área y aturdimiento centrados en el jugador (50% de las estadísticas base del arma).
- **Cooldown:** El tiempo de recarga del Skybreaker ahora es independiente para cada material (30s por defecto).

### Corrección de Errores
- **Crash en Servidores:** Se solucionó un problema crítico que causaba que el servidor se cerrara de forma inesperada al intentar generar las partículas visuales de las habilidades en área.
- **Entidades Fantasma:** Resuelto un grave problema de desincronización en servidores donde golpear a enemigos con habilidades de área a veces generaba monstruos "inmortales" para el jugador.
- **Precisión del Spikehammer:** Mejorada drásticamente la puntería del efecto del Spikehammer para asegurar que siempre impacte exactamente en la dirección a la que estás mirando.
- **Aislamiento de Configuración:** Al unirse a un servidor multijugador, la configuración de ese servidor ya no alterará o sobreescribirá de forma permanente tu configuración personal al volver a un mundo de un jugador.

### Rendimiento y Optimización
- **Fluidez Multijugador:** El efecto del *Blood Pact* fue completamente reconstruido internamente para evitar que sature la conexión de internet de los jugadores, eliminando los tirones de red (lag spikes).
- **Carga Gráfica y Fugas de Memoria:** Implementado *Frustum Culling* en las partículas del mod (ahora no se renderizan si no las estás mirando, ahorrando recursos de la tarjeta gráfica) y se ha eliminado una fuga de memoria que ralentizaba el juego en partidas largas.
- **Mejora de CPU:** Reducción sustancial del estrés en el servidor generado por el chequeo constante de estados de combate, haciendo que el mod sea mucho más liviano en mundos poblados.

### Interfaz del Jugador (HUD)
- **Indicador de Enfriamiento:** Se añadió un HUD en pantalla que muestra la barra de recarga de la habilidad del arma equipada.

[/ES]

[EN]
## Changelog Summary
This version introduces a brand new, exclusive crafting station for the mod's hammers, featuring a dark-themed interactive interface, JEI integration, and several usability improvements.

### Dependencies (Important)
- **New X4UI Library:** The entire menu and graphical interface system of the mod has been extracted into its own core library called **X4UI**.
- **Requirement:** Starting from this version, it is strictly required to download and install the X4UI mod alongside Hammers Unbound to play. [Download X4UI r1.0b1](https://github.com/X4yi/X4UI/releases/latest)

### New Hammer Forge
- **Exclusive Block:** Warhammers and Spikehammers are no longer crafted in the standard Vanilla crafting table. They now require their own specialized station: the **Hammer Forge**.
- **Crafting Recipe:** You can craft the forge using 1 iron block, 2 iron ingots, and 1 crafting table in the center.

### User Interface (UI) & Experience
- **Custom Design:** The Hammer Forge features a dark-themed interface.
- **Tabs and Selectors:** Navigation is done through outer tabs to switch between hammer types, and a horizontal selector at the top showcasing the different available materials.
- **Smart Ingredient List (Flow Layout):** The recipe displays required items in an adaptive grid format. To save space and maintain a clean design, only the 3D icons and the required amount are shown (you can see the item's name by hovering over it).
- **Fully Interactive Inventory:** The player's inventory is natively integrated into the bottom of the interface, allowing for seamless drag, drop, and organization of items without closing the forge table.
- **Auto-selection:** When switching hammer types, the interface will automatically scan your inventory to pre-select a material you have enough resources for.
- **Scale Fix (Hammer Forge):** Base resolution adjusted to 210x220 to prevent overflowing and clipping on high GUI scales (Scale 4).

### New In-Game Guide & UI Improvements
- **Integrated Manual:** Added a complete, fully interactive manual inside the game. You can now consult hammer mechanics, features, and configuration details without leaving your session.
- **Accessibility:** The new guide can be easily accessed via dedicated buttons in both the Configuration and Changelog screens.
- **Dynamic Language Selector:** The language toggle button (English/Spanish) has been completely redesigned into a modern sliding "pill" on the outer edge of the UI, replacing the old static flags.

### Configuration Menu
- **Individual and Category Reset:** Added an "R" button to each individual setting and to each category header for restoring factory defaults either independently or in groups.
- **Numerical Precision:** Slider-based settings now include an interactive text field for inputting exact numbers.
- **Bug Fix (Integer Values):** Fixed an internal UI controller flaw that prevented reading and writing integer settings (like Durability), allowing them to be configured normally.


### Mod Integration
- **Just Enough Items (JEI) Support:** All Hammer Forge recipes can now be viewed directly through JEI, complete with its own custom category.



### Skybreaker Improvements (Warhammer)
- **Jump:** Looking up, pressing Shift + Right-Click launches the player 15 blocks into the air (cancels fall damage).
- **Knockback:** Immune to enemy knockback while falling after using the ability.
- **Ground Slam:** Landing while holding Shift triggers radial Area of Effect damage and stun centered on the player (deals 50% of base weapon stats).
- **Cooldown:** The Skybreaker cooldown is now independent for each material (30s by default).

### Bug Fixes
- **Server Crash:** Fixed a critical issue that caused dedicated servers to crash when attempting to spawn the visual particles for Area of Effect abilities.
- **Ghost Entities:** Resolved a severe desync bug in multiplayer where attacking enemies with AoE skills would sometimes leave "immortal" monsters on the player's screen.
- **Spikehammer Precision:** Drastically improved the targeting logic of the Spikehammer's effect to ensure it always hits exactly where you are aiming.
- **Configuration Isolation:** Joining a multiplayer server will no longer permanently overwrite your personal mod settings when returning to a singleplayer world.

### Performance & Optimization
- **Multiplayer Smoothness:** The *Blood Pact* visual effect was internally rebuilt to stop flooding the players' internet connection, completely eliminating the classic "lag spikes" when used.
- **Graphics & Memory Leaks:** Implemented *Frustum Culling* for custom particles (saving GPU resources by hiding them when out of view) and fixed a major memory leak that degraded performance over long play sessions.
- **CPU Overhead:** Substantially reduced server stress caused by constant combat state checks on monsters, making the mod incredibly lightweight in highly populated worlds.

### Player HUD
- **Cooldown Indicator:** Added an on-screen HUD element that displays the skill cooldown progress for the equipped weapon.

[/EN]
