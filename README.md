# POOBSoccer (2026-2)

Proyecto final de **DOPO** — Escuela Colombiana de Ingeniería Julio Garavito.

## Sobre el juego

**POOBSoccer** es una versión de escritorio en Java inspirada en *Head Soccer* (D&D Dream, 2012). Son partidos rápidos 1 contra 1 en una cancha vista de lado, donde cada jugador controla un personaje con estadísticas propias y un poder especial que afecta al rival durante unos segundos.

El proyecto incluye:
- Tres modalidades: Jugador vs Jugador (PvsP), Jugador vs Máquina (PvsM) y Máquina vs Máquina (MvsM).
- Dos tipos de partido: Por goles (3, 5 o 7) y Supervivencia (solo en PvsM).
- Catálogo editable con 4 personajes iniciales y opciones para crear, editar o eliminar personajes (dejando mínimo 2).
- Interfaz en español e inglés.

## Mecánicas y reglas

- **Movimiento y física:** Los personajes corren dentro de la cancha, saltan hasta una altura según su atributo de salto y patean al balón o al rival si están muy cerca. El balón se mueve por impulsos discretos y rebota contra paredes, suelo, techo y travesaños.
- **Cabeza y patada:** Si el balón toca la parte superior de la cabeza, sale elevado hacia el campo rival (salvo si el jugador está aturdido o congelado, caso en el que solo rebota arriba).
- **Goles:** Cuenta como gol cuando el balón entra por completo en un arco por debajo del travesaño (+1 punto al atacante, o +1 al rival si es autogol). Luego del gol ambos jugadores y el balón vuelven a su posición inicial.
- **Aturdimiento:** Una patada cuerpo a cuerpo o recibir un balonazo muy fuerte del oponente deja al jugador aturdido por 3 segundos.
- **Supervivencia:** Empieza con 3 balones por lado. Cada gol recibido descuenta un balón. Si cae la máquina sube la racha y entra el siguiente personaje del catálogo; el juego termina cuando el jugador 1 se queda sin balones.

## Controles

| Acción | Jugador 1 | Jugador 2 |
| :--- | :---: | :---: |
| Mover izquierda / derecha | `A` / `D` | `←` / `→` |
| Saltar | `W` | `↑` |
| Patear | `S` | `↓` |
| Armar poder especial | `Q` | `Ctrl Derecho` |
| Pausar | `P` o `Esc` | `P` o `Esc` |

Las teclas funcionan mientras se mantengan presionadas. En PvsM la máquina controla al jugador 2, y en MvsM controla a ambos.

## Personajes y poderes

Cada personaje tiene 4 estadísticas de **1 a 5**: Velocidad, Salto, Fuerza y Poder Único. La barra de poder se carga a razón de 2 × Poder Único % por segundo hasta llegar al 100 %. Con la barra llena, al presionar la tecla de poder este queda armado y se dispara en la siguiente patada al balón.

| Personaje | País | Velocidad | Salto | Fuerza | Poder Único | Poder especial |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| **Jacob** | Canadá | 3 | 3 | 4 | 3 | Congelamiento (bloquea 3 s) |
| **Kenji** | Japón | 5 | 4 | 2 | 2 | Aturdimiento (bloquea 3 s) |
| **Mateo** | Colombia | 4 | 3 | 3 | 4 | Confusión (invierte controles 5 s) |
| **Rafael** | Brasil | 4 | 5 | 3 | 3 | Reducción (mitad de tamaño 6 s) |

## Cómo ejecutar

Desde la raíz del proyecto (requiere JDK 17+):

### Compilar y abrir el juego (PowerShell / Windows)

```powershell
mkdir out -Force
$fuentes = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $fuentes
java -cp out presentacion.HeadSoccerGUI
```

### Ejecutar las pruebas unitarias (JUnit 5)

```powershell
$todos = Get-ChildItem -Recurse -Filter *.java src, test | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "lib\junit-platform-console-standalone.jar" -d out $todos
java -jar "lib\junit-platform-console-standalone.jar" execute --class-path out --scan-class-path
```

## Arquitectura

- `src/dominio/`: Fachada `HeadSoccer`, simulación `Match`, física (`Field`, `Goal`, `Ball`), DTOs de lectura (`ResumenJugador`, `FichaPersonaje`), personajes (`dominio.personajes`), modalidades (`dominio.modalidades`), reglas (`dominio.partidos`), poderes y efectos (`dominio.poderes`, `dominio.efectos`) y excepciones (`dominio.excepciones`).
- `src/presentacion/`: Ventana principal `HeadSoccerGUI`, menú, diálogos sobre `DialogoBase`, utilidades visuales en `Estilo` y dibujo en Swing sin lógica de negocio.
- `test/dominio/`: Pruebas unitarias separadas por módulo (`BallTest`, `CharacterTest`, `SurvivalMatchTest`, `CatalogosTest`, `HeadSoccerTest`).

## Uso de herramientas de IA

Durante el desarrollo de este proyecto se utilizaron herramientas de inteligencia artificial como apoyo para estructurar prototipos iniciales, revisar errores de compilación y refactorizar partes del diseño, pruebas y documentación. El código, las reglas del juego, los diagramas y las pruebas fueron revisados, ajustados y validados por los autores.

## Autores y colaboradores

- **Desarrollo:** Santiago Córdoba, Camilo Rivas
- **Propuesta:** Mateo Sánchez García, María Angélica Pérez Mesa
- **Colaboradores:** Wilmar Edison Beltrán, Santiago Hernández Sarmiento

## Referencias

- D&D Dream. *Head Soccer*. https://dnddream.com/dnddream_web/app_game_06.htm
- TouchArcade. *Head Soccer*. https://toucharcade.com/games/head-soccer
- TV Tropes. *Head Sports*. https://tvtropes.org/pmwiki/pmwiki.php/VideoGame/HeadBoxing
