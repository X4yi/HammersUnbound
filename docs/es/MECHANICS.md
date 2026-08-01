# Especificaciones Técnicas y Fórmulas

Detalle de ecuaciones, modificadores de atributos, físicas de impacto y estructura de red de **Hammers Unbound**.

---

##  Mecánica de Sangrado (Bleeding)

Se calcula en las entidades usando la Capability `IBleedingCapability`. El nivel del stack de sangrado ($L$) altera tres variables: daño por tic, duración y el intervalo entre tics.

### 1. Ecuación de Daño por Tic

$$\text{Daño} = \text{damagePerLevel} \times \left(0.5 + 0.25 \times (L - 1)\right)$$

### 2. Duración y Decaimiento
Cuando `ticksUntilDecay` llega a cero, $L$ se reduce en 1 y se reinicia el temporizador:

$$\text{ticksUntilDecay} = \text{baseDuration} \times \max\left(0.4, \; 2.0 - (L - 1) \times 0.3\right)$$

### 3. Ecuación de Intervalo entre Tics de Daño
Controla la frecuencia con la que se aplica el daño del sangrado:

$$\text{ticksUntilDamage} = \max\left(5, \; \text{configTickInterval} \times \text{multiplier}\right)$$

Donde el multiplicador depende de:
*   **Nivel 1:** $\times 4.0$
*   **Nivel 2:** $\times 2.5$
*   **Nivel 3:** $\times 1.5$
*   **Nivel 4:** $\times 1.0$
*   **Nivel 5+:** $\max\left(0.5, \; 1.0 - (L - 4) \times 0.1\right)$ (se limita a un mínimo de $\times 0.5$ a nivel 9)

---

##  Mecánica de Aturdimiento (Stun)

Aplicado bajo la poción `hammersunbound:stun`. Se ejecuta en el lado del servidor y del cliente:

### 1. Física e IA en el Servidor
*   **Atributo:** Aplica un modificador en `SharedMonsterAttributes.MOVEMENT_SPEED` de $-100\%$ (Valor: `-1.0D`, Operación: `2`).
*   **Movimiento:** En `LivingUpdateEvent`, anula los vectores de movimiento:
    *   `motionX = 0.0D`, `motionZ = 0.0D`
    *   `motionY = \min(motionY, 0.0D)` (permite caídas de gravedad, anula saltos)
*   **IA de Mobs:** Ejecuta `living.getNavigator().clearPath()` y `living.setAttackTarget(null)`.
*   **Ángulos:** Los ángulos de rotación (`rotationYaw`, `rotationPitch`, etc.) se registran en el NBT y se fuerzan constantemente para evitar desincronización de red.

### 2. Override de Ratón en el Cliente
*   **Control del Ratón:** `StunMouseHelper` reemplaza al `MouseHelper` por defecto.
*   **Intercepción:** Sobrescribe `mouseXYChange()` para forzar los deltas raw del ratón a `deltaX = 0` y `deltaY = 0` cuando el efecto de aturdimiento está activo, inhabilitando la cámara.

---

##  Mecánica de Pacto de Sangre (BloodPact)

Registrado en el jugador bajo la Capability `IBloodPactCapability`.

### 1. Modificadores de Atributos
Modificadores aditivos y multiplicativos basados en el valor de la **Locura** ($M$, escala de 0 a 100):

*   **Velocidad de Movimiento:**
    *   **UUID:** `6d7f022b-2a71-46ab-a021-e0e56b4685ff`
    *   **Ecuación:** $\text{Bufo} = \left(\frac{M}{100.0}\right) \times 0.20$ (Operación: `2`, hasta $+20\%$).
*   **Velocidad de Ataque:**
    *   **UUID:** `a1b6a7b3-c15c-4d57-b088-348f9fa4ea88`
    *   **Ecuación:** $\text{Bufo} = \left(\frac{M}{100.0}\right) \times 0.50$ (Operación: `2`, hasta $+50\%$).
*   **Rango de Ataque (Reach):**
    *   **UUID:** `2d7d8e6a-5a91-4d37-88cc-f5e94b26715f`
    *   **Ecuación:** $+1.0D$ constante (Operación: `0`).

### 2. Física del Campo de Sangre
*   **Atracción (Objetivos Pactados):**
    Añade velocidad física en dirección al jugador:
    
    $$\Delta v = \text{attractionForce} \times \text{Normalize}(\vec{pos}_{\text{jugador}} - \vec{pos}_{\text{mob}})$$
    
    Si el mob hereda de navegación por rutas, fuerza el destino de movimiento hacia el jugador con velocidad `1.25D`.
*   **Repulsión (Enemigos no Pactados):**
    Para mobs dentro del radio `fieldRadius` ($R$):
    
    $$\text{fuerza} = \left(\frac{R - \text{dist}}{R}\right) \times \text{repulsionForce}$$
    $$\Delta v_x = \text{fuerza} \times \frac{dx}{\text{dist}}, \quad \Delta v_z = \text{fuerza} \times \frac{dz}{\text{dist}}, \quad v_y = \max(v_y, 0.08D)$$

### 3. Explosión Sanguínea (Burst)
Cada 200 ticks (10s), detona daño mágico:

$$\text{Daño Burst} = \frac{\text{accumulatedDamage}}{3.0}$$

### 4. Habilidad Ping-Pong
*   **Fase 1 (Ping):** Velocidad fijada en $\vec{look}_{\text{jugador}} \times 0.95D$ y $v_y = 0.22D$ por 8 ticks.
*   **Fase 2 (Pong):** Velocidad fijada en:
    
    $$\vec{pull} = \text{Normalize}(\vec{pos}_{\text{jugador}} - \vec{pos}_{\text{mob}})$$
    $$v_x = \text{pull}_x \times 1.10D, \quad v_z = \text{pull}_z \times 1.10D, \quad v_y = \text{pull}_y \times 0.50D + 0.1D \quad \text{por 12 ticks.}$$

---

##  Barrido Frontal (AABB Collision)

Cálculo de barrido de daño AoE del SpikeHammer:
1.  **Centro de la Caja ($\vec{C}$):** Posicionado a una distancia de $1.5D$ bloques al frente de los ojos del jugador:

$$\vec{C} = \vec{pos}_{\text{jugador}} + (\vec{look} \times 1.5)$$

2.  **Caja AABB:** Expandida en tres dimensiones según el valor `aoeAttackSize` ($S$) del material:

$$\text{AABB} = [C_x - S, \; C_y - S, \; C_z - S] \times [C_x + S, \; C_y + S, \; C_z + S]$$

3.  **Filtro Anti-Spam:** El servidor descarta paquetes `PacketSpikeHammerAoE` si la diferencia entre el tiempo actual y `LastSpikeHammerAoETick` es menor a **5 ticks (0.25s)**.
