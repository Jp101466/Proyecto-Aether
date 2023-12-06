package com.app.aether;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SetDeviceActivity extends AppCompatActivity {

    Button btn_horario, btn_sensores;
    ImageButton btn_infoSet;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_device);

        btn_horario = findViewById(R.id.btn_horario);
        btn_sensores = findViewById(R.id.btn_sensores);
        btn_infoSet = findViewById(R.id.btn_infoSet);

        btn_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetDeviceActivity.this, CrearHorarioActivity.class));
            }
        });

        btn_sensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetDeviceActivity.this, SensorActivity.class));
            }
        });

        btn_infoSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoInfo();
            }
        });
    }

    private void mostrarDialogoInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Establecer control");
        builder.setMessage("Pudes controlar tus electrodomésticos mediante 3 formas distintas, estableciendo un horario de uso, estableciendo un control mediante sensores o ambas opciones simultaneamente, dentro de cada opción puedes hayar información más detallada al respecto.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}