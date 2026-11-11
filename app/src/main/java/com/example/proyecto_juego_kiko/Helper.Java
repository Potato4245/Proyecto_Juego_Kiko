
package com.example.proyecto_juego_kiko;

import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.BlurTarget;

public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etPassword;
    Button btnLogin;
    TextView btnReset;

    DdHelper dbHelper;

    BlurView blurView;
    BlurTarget blurTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_login);

        // --------------------------------
        // CONFIGURACION DEL EFECTO BLUR
        // --------------------------------

        blurView = findViewById(R.id.blurView);
        blurTarget = findViewById(R.id.blurTarget);

        blurView.setupWith(blurTarget)
                .setBlurRadius(20f)
                .setOverlayColor(Color.parseColor("#33202A30"))
                .setBlurAutoUpdate(true);

        // Bordes redondeados del cristal
        blurView.setOutlineProvider(
                ViewOutlineProvider.BACKGROUND
        );

        blurView.setClipToOutline(true);

        // --------------------------------
        // CONEXION DE LOS ELEMENTOS
        // --------------------------------

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.button);
        btnReset = findViewById(R.id.buttonreset);

        // --------------------------------
        // CONEXION CON LA BASE DE DATOS
        // --------------------------------

        dbHelper = new DdHelper(MainActivity.this);

        // --------------------------------
        // BOTON INICIAR SESION
        // --------------------------------

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String usuario = etUsuario.getText()
                        .toString().trim();

                String password = etPassword.getText()
                        .toString().trim();

                if (usuario.isEmpty() || password.isEmpty()) {

                    Toast.makeText(
                            MainActivity.this,
                            "Rellena todos los campos",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    boolean loginCorrecto =
                            dbHelper.comprobarLogin(usuario, password);

                    if (loginCorrecto) {

                        Toast.makeText(
                                MainActivity.this,
                                "¡Bienvenido al hangar, " + usuario + "!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                }
            }
        });

        // --------------------------------
        // RECUPERAR CONTRASENA
        // --------------------------------

        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(
                        MainActivity.this,
                        "Recuperacion de contraseña proximamente",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

    }
}
