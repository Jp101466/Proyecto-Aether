package com.app.aether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Inicialización de variables y objetos del layout
    Button btn_login, btn_crearCuenta;
    EditText correoLogin, contraLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        correoLogin = findViewById(R.id.correoLogin);
        contraLogin = findViewById(R.id.contraLogin);
        btn_login = findViewById(R.id.btn_login);
        btn_crearCuenta = findViewById(R.id.btn_crearCuenta);

        //Botón Login para iniciar sesión
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = correoLogin.getText().toString().trim();
                String pass = contraLogin.getText().toString().trim();

                if(email.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Complete todos los datos", Toast.LENGTH_SHORT).show();
                } else {
                    LoginModel.loginUser(MainActivity.this, email, pass);
                }
            }
        });

        //Botón crearCuenta para crear una cuenta en la aplicación
        btn_crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterModelView.class));
            }
        });
    }

    //Método para validar las credenciales del usuario mediante Firebase Auth
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            startActivity(new Intent(MainActivity.this, LobbyActivityModel.class));
        }
    }
}