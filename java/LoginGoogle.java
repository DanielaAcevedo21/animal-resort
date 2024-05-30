package com.example.animalresort;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import utilidades.Utilidades;

public class LoginGoogle extends AppCompatActivity {
    ImageView imageView;
    Button signout;
    String url = "http://app.animalresort.com.co/updateUsuario.php";
    String str_documento, str_celular, str_tipo_doc;

    GoogleSignInClient mGoogleSignInClient;

    private Button botonGuardar;
    private static String docu_usu, numCel_usu, id_usu, tipo_doc_usu;
    private TextView name, email, accercaDe;
    private EditText documento, celular;
    private ImageButton mainMenu;
    private Spinner tipos_documento;
    private static String personEmail, nombre_usu;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        botonGuardar = findViewById(R.id.buttonGuardar);
        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signout = findViewById(R.id.button);
        documento = findViewById(R.id.documento);
        celular = findViewById(R.id.celular);
        mainMenu = findViewById(R.id.mainMenu);
        tipos_documento = findViewById(R.id.tipo_doc);
        accercaDe=findViewById(R.id.txAcercaDe);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(LoginGoogle.this, R.layout.item_spinner,
                getResources().getStringArray(R.array.tipo_doc));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipos_documento.setAdapter(myAdapter);

        signout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        accercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginGoogle.this, About_us.class);
                startActivity(intent);
            }
        });
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (documento.getText().toString().trim().equalsIgnoreCase("") || celular.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    actualizarDatos();
                    update_usuario();
                    //verificarBaseDatos();
                    Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), mainMenu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.popup_perfil:
                                Intent intent = new Intent(LoginGoogle.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(LoginGoogle.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(LoginGoogle.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(LoginGoogle.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor = db.rawQuery("SELECT tipo_usuario FROM usuario", null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if (!tipo_usu.equals("Usuario")) {
                                    Intent inten5 = new Intent(LoginGoogle.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                } else {
                                    Intent inten4 = new Intent(LoginGoogle.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(LoginGoogle.this, Mis_compras.class);
                                startActivity(intent6);
                                finish();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        datos_usuario();

    }

    private void actualizarDatos() {

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Utilidades.CAMPO_DOCUMENTO_USUARIO, documento.getText().toString());
        values.put(Utilidades.CAMPO_CELULAR_USUARIO, celular.getText().toString());
        values.put(Utilidades.CAMPO_TIPO_DOCUMENTO, tipos_documento.getSelectedItem().toString());
        int usuario_result = db.update(Utilidades.TABLA_USUARIO, values, "id_usuario =?", new String[]{id_usu});
        db.close();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        borrarTablas("reg_mascota");
                        borrarTablas("usuario");
                        borrarTablas("mis_direcciones");
                        borrarTablas("mascotas_reservadas");
                        borrarTablas("reserva");
                        borrarTablas("proservicios");
                        borrarTablas("descuentos");
                        borrarTablas("productos_tienda");
                        borrarTablas("multimedia_productos");
                        borrarTablas("etiqueta_productos");
                        borrarTablas("ventas_tienda");
                        borrarTablas("precios_raza");
                        borrarTablas("cuenta");

                        Toast.makeText(LoginGoogle.this, "Ha salido de su cuenta Correctamente!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginGoogle.this, Login.class);
                        startActivity(intent);
                        finish();

                    }
                });
    }

    private void datos_usuario() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_usuario, celular_usuario,documento_usuario, tipo_documento, mail_usuario,nombre_usuario FROM usuario", null);
        try {

            if (cursor.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "el cursor es 0", Toast.LENGTH_SHORT).show();
            } else {

                cursor.moveToFirst();
                id_usu = cursor.getString(cursor.getColumnIndex("id_usuario"));
                numCel_usu = cursor.getString(cursor.getColumnIndex("celular_usuario"));
                docu_usu = cursor.getString(cursor.getColumnIndex("documento_usuario"));
                tipo_doc_usu = cursor.getString(cursor.getColumnIndex("tipo_documento"));
                personEmail = cursor.getString(cursor.getColumnIndex("mail_usuario"));
                nombre_usu = cursor.getString(cursor.getColumnIndex("nombre_usuario"));
                cursor.close();

            }

            if (docu_usu.equals("vacio")) {
                documento.setText("");
                celular.setText("");
            } else {
                documento.setText(docu_usu);
                celular.setText(numCel_usu);
                name.setText(nombre_usu);
                email.setText(personEmail);
            }

            if (tipo_doc_usu.equals("Pasaporte")) {
                tipos_documento.setSelection(1);
            } else {
                tipos_documento.setSelection(0);
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error en verificarBaseDatos " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void update_usuario() {

        str_documento = documento.getText().toString().trim();
        str_celular = celular.getText().toString().trim();
        str_tipo_doc = tipos_documento.getSelectedItem().toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                } catch (Exception e) {
                    Toast.makeText(LoginGoogle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", id_usu);
                params.put("documento", str_documento);
                params.put("celular", str_celular);
                params.put("tipo_doc", str_tipo_doc);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginGoogle.this);
        requestQueue.add(request);
    }

    private void borrarTablas(String tabla) {
        SQLiteDatabase db1 = conn.getReadableDatabase();
        db1.execSQL("DELETE  FROM " + tabla);
        db1.close();
    }
}