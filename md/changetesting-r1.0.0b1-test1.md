# Reporte de Pruebas y Cambios - Versión r1.0.0b1 - Test 1

## Problemas Solucionados
1. **Conflicto de Nombres en Modelos 3D**:
   - **Descripción**: Los nuevos modelos 3D exportados desde Blockbench tienen nombres de archivo sin guiones bajos (ej. `warhammerwood.json`, `spikehammerstone.json`), mientras que los nombres de registro internos de los ítems en Java contienen guiones bajos (`warhammer_wood`, `spikehammer_stone`). Esto impedía que Forge localizara y cargara los modelos 3D correctos.
   - **Solución**: Se adaptó el código Java en `ClientProxy.registerModels` para registrar dinámicamente las localizaciones de recursos de los modelos removiendo los guiones bajos.

2. **Error de Ruta de Texturas (items vs item)**:
   - **Descripción**: Los archivos JSON de los nuevos modelos Blockbench hacen referencia a texturas en la ruta `hammersunbound:items/` (en plural, ej. `hammersunbound:items/wood`), pero la carpeta de texturas real en el código es `textures/item/` (en singular). Esto provocaba que los mangos de los martillos se renderizaran con la textura de error (cuadrícula morada y negra).
   - **Solución**: Se implementó un paquete de recursos programático del lado del cliente (`HammerResourcePack`) que intercepta las peticiones de recursos dirigidas a `textures/items/` y las redirige de manera transparente a la ruta correcta `textures/item/` en la ruta de clases del mod, sin necesidad de modificar los archivos JSON exportados de Blockbench.

## Cómo Comprobarlo
1. **Compilación**: Ejecutar `./gradlew compileJava` para verificar que el código compila sin errores.
2. **Prueba en Juego**:
   - Ejecutar `./gradlew runClient` para iniciar Minecraft.
   - Entrar a un mundo de prueba.
   - Ir al inventario creativo o craftear los martillos de guerra (`WarHammers`) y martillos de picos (`SpikeHammers`) de diferentes materiales (Madera, Piedra, Hierro, Oro, Diamante).
   - Equipar los martillos y verificar visualmente en primera y tercera persona que:
     - El modelo 3D del martillo se renderiza correctamente (sin textura de error).
     - La textura del mango (madera/planks) se muestra de manera correcta y con sus colores/opacidades correspondientes.

---

# Reporte de Pruebas y Cambios - Versión r1.0.0b1 - Test 2

## Problemas Solucionados
1. **Rework Completo de Aturdimiento (Stun)**:
   - **Descripción**: El aturdimiento anterior dependía de los efectos vanilla de Lentitud y Fatiga de Minería, lo cual no inhabilitaba el movimiento de los enemigos por completo ni bloqueaba el input del mouse o del teclado del jugador.
   - **Solución**: Se registró una poción personalizada `STUN` (`ModPotions.STUN`). Se implementó un bloqueo de IA de navegación y velocidad física en `HammerCombatHandler.onLivingUpdate` para paralizar mobs y congelar la posición del jugador afectado. En el cliente, se interceptó `InputUpdateEvent` para anular teclas WASD, salto y sigilo, y `RenderTickEvent` para anular la rotación del mouse (`deltaX = 0`, `deltaY = 0`) y bloquear los ángulos yaw/pitch originales.

2. **Gravedad y Comportamiento Físico de Partículas AOE**:
   - **Descripción**: Las partículas AOE tenían un desvanecimiento gradual al nacer (fade-in) y se movían lateralmente sin verse afectadas por la gravedad física, perdiendo el impacto visual de impacto pesado en el suelo.
   - **Solución**: Se modificó `ParticleHammerAOE` para que las partículas se impulsen hacia arriba con velocidades radiales y verticales aleatorias al nacer. Se añadió la física de gravedad en `onUpdate` y se eliminó la transición gradual de aparición inicial para que la opacidad inicie al máximo y solo realice un desvanecimiento suave (fade-out) en la última fase de su vida.

## Cómo Comprobarlo
1. **Compilación**: Ejecutar `./gradlew compileJava` para verificar que el código compila sin errores.
2. **Prueba en Juego (Mecanica de Stun)**:
   - Golpear críticamente a un mob con un Martillo de Guerra. El mob debe quedar completamente inmóvil, sin poder caminar ni girar su cabeza para perseguir.
   - Si se aplica a un jugador, verificar que las teclas WASD, barra espaciadora, shift, y el movimiento de mirada del mouse quedan completamente inmovilizados durante la duración del efecto.
