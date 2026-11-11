package com.example.proyecto_juego_kiko;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;

    static void iniciar(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.music1);
            if (mediaPlayer == null) return;
            volumen(context.getSharedPreferences("audio", Context.MODE_PRIVATE).getInt("volumen", 3));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    static void volumen(int nivel) {
        if (mediaPlayer != null) mediaPlayer.setVolume(nivel / 10f, nivel / 10f);
    }

    public static void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pausar() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void reanudar() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
