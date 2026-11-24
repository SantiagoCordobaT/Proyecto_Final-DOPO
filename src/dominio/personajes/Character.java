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
 * Personaje que juega el partido. Guarda su posición (x centro, y pies), sus
 * estadísticas, su barra de poder y los efectos que tenga encima.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Character {

    private static final int MIN_STAT = 1;
    private static final int MAX_STAT = 5;

    private static final double ANCHO_BASE = 60.0;
    private static final double ALTO_BASE = 90.0;
    private static final double VELOCIDAD_POR_PUNTO = 70.0;
    private static final double RAPIDEZ_SUBIDA = 360.0;
    private static final double RAPIDEZ_CAIDA = 270.0;
    private static final double ALTURA_SALTO_BASE = 60.0;
    private static final double ALTURA_SALTO_POR_PUNTO = 25.0;
    private static final double DURACION_PATADA = 0.4;

    private static final double ALCANCE_PATADA = 15.0;
    private static final double ALCANCE_RIVAL = 10.0;

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
    private boolean quierePatear;
    private double tiempoPatada;

    public Character(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            Power poder) throws POOBSoccerException {
        if (nombre == null || nombre.trim().isEmpty() || pais == null || pais.trim().isEmpty() || poder == null) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        validarStat(velocidad);
        validarStat(salto);
        validarStat(fuerza);
        validarStat(nivelPoder);
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

    private static void validarStat(int valor) throws POOBSoccerException {
        if (valor < MIN_STAT || valor > MAX_STAT) {
            throw new POOBSoccerException(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO);
        }
    }

    public void entrarEn(double xInicial, int orientacion) {
        this.xInicial = xInicial;
        this.orientacion = orientacion;
        goles = 0;
        barraPoder.reiniciar();
        reiniciarJugada();
    }

    public void reiniciarGoles() {
        goles = 0;
    }

    public void reiniciarJugada() {
        x = xInicial;
        y = Field.SUELO_Y;
        subiendo = false;
        enElAire = false;
        direccionMovimiento = 0;
        quierePatear = false;
        tiempoPatada = 0.0;
        efectos.limpiar();
    }

    public void establecerDireccionMovimiento(int direccion) {
        if (direccion > 0) {
            direccionMovimiento = 1;
        } else if (direccion < 0) {
            direccionMovimiento = -1;
        } else {
            direccionMovimiento = 0;
        }
    }

    public void saltar() {
        if (efectos.permitenAcciones() && !enElAire) {
            subiendo = true;
            enElAire = true;
        }
    }

    public void solicitarPatada() {
        if (efectos.permitenAcciones() && tiempoPatada <= 0.0) {
            quierePatear = true;
        }
    }

    public boolean intentarPatear(Ball balon, Character oponente) {
        if (!efectos.permitenAcciones() || !quierePatear || tiempoPatada > 0.0) {
            return false;
        }
        quierePatear = false;

        boolean balonCerca = seSolapaCon(balon, ALCANCE_PATADA);
        boolean rivalCerca = estaCercaDe(oponente);
        if (!balonCerca && !rivalCerca) {
            return false;
        }

        tiempoPatada = DURACION_PATADA;
        if (rivalCerca) {
            oponente.aturdir();
        }
        if (balonCerca) {
            if (estaEnZonaCabeza(balon)) {
                balon.patearArriba(fuerza, orientacion);
            } else {
                balon.patear(fuerza, orientacion);
            }
            barraPoder.dispararSobre(oponente);
        }
        return balonCerca;
    }

    public boolean toca(Ball balon) {
        return seSolapaCon(balon, 0.0);
    }

    public void empujar(Ball balon) {
        if (estaEnZonaCabeza(balon)) {
            balon.rebotarArriba(obtenerBordeSuperior());
            // Solo cabecea hacia adelante si no está congelado o aturdido
            if (efectos.permitenAcciones()) {
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
        efectos.agregar(new StunEffect());
    }

    public void actualizar(double segundos) {
        efectos.transcurrir(segundos);
        barraPoder.recargar(segundos);
        if (tiempoPatada > 0.0) {
            tiempoPatada -= segundos;
            if (tiempoPatada < 0.0) {
                tiempoPatada = 0.0;
            }
        }

        if (efectos.permitenAcciones()) {
            x += direccionMovimiento * efectos.signoHorizontal() * velocidad * VELOCIDAD_POR_PUNTO * segundos;
        }
        double mitad = obtenerAncho() / 2.0;
        if (x < mitad) {
            x = mitad;
        } else if (x > Field.ANCHO - mitad) {
            x = Field.ANCHO - mitad;
        }

        actualizarSalto(segundos);
    }

    private void actualizarSalto(double segundos) {
        double topeY = Field.SUELO_Y - (ALTURA_SALTO_BASE + salto * ALTURA_SALTO_POR_PUNTO);
        if (subiendo) {
            y -= RAPIDEZ_SUBIDA * segundos;
            if (y <= topeY) {
                y = topeY;
                subiendo = false;
            }
        } else if (enElAire) {
            y += RAPIDEZ_CAIDA * segundos;
            if (y >= Field.SUELO_Y) {
                y = Field.SUELO_Y;
                enElAire = false;
            }
        }
    }

    public void sumarGol() {
        goles++;
    }

    public boolean estaPateando() {
        return tiempoPatada > 0.0;
    }

    private boolean seSolapaCon(Ball balon, double margen) {
        return balon.seSolapaCon(obtenerIzquierda() - margen, obtenerBordeSuperior() - margen,
                obtenerDerecha() + margen, y + margen);
    }

    private boolean estaCercaDe(Character otro) {
        double maxDistanciaX = (obtenerAncho() + otro.obtenerAncho()) / 2.0 + ALCANCE_RIVAL;
        double maxDistanciaY = (obtenerAlto() + otro.obtenerAlto()) / 2.0 + ALCANCE_RIVAL;
        return Math.abs(x - otro.x) <= maxDistanciaX
                && Math.abs(obtenerCentroY() - otro.obtenerCentroY()) <= maxDistanciaY;
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
        return ANCHO_BASE * efectos.escala();
    }

    private double obtenerAlto() {
        return ALTO_BASE * efectos.escala();
    }

    private double obtenerIzquierda() {
        return x - obtenerAncho() / 2.0;
    }

    private double obtenerDerecha() {
        return x + obtenerAncho() / 2.0;
    }
}
