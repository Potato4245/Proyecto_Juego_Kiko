package com.example.proyecto_juego_kiko;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText etUsuario, etPassword;
    Button btnLogin;
    DdHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asegúrate de tener estos IDs en tu activity_main.xml
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.button);

        dbHelper = new DdHelper(MainActivity.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Comprobar en la base de datos si las credenciales son correctas
                    boolean loginCorrecto = dbHelper.comprobarLogin(usuario, password);

                    if (loginCorrecto) {
                        Toast.makeText(MainActivity.this, "¡Bienvenido al hangar, " + usuario + "!", Toast.LENGTH_SHORT).show();
                        // Aquí puedes añadir un Intent para pasar a la pantalla del juego
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
