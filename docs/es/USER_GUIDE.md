# Guía del Usuario: Hammers Unbound

Hammers Unbound añade martillos pesados con mecánicas de física de combate personalizadas, efectos de estado y sincronización de red en Minecraft 1.12.2.

---

## 🔨 WarHammer

El **WarHammer** es un arma de control de masas diseñada para deshabilitar grupos de objetivos.

### Mecánicas

#### 1. Efecto de Aturdimiento (Stun)
Los golpes críticos aplican el efecto **Stun** (registro: `hammersunbound:stun`):
*   **Inmovilización:** La velocidad de movimiento del objetivo se reduce en un 100%. La gravedad aplica, pero el movimiento horizontal y los saltos se deshabilitan.
*   **IA Desactivada:** Los mobs limpian sus rutas de navegación activa y sus estados de ataque.
*   **Bloqueo de Cámara:** Los jugadores aturdidos no pueden rotar la cámara (`StunMouseHelper` intercepta la entrada del ratón).
*   **Bloqueo de Ángulos:** Los ángulos de rotación (yaw/pitch) del objetivo se bloquean en el servidor y se fuerzan en el cliente para evitar desincronizaciones de red.

#### 2. Impacto Terrestre (AOE)
Los golpes críticos provocan un impacto de área:
*   **Daño y Stun:** Inflige daño y aturde a los objetivos secundarios dentro del radio de configuración.
*   **Visuales:** Las partículas de rotura de bloques se generan según la textura del bloque debajo del objetivo.

---

## 🗡️ SpikeHammer

El **SpikeHammer** se enfoca en daño por tiempo, acumulación de sangrado y manipulación de sangre.

### Mecánicas

#### 1. Efecto de Sangrado (Bleeding)
*   **Aplicación:** Los ataques en sprint o completamente cargados aplican Sangrado de Nivel 1. Los golpes críticos acumulan niveles adicionales.
*   **Daño:** Daño mágico periódico que ignora armadura. Niveles más altos de sangrado tiquean más rápido pero decaen rápidamente.

#### 2. Pacto de Sangre (BloodPact)
Hacer click derecho sobre un objetivo activa el **BloodPact**:
*   **Enlaces:** Vincula al jugador con hasta 3 entidades cercanas.
*   **Robo de Vida:** Convierte un porcentaje del daño infligido a objetivos pactados en curación para el jugador.
*   **Campo de Repulsión:** Repele continuamente a enemigos no pactados en un radio específico. Se aplica elevación vertical para romper la fricción del suelo.
*   **Atracción:** Los objetivos enlazados son arrastrados hacia el jugador y forzados a navegar hacia sus coordenadas.
*   **Rango de Ataque:** Otorga +1.0 de alcance de ataque (Reach) mientras esté activo.
*   **Barridos AoE:** Los clics izquierdos realizan un chequeo frontal tridimensional (caja de colisión AABB), dañando a los enemigos al frente (cooldown interno de 0.25s).

#### 3. Barra de Locura (Madness)
Golpear objetivos pactados carga el indicador de **Locura** (0-100):
*   **Carga:** +10 por ataque. Decae por -5 cada segundo (20 ticks).
*   **Bufos:** Otorga hasta un +20% de velocidad de movimiento y +50% de velocidad de ataque de forma proporcional.

#### 4. Explosión Sanguínea (Burst)
Acumula el daño infligido a los objetivos pactados. Cada 10 segundos, detona infligiendo **1/3 del daño total acumulado** a todos los objetivos enlazados.

#### 5. Habilidad Ping-Pong
Hacer click derecho hacia un objetivo pactado inicia el lanzamiento:
1.  **Ping:** El objetivo es lanzado 4 bloques hacia atrás en la dirección de la cámara del jugador.
2.  **Pong:** El objetivo es atraído rápidamente hacia el jugador.
3.  **Golpe de Retorno:** Golpear al objetivo durante el retorno inflige **+50% de daño** y reinicia el ciclo de Ping-Pong.
4.  **Penalizaciones:** Fallar el clic aplica un cooldown de 10s. Ser golpeado por el objetivo de retorno cancela el ciclo y penaliza la duración del pacto.
