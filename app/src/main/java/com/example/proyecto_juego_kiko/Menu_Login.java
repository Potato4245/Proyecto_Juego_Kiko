package com.example.proyecto_juego_kiko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Menu_Login extends AppCompatActivity {
    private EditText etUsuario, etPassword;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        View btnLogin = findViewById(R.id.button);

        // El formulario usa la base local tanto para registrar cuentas como para comprobarlas.
        dbHelper = new DbHelper(this);
        findViewById(R.id.buttonreset).setOnClickListener(v ->
                startActivity(new Intent(this, Reset_Password.class)));
        // Registro reutiliza los dos campos visibles y comunica si se pudo crear la cuenta.
        findViewById(R.id.btnRegistro).setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String clave = etPassword.getText().toString().trim();
            if (usuario.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Introduce usuario y contraseña para registrarte", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean creado = dbHelper.registrarUsuario(usuario, clave) != -1;
            Toast.makeText(this, creado ? "Cuenta creada. Ya puedes acceder" : "No se pudo crear la cuenta",
                    Toast.LENGTH_SHORT).show();
        });

        // Un acceso correcto abre el hangar. finish() deja la portada como destino al volver.
        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.comprobarLogin(usuario, password)) {
                Toast.makeText(this, "¡Bienvenido al hangar, " + usuario + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SelectorRobot.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
