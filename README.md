# File Explorer

Explorador de archivos desktop — Java 21 + JavaFX 21.

## Requisitos

- **JDK 21** (testeado con Eclipse Temurin 21)
- **Maven** (incluido via `mvnw`)

## Cómo ejecutar

```bash
# Windows
set JAVA_HOME=C:\Path\To\JDK21
mvnw.cmd javafx:run

# O con Maven instalado globalmente
mvn javafx:run
```

## Estructura del proyecto

```
src/main/
  java/
    uptc/edu/co/file_explorer_doj/
      MainApp.java                        ← Entry point
      Launcher.java                       ← Launcher sin módulo
      controller/
        MainController.java               ← Controlador principal
        SearchController.java             ← Diálogo de búsqueda
        PermissionsController.java        ← Diálogo de permisos/atributos
      service/
        FileCommandService.java           ← Todos los ProcessBuilder/cmd
        FileSearchService.java            ← Búsqueda via dir /s /b
      model/
        FileItem.java                     ← Modelo de archivo/carpeta
  resources/
    uptc/edu/co/file_explorer_doj/
      fxml/
        main.fxml                         ← Vista principal
        search.fxml                       ← Diálogo de búsqueda
        permissions.fxml                  ← Diálogo de atributos
      css/
        theme.css                         ← Tema oscuro
```

## Funcionalidades

### Navegación
- Panel de árbol (izquierda) con todas las unidades y carpetas
- Vista de tabla (derecha): nombre, tamaño, tipo, fecha de modificación
- Doble click en carpeta para entrar; doble click en archivo para abrir con app predeterminada
- Barra de ruta editable — pulsa Enter para navegar
- Botones ◀ ▶ ▲ (atrás, adelante, subir nivel)
- Tecla Backspace para subir un nivel

### Operaciones de archivo (via cmd.exe)
| Operación | Comando cmd |
|-----------|-------------|
| Copiar archivo | `copy /Y` |
| Copiar carpeta | `xcopy /E /I /Y /H` |
| Mover / Cortar-Pegar | `move /Y` |
| Eliminar archivo | `del /F /Q` |
| Eliminar carpeta | `rmdir /S /Q` |
| Renombrar | `ren` |
| Crear archivo vacío | `copy /b nul` |
| Crear carpeta | `mkdir` |

### Permisos / Atributos
- Clic derecho → **Propiedades** sobre cualquier item
- Muestra salida de `attrib`
- Permite marcar/desmarcar **Solo lectura (+R)** y **Oculto (+H)**

### Búsqueda
- Botón **Buscar** en la barra de herramientas
- Busca por nombre dentro del directorio actual y subdirectorios (`dir /s /b`)
- Doble click en resultado navega al archivo/carpeta

### Interfaz
- Menú contextual (clic derecho): Copiar, Cortar, Pegar, Eliminar, Renombrar, Nueva carpeta, Nuevo archivo, Propiedades
- Menú superior: Archivo, Editar, Ver, Ayuda
- Barra de estado inferior: ruta actual, número de elementos, espacio libre en disco
- Selección múltiple con Ctrl+Click y Shift+Click (nativo de JavaFX TableView)
- Iconos de tipo por extensión en columna inicial
- Tema oscuro completo via CSS

### Atajos de teclado
| Tecla | Acción |
|-------|--------|
| Enter | Abrir / entrar en carpeta |
| Backspace | Subir un nivel |
| Delete | Eliminar selección |
| F2 | Renombrar |
| F5 | Actualizar |
| Ctrl+C | Copiar |
| Ctrl+X | Cortar |
| Ctrl+V | Pegar |
| Ctrl+A | Seleccionar todo |
