package com.example.proyecto_juego_kiko;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

// Android crea una sola instancia para el proceso. Todas las pantallas comparten la pista.
public class JuegoApp extends Application implements Application.ActivityLifecycleCallbacks {
    private int visibles;
    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }
    @Override public void onActivityStarted(Activity actividad) {
        // La primera pantalla visible abre el reproductor. El contador evita duplicar la
        // música al pasar de la portada al login o al hangar.
        actividad.setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);
        if (visibles++ == 0) MusicManager.iniciar(this);
    }
    @Override public void onActivityStopped(Activity actividad) {
        // Cuando desaparece la última pantalla, la aplicación deja de emitir sonido.
        if (--visibles == 0) MusicManager.detener();
    }
    // La interfaz exige estos eventos. Para la música basta con Started y Stopped.
    @Override public void onActivityCreated(Activity a, Bundle b) {}
    @Override public void onActivityResumed(Activity a) {}
    @Override public void onActivityPaused(Activity a) {}
    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
    @Override public void onActivityDestroyed(Activity a) {}
}
