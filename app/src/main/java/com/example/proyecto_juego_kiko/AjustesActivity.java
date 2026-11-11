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
        conectar(R.id.seekBarVolumen, "volumen");
        conectar(R.id.seekBarEfectos, "efectos");
    }

    private void conectar(int id, String clave) {
        SharedPreferences audio = getSharedPreferences("audio", MODE_PRIVATE);
        SeekBar barra = findViewById(id);
        barra.setProgress(audio.getInt(clave, 3));
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
            public void onProgressChanged(SeekBar b, int nivel, boolean usuario) {
                if (!usuario) return;
                audio.edit().putInt(clave, nivel).apply();
                if (id == R.id.seekBarVolumen) MusicManager.volumen(nivel);
                else getSystemService(AudioManager.class).playSoundEffect(AudioManager.FX_KEY_CLICK, nivel / 10f);
            }
        });
    }
}
