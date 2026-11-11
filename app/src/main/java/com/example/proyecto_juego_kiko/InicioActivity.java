
package com.example.proyecto_juego_kiko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        String elegido = SelectorRobot.robotGuardado(this);
        android.widget.TextView robot = findViewById(R.id.robotElegido);
        robot.setVisibility(elegido.isEmpty() ? View.GONE : View.VISIBLE);
        robot.setText(getString(R.string.robot_elegido, elegido));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        findViewById(R.id.ajustes).setOnClickListener(v -> startActivity(new Intent(this, AjustesActivity.class)));
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
