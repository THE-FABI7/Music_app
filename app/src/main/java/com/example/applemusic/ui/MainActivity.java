package com.example.applemusic.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.applemusic.R;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch = null;
    private ScrollView request = null;

    private EditText buscar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    public void initViews(){
        btnSearch = findViewById(R.id.search_button);
        buscar = findViewById(R.id.search_field);
    }

    public void initEvents(){
        btnSearch.setOnClickListener(view ->{
         requestMusic();

        });
    }

    public void requestMusic(){
        String buscarName =  buscar.getText().toString();

        if (buscarName.isEmpty()) {
            // Mostrar mensaje de error si el campo está vacío
            Toast.makeText(MainActivity.this, "Por favor ingrese el nombre de la cancion", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}