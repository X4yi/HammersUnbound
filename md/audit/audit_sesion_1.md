# Auditoría de Proyecto - Sesión 1

## 1. Resumen de la Auditoría
Esta auditoría ha examinado el nivel de seguridad, eficiencia, optimización, escalabilidad, buenas prácticas y posibles vulnerabilidades en la arquitectura base y flujo del mod *Hammers Unbound*. Se han analizado en detalle los sistemas de configuración, combate, y renderizado de interfaces (GUIs).

## 2. Errores de Diseño y "God Classes" (Antipatrones)
Se han identificado múltiples clases que sufren del antipatrón "God Class" (clase dios) que concentran demasiadas responsabilidades y violan principios SOLID (específicamente SRP - Principio de Responsabilidad Única, y OCP - Principio de Abierto/Cerrado).

### A. `GuiConfigScreen.java` (CRÍTICO)
- **Problema:** Posee más de 580 líneas de código y concentra toda la lógica de dibujo, animación de UI, y lo más grave: **mapeos duros ("hardcoded")** de cada propiedad de configuración. Los métodos `getFloatValue`, `setFloatValue`, `getBooleanValue` y `setBooleanValue` son gigantescos bloques `switch/case` que deben ser actualizados manualmente cada vez que se agrega una configuración nueva.
- **Consecuencia:** Pésima escalabilidad, alta propensión a errores de tipo "copy-paste" y acoplamiento extremo entre la Capa Visual (GUI) y el Modelo (Configs).
- **Propuesta de Mejora:** Implementar un sistema basado en interfaces o reflexión (por ejemplo, `IConfigBinder` o `ConfigProperty`) que vincule los campos visuales directamente con su origen de datos de manera agnóstica.

### B. `GuiChangelogScreen.java` (MODERADO)
- **Problema:** Mezcla el renderizado de la interfaz con un parser complejo de Markdown (`drawWrappedMarkdown`) y lógica de red.
- **Propuesta de Mejora:** Extraer el motor de renderizado Markdown a una utilidad estática separada (ej. `MarkdownRenderer`) que tome un String y unas coordenadas para dibujar.

### C. `BloodPactEffect.java` (ALTO)
- **Problema:** Concentra el ticking, mecánicas puras (Ping-Pong, Burst, repulsión), control de red (lanzando paquetes manualmente `ModNetworkHandler.INSTANCE.sendTo...`) y serialización/deserialización NBT masiva.
- **Propuesta de Mejora:** Mover la serialización a un `Serializer` dedicado. Centralizar las llamadas de red para no acoplar el efecto puro del servidor con implementaciones de paquetes del mod. 

## 3. Implementaciones Incompletas y Errores Lógicos
- **Método Vacío en `BloodPactEffect.java`:** El método `private void drain()` está declarado pero su cuerpo está completamente vacío (línea 275). La lógica real de curación está en `onHitTarget()`, por lo que el método `drain()` sobra, o bien la lógica periódica de drenaje pasivo quedó inconclusa.
- **Flujo de Daño en Área (AoE) Desubicado:** En `HammerCombatHandler.java`, la función estática `triggerPacketAoEAttack` maneja de forma explícita el tamaño del ataque AoE para *SpikeHammer*. Esto rompe el polimorfismo. La lógica debería pertenecer a una interfaz `IAoEWeapon` en los ítems, y el manejador de combate solo debería invocar `weapon.performAoE(player, target)`.
- **Sincronía de Guardado de Configs:** En `GuiConfigScreen.java`, el método `saveAndClose()` invoca a `ConfigManager.save()`. Al ser síncrono e involucrar I/O (JSON parsing), puede causar un pequeño *freeze* en el cliente, además de que `ConfigManager.save()` regenera el objeto entero.

## 4. Ineficiencias de Código
- **Búsqueda Repetitiva de Capabilities:** En `HammerCombatHandler.java` (`onLivingHurt`), se realizan múltiples comprobaciones `entity.hasCapability()` seguidas inmediatamente de un `entity.getCapability()` del mismo tipo. Es ineficiente. Se debe guardar el resultado y usar `if(cap != null)`.

## 5. Conclusión
El proyecto funciona de manera estable tras las últimas correcciones (ej. lazy loading de JSON), pero la escalabilidad técnica a largo plazo se encuentra muy comprometida por la falta de abstracción en las interfaces gráficas y eventos de combate. Se recomienda un refactor urgente de las God Classes para sanear la arquitectura.
