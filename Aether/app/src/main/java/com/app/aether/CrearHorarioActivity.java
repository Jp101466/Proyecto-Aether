package com.app.aether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class CrearHorarioActivity extends AppCompatActivity {
    // Inicialización de variables y objetos del layout
    Button btn_done, btn_fechaInicio, btn_fechaFin, btn_horaInicio, btn_horaFin;
    int active, fechaInicioInt, fechaFinInt, horaInicioInt, horaFinInt, estadoHorario;
    double inicio, fin;
    Switch switchHorario;
    ImageButton btn_info;
    EditText fechaInicio, fechaFin, horaInicio, horaFin;
    TextView verCorreo, textSwitch;
    DatabaseReference reference;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    LobbyActivityModel lam = new LobbyActivityModel();
    FirebaseAuth auth;
    FirebaseUser user;
    SensorActivity sa;
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_horario);

        // Declaración de variables y objetos del layout
        reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        auth = FirebaseAuth.getInstance();
        btn_done = findViewById(R.id.btn_done);
        fechaInicio = findViewById(R.id.fechaInicio);
        fechaFin = findViewById(R.id.fechaFin);
        horaInicio = findViewById(R.id.horaInicio);
        horaFin = findViewById(R.id.horaFin);
        btn_info = findViewById(R.id.btn_info);
        verCorreo = findViewById(R.id.verCorreo);
        switchHorario = findViewById(R.id.switchHorario);
        textSwitch = findViewById(R.id.textHorario);
        btn_fechaInicio = findViewById(R.id.btn_fechaInicio);
        btn_fechaFin = findViewById(R.id.btn_fechaFin);
        btn_horaInicio = findViewById(R.id.btn_horaInicio);
        btn_horaFin = findViewById(R.id.btn_horaFin);

        reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sa = new SensorActivity();

        //Definimos las variables que muestran el horario y si está activado en 0
        active = 0;
        inicio = 0;
        fin = 0;

        //Función del botón switchHorario, establece si el usuario va a crear un horario de uso o no
        switchHorario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    active = 1;
                    textSwitch.setText("Control por horario activado");
                    fechaInicio.setEnabled(true);
                    fechaInicio.setVisibility(View.VISIBLE);
                    fechaFin.setEnabled(true);
                    fechaFin.setVisibility(View.VISIBLE);
                    horaInicio.setEnabled(true);
                    horaInicio.setVisibility(View.VISIBLE);
                    horaFin.setEnabled(true);
                    horaFin.setVisibility(View.VISIBLE);
                    btn_fechaInicio.setEnabled(true);
                    btn_fechaInicio.setVisibility(View.VISIBLE);
                    btn_fechaFin.setEnabled(true);
                    btn_fechaFin.setVisibility(View.VISIBLE);
                    btn_horaInicio.setEnabled(true);
                    btn_horaInicio.setVisibility(View.VISIBLE);
                    btn_horaFin.setEnabled(true);
                    btn_horaFin.setVisibility(View.VISIBLE);

                }else {
                    active = 0;
                    textSwitch.setText("Control por horario desactivado");
                    fechaInicio.setEnabled(false);
                    fechaInicio.setVisibility(View.GONE);
                    fechaFin.setEnabled(false);
                    fechaFin.setVisibility(View.GONE);
                    horaInicio.setEnabled(false);
                    horaInicio.setVisibility(View.GONE);
                    horaFin.setEnabled(false);
                    horaFin.setVisibility(View.GONE);
                    btn_fechaInicio.setEnabled(false);
                    btn_fechaInicio.setVisibility(View.GONE);
                    btn_fechaFin.setEnabled(false);
                    btn_fechaFin.setVisibility(View.GONE);
                    btn_horaInicio.setEnabled(false);
                    btn_horaInicio.setVisibility(View.GONE);
                    btn_horaFin.setEnabled(false);
                    btn_horaFin.setVisibility(View.GONE);
                }
            }
        });
        //Obtención del correo del usuario para sobreescribir los datos a guardar
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
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
        reference.addListenerForSingleValueEvent(eventListener);

        //Este botón muestra un fragment de como funciona el modo de creación por horarios
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoInfo();
            }
        });

        //Lee los horarios creados por el usuario para sobreescribirlos en la base de datos
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                createNotification();

                fechaInicioInt = convertirFecha(fechaInicio.getText().toString());
                fechaFinInt = convertirFecha(fechaFin.getText().toString());
                horaInicioInt = convertHora(horaInicio.getText().toString());
                horaFinInt = convertHora(horaFin.getText().toString());
                inicio = ((fechaInicioInt*Math.pow(10,-4)) + (horaInicioInt*Math.pow(10,4)));
                fin = ((fechaFinInt*Math.pow(10,-4)) + (horaFinInt*Math.pow(10,4)));

                Map<String, Object> horario = new HashMap<>();
                horario.put("Inicio", inicio);
                horario.put("Fin", fin);
                horario.put("Active", active);

                reference.child(verCorreo.getText().toString()).child("Dispositivos").child(lam.textSpinner.getText().toString()).child("Horario").setValue(horario).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CrearHorarioActivity.this, "Datos escritos con exito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearHorarioActivity.this, "Datos no escritos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //Método necesario para convertir las fechas en un entero que pueda leer el ESP32
    private int convertirFecha(String dateString) {
        String dateWithoutSlash = dateString.replace("/", "");
        return Integer.parseInt(dateWithoutSlash);
    }

    //Método necesario para convertir las horas en un entero que pueda leer el ESP32
    private int convertHora(String timeString) {
        String timeWithoutColon = timeString.replace(":", "");
        return Integer.parseInt(timeWithoutColon);
    }

    //Método el cual contiene el fragment de información de uso del modo de horarios
    private void mostrarDialogoInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear horario");
        builder.setMessage("Esta función permite controlar un electrodoméstico automáticamente mediante tiempos de activación y finalización. Las fechas y horas no pueden ser las mismas entre sí.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //Método para instanciar una notificación

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Horario creado";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    //Método para editar y mostrar el contenido una notificación
    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_logoview);
        builder.setContentTitle("Horario creado");
        builder.setContentText("Fecha de activación: " + fechaInicio.getText().toString() + "\nHora de activación: " + horaInicio.getText().toString() + "\nFecha de finalización: " + fechaFin.getText().toString() + "\nHora de finalización: " + horaFin.getText().toString());
        builder.setColor(Color.GREEN);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.WHITE, 1000, 1000);
        builder.setVibrate(new long[]{2000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Fecha de activación: " + fechaInicio.getText().toString() + "\nHora de activación: " + horaInicio.getText().toString() + "\nFecha de finalización: " + fechaFin.getText().toString() + "\nHora de finalización: " + horaFin.getText().toString()));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }
    //Método para mostrar un calendario al usuario
    public void mostrarCalendarioInicio(View view) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(CrearHorarioActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fechaInicio.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, dayOfMonth);

        dialog.show();
    }

    //Método para mostrar un reloj al usuario
    public void mostrarCalendarioFin(View view) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(CrearHorarioActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fechaFin.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, dayOfMonth+1);

        dialog.show();
    }

    public void mostrarHorarioInicio(View view) {

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(CrearHorarioActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                horaInicio.setText(formattedTime);
            }
        }, hour, minute, true);
        dialog.show();
    }

    public void mostrarHorarioFin(View view) {

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(CrearHorarioActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                horaFin.setText(formattedTime);
            }
        }, hour, minute+1, true);
        dialog.show();
    }
}