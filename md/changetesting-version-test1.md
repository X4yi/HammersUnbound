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
