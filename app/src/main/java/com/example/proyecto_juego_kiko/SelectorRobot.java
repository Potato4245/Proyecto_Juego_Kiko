package com.example.proyecto_juego_kiko;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

// Pantalla que coordina la escena, la ficha y la elección. HangarView se encarga del dibujo.
public class SelectorRobot extends ComponentActivity {
    // Cada entrada reúne el PNG y el texto de un robot. El orden coincide con las seis plataformas,
    // de izquierda a derecha. ordinal() da su posición y name() es el nombre que se guarda.
    enum Robot {
        Atlas(R.drawable.sel_atlas, R.string.sel_desc_atlas),
        Volt(R.drawable.sel_volt, R.string.sel_desc_volt),
        Cronos(R.drawable.sel_cronos, R.string.sel_desc_cronos),
        Zepelin(R.drawable.sel_zepelin, R.string.sel_desc_zepelin),
        Vapora(R.drawable.sel_vapora, R.string.sel_desc_vapora),
        Nox(R.drawable.sel_nox, R.string.sel_desc_nox);

        final int imagen;
        final int descripcion;

        Robot(int imagen, int descripcion) {
            this.imagen = imagen;
            this.descripcion = descripcion;
        }
    }

    private HangarView escena;
    private ScrollView ficha;
    // Robot cuya ficha estamos leyendo. null representa la vista general con las seis plataformas.
    private Robot enfocado;

    // Lee la última elección confirmada en las preferencias privadas de la app. La cadena vacía
    // significa que aún no se ha elegido ningún robot. El menú también usa este método.
    static String robotGuardado(Context contexto) {
        return contexto.getSharedPreferences("hangar", MODE_PRIVATE).getString("robot", "");
    }

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        // Aprovechamos la pantalla horizontal para ver el hangar completo. En modo inmersivo,
        // un gesto desde el borde permite mostrar temporalmente las barras de Android.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.selector_robot);
        escena = findViewById(R.id.escena);
        ficha = findViewById(R.id.ficha);
        // Añadimos cada figura con su propia acción de toque. Si Android recrea la actividad,
        // recuperamos la ficha que estaba abierta, aunque el jugador aún no hubiera confirmado.
        for (Robot robot : Robot.values()) {
            escena.agregarRobot(robot.name(), robot.imagen, v -> mostrar(robot, true));
            if (estado != null && robot.name().equals(estado.getString("vista"))) enfocado = robot;
        }
        // El zoom depende del ancho y el alto reales. Recalculamos el encuadre cuando la escena
        // recibe su tamaño inicial o cambia de tamaño, sin repetir la animación del toque.
        escena.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            if (r - l != or - ol || b - t != ob - ot) mostrar(enfocado, false);
        });
        // «No elegir» cierra la ficha. La escritura en preferencias ocurre únicamente en «Elegir».
        findViewById(R.id.volver).setOnClickListener(v -> mostrar(null, true));
        findViewById(R.id.confirmar).setOnClickListener(v -> {
            if (enfocado == null) return;
            getSharedPreferences("hangar", MODE_PRIVATE).edit()
                    .putString("robot", enfocado.name()).apply();
            // La pantalla que abrió el selector vuelve a primer plano con la elección guardada.
            finish();
        });
        // El gesto Atrás tiene dos destinos. Desde una ficha vuelve a las plataformas y desde
        // la vista general sale del selector. Ninguno de esos gestos confirma un personaje.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (enfocado == null) finish();
                else mostrar(null, true);
            }
        });
    }

    private void mostrar(Robot robot, boolean animar) {
        enfocado = robot;
        // La escena recibe el índice del robot o -1 para alejar la cámara. La ficha es otra vista,
        // así que el zoom del hangar mantiene los textos y botones a su tamaño habitual.
        escena.enfocar(robot == null ? -1 : robot.ordinal(), animar);
        ficha.animate().cancel();
        findViewById(R.id.indicacion).setVisibility(robot == null ? View.VISIBLE : View.GONE);
        if (robot == null) {
            ficha.setVisibility(View.GONE);
            return;
        }
        // Para los tres robots de la izquierda, la ficha va abajo a la derecha. Para los otros tres
        // se invierten los lados. El ancho se limita al 44 % de la escena y a un máximo de 328 dp.
        boolean aDerecha = robot.ordinal() < 3;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ficha.getLayoutParams();
        float densidad = getResources().getDisplayMetrics().density;
        lp.width = Math.min(Math.round(escena.getWidth() * 0.44f), Math.round(328 * densidad));
        lp.gravity = Gravity.BOTTOM | (aDerecha ? Gravity.RIGHT : Gravity.LEFT);
        ficha.setLayoutParams(lp);
        ((TextView) findViewById(R.id.nombre)).setText(robot.name());
        ((TextView) findViewById(R.id.descripcion)).setText(robot.descripcion);
        // Cada ficha empieza por su título. Tras un toque, aparece con un breve retraso y sube
        // 12 dp mientras gana opacidad para acompañar el movimiento de la cámara.
        ficha.scrollTo(0, 0);
        ficha.setAlpha(animar ? 0f : 1f);
        ficha.setVisibility(View.VISIBLE);
        ficha.setTranslationY(animar ? 12 * densidad : 0f);
        if (animar) ficha.animate().alpha(1f).translationY(0f)
                .setStartDelay(160).setDuration(260).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        super.onSaveInstanceState(estado);
        // El Bundle conserva la ficha temporal durante una recreación de Android.
        // Por ejemplo, se puede estar leyendo Atlas aunque la elección guardada siga siendo Volt.
        if (enfocado != null) estado.putString("vista", enfocado.name());
    }
}
