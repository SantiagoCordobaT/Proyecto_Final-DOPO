<div align="center">

# POOBSoccer (2026-2)
### DOPO
**Escuela Colombiana de Ingeniería Julio Garavito**

---

[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Platform](https://img.shields.io/badge/Platform-PC%20Desktop-blue.svg)](#)
[![Genre](https://img.shields.io/badge/Genre-2D%20Arcade%20Soccer-green.svg)](#)
[![Status](https://img.shields.io/badge/Status-Project%20Proposal-brightgreen.svg)](#)

</div>

---

## Tabla de Contenidos
- [Head Soccer](#head-soccer)
- [Nuestra versión: POOBSoccer](#nuestra-versión-poobsoccer)
- [Mecánicas del Juego](#mecánicas-del-juego)
- [Controles](#controles)
- [Modalidades de Juego](#modalidades-de-juego)
- [Tipos de Partido](#tipos-de-partido)
- [Jugadores y Estadísticas](#jugadores-y-estadísticas)
- [Sistema de Poderes](#sistema-de-poderes)
- [Reglas y Puntajes](#reglas-y-puntajes)
- [Cómo Ejecutar](#cómo-ejecutar)
- [Arquitectura](#arquitectura)
- [Uso de Herramientas de IA](#uso-de-herramientas-de-ia)
- [Autores y Colaboradores](#autores-y-colaboradores)
- [Referencias](#referencias)

---

## Head Soccer

**Head Soccer** es un videojuego de fútbol arcade en 2D para celulares desarrollado por la empresa surcoreana **D&D Dream** y lanzado en 2012. Se ha vuelto muy popular por sus partidos rápidos de uno contra uno y por unos controles tan sencillos que cualquiera aprende a jugar en segundos.

* **Dinámica básica:** Cada jugador controla un personaje que representa a un país. Los personajes se mueven a los lados, saltan y patean para meter el balón en el arco rival.
* **Tiros especiales:** Al llenar una barra de poder, el personaje lanza un tiro especial (congelamiento, aturdimiento, confusión o reducción) que altera las condiciones del rival por unos segundos.
* **Atributos:** Cada personaje posee sus propias estadísticas de velocidad, salto, fuerza y poder único, además de poder crear, editar o eliminar personajes personalizados.
* **Modos en la versión móvil original:** Arcade, Torneo, Supervivencia, Liga, Head Cup y Death Mode.

---

## Nuestra versión: POOBSoccer

En este proyecto final llevaremos **Head Soccer** del celular al computador. Nuestra versión, **POOBSoccer**, conserva el dinamismo de los partidos rápidos con poderes especiales, permitiendo que dos jugadores compitan en el mismo teclado e incorporando las siguientes novedades:

- [x] **Tres modalidades:** Jugador vs Jugador, Jugador vs Máquina y Máquina vs Máquina.
- [x] **Dos tipos de partido:** Por goles (3, 5 o 7) y supervivencia.
- [x] **Personajes configurables:** Cuatro personajes iniciales y creación, edición y eliminación de personajes con atributos y poder elegibles.
- [x] **Impacto estratégico:** Poderes con efectos de estado que alteran las condiciones del oponente.

---

## Mecánicas del Juego

* **Perspectiva y Entorno:** Partido de fútbol 2D de uno contra uno con vista lateral. Cada jugador defiende su portería y busca anotar en el arco contrario.
* **Física:** El balón avanza por impulsos discretos con rebote en las paredes laterales, los travesaños de los arcos y tope en suelo y techo; los personajes tienen salto y caída por estados según su atributo de salto.
* **Zonas de golpeo:** Al patear o contactar el balón con la zona superior de la cabeza se ejecuta un tiro/cabezazo elevado hacia el campo rival.
* **Información Abierta:** Ambos jugadores tienen visibilidad total en tiempo real de toda la cancha, el balón, los personajes, el marcador, el estado del partido (meta de goles y tiempo jugado, o balones y racha) y las barras de poder.
* **Reanudación tras Gol:** Después de cada anotación, tanto los jugadores como el balón regresan a su posición inicial, cayendo el balón nuevamente desde el centro del campo.

---

## Controles

Ambos jugadores interactúan de forma local compartiendo el mismo teclado:

| Acción | Jugador 1 | Jugador 2 |
| :--- | :---: | :---: |
| **Mover a la izquierda** | <kbd>A</kbd> | <kbd>←</kbd> |
| **Mover a la derecha** | <kbd>D</kbd> | <kbd>→</kbd> |
| **Saltar** | <kbd>W</kbd> | <kbd>↑</kbd> |
| **Patear** | <kbd>S</kbd> | <kbd>↓</kbd> |
| **Usar poder especial** | <kbd>Q</kbd> | <kbd>Ctrl Derecho</kbd> |
| **Pausar el partido** | <kbd>P</kbd> o <kbd>Esc</kbd> | <kbd>P</kbd> o <kbd>Esc</kbd> |

Las teclas se mantienen presionadas: moverse, saltar, patear y armar el poder duran mientras la tecla siga abajo. En **Máquina vs Máquina**, y para el jugador 2 en **Jugador vs Máquina**, el teclado no controla a la máquina.

---

## Modalidades de Juego

POOBSoccer implementa tres configuraciones de control y enfrentamiento:

1. **Jugador vs Jugador (PvsP):** Dos jugadores humanos enfrentándose en el mismo teclado.
2. **Jugador vs Máquina (PvsM):** Un jugador humano compitiendo contra un oponente controlado por la inteligencia del juego.
3. **Máquina vs Máquina (MvsM):** Ambos personajes son operados de forma automatizada por la máquina.

---

## Tipos de Partido

* **Por Goles:**
  * Sin límite de tiempo.
  * Resulta ganador el primero en alcanzar la meta pactada: **3, 5 o 7 goles**.
* **Supervivencia:**
  * Modo exclusivo para la modalidad **PvsM**.
  * El jugador disputa una serie continua de partidos contra la máquina sin tiempo límite.
  * Se compite mediante un sistema de vidas o balones (3 por lado): cada vez que se recibe una anotación, se pierde un balón. Cuando cae la máquina, sube la racha y entra un nuevo rival; la serie termina cuando el jugador 1 agota sus balones.

---

## Jugadores y Estadísticas

### Interfaz del Jugador
En pantalla se visualizan en todo momento los siguientes datos de cada competidor:
* **Identificación:** Nombre y país representativo (equipo).
* **Goles:** Conteo de anotaciones registradas en el encuentro actual.
* **Barra de poder:** Indicador gráfico con el porcentaje de carga.
* **Poder activo:** Indicador del poder disponible en ese instante.

### Atributos
Cada personaje posee estadísticas graduadas en una escala numérica de **1 a 5**:
* **Velocidad**
* **Salto**
* **Fuerza**
* **Poder Único**

---

## Sistema de Poderes

Cada personaje cuenta con un poder especial elegido entre los poderes disponibles en `PowerCatalog`.

* **Carga:** La estadística **Poder Único** fija la rapidez de la barra: se recarga $2 \times \text{Poder Único}\,\%$ por segundo hasta llegar al $100\%$.
* **Activación:** Con la barra llena, la tecla de poder **arma** el poder (con la barra incompleta no tiene efecto) y la siguiente patada que conecta con el balón lo **dispara**: el rival recibe el efecto y la barra vuelve a cero. Si el rival ya tiene ese efecto, el nuevo lo reemplaza y reinicia su duración.

### Personajes iniciales disponibles
Además de poder **crear**, **editar** y **eliminar** personajes personalizados desde la pantalla de selección (conservando al menos 2 disponibles), el catálogo inicia con:

| Personaje | País | Velocidad | Salto | Fuerza | Poder Único | Poder especial |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| **Jacob** | Canadá | 3 | 3 | 4 | 3 | Congelamiento |
| **Kenji** | Japón | 5 | 4 | 2 | 2 | Aturdimiento |
| **Mateo** | Colombia | 4 | 3 | 3 | 4 | Confusión |
| **Rafael** | Brasil | 4 | 5 | 3 | 3 | Reducción |

### Habilidades disponibles:
* **Congelamiento:** Encierra al oponente en un bloque de hielo sólido durante 3 segundos, impidiéndole actuar.
* **Aturdimiento:** Aturde al oponente durante 3 segundos, bloqueando movimiento, salto y patada.
* **Confusión:** Invierte los controles horizontales (izquierda/derecha) del oponente durante 5 segundos.
* **Reducción:** Encoge temporalmente al oponente a la mitad de su tamaño durante 6 segundos.

---

## Reglas y Puntajes

### Sistema de Puntuación
* **Gol a favor:** Balón completamente dentro del arco rival suma **$+1$ punto**.
* **Autogol:** Si el balón ingresa en la portería propia, se concede **$+1$ punto al rival**.

### Normas del Encuentro
1. **Inicio:** El juego arranca con marcador inicial de 0-0, barras de poder vacías y cada participante ubicado en su mitad respectiva de campo.
2. **Convalidación de gol:** El balón debe traspasar en su totalidad la línea de meta por debajo del travesaño.
3. **Uso de poder:** Requiere obligatoriamente que la barra se encuentre al tope de carga.
4. **Estado de Aturdimiento:** Un personaje alcanzado por ataques cuerpo a cuerpo o balonazos fuertes del rival entra en estado de aturdimiento durante 3 segundos, periodo en el cual queda inhabilitado para moverse, saltar o patear.
5. **Pausa:** La partida puede suspenderse de manera provisional en cualquier instante.

---

## Cómo Ejecutar

Desde la raíz del proyecto con JDK 21:

```powershell
mkdir out -Force | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
java -cp out presentacion.HeadSoccerGUI
```

Para compilar y ejecutar las pruebas unitarias con JUnit:

```powershell
javac -encoding UTF-8 -d out -cp "lib\junit-platform-console-standalone.jar" (Get-ChildItem -Path src, test -Recurse -Filter *.java).FullName
java -jar "lib\junit-platform-console-standalone.jar" execute --class-path out --scan-class-path
```

---

## Arquitectura

* **`src/dominio/`**: Fachada `HeadSoccer`, simulación `Match`, física de cancha/arcos/balón (`Field`, `Goal`, `Ball`), personajes (`dominio.personajes`), modalidades y controles (`dominio.modalidades`), reglas de partido (`dominio.partidos`), poderes y efectos (`dominio.poderes`, `dominio.efectos`) y excepciones (`dominio.excepciones`).
* **`src/presentacion/`**: Ventana principal `HeadSoccerGUI`, menú, diálogos de configuración/selección/edición de personajes y renderizado Swing sin lógica de reglas.
* **`test/dominio/`**: Pruebas unitarias de reglas de negocio, colisiones, poderes y fachada.

---

## Uso de Herramientas de IA

Durante el desarrollo inicial y refactorizaciones de este repositorio se utilizaron herramientas de inteligencia artificial (Claude / Antigravity) como apoyo para estructuración inicial, traducción de identificadores y pruebas; actualmente el código está siendo revisado, simplificado y reescrito directamente por los autores del proyecto para asegurar comprensión y autoría propia de toda la solución.

---

## Autores y Colaboradores

* **Desarrollo del proyecto:**
  * Santiago Córdoba
  * Camilo Rivas
* **Propuesta:**
  * Mateo Sánchez García
  * María Angélica Pérez Mesa
* **Colaboradores:**
  * Wilmar Edison Beltrán
  * Santiago Hernández Sarmiento

---

## Referencias

* D&D Dream. (s. f.). *Head Soccer*. https://dnddream.com/dnddream_web/app_game_06.htm
* TouchArcade. (s. f.). *Head Soccer*. https://toucharcade.com/games/head-soccer
* TV Tropes. (s. f.). *Head Sports (serie de videojuegos)*. https://tvtropes.org/pmwiki/pmwiki.php/VideoGame/HeadBoxing
