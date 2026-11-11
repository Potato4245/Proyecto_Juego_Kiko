package com.example.proyecto_juego_kiko;

import android.content.Context;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatImageView;

// Contenedor de la escena. Coloca el fondo y los seis robots, dibuja la marca de selección
// y mueve el conjunto cuando SelectorRobot pide enfocar una plataforma.
public class HangarView extends FrameLayout {
    // Medimos los centros de las seis bases sobre una referencia de 768 × 432. SUELO fija
    // la altura de los pies. Cambiar estos valores desplaza las figuras sobre el fondo.
    private static final float[] PLATAFORMAS = {77, 199, 322, 446, 570, 692};
    private static final int ANCHO = 768;
    private static final int ALTO = 432;
    private static final int SUELO = 264;
    private static final int ALTURA_ROBOT = 151;
    // Posición del pie dentro de cada PNG cuadrado, medida desde su borde superior.
    private static final float[] PIES = {0.93f, 0.955f, 0.965f, 0.945f, 0.95f, 0.935f};
    // escala convierte las unidades del dibujo en píxeles del dispositivo. Los bordes son
    // los desplazamientos hasta la esquina del fondo y se reutilizan para colocar el anillo.
    private float escala;
    private float bordeX;
    private float bordeY;
    // elegido identifica el robot confirmado y controla el ✓. destacado identifica el robot
    // que se está mirando y controla el halo. En ambos casos -1 significa «ninguno».
    private int elegido = -1;
    private int destacado = -1;
    private final ValueAnimator luzAmbiente = new ValueAnimator();
    private int sombra;
    // Reutilizamos el pincel y el trazado del símbolo al redibujar la plataforma.
    private final Paint marca = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path check = new Path();