3. **Prueba en Juego (Partículas AOE)**:
   - Golpear críticamente a un enemigo con un Martillo de Guerra. Observar que las partículas surgen del suelo en forma de fuente radial, ascienden describiendo una parábola descendente por gravedad, y desaparecen con un fade-out suave al final sin tener fade-in inicial.

---

# Reporte de Pruebas y Cambios - Versión r1.0.0b1 - Test 3

## Problemas Solucionados
1. **Optimización de Memoria RAM de Gradle**:
   - **Descripción**: La tarea `runClient` reservaba 2GB de RAM por defecto, lo cual era excesivo y podía provocar el cierre abrupto del demonio en sistemas con restricciones de recursos.
   - **Solución**: Se añadieron los argumentos `jvmArgs '-Xms800M', '-Xmx800M'` en la configuración de la tarea `client` en `build.gradle` para limitar la asignación a 800MB.

2. **Ocultación del Efecto Stun en la Interfaz (Inventario/HUD)**:
   - **Descripción**: El efecto visual del Stun se renderizaba en el inventario del jugador, textos y en la interfaz general (HUD), lo cual no es coherente con una parálisis física/mecánica del mod.
   - **Solución**: Se sobrescribieron los métodos `shouldRender`, `shouldRenderInvText` y `shouldRenderHUD` en `StunPotion.java` retornando `false`.

3. **Bloqueo Absoluto de Mobs y Parálisis Nativa**:
   - **Descripción**: Mobs afectados por el Stun aún intentaban rotar su cuerpo, cabeza y realizar saltos.
   - **Solución**: Se configuró un modificador de lentitud nativo de `-100%` en la velocidad de movimiento en el constructor de `StunPotion` usando `MOVEMENT_SPEED`. En `HammerCombatHandler.onLivingUpdate`, se congelaron las rotaciones de Yaw, Pitch, YawHead y RenderYawOffset a sus valores anteriores, se limpiaron los objetivos de ataque y se anuló el movimiento hacia arriba (salto).

4. **Bloqueo Físico del Mouse del Jugador via StunMouseHelper**:
   - **Descripción**: Anteriormente, el cursor/cámara en primera persona del jugador no quedaba bloqueado por completo ante la lectura de la velocidad física del mouse por parte de Minecraft.
   - **Solución**: Se implementó la clase `StunMouseHelper` extendiendo `MouseHelper` para forzar `deltaX = 0; deltaY = 0;` en `mouseXYChange()` cuando el jugador está aturdido. En `HammerClientHandler`, se envuelve `mc.mouseHelper` en el helper personalizado al conectar y en cada actualización de vida.

5. **Alineación de Anillo AOE, UV Cut de Bloque y Sombreado Realista**:
   - **Descripción**: Las partículas AOE eran dispersas y desordenadas (spread aleatorio), no tenían UV Cut (se renderizaba la textura completa del bloque reducida en lugar de fragmentos pequeños del bloque) y carecían del sombreado de luz/sombra del mundo.
   - **Solución**: Se actualizó `AOEParticleSpawner` para distribuir las partículas en forma de anillo perfecto exactamente sobre la circunferencia del radio de impacto. Se implementó una búsqueda vertical hacia abajo en el terreno en cada punto de la circunferencia para iniciar el spawn de forma tridimensional sobre el relieve del suelo. Se modificó `ParticleHammerAOE` para calcular el UV Cut exacto de `ParticleDigging` y el sombreado/color del bloque del mundo, aplicando gravedad física y movimiento radial hacia afuera.

## Cómo Comprobarlo
1. **Verificar Memoria**:
   - Ejecutar `./gradlew runClient` y comprobar en los logs o el monitor del sistema que Minecraft se ejecuta con un límite de 800MB.
2. **Verificar Ocultación de Potion en Inventario**:
   - Al ser golpeado por un martillo de guerra o recibir el efecto de aturdimiento, abrir el inventario y constatar que no aparece ningún recuadro de efecto de poción activo.
3. **Verificar Parálisis Total de Mobs**:
   - Aplicar el stun a un mob (zombie, creeper, etc.). El mob no debe caminar, no debe saltar, no debe apuntar con su mirada al jugador ni poder girar su cabeza en absoluto.
4. **Verificar Bloqueo del Mouse**:
   - Al estar aturdido en juego, intentar mover el mouse físico. La cámara en primera persona debe permanecer completamente congelada y fija sin tirones.
5. **Verificar Anillo AOE y UV Cut**:
   - Golpear un bloque o enemigo en una zona con relieve o variaciones de iluminación. Observar que las partículas se generan como un anillo perfecto delineando la circunferencia en el suelo. Cada partícula debe mostrar un fragmento (UV Cut) del bloque sobre el que nació (tierra, césped, piedra, etc.) y tener la coloración y brillo del bloque y luz circundante.
