package com.app.aether;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class  SensorActivity extends AppCompatActivity {

    SeekBar barraIntensidad;
    String textoCase;
    int intensity, estadoIntensidad, estadoSensor;
    int switchIntensity, switchState;
    Switch switchSensor, active;
    TextView statusText, intensidad, verCorreo, adaptiveLuminosidad, textDevice;
    Button btn_setSensor;
    ImageButton btn_info;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    LobbyActivityModel lam;
    CrearHorarioActivity cha;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        switchSensor = findViewById(R.id.switchSensor);
        statusText = findViewById(R.id.statusText);
        btn_setSensor = findViewById(R.id.btn_setSensor);
        btn_info = findViewById(R.id.btn_sensorInfo);
        barraIntensidad = findViewById(R.id.barraIntensidad);
        intensidad = findViewById(R.id.Intensidad);
        verCorreo = findViewById(R.id.viewCorreo);
        active = findViewById(R.id.switchActive);
        adaptiveLuminosidad = findViewById(R.id.adptiveLuminosidad);
        lam = new LobbyActivityModel();
        textDevice = findViewById(R.id.textDevice);

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        textDevice.setText(textoCase);
        cha = new CrearHorarioActivity();

        switchState = 0;
        switchIntensity = 0;
        intensity = 20;

        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adaptiveLuminosidad.setText("Control por luminosidad activado");
                    barraIntensidad.setVisibility(View.VISIBLE);
                    intensidad.setVisibility(View.VISIBLE);
                    barraIntensidad.setEnabled(true);
                    switchIntensity = 1;
                } else {
                    adaptiveLuminosidad.setText("Control por luminosidad desactivado");
                    barraIntensidad.setVisibility(View.INVISIBLE);
                    intensidad.setVisibility(View.INVISIBLE);
                    barraIntensidad.setEnabled(false);
                    switchIntensity = 0;
                    intensity = 20;                }
            }
        });

        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    verCorreo.setText("" + snapshot.child("Usuarios").getValue());
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
                    verCorreo.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        switchSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchState = 1;
                    statusText.setText("Encendido");
                } else {
                    switchState = 0;
                    statusText.setText("Apagado");
                }
            }
        });

        barraIntensidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                barraIntensidad.setMax(80);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    barraIntensidad.setMin(20);
                }
                intensidad.setText("Intensidad: " + progress);
                intensity = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btn_setSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> sensores = new HashMap<>();
                sensores.put("Active", switchState);
                databaseReference.child(verCorreo.getText().toString()).child("Dispositivos").child(lam.textSpinner.getText().toString()).child("Sensor").setValue(sensores).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SensorActivity.this, "Cambio establecido", Toast.LENGTH_SHORT).show();

                            Map<String, Object> intensidad = new HashMap<>();
                            intensidad.put("Intensity", switchIntensity);
                            intensidad.put("Value", intensity);
                            databaseReference.child(verCorreo.getText().toString()).child("Dispositivos").child(lam.textSpinner.getText().toString()).child("Sensor").child("Intensidad").setValue(intensidad).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SensorActivity.this, "Intensidad establecida", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SensorActivity.this, "No se pudo establecer la intensidad", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(SensorActivity.this, "Error al establecer el cambio", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoInfo();
            }
        });
    }

    private void mostrarDialogoInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Control por sensores");
        builder.setMessage("Esta función permite controlar un sistema lumínico manualmente prendiendo y apagando el electrodoméstico mediante un Switch o bien controlando la intensidad lumínica.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

