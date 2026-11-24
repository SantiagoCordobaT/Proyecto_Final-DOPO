package dominio.personajes;

import dominio.Ball;
import dominio.Field;
import dominio.efectos.Effect;
import dominio.efectos.StatusEffects;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.poderes.Power;
import dominio.poderes.PowerBar;

/**
 * Representa un personaje del juego. Las coordenadas (x, y) indican su centro
 * horizontal y la posición de los pies en el suelo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Character {

    private static final int ESTADISTICA_MIN = 1;
    private static final int ESTADISTICA_MAX = 5;

    private static final double ANCHO_BASE = 60.0;
    private static final double ALTO_BASE = 90.0;
    private static final double VELOCIDAD_POR_PUNTO = 70.0;
    private static final double RAPIDEZ_SUBIDA = 360.0;
    private static final double RAPIDEZ_CAIDA = 270.0;
    private static final double ALTURA_SALTO_BASE = 60.0;
    private static final double ALTURA_SALTO_POR_PUNTO = 25.0;
    private static final double VENTANA_PATADA_SEGUNDOS = 0.15;
    private static final double ENFRIAMIENTO_PATADA_SEGUNDOS = 0.4;

    private static final double ALCANCE_PATADA = 15.0;
    private static final double ALCANCE_CUERPO_A_CUERPO = 10.0;

    private final String nombre;
    private final String pais;
    private final int velocidad;
    private final int salto;
    private final int fuerza;
    private int goles;
    private final PowerBar barraPoder;
    private final StatusEffects efectos = new StatusEffects();

    private double x;
    private double y;
    private boolean subiendo;
    private boolean enElAire;
    private double xInicial;
    private int orientacion = 1;
    private int direccionMovimiento;
    private double esperaPatada;
    private double enfriamientoPatada;

    public Character(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            Power poder) throws POOBSoccerException {
        if (nombre == null || nombre.trim().isEmpty() || pais == null || pais.trim().isEmpty() || poder == null) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        validarEstadistica(velocidad);
        validarEstadistica(salto);
        validarEstadistica(fuerza);
        validarEstadistica(nivelPoder);
        this.nombre = nombre;
        this.pais = pais;
        this.velocidad = velocidad;
        this.salto = salto;
        this.fuerza = fuerza;
        this.barraPoder = new PowerBar(poder, nivelPoder);
    }

    public Character copiar() throws POOBSoccerException {
        return new Character(nombre, pais, velocidad, salto, fuerza, barraPoder.obtenerNivel(),
                barraPoder.obtenerPoder());
    }

    private static void validarEstadistica(int valor) throws POOBSoccerException {
        if (valor < ESTADISTICA_MIN || valor > ESTADISTICA_MAX) {
            throw new POOBSoccerException(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO);
        }
    }

    public void entrarEn(double xInicial, int orientacion) {
        this.xInicial = xInicial;
        this.orientacion = orientacion;
        reiniciarGoles();
        barraPoder.reiniciar();
        reiniciarJugada();
    }

    public void reiniciarGoles() {
        goles = 0;
    }

    public void reiniciarJugada() {
        this.x = xInicial;
        this.y = Field.SUELO_Y;
        this.subiendo = false;
        this.enElAire = false;
        this.direccionMovimiento = 0;
        this.esperaPatada = 0.0;
        this.enfriamientoPatada = 0.0;
        this.efectos.limpiar();
    }

    public void establecerDireccionMovimiento(int direccion) {
        if (direccion > 0) {
            this.direccionMovimiento = 1;
        } else if (direccion < 0) {
            this.direccionMovimiento = -1;
        } else {
            this.direccionMovimiento = 0;
        }
    }

    public void saltar() {
        if (puedeActuar() && !enElAire) {
            subiendo = true;
            enElAire = true;
        }
    }

    public void solicitarPatada() {
        if (puedeActuar()) {
            esperaPatada = VENTANA_PATADA_SEGUNDOS;
        }
    }

    public boolean intentarPatear(Ball balon, Character oponente) {
        boolean balonAlAlcance = seSolapaCon(balon, ALCANCE_PATADA);
        boolean oponenteAlAlcance = estaEnRangoCuerpoACuerpo(oponente);
        if (!puedePatearAhora() || !(balonAlAlcance || oponenteAlAlcance)) {
            return false;
        }
        esperaPatada = 0.0;
        enfriamientoPatada = ENFRIAMIENTO_PATADA_SEGUNDOS;
        if (oponenteAlAlcance) {
            oponente.aturdir();
        }
        if (balonAlAlcance) {
            if (estaEnZonaCabeza(balon)) {
                balon.patearArriba(fuerza, orientacion);
            } else {
                balon.patear(fuerza, orientacion);
            }
            barraPoder.dispararSobre(oponente);
        }
        return balonAlAlcance;
    }

    public boolean toca(Ball balon) {
        return seSolapaCon(balon, 0.0);
    }

    public void empujar(Ball balon) {
        if (estaEnZonaCabeza(balon)) {
            balon.rebotarArriba(obtenerBordeSuperior());
            // Si no está bloqueado por un efecto, cabecea hacia adelante
            if (puedeActuar()) {
                balon.patearArriba(fuerza, orientacion);
            }
        } else {
            int lado = balon.ladoRespectoA(x);
            balon.empujarDeLado(x + lado * obtenerAncho() / 2.0, lado);
        }
    }

    private boolean estaEnZonaCabeza(Ball balon) {
        return balon.estaPorEncimaDe(obtenerBordeSuperior());
    }

    public void armarPoder() {
        barraPoder.armar();
    }

    public void agregarEfecto(Effect efecto) {
        efectos.agregar(efecto);
    }

    public void aturdir() {
        agregarEfecto(new StunEffect());
    }

    public void actualizar(double segundosTranscurridos) {
        efectos.transcurrir(segundosTranscurridos);
        barraPoder.recargar(segundosTranscurridos);
        esperaPatada -= segundosTranscurridos;
        if (esperaPatada < 0.0) {
            esperaPatada = 0.0;
        }
        enfriamientoPatada -= segundosTranscurridos;
        if (enfriamientoPatada < 0.0) {
            enfriamientoPatada = 0.0;
        }

        if (puedeActuar()) {
            x += direccionMovimiento * efectos.signoHorizontal() * velocidad * VELOCIDAD_POR_PUNTO
                    * segundosTranscurridos;
        }
        double mitad = obtenerAncho() / 2.0;
        if (x < mitad) {
            x = mitad;
        } else if (x > Field.ANCHO - mitad) {
            x = Field.ANCHO - mitad;
        }

        actualizarSalto(segundosTranscurridos);
    }

    private void actualizarSalto(double segundosTranscurridos) {
        double alturaMaximaY = Field.SUELO_Y - (ALTURA_SALTO_BASE + salto * ALTURA_SALTO_POR_PUNTO);
        if (subiendo) {
            y -= RAPIDEZ_SUBIDA * segundosTranscurridos;
            if (y <= alturaMaximaY) {
                y = alturaMaximaY;
                subiendo = false;
            }
        } else if (enElAire) {
            y += RAPIDEZ_CAIDA * segundosTranscurridos;
            if (y >= Field.SUELO_Y) {
                y = Field.SUELO_Y;
                enElAire = false;
            }
        }
    }

    public void sumarGol() {
        goles++;
    }

    private boolean puedeActuar() {
        return efectos.permitenAcciones();
    }

    public boolean estaPateando() {
        return enfriamientoPatada > 0.0;
    }

    private boolean puedePatearAhora() {
        return puedeActuar() && esperaPatada > 0.0 && enfriamientoPatada <= 0.0;
    }

    private boolean seSolapaCon(Ball balon, double margen) {
        return balon.seSolapaCon(obtenerIzquierda() - margen, obtenerBordeSuperior() - margen,
                obtenerDerecha() + margen, obtenerAbajo() + margen);
    }

    private boolean estaEnRangoCuerpoACuerpo(Character otro) {
        return obtenerIzquierda() - ALCANCE_CUERPO_A_CUERPO <= otro.obtenerDerecha()
                && obtenerDerecha() + ALCANCE_CUERPO_A_CUERPO >= otro.obtenerIzquierda()
                && obtenerBordeSuperior() - ALCANCE_CUERPO_A_CUERPO <= otro.obtenerAbajo()
                && obtenerAbajo() + ALCANCE_CUERPO_A_CUERPO >= otro.obtenerBordeSuperior();
    }

    private double obtenerEscala() {
        return efectos.escala();
    }

    public String[] obtenerNombresEfectos() {
        return efectos.obtenerNombres();
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerPais() {
        return pais;
    }

    public int obtenerVelocidad() {
        return velocidad;
    }

    public int obtenerSalto() {
        return salto;
    }

    public int obtenerFuerza() {
        return fuerza;
    }

    public int obtenerNivelPoder() {
        return barraPoder.obtenerNivel();
    }

    public int obtenerGoles() {
        return goles;
    }

    public double obtenerCargaPoder() {
        return barraPoder.obtenerCarga();
    }

    public boolean estaPoderArmado() {
        return barraPoder.estaArmado();
    }

    public String obtenerNombrePoder() {
        return barraPoder.obtenerNombrePoder();
    }

    public String obtenerDescripcionPoder() {
        return barraPoder.obtenerDescripcionPoder();
    }

    public double[] obtenerLimites() {
        return new double[] { obtenerIzquierda(), obtenerBordeSuperior(), obtenerAncho(), obtenerAlto() };
    }

    public double obtenerX() {
        return x;
    }

    public double obtenerBordeSuperior() {
        return y - obtenerAlto();
    }

    public double obtenerCentroY() {
        return obtenerBordeSuperior() + obtenerAlto() / 2.0;
    }

    public int obtenerOrientacion() {
        return orientacion;
    }

    private double obtenerAncho() {
        return ANCHO_BASE * obtenerEscala();
    }

    private double obtenerAlto() {
        return ALTO_BASE * obtenerEscala();
    }

    private double obtenerIzquierda() {
        return x - obtenerAncho() / 2.0;
    }

    private double obtenerDerecha() {
        return x + obtenerAncho() / 2.0;
    }

    private double obtenerAbajo() {
        return y;
    }
}