    public HangarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // El zoom usa la esquina superior izquierda como origen. Así podemos calcular dónde
        // quedará cada plataforma después de escalar y desplazar toda la escena.
        setPivotX(0);
        setPivotY(0);
        setClipChildren(false);
        ImageView fondo = new ImageView(context);
        fondo.setImageResource(R.drawable.sel_hangar);
        fondo.setScaleType(ImageView.ScaleType.FIT_XY);
        fondo.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        // La primera vista hija, con índice 0, es el fondo. El robot de la plataforma i está
        // en la vista hija i + 1. Ese desplazamiento se usa al dibujar y al aplicar la sombra.
        addView(fondo);
        // Si el editor instancia HangarView directamente, necesita crear sus propias figuras.
        // La vista previa de selector_robot.xml usa el dibujo de hangar_diseno.xml. En el
        // juego, SelectorRobot añade las figuras táctiles después de inflar esta vista.
        if (isInEditMode()) {
            for (SelectorRobot.Robot robot : SelectorRobot.Robot.values()) {
                ImageView figura = new ImageView(context);
                figura.setImageResource(robot.imagen);
                figura.setScaleType(ImageView.ScaleType.FIT_CENTER);
                figura.setContentDescription(robot.name());
                addView(figura);
            }
            return;
        }
        luzAmbiente.addUpdateListener(animacion -> aplicarSombra((int) animacion.getAnimatedValue()));
    }

    private void aplicarSombra(int intensidad) {
        // El animador entrega un valor de opacidad por fotograma. Con SRC_ATOP, el negro
        // oscurece los píxeles visibles del PNG y conserva los huecos transparentes del robot.
        sombra = intensidad;
        for (int i = 0; i < getChildCount(); i++) {
            ImageView imagen = (ImageView) getChildAt(i);
            if (intensidad == 0 || (destacado >= 0 && i == destacado + 1)) {
                imagen.clearColorFilter();
            } else {
                // El desplazamiento coloca la intensidad en el canal alfa del color negro.
                imagen.setColorFilter(intensidad << 24, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    void agregarRobot(String nombre, int imagen, OnClickListener alTocar) {
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        // Decodificar el PNG a la mitad del ancho y del alto reduce la memoria de las seis figuras.
        // FIT_CENTER conserva sus proporciones al encajarlas en el espacio de la plataforma.
        opciones.inSampleSize = 2;
        Bitmap original = BitmapFactory.decodeResource(getResources(), imagen, opciones);
        Figura figura = new Figura(getContext(), original);
        // Al construir el hangar recuperamos el ✓ de la sesión anterior. Todavía no hemos añadido
        // esta figura, por eso su índice de plataforma es el número actual de hijos menos el fondo.
        if (nombre.equals(SelectorRobot.robotGuardado(getContext()))) elegido = getChildCount() - 1;
        figura.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // El nombre identifica la figura ante un lector de pantalla. El listener comunica a la
        // actividad qué robot se ha tocado para que pueda abrir su ficha.
        figura.setContentDescription(nombre);
        figura.setFocusable(true);
        figura.setOnClickListener(alTocar);
        addView(figura);
    }

    private static class Figura extends AppCompatImageView {
        private final Bitmap original;

        Figura(Context contexto, Bitmap original) {
            super(contexto);
            this.original = original;
            setImageBitmap(original);
        }

        // La imagen original siempre se conserva. Al cerrar la ficha volvemos a mostrarla,
        // de modo que un robot guardado no se quede iluminado en la vista general.
        void iluminar(boolean activa) {
            setSelected(activa);
            if (!activa) {
                setImageBitmap(original);
                return;
            }
            // La máscara alfa conserva los huecos entre las piezas de cada autómata.
            Bitmap luz = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
            luz.setDensity(original.getDensity());
            Canvas lienzo = new Canvas(luz);
            Bitmap silueta = original.extractAlpha();
            Paint brillo = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            brillo.setColor(0xffffe6a8);
            brillo.setShadowLayer(14f, 0, 0, 0xffffae3d);
            lienzo.drawBitmap(silueta, 0, 0, brillo);
            brillo.clearShadowLayer();
            // Dibujamos la silueta dorada desplazada cuatro píxeles en ocho direcciones. Después
            // superponemos el PNG original: se ven sus detalles y el borde sobresale alrededor.
            for (int paso = 0; paso < 8; paso++) {
                double angulo = paso * Math.PI / 4;
                lienzo.drawBitmap(silueta, (float) Math.cos(angulo) * 4,
                        (float) Math.sin(angulo) * 4, brillo);
            }
            lienzo.drawBitmap(original, 0, 0, null);
            silueta.recycle();
            setImageBitmap(luz);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View figura, long tiempo) {
        // Antes de pintar la figura, decoramos su plataforma. En detalle dibujamos el anillo
        // y en la vista general dibujamos el ✓ bajo la elección guardada.
        int plataforma = destacado >= 0 ? destacado : elegido;
        if (plataforma >= 0 && figura == getChildAt(plataforma + 1)) {
            // Trasladamos el origen a los pies para dibujar el adorno en coordenadas locales.
            // restore() recupera el sistema de coordenadas antes de que Android pinte la figura.
            canvas.save();
            canvas.translate(bordeX + PLATAFORMAS[plataforma] * escala, bordeY + SUELO * escala);
            canvas.scale(escala, escala);
            if (destacado >= 0) {
                marca.setStyle(Paint.Style.FILL);
                marca.setColor(0x75ffc46d);
                canvas.drawOval(-41, -5, 41, 5, marca);
                marca.setStyle(Paint.Style.STROKE);
                marca.setStrokeWidth(1.8f);
                marca.setColor(0xffffe29b);
                canvas.drawOval(-44, -6, 44, 6, marca);
            } else {
                marca.setStyle(Paint.Style.FILL);
                marca.setColor(0xffffe29b);
                canvas.drawCircle(0, 18, 7, marca);
                marca.setStyle(Paint.Style.STROKE);
                marca.setColor(0xff382817);
                marca.setStrokeWidth(1.6f);
                check.rewind();
                check.moveTo(-3.8f, 18);
                check.lineTo(-1, 20.6f);
                check.lineTo(4, 15.5f);
                canvas.drawPath(check, marca);
            }
            canvas.restore();
        }
        return super.drawChild(canvas, figura, tiempo);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int ancho = MeasureSpec.getSize(widthSpec);
        int alto = MeasureSpec.getSize(heightSpec);
        // Una escala común mantiene alineados el fondo, las bases y los robots. El ancho limita
        // la escena para que quepan las seis bases. El factor 0,77 permite recortar parte del
        // fondo por arriba y por abajo en pantallas panorámicas.
        escala = Math.min(ancho / (float) ANCHO, alto / (float) (ALTO * 0.77));
        bordeX = (ancho - ANCHO * escala) / 2f;
        bordeY = (alto - ALTO * escala) / 2f;
        // Primero dimensionamos el fondo completo. Para cada PNG, restamos medio ancho
        // al centro de la base y la distancia hasta los pies a la altura del suelo. Así
        // obtenemos la esquina superior izquierda que FrameLayout necesita para colocarlo.
        colocar(0, 0, 0, ANCHO, ALTO);
        for (int i = 1; i < getChildCount(); i++) {
            int plataforma = i - 1;
            colocar(i, PLATAFORMAS[plataforma] - ALTURA_ROBOT / 2f,
                    SUELO - ALTURA_ROBOT * PIES[plataforma], ALTURA_ROBOT, ALTURA_ROBOT);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    private void colocar(int indice, float x, float y, float ancho, float alto) {
        // Convertimos las medidas de referencia a píxeles. bordeX y bordeY centran el conjunto
        // en el espacio disponible del dispositivo.
        LayoutParams lp = (LayoutParams) getChildAt(indice).getLayoutParams();
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.width = Math.round(ancho * escala);
        lp.height = Math.round(alto * escala);
        lp.leftMargin = Math.round(bordeX + x * escala);
        lp.topMargin = Math.round(bordeY + y * escala);
    }

    void enfocar(int indice, boolean animar) {
        animate().cancel();
        boolean detalle = indice >= 0;
        // Al cambiar de foco restauramos primero el PNG anterior. Solo el nuevo robot recibe
        // el halo; al regresar al hangar el índice vale -1 y todos recuperan su imagen original.
        if (indice != destacado) {
            if (destacado >= 0) ((Figura) getChildAt(destacado + 1)).iluminar(false);
            destacado = indice;
            if (destacado >= 0) ((Figura) getChildAt(destacado + 1)).iluminar(true);
        }
        // Pedimos otro dibujo para que drawChild cambie la marca de la plataforma.
        invalidate();
        luzAmbiente.cancel();
        // 108 sobre 255 atenúa el entorno aproximadamente un 42 %. Partimos de la sombra actual
        // para que un cambio de vista durante la transición no produzca un salto de luz.
        int destinoSombra = detalle ? 108 : 0;
        if (animar && sombra != destinoSombra) {
            luzAmbiente.setIntValues(sombra, destinoSombra);
            luzAmbiente.setDuration(420);
            luzAmbiente.start();
        } else {
            aplicarSombra(destinoSombra);
        }
        // Buscamos que el robot ocupe el 93 % del alto, con un aumento máximo de 2,85 veces.
        // En la vista general, la escala 1 y el desplazamiento 0 devuelven el encuadre inicial.
        float zoom = detalle ? Math.min(2.85f, getHeight() * 0.93f / (ALTURA_ROBOT * escala)) : 1f;
        float x = 0;
        float y = 0;
        if (detalle) {
            // Situamos el personaje al 38 % o al 62 % del ancho según el lado de su ficha y sus pies
            // cerca del borde inferior. limitar() impide desplazar la vista más allá de sus bordes.
            float destino = indice < 3 ? 0.38f : 0.62f;
            x = limitar(getWidth() * destino - (bordeX + PLATAFORMAS[indice] * escala) * zoom,
                    getWidth() * (1 - zoom), 0);
            y = limitar(getHeight() * 0.96f - (bordeY + SUELO * escala) * zoom,
                    getHeight() * (1 - zoom), 0);
        }
        for (int i = 1; i < getChildCount(); i++) {
            // Mientras la ficha está abierta, los vecinos siguen visibles. El jugador usa los botones
            // de la ficha antes de seleccionar otra plataforma.
            getChildAt(i).setEnabled(!detalle);
            getChildAt(i).setImportantForAccessibility(detalle ? IMPORTANT_FOR_ACCESSIBILITY_NO : IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
        // La cámara es el movimiento de este FrameLayout completo: fondo, figuras y marcas
        // se acercan juntos. La interpolación suaviza el arranque y el final del recorrido.
        animate().scaleX(zoom).scaleY(zoom).translationX(x).translationY(y)
                .setDuration(animar ? 540 : 0)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).start();
    }

    // Si la cámara rebasa un extremo, devuelve ese extremo. Dentro del intervalo conserva
    // el desplazamiento calculado, también para las plataformas centrales.
    private static float limitar(float valor, float minimo, float maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }
}
