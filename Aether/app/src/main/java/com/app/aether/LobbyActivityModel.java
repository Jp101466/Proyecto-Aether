package com.app.aether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyActivityModel extends AppCompatActivity {
    // Inicialización de variables y objetos del layout
    TextView state, mostrarCorreo;
    static TextView textSpinner;
    Button btn_logout;
    ImageButton btn_infoAlert;
    FirebaseAuth mAuth;
    Switch modoSeguro;
    Spinner Dispositivos;
    Boolean switchState;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    SensorActivity sa;
    CrearHorarioActivity cha;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        // Declaración de variables y objetos del layout

        Dispositivos = findViewById(R.id.Dipositivos);
        btn_logout = findViewById(R.id.btn_logout);
        btn_infoAlert = findViewById(R.id.btn_infoSecurity);
        modoSeguro = findViewById(R.id.modoSeguro);
        state = findViewById(R.id.modoSeguroState);
        mostrarCorreo = findViewById(R.id.mostrarNombre);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        textSpinner = findViewById(R.id.textSpinner);
        switchState = false;
        sa = new SensorActivity();
        cha = new CrearHorarioActivity();
        Dispositivos = (Spinner) findViewById(R.id.Dipositivos);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Dispositivos, android.R.layout.simple_spinner_item);
        Dispositivos.setAdapter(adapter1);

        //Obtención del correo del usuario para sobreescribir los datos a guardar
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mostrarCorreo.setText("" + snapshot.child("Usuarios").getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.getKey();
                    mostrarCorreo.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        //Configuración de un botón switch que establece si el modo seguro está activado o no
        modoSeguro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchState = isChecked;
                Map<String, Object> map = new HashMap<>();
                map.put("Active", switchState);

                if (isChecked) {
                    state.setText("Alarma activada");
                } else {
                    state.setText("Alarma desactivada");
                }
                databaseReference.child(mostrarCorreo.getText().toString()).child("Seguridad").setValue(map);
            }
        });
        //Botón para cerrar sesión del usuario
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(LobbyActivityModel.this, MainActivity.class));
            }
        });

        //Botón que muestra la información de lo que es el modo seguro
        btn_infoAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoInfo();
            }
        });
        //Configuración del spinner de selección múltiple de dispositivos a configurar
        Dispositivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                parentView.getItemAtPosition(position).toString();
                textSpinner.setText(parentView.getItemAtPosition(position).toString());
                switch (textSpinner.getText().toString()) {
                    case "Seleccionar":
                        break;
                    case "Luces":
                        configurarLuces();
                        startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        break;
                    case "Televisor":
                        configurarTelevisor();
                        startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        break;
                    case "Ventilador":
                        configurarVentilador();
                        startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        break;
                    case "Foco":
                        configurarFoco();
                        startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Acciones adicionales si nada está seleccionado
            }
        });
    }

    //Método que contiene la información del botón info
    private void mostrarDialogoInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modo alerta");
        builder.setMessage("Esta función permite recibir notificaciones y mensajes de alerta cuando se detecte un movimiento en su hogar");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int a) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Método que establece la ruta en la base de datos del dispositivo luces
    private void configurarLuces() {
        Map<String, Object> horarioLuces = new HashMap<>();
        horarioLuces.put("Active", cha.active);
        horarioLuces.put("Inicio", cha.inicio);
        horarioLuces.put("Fin", cha.fin);
        databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Luces").child("Horario").setValue(horarioLuces).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> sensorLuces = new HashMap<>();
                    sensorLuces.put("Active", sa.switchState);
                    databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Luces").child("Sensor").setValue(sensorLuces).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()) {
                                Map<String, Object> intensidadLuces = new HashMap<>();
                                intensidadLuces.put("Intensity", sa.switchIntensity);
                                intensidadLuces.put("Value", sa.intensity);
                                databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Luces").child("Sensor").child("Intensidad").setValue(intensidadLuces).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task3) {
                                        if(task3.isSuccessful()) {
                                            startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                                        }else {
                                            Toast.makeText(LobbyActivityModel.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    //Método que establece la ruta en la base de datos del dispositivo televisor
    private void configurarTelevisor() {
        Map<String, Object> horarioTV = new HashMap<>();
        horarioTV.put("Active", cha.active);
        horarioTV.put("Inicio", cha.inicio);
        horarioTV.put("Fin", cha.fin);
        databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Televisor").child("Horario").setValue(horarioTV).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()) {
                    Map<String, Object> sensorVentilador = new HashMap<>();
                    sensorVentilador.put("Active", sa.switchState);
                    databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Televisor").child("Sensor").setValue(sensorVentilador).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        }
                    });
                }
            }
        });
    }
    //Método que establece la ruta en la base de datos del dispositivo ventilador
    private void configurarVentilador() {
        Map<String, Object> horarioVentilador = new HashMap<>();
        horarioVentilador.put("Active", cha.active);
        horarioVentilador.put("Inicio", cha.inicio);
        horarioVentilador.put("Fin", cha.fin);
        databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Ventilador").child("Horario").setValue(horarioVentilador).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()) {
                    Map<String, Object> sensorVentilador = new HashMap<>();
                    sensorVentilador.put("Active", sa.switchState);
                    databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Ventilador").child("Sensor").setValue(sensorVentilador).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                        }
                    });
                }
            }
        });
    }
    //Método que establece la ruta en la base de datos del dispositivo foco
    private void configurarFoco() {
        Map<String, Object> horarioFoco = new HashMap<>();
        horarioFoco.put("Active", cha.active);
        horarioFoco.put("Inicio", cha.inicio);
        horarioFoco.put("Fin", cha.fin);
        databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Foco").child("Horario").setValue(horarioFoco).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> sensorFoco = new HashMap<>();
                    sensorFoco.put("Active", sa.switchState);
                    databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Foco").child("Sensor").setValue(sensorFoco).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()) {
                                Map<String, Object> intensidadFoco = new HashMap<>();
                                intensidadFoco.put("Intensity", sa.switchIntensity);
                                intensidadFoco.put("Value", sa.intensity);
                                databaseReference.child(mostrarCorreo.getText().toString()).child("Dispositivos").child("Foco").child("Sensor").child("Intensidad").setValue(intensidadFoco).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task3) {
                                        if(task3.isSuccessful()) {
                                            startActivity(new Intent(LobbyActivityModel.this, SetDeviceActivity.class));
                                        }else {
                                            Toast.makeText(LobbyActivityModel.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}

