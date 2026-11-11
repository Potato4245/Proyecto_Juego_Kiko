
package com.example.proyecto_juego_kiko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        View btnJugar = findViewById(R.id.btnJugar);

        btnJugar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    InicioActivity.this,
                    Menu_Login.class
            );

            startActivity(intent);
        });
    }
}
