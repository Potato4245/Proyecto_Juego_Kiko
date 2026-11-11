package com.example.proyecto_juego_kiko;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

/** Reproduce la pista incluida en res/raw/music1.mp3 durante las pantallas del juego. */
public final class MusicManager {
    // Las instalaciones nuevas empiezan a este nivel. Un nivel guardado, incluido cero, tiene prioridad.
    static final int VOLUMEN_INICIAL = 7;
    private static MediaPlayer reproductor;
    private static int posicionMs;

    private MusicManager() {}

    static void iniciar(Context contexto) {
        if (reproductor != null) return;

        // Android trata la pista como música del juego y la envía a la salida multimedia activa.
        AudioAttributes atributos = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        reproductor = MediaPlayer.create(contexto, R.raw.music1, atributos, 0);
        if (reproductor == null) {
            Log.w("MusicManager", "Android no ha podido abrir la pista music1");
            return;
        }

        // Aplicamos el volumen antes de arrancar: si el jugador eligió silencio, se respeta
        // desde el primer instante. El bucle vuelve al comienzo cuando termina el MP3.
        volumen(contexto.getSharedPreferences("audio", Context.MODE_PRIVATE)
                .getInt("volumen", VOLUMEN_INICIAL));
        reproductor.setLooping(true);
        if (posicionMs > 0) reproductor.seekTo(posicionMs);
        reproductor.start();
    }

    static void volumen(int nivel) {
        // La barra usa 0–10 y MediaPlayer espera 0–1 para los canales izquierdo y derecho.
        float ganancia = nivel / 10f;
        if (reproductor != null) reproductor.setVolume(ganancia, ganancia);
    }

    static void detener() {
        if (reproductor == null) return;
        // Al salir del juego liberamos el dispositivo de audio. Si el proceso sigue vivo,
        // recuperamos este punto al volver desde la pantalla de inicio de Android.
        posicionMs = reproductor.getCurrentPosition();
        reproductor.release();
        reproductor = null;
    }
}
