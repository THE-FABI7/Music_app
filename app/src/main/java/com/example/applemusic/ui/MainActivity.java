package com.example.applemusic.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.applemusic.Datasource.ApleeMusicServiceApi;
import com.example.applemusic.Datasource.RemoteDataSource;

import com.example.applemusic.R;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch = null;
    private ScrollView request = null;

    private EditText buscar = null;

    private ProgressDialog progressDialog;

    private ApleeMusicServiceApi apleeMusicServiceApi = new ApleeMusicServiceApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();

    }

    public void initViews(){
        btnSearch = findViewById(R.id.search_button);
        buscar = findViewById(R.id.search_field);
    }

    public void initEvents(){
        btnSearch.setOnClickListener(view ->{
            showProgressDialog();
            requestMusic();

        });
    }

    public void requestMusic() {
        String buscarName = buscar.getText().toString();

        if (buscarName.isEmpty()) {
            progressDialog.dismiss();  // Cierra el diálogo si el campo está vacío
            Toast.makeText(MainActivity.this, "Por favor ingrese el nombre del artista", Toast.LENGTH_SHORT).show();
            return;
        }

        apleeMusicServiceApi.requestSongsByTerm(buscarName, 10,
                text -> {
                    // Handle the text response here
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.d("API_RESPONSE", text);
                        // Update your UI with the search results
                        Toast.makeText(MainActivity.this, "Búsqueda completada", Toast.LENGTH_SHORT).show();
                    });
                },
                errorCode -> {
                    // Handle the error code here
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.e("API_ERROR", "Error code: " + errorCode);
                        Toast.makeText(MainActivity.this, "Error en la búsqueda: " + errorCode, Toast.LENGTH_SHORT).show();
                    });
                }
        );



        // Simular una operación de red con un retardo
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();  // Cierra el diálogo después de completar la búsqueda
            // Aquí puedes agregar el código para manejar la búsqueda efectiva de la música
            Toast.makeText(MainActivity.this, "Búsqueda completada", Toast.LENGTH_SHORT).show();
        }, 2000); // 2000 milisegundos de simulación de búsqueda
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Buscando...");
        progressDialog.setCancelable(false);  // Hacer que el dialogo no sea cancelable
        progressDialog.show();
    }
}