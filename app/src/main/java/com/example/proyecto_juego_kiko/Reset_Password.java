package com.example.proyecto_juego_kiko;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Reset_Password extends AppCompatActivity {
    private EditText etUsuario, etNuevaPassword, etRepetirPassword;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etUsuario = findViewById(R.id.etUsuarioReset);
        etNuevaPassword = findViewById(R.id.etNuevaPassword);
        etRepetirPassword = findViewById(R.id.etRepetirPassword);
        Button btnGuardar = findViewById(R.id.btnGuardarPassword);
        TextView btnVolver = findViewById(R.id.btnVolverLogin);

        dbHelper = new DbHelper(this);

        btnGuardar.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String nueva = etNuevaPassword.getText().toString().trim();
            String repetida = etRepetirPassword.getText().toString().trim();

            if (usuario.isEmpty() || nueva.isEmpty() || repetida.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!nueva.equals(repetida)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.cambiarPassword(usuario, nueva)) {
                Toast.makeText(this, "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ese usuario no existe", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
