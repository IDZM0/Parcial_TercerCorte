package com.example.proyecsql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNota;
    private Button buttonGuardarNota, buttonMostrarNotas;
    private NotasDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNota = findViewById(R.id.editTextNota);
        buttonGuardarNota = findViewById(R.id.buttonGuardarNota);
        buttonMostrarNotas = findViewById(R.id.buttonMostrarNotas);
        dbHelper = new NotasDatabaseHelper(this);

        buttonGuardarNota.setOnClickListener(v -> {
            String textoNota = editTextNota.getText().toString().trim();
            if (!textoNota.isEmpty()) {
                long id = dbHelper.insertarNota(textoNota);
                if (id > 0) {
                    Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
                    editTextNota.getText().clear();
                } else {
                    Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, escribe una nota", Toast.LENGTH_SHORT).show();
            }
        });

        buttonMostrarNotas.setOnClickListener(v -> {
            MostrarNotasFragment fragment = new MostrarNotasFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}