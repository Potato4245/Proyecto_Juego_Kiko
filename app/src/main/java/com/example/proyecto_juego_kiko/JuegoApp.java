package com.example.proyecto_juego_kiko;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class JuegoApp extends Application implements Application.ActivityLifecycleCallbacks {
    private int visibles;
    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }
    @Override public void onActivityStarted(Activity actividad) {
        if (visibles++ == 0) MusicManager.iniciar(this);
    }
    @Override public void onActivityStopped(Activity actividad) {
        if (--visibles == 0) MusicManager.detener();
    }
    @Override public void onActivityCreated(Activity a, Bundle b) {}
    @Override public void onActivityResumed(Activity a) {}
    @Override public void onActivityPaused(Activity a) {}
    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
    @Override public void onActivityDestroyed(Activity a) {}
}
