package com.app.aether;

import static com.app.aether.RegisterModel.validarCorreo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterModelView extends AppCompatActivity {

    // Inicialización de variables y objetos del layout
    private Button btn_register;
    TextView verName;
    private EditText nombre, correoRegister, contraRegister;
    public String name = "";
    public String email = "";
    public String pass = "";


    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Declaración de variables y objetos del layout
        btn_register = (Button) findViewById(R.id.btn_register);

        nombre = (EditText) findViewById(R.id.nombre);
        correoRegister = (EditText) findViewById(R.id.correoRegister);
        contraRegister = (EditText) findViewById(R.id.contraRegister);
        String verName = name;
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Botón registro que verifica las credenciales ingresadas por el usuario
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = correoRegister.getText().toString();
                pass = contraRegister.getText().toString();
                name = nombre.getText().toString();
                //Verifica si los campos están vacíos
                if (name.isEmpty() && email.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(RegisterModelView.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                //Verifica si el nombre ingresado es mayor a 3 caracteres
                } else if (name.length() < 3) {
                    Toast.makeText(RegisterModelView.this, "Nombre muy corto", Toast.LENGTH_SHORT).show();
                //Verifica si la contraseña es mayor a 8 caracteres
                } else if (pass.length() < 8) {
                    Toast.makeText(RegisterModelView.this, "Contraseña muy corta", Toast.LENGTH_SHORT).show();
                //Verifica si el correo es válido
                } else if (!validarCorreo(email)) {
                    Toast.makeText(RegisterModelView.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                //Despues de ser evaluadas las credenciales se crea la cuenta y se redirige al usuario a la ventana principaL de la aplicación
                } else {
                    Toast.makeText(RegisterModelView.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    RegisterModel.registerUser(RegisterModelView.this, name, email, pass);
                }
            }
        });
    }
    //Método para tomar el texto del correo y dejar solo el nombre de este sin el dominio para guardarlo y definir así la colección del usuario en la base de datos
    public static String quitarDominio(String correo) {
        int indiceArroba = correo.indexOf('@');
        if (indiceArroba == -1) {
            return correo;
        }
        String resultado = correo.substring(0, indiceArroba);

        return resultado;
    }
}