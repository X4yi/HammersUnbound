# Hammers Unbound

Hammers Unbound es un mod para Minecraft Forge 1.12.2 que agrega dos familias de armas pesadas:

- **WarHammers**: martillos lentos y brutales centrados en stun, dano en area y control de grupos.
- **SpikeHammers**: martillos con picos centrados en sangrado, presion constante y la mecanica de Blood Pact.

## Features principales

- 10 armas nuevas:
  - Wooden, Stone, Iron, Golden y Diamond WarHammer.
  - Wooden, Stone, Iron, Golden y Diamond SpikeHammer.
- WarHammer con:
  - Stun al objetivo principal.
  - Dano AOE alrededor del impacto.
  - Particulas de impacto
  - Sonidos de impacto

- SpikeHammer con:
  - Bleeding acumulable.
  - Blood Pact por click derecho.
  - Particulas de sangre sincronizadas con el dano.
- GUI propia de configuracion.
  - `items.json`
  - `server.json`
  - `client.json`
- Comando de recarga de configuracion.
- Sincronizacion servidor-cliente para config, particulas y efectos visuales.

## Documentacion

- [Mecanicas](docs/mecanics.md)
- [Items y recetas](docs/items.md)
- [Configuracion](docs/CONFIGURATION.md)
- [Guia para modpacks](docs/PACKMAKER_GUIDE.md)
- [Desarrollo y build](docs/DEVELOPMENT.md)

## Estado del proyecto

Hammers Unbound esta en desarrollo. Si algo se rompe, revisa primero las configs generadas y el changelog. El mod intenta conservar compatibilidad con configs viejas, especialmente en cambios recientes como `attackSpeed` positivo y timers expresados en segundos.
