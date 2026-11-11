package com.example.proyecto_juego_kiko;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;

    private static void iniciar(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.music1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
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
