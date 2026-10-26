<div align="center">

# ⚽ POOBSoccer (2026-2)
### Programación Orientada a Objetos
**Escuela Colombiana de Ingeniería Julio Garavito**

---

[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Platform](https://img.shields.io/badge/Platform-PC%20Desktop-blue.svg)](#)
[![Genre](https://img.shields.io/badge/Genre-2D%20Arcade%20Soccer-green.svg)](#)
[![Status](https://img.shields.io/badge/Status-Project%20Proposal-brightgreen.svg)](#)

</div>

---

## 📑 Tabla de Contenidos
- [Inspiración: Head Soccer](#-inspiración-head-soccer)
- [Propuesta: POOBSoccer](#-propuesta-poobsoccer)
- [Mecánicas del Juego](#-mecánicas-del-juego)
- [Controles](#-controles)
- [Modalidades de Juego](#-modalidades-de-juego)
- [Tipos de Partido](#-tipos-de-partido)
- [Jugadores y Estadísticas](#-jugadores-y-estadísticas)
- [Sistema de Poderes](#-sistema-de-poderes)
- [Reglas y Puntajes](#-reglas-y-puntajes)
- [Autores y Colaboradores](#-autores-y-colaboradores)
- [Referencias](#-referencias)

---

## 🎮 Inspiración: Head Soccer

**Head Soccer** es un videojuego de fútbol arcade en 2D para celulares desarrollado por la empresa surcoreana **D&D Dream** y lanzado en 2012. Se ha vuelto muy popular por sus partidos rápidos de uno contra uno y por unos controles tan sencillos que cualquiera aprende a jugar en segundos.

* **Dinámica básica:** Cada jugador controla un personaje que representa a un país. Los personajes se mueven a los lados, saltan y patean para meter el balón en el arco rival.
* **Tiros especiales:** Al llenar una barra de poder, el personaje lanza un tiro especial (fuego, hielo, rayo, etc.) que puede terminar en gol o dejar al rival inmovilizado por unos segundos.
* **Atributos:** Cada personaje posee sus propias estadísticas de velocidad, salto y fuerza, además de un tiro especial único.
* **Modos en la versión móvil original:** Arcade, Torneo, Supervivencia, Liga, Head Cup y Death Mode.

---

## 🚀 Propuesta: POOBSoccer

En este proyecto final llevaremos la esencia de **Head Soccer** desde los dispositivos móviles al computador. Nuestra versión, **POOBSoccer**, conserva el dinamismo de los partidos rápidos con poderes especiales, permitiendo que dos jugadores compitan en el mismo teclado e incorporando las siguientes novedades:

- [x] **Tres modalidades:** Jugador vs Jugador, Jugador vs Máquina y Máquina vs Máquina.
- [x] **Tres tipos de partido:** Por tiempo, por goles y supervivencia.
- [x] **Personajes diferenciados:** Atributos propios y poderes especiales temáticos según el país.
- [x] **Impacto estratégico:** Poderes con efectos de estado que alteran las condiciones del oponente y cambian el rumbo del partido.

---

## 🏟️ Mecánicas del Juego

* **Perspectiva y Entorno:** Partido de fútbol 2D de uno contra uno con vista lateral. Cada jugador defiende su portería y busca anotar en el arco contrario.
* **Física:** El balón está afectado por la gravedad y cuenta con física de rebote en el suelo, los travesaños/arcos y los límites de la cancha.
* **Información Abierta:** Ambos jugadores tienen visibilidad total y en tiempo real de toda la cancha, el balón, los personajes, el marcador, el cronómetro y las barras de poder.
* **Reanudación tras Gol:** Después de cada anotación, tanto los jugadores como el balón regresan a su posición inicial, cayendo el balón nuevamente desde el centro del campo.

---

## 🕹️ Controles

Ambos jugadores interactúan de forma local compartiendo el mismo teclado:

| Acción | Jugador 1 | Jugador 2 |
| :--- | :---: | :---: |
| **Mover a la izquierda** | <kbd>A</kbd> | <kbd>←</kbd> |
| **Mover a la derecha** | <kbd>D</kbd> | <kbd>→</kbd> |
| **Saltar** | <kbd>W</kbd> | <kbd>↑</kbd> |
| **Patear** | <kbd>S</kbd> | <kbd>↓</kbd> |
| **Usar poder especial** | <kbd>Q</kbd> | <kbd>Ctrl Derecho</kbd> |

---

## 👥 Modalidades de Juego

POOBSoccer implementa tres configuraciones de control y enfrentamiento:

1. **Jugador vs Jugador (PvsP):** Dos jugadores humanos enfrentándose en el mismo teclado.
2. **Jugador vs Máquina (PvsM):** Un jugador humano compitiendo contra un oponente controlado por la inteligencia del juego.
3. **Máquina vs Máquina (MvsM):** Ambos personajes son operados de forma automatizada por la máquina.

---

## ⏱️ Tipos de Partido

* **Por Tiempo:**
  * Duración estándar de 60 segundos.
  * Gana quien posea mayor número de anotaciones al expirar el tiempo.
  * En caso de empate, se define por **Gol de Oro** (muerte súbita: el primero en anotar gana).
* **Por Goles:**
  * Sin límite de tiempo.
  * Resulta ganador el primero en alcanzar la meta pactada: **3, 5 o 7 goles**.
* **Supervivencia:**
  * Modo exclusivo para la modalidad **PvsM**.
  * El jugador disputa una serie continua de partidos contra la máquina sin tiempo límite.
  * Se compite mediante un sistema de vidas o balones: cada vez que se recibe una anotación, se pierde un balón. El partido finaliza para quien agote primero sus balones.

---

## 👤 Jugadores y Estadísticas

### Interfaz del Jugador
En pantalla se visualizan en todo momento los siguientes datos de cada competidor:
* **Identificación:** Nombre y país representativo (equipo).
* **Goles:** Conteo de anotaciones registradas en el encuentro actual.
* **Barra de poder:** Indicador gráfico con el porcentaje de carga.
* **Poder activo:** Indicador del poder disponible en ese instante.

### Atributos
Cada personaje posee estadísticas graduadas en una escala numérica de **1 a 5**:
* 🏃 **Velocidad**
* 🦘 **Salto**
* 💥 **Fuerza**
* ⭐ **Poder Único**

---

## ⚡ Sistema de Poderes

El poder especial de cada personaje está directamente vinculado a su país de origen. 

* **Carga:** La barra de energía se recarga progresivamente con el tiempo. Solo puede liberarse cuando alcanza el $100\%$ de su capacidad.
* **Activación:** Se activa al impactar el balón y desencadena efectos directos sobre el rival.

### Ejemplos de Habilidades:
* ❄️ **Congelación (Canadá):** Encierra al oponente en un bloque de hielo sólido durante unos segundos, anulando su movimiento.
* 🔍 **Reducción:** Encoge temporalmente al oponente a un tamaño diminuto, reduciendo su alcance de cobertura.
* 🌀 **Confusión:** Invierte por unos momentos los controles de desplazamiento del oponente.

---

## ⚖️ Reglas y Puntajes

### Sistema de Puntuación
* **Gol a favor:** Balón en el arco rival suma **$+1$ punto**.
* **Autogol:** Si el balón ingresa en la portería propia, se concede **$+1$ punto al rival**.

### Normas del Encuentro
1. **Inicio:** El juego arranca con marcador inicial de $\$0-0\$$, barras de poder vacías y cada participante ubicado en su mitad respectiva de campo.
2. **Convalidación de gol:** El balón debe traspasar en su totalidad la línea de meta.
3. **Uso de poder:** Requiere obligatoriamente que la barra se encuentre al tope de carga.
4. **Estado de Aturdimiento:** Un personaje alcanzado por ataques o patadas del rival entra en estado de aturdimiento durante **3 segundos**, periodo en el cual queda inhabilitado para moverse, saltar o patear.
5. **Pausa:** La partida puede suspenderse de manera provisional en cualquier instante.
6. **Desempate:** En partidas por tiempo terminadas en empate, se aplica el criterio de muerte súbita (**Gol de Oro**).

---

## 👥 Autores y Colaboradores

* **Propuesta:**
  * Mateo Sánchez García
  * María Angélica Pérez Mesa
* **Colaboradores:**
  * Wilmar Edison Beltrán
  * Santiago Hernández Sarmiento

---

## 📚 Referencias

* D&D Dream. (s. f.). *Head Soccer*. https://dnddream.com/dnddream_web/app_game_06.htm
* TouchArcade. (s. f.). *Head Soccer*. https://toucharcade.com/games/head-soccer
* TV Tropes. (s. f.). *Head Sports (serie de videojuegos)*. https://tvtropes.org/pmwiki/pmwiki.php/VideoGame/HeadBoxing
