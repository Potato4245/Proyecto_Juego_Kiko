package com.example.proyecto_juego_kiko;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;

public class AjustesActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.ajustes);
        findViewById(R.id.buttonSalir).setOnClickListener(v -> finish());
        // Cada barra tiene su propia clave para recordar la elección al volver a Ajustes.
        conectar(R.id.seekBarVolumen, "volumen");
        conectar(R.id.seekBarEfectos, "efectos");
    }

    private void conectar(int id, String clave) {
        SharedPreferences audio = getSharedPreferences("audio", MODE_PRIVATE);
        SeekBar barra = findViewById(id);
        int inicial = id == R.id.seekBarVolumen ? MusicManager.VOLUMEN_INICIAL : 3;
        barra.setProgress(audio.getInt(clave, inicial));
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
            public void onProgressChanged(SeekBar b, int nivel, boolean usuario) {
                // Solo guardamos movimientos del jugador. La música cambia en directo;
                // la otra barra permite escuchar el nivel mediante un clic de muestra.
                if (!usuario) return;
                audio.edit().putInt(clave, nivel).apply();
                if (id == R.id.seekBarVolumen) MusicManager.volumen(nivel);
                else getSystemService(AudioManager.class).playSoundEffect(AudioManager.FX_KEY_CLICK, nivel / 10f);
            }
        });
    }
}
