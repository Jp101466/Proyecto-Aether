package com.app.aether;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterModel {

    //Método para crear una cuenta y guardar la información de las credenciales del usuario
    public static void registerUser(Context context, String name, String email, String pass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String emailFinal = RegisterModelView.quitarDominio(email);
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("Nombre", name);
                    info.put("Contraseña", pass);

                    databaseReference.child("Usuarios").child(emailFinal).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                context.startActivity(new Intent(context, MainActivity.class));
                            } else {
                                Toast.makeText(context, "No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método para verificar que el correo ingresado por el usuario sea válido
    public static boolean validarCorreo(String correo) {
        String patron = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(correo);

        return matcher.matches();
    }
}

