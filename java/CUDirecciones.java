package com.example.animalresort;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import utilidades.Utilidades;

public class CUDirecciones extends AppCompatActivity {

    private EditText direccion, descripcionCasa;
    private RadioButton sogaRb, tunjRb;
    private String ciudad = "", invocador;
    private String usuario_direccion;
    private Button guardarDirecBtn;
    private String defecto,ToF;
    private int idActualizarDireccion;

    private final String url = "http://app.animalresort.com.co/insertarDireccionesNew.php";
    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_reservas", null, 1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cudirecciones);

        direccion = findViewById(R.id.direccion_cu);
        descripcionCasa = findViewById(R.id.desc_direccioncu);
        sogaRb = findViewById(R.id.sogamosoRB);
        tunjRb = findViewById(R.id.tunjaRB);
        guardarDirecBtn = findViewById(R.id.guardar_direccion);

        traerDefecto();

        if (getIntent().getExtras() != null) {
            invocador = getIntent().getStringExtra("invocador");

            if (invocador.equals("guarderia") || invocador.equals("hotel") || invocador.equals("consulta") || invocador.equals("banos") || invocador.equals("peluqueria") || invocador.equals("crear")) {
                guardarDirecBtn.setText("CREAR");
                sogaRb.setChecked(true);
                ciudad = "Sogamoso";
            } else {
                guardarDirecBtn.setText("Actualizar");
                String[] cadenaDatos = invocador.split(",");
                idActualizarDireccion = Integer.parseInt(cadenaDatos[0]);
                direccion.setText(cadenaDatos[1]);
                descripcionCasa.setText(cadenaDatos[3]);

                if (cadenaDatos[2].equals("Sogamoso")) {
                    sogaRb.setChecked(true);
                    ciudad = "Sogamoso";
                } else {
                    tunjRb.setChecked(true);
                    ciudad = "Tunja";
                }

            }
        }

        guardarDirecBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (descripcionCasa.getText().toString().trim().equalsIgnoreCase("") || direccion.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    if (guardarDirecBtn.getText().toString().equals("Actualizar")) {
                        actualizarDireccionServer();
                    } else {
                        subirDirecciones();
                    }
                }
            }
        });

    }

    public void onRadioButtonClicked(View v) {

        boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.sogamosoRB:
                if (checked)
                    ciudad = "Sogamoso";
                break;
            case R.id.tunjaRB:
                if (checked)
                    ciudad = "Tunja";
                break;
        }
    }

    public void subirDirecciones() {

        SQLiteDatabase db1 = conn.getReadableDatabase();
        Cursor cursorUsuario = db1.rawQuery("SELECT id_usuario FROM usuario", null);
        cursorUsuario.moveToFirst();
        usuario_direccion = cursorUsuario.getString(cursorUsuario.getColumnIndex("id_usuario"));
        db1.close();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    guardarDatos(response);

                } catch (Exception e) {
                    Toast.makeText(CUDirecciones.this, "en response de subirDirecciones " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(CUDirecciones.this, "on response error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("ciudad", ciudad);
                params.put("direccion", direccion.getText().toString());
                params.put("defecto", "true");
                params.put("descripcion", descripcionCasa.getText().toString());
                params.put("usuario_direccion", usuario_direccion);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CUDirecciones.this);
        requestQueue.add(request);
    }

    private void guardarDatos(String id_res) {

        try {
            SQLiteDatabase db = conn.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_ID_DIRECCIONES, Integer.parseInt(id_res));
            values.put(Utilidades.CAMPO_CIUDAD_DIRECCION, ciudad);
            values.put(Utilidades.CAMPO_DIRECCION, direccion.getText().toString());
            values.put(Utilidades.CAMPO_SELECCION_DEFECTO, "true");
            values.put(Utilidades.CAMPO_DESC_CASA, descripcionCasa.getText().toString());
            values.put(Utilidades.CAMPO_ID_USUARIO_DIRECCION, usuario_direccion);

            Long resultadoRegistro = db.insert(Utilidades.TABLA_DIRECCIONES, null, values);

            db.close();
            if (defecto != null) {
                cambiarDireccionDefectoServer(defecto, id_res);
            }

            Toast.makeText(getApplicationContext(), "Direccion guardada", Toast.LENGTH_SHORT).show();

            descripcionCasa.setText("");
            direccion.setText("");

            if (invocador.equals("guarderia")) {
                Intent intent = new Intent(CUDirecciones.this, Guarderia_calendar.class);
                startActivity(intent);
                finish();
            } else if (invocador.equals("hotel")) {
                Intent intent = new Intent(CUDirecciones.this, Hotel_reservar.class);
                startActivity(intent);
                finish();
            } else if (invocador.equals("peluqueria")) {
                Intent intent = new Intent(CUDirecciones.this, Peluqueria_new.class);
                startActivity(intent);
                finish();
            } else if (invocador.equals("consulta")) {
                Intent intent = new Intent(CUDirecciones.this, Consulta_medica.class);
                startActivity(intent);
                finish();
            } else if (invocador.equals("banos")) {
                Intent intent = new Intent(CUDirecciones.this, Banos_new.class);
                startActivity(intent);
                finish();
            } else if (invocador.equals("crear")) {
                Intent intent = new Intent(CUDirecciones.this, Direcciones.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error registro direcciones local !!!!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarDireccionServer() {
        String urlUpdate = "http://app.animalresort.com.co/upDateDireccion.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    actualizarDireccion();

                } catch (Exception e) {
                    Toast.makeText(CUDirecciones.this, "en response de actualizarDireccionServer " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(CUDirecciones.this, "on response error actualizarDireccionServer " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", Integer.toString(idActualizarDireccion));
                params.put("ciudad", ciudad);
                params.put("direccion", direccion.getText().toString());
                params.put("descripcion", descripcionCasa.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CUDirecciones.this);
        requestQueue.add(request);
    }

    private void actualizarDireccion() {

        int dirreccionResul = 0;

        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_CIUDAD_DIRECCION, ciudad);
        values.put(Utilidades.CAMPO_DIRECCION, direccion.getText().toString());
        values.put(Utilidades.CAMPO_DESC_CASA, descripcionCasa.getText().toString());
        dirreccionResul = db.update(Utilidades.TABLA_DIRECCIONES, values, "id_direccion =?", new String[]{String.valueOf(idActualizarDireccion)});
        db.close();
        Toast.makeText(getApplicationContext(), "Direccion actualizada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CUDirecciones.this, Direcciones.class);
        startActivity(intent);
        finish();

    }

    public void cambiarDireccionDefecto(String old, String n3w) {

        try {

            int descuento_result = 0;

            SQLiteDatabase db = conn.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_SELECCION_DEFECTO, "false");
            descuento_result = db.update(Utilidades.TABLA_DIRECCIONES, values, "id_direccion =?", new String[]{old});
            db.close();

            SQLiteDatabase db2 = conn.getWritableDatabase();
            ContentValues values2 = new ContentValues();
            values2.put(Utilidades.CAMPO_SELECCION_DEFECTO, "true");
            descuento_result = db2.update(Utilidades.TABLA_DIRECCIONES, values2, "id_direccion =?", new String[]{n3w});
            db2.close();

            defecto = n3w;

            Toast.makeText(getApplicationContext(), "Direccion por defecto establecida", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error en funcion direccionDefecto()" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    public void cambiarDireccionDefectoServer(String old, String idNew) {

        String urlServer = "http://app.animalresort.com.co/cambiarDireccionDefecto.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlServer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    cambiarDireccionDefecto(old, idNew);
                } catch (Exception e) {
                    Toast.makeText(CUDirecciones.this, "en response de subirDirecciones " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CUDirecciones.this, "on response error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("old", old);
                params.put("idNew", idNew);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CUDirecciones.this);
        requestQueue.add(request);
    }

    public void traerDefecto() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT id_direccion,seleccion_defecto  FROM mis_direcciones",null);
        try {

            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++){

                ToF= cursor.getString(cursor.getColumnIndex("seleccion_defecto"));

                if(ToF.equals("true")){
                    defecto= cursor.getString(cursor.getColumnIndex("id_direccion"));
                }

                cursor.moveToNext();
            }
            cursor.close();
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_direcciones()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}