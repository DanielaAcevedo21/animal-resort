package com.example.animalresort;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import utilidades.Utilidades;
public class Login extends AppCompatActivity {

    private TextView  registro;
    private EditText txt_mail;
    private Button btn_log;
    String url2 = "http://app.animalresort.com.co/verificacionCorreo.php";
    String url3 = "http://app.animalresort.com.co/traerDatosUsuario.php";
    String url4 = "http://app.animalresort.com.co/traerDatosMascotas.php";
    String url6 = "http://app.animalresort.com.co/traerDatosDirecciones.php";
    String url7 = "http://app.animalresort.com.co/traerDatosReservas.php";
    String url8 = "http://app.animalresort.com.co/traerDatosMascotasRes.php";
    String url9 = "http://app.animalresort.com.co/traerProductosTienda.php";
    String url11 = "http://app.animalresort.com.co/traerEtiquetasProductos.php";
    static String email, id_usu, TIPO_USUARIO, version_app;
    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_reservas", null, 1);
    long timeEnd;
    long timeInicio =System.currentTimeMillis();
    private static boolean sesionActiva=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_button);

        txt_mail=findViewById(R.id.gmail);
        btn_log=findViewById(R.id.btn_log);
        registro=findViewById(R.id.registrar);

        version_app=BuildConfig.VERSION_NAME;

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

        //Toast.makeText(this, "antes de verificar version", Toast.LENGTH_SHORT).show();
        sesionActiva();
        verificarVersion();

         btn_log.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (txt_mail.getText().toString().equals("")){
                     Toast.makeText(getApplicationContext(), "Debes ingresar un email", Toast.LENGTH_SHORT).show();
                 }
                 else{
                     email=txt_mail.getText().toString();
                     verificarExistencia();
                 }
             }
         });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registrar.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sesionActiva(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT mail_cuenta FROM cuenta", null);

        try {

            if (cursor.getCount() == 0) {

            } else {

                cursor.moveToFirst();
                txt_mail.setText(cursor.getString(cursor.getColumnIndex("mail_cuenta")));
                cursor.close();
                sesionActiva=true;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error en sesionActiva " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void verificarVersion() {
        String url_verificacion = "http://app.animalresort.com.co/verificarVersionApk.php";

        StringRequest request = new StringRequest(Request.Method.POST, url_verificacion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                //    Toast.makeText(Login.this, "respose verificar", Toast.LENGTH_SHORT).show();
                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos =jsonArray.getJSONObject(jsonArray.length()-1);
                    String version= datos.optString("version");
                    String notes=datos.optString("patch_notes");

                    if (version.equals(version_app) ){
                  //      Toast.makeText(Login.this, "misma version", Toast.LENGTH_SHORT).show();
                    } else {
                    //    Toast.makeText(Login.this, "actualizar", Toast.LENGTH_SHORT).show();
                        String urlDescarga = "http://animalresort.com.co/app-debug.apk";

                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        builder.setTitle("Nueva version disponible:");
                        builder.setMessage("Desea actualizar ahora? "+notes);

                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Uri link = Uri.parse(urlDescarga);
                                Intent descarga = new Intent(Intent.ACTION_VIEW, link);

                                startActivity(descarga);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response de verificarVersion " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error verificarVersion " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }

    public void verificarExistencia() {
        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response.equals("0")) {
                        Toast.makeText(Login.this, "Usuario no registrado" , Toast.LENGTH_SHORT).show();
                        txt_mail.setText("");

                    } else {

                        SQLiteDatabase db = conn.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(Utilidades.CAMPO_MAIL_CUENTA, txt_mail.getText().toString());

                        if(!sesionActiva){
                            Long resultadoRegistro = db.insert(Utilidades.TABLA_CUENTA, null, values);
                            db.close();
                        }else {
                            int usuario_result = db.update(Utilidades.TABLA_CUENTA, values, null,null);
                            db.close();
                        }

                       traerDatosUsuario();
                    }

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response verificarExistencia " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, "on response error verificarExistencia " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }

    public void traerDatosUsuario() {

        StringRequest request = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    String[] vector = response.split(",");

                    SQLiteDatabase db = conn.getWritableDatabase();

                    ContentValues values = new ContentValues();

                    id_usu = vector[0];

                    values.put(Utilidades.CAMPO_ID_USUARIO, Integer.parseInt(vector[0]));
                    values.put(Utilidades.CAMPO_NOMBRE_USUARIO, vector[1]);
                    values.put(Utilidades.CAMPO_MAIL_USUARIO, vector[2]);
                    values.put(Utilidades.CAMPO_DOCUMENTO_USUARIO, vector[3]);
                    values.put(Utilidades.CAMPO_CELULAR_USUARIO, vector[4]);
                    values.put(Utilidades.CAMPO_TIPO_DOCUMENTO, vector[5]);
                    values.put(Utilidades.CAMPO_TIPO_USUARIO, vector[6]);
                    Long resultadoRegistro = db.insert(Utilidades.TABLA_USUARIO, null, values);

                    TIPO_USUARIO = vector[6];
                    db.close();

                    GestionDescarga descarga1= new GestionDescarga(4);
                    Thread hilo= new Thread(descarga1);
                    GestionDescarga descarga2= new GestionDescarga(5);
                    Thread hilo2= new Thread(descarga2);
                    GestionDescarga descarga3= new GestionDescarga(6);
                    Thread hilo3= new Thread(descarga3);

                    hilo.start();
                    hilo2.start();
                    hilo3.start();
                    try {
                        hilo.join();
                        hilo2.join();
                        hilo3.join();
                    }catch (Exception exception){

                    }

                    nextDownLoad();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerDatosUsuario " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, "on response error traerDatosUsuario " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }

    private void nextDownLoad(){

        GestionDescarga descarga1= new GestionDescarga(1);
        Thread hilo= new Thread(descarga1);
        GestionDescarga descarga2= new GestionDescarga(2);
        Thread hilo2= new Thread(descarga2);
        GestionDescarga descarga3= new GestionDescarga(3);
        Thread hilo3= new Thread(descarga3);

        hilo.start();
        hilo2.start();
        hilo3.start();
        try {
            hilo.join();
            hilo2.join();
            hilo3.join();
        }catch (Exception exception){

        }
    }
    public void traerDatosMascotas() {

        StringRequest request = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);

                    JSONArray jsonArray = datosBajados.optJSONArray("datos");

                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        values.put(Utilidades.CAMPO_ID_MASC, datos.optString("id"));
                        values.put(Utilidades.CAMPO_NOMBRE, datos.optString("nombre_mascota"));
                        values.put(Utilidades.CAMPO_ANIOS, datos.optString("anios"));
                        values.put(Utilidades.CAMPO_MESES, datos.optString("meses"));
                        values.put(Utilidades.CAMPO_RAZA, datos.optString("raza"));
                        values.put(Utilidades.CAMPO_GENERO, datos.optString("genero"));
                        values.put(Utilidades.CAMPO_TAMANNO, datos.optString("peso_mascota"));
                        values.put(Utilidades.CAMPO_USUARIO_MASCOTA, datos.optString("usuario_mascota"));
                        values.put(Utilidades.CAMPO_ACTIVO_MASCOTA, datos.optString("estado_mascota"));
                        values.put(Utilidades.CAMPO_PRECIO_BANNO, datos.optString("banno"));
                        values.put(Utilidades.CAMPO_PRECIO_PELUQUERIA, datos.optString("peluqueria"));

                        Long resultadoRegistro = db.insert(Utilidades.TABLA_REG_MASCOTA, null, values);
                    }

                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerDatosMascotas " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerDatosMascotas " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario_mascota", id_usu);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }

    public void traerDirecciones() {

        StringRequest request = new StringRequest(Request.Method.POST, url6, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);

                    JSONArray jsonArray = datosBajados.optJSONArray("datos");

                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);

                        values.put(Utilidades.CAMPO_ID_DIRECCIONES, datos.optString("id"));
                        values.put(Utilidades.CAMPO_CIUDAD_DIRECCION, datos.optString("ciudad"));
                        values.put(Utilidades.CAMPO_DIRECCION, datos.optString("direccion"));
                        values.put(Utilidades.CAMPO_SELECCION_DEFECTO, datos.optString("defecto"));
                        values.put(Utilidades.CAMPO_DESC_CASA, datos.optString("descripcion"));
                        values.put(Utilidades.CAMPO_ID_USUARIO_DIRECCION, datos.optString("usuario_direccion"));

                        Long resultadoDirecciones = db.insert(Utilidades.TABLA_DIRECCIONES, null, values);
                    }

                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerDatosDirecciones " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerDatosDirecciones " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("usuario_direccion", id_usu);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }

    public void traerReservas() {

        StringRequest request = new StringRequest(Request.Method.POST, url7, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);

                    JSONArray jsonArray = datosBajados.optJSONArray("datos");

                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    //
                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);

                        values.put(Utilidades.CAMPO_ID, datos.optString("id"));

                            values.put(Utilidades.CAMPO_FECHA_INICIO, datos.optString("fecha_inicio"));

                            values.put(Utilidades.CAMPO_FECHA_FIN, datos.optString("fecha_fin"));

                            values.put(Utilidades.CAMPO_CIUDAD, datos.optString("direccion"));

                            values.put(Utilidades.CAMPO_DESCUENTO, datos.optString("descuento"));

                            values.put(Utilidades.CAMPO_PRECIO_TOTAL, datos.optString("precio_total"));

                            values.put(Utilidades.CAMPO_DIAS_RESERVA, datos.optString("dias_reserva"));

                            values.put(Utilidades.CAMPO_USUARIO_RESERVA, datos.optString("id_usuario_reserva"));

                            values.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO, datos.optString("servicio_especifico"));

                            values.put(Utilidades.CAMPO_ESTADO_RESERVA, datos.optString("estado_reserva"));

                            Long resultadoReservas = db.insert(Utilidades.TABLA_RESERVA, null, values);

                    }

                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerReservas " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerReservas " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario_reserva", id_usu);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }

    public void traerMascotasReservadas() {
        StringRequest request = new StringRequest(Request.Method.POST, url8, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        values.put(Utilidades.CAMPO_ID_MASCOTARES, datos.optString("id"));
                        values.put(Utilidades.CAMPO_ID_RESERVACION, datos.optString("id_reservacion"));
                        values.put(Utilidades.CAMPO_ID_MASCOTA, datos.optString("id_mascota"));
                        values.put(Utilidades.CAMPO_USUARIO_MASCOTA_RES, datos.optString("usuario_mascota_res"));

                        Long resultadoMasReservas = db.insert(Utilidades.TABLA_MASCOTAS_RESERVADAS, null, values);
                    }

                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerDatosMascotasRes " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerDatosMascotasRes " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("usuario_mascota_res", id_usu);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }
    public void traerProductos() {

        StringRequest request = new StringRequest(Request.Method.POST, url9, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;
                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        values.put(Utilidades.CAMPO_ID_PRODUCTO, datos.optString("id"));
                        values.put(Utilidades.CAMPO_PRECIO_PROVEEDOR, datos.optString("precio_proveedor"));
                        values.put(Utilidades.CAMPO_PRECIO_VENTA, datos.optString("precio_venta"));
                        values.put(Utilidades.CAMPO_PRECIO_DESCUENTO, datos.optString("precio_descuento"));
                        values.put(Utilidades.CAMPO_NOMBRE_PRODUCTO, datos.optString("nombre_producto"));
                        values.put(Utilidades.CAMPO_DESCRIPCION_PRODUCTO, datos.optString("descripcion_producto"));
                        values.put(Utilidades.CAMPO_ESTRELLAS, datos.optString("estrellas"));
                        values.put(Utilidades.CAMPO_CODIGO_BARRAS, datos.optString("codigoBarras"));
                        values.put(Utilidades.CAMPO_CATEGORIA, datos.optString("categoria_productos"));
                        values.put(Utilidades.CAMPO_STOCK,datos.optString("stock"));
                        Long resultadoRegistro = db.insert(Utilidades.TABLA_PRODUCTOS_TIENDA, null, values);

                    }
                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerProductos " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }

    public void traerEtiquetas() {

        StringRequest request = new StringRequest(Request.Method.POST, url11, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);

                        values.put(Utilidades.CAMPO_ID_ETIQUETA, datos.optString("id"));
                        values.put(Utilidades.CAMPO_NOMBRE_ETIQUETA, datos.optString("nombreEtiqueta"));
                        values.put(Utilidades.CAMPO_VALOR_ETIQUETA, datos.optString("valorEtiqueta"));
                        values.put(Utilidades.CAMPO_ID_PRODUCTO_ETIQUETA, datos.optString("id_producto_etiqueta"));

                        Long resultadoRegistro = db.insert(Utilidades.TABLA_ETIQUETAS_PRODUCTOS, null, values);

                    }
                    db.close();
                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traer etiquetas " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error  traer etiquetas " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);
    }
    public void traerPrecios() {

        String urlPrecio = "http://app.animalresort.com.co/consultasPreciosNew.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlPrecio, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;
                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        values.put(Utilidades.CAMPO_ID_DESCUENTO, datos.optString("idprecios"));
                        values.put(Utilidades.CAMPO_NOMBRE_DESCUENTO, datos.optString("nombre_descu"));
                        values.put(Utilidades.CAMPO_DIAS_INICIO, datos.optString("min_dias"));
                        values.put(Utilidades.CAMPO_DIAS_FIN, datos.optString("max_dias"));
                        values.put(Utilidades.CAMPO_VALOR_DESCUENTO, datos.optString("precios"));
                        values.put(Utilidades.CAMPO_ID_SERVICIO2, datos.optString("productos_y_servicios_id"));
                        Long resultadoMasReservas = db.insert(Utilidades.TABLA_DESCUENTOS, null, values);
                    }

                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerPrecios " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerPrecios " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }

    public void traerComprasTienda() {

        String urlCompras = "http://app.animalresort.com.co/traerComprasCliente.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlCompras, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);

                        values.put(Utilidades.CAMPO_ID_VENTA, datos.optString("id"));
                        values.put(Utilidades.CAMPO_FECHA_VENTA, datos.optString("fecha_venta"));
                        values.put(Utilidades.CAMPO_PRECIO_VENTA_TIENDA, datos.optString("precio_venta"));
                        values.put(Utilidades.CAMPO_DESCUENTO_VENTA, datos.optString("descuento_venta"));
                        values.put(Utilidades.CAMPO_PRODUCTO, datos.optString("producto"));
                        values.put(Utilidades.CAMPO_ID_CLIENTE, datos.optString("id_cliente"));
                        values.put(Utilidades.CAMPO_DIRECCION_VENTA, datos.optString("direccion"));
                        values.put(Utilidades.CAMPO_CANTIDAD_PRODUCTO, datos.optString("cantidad"));
                        values.put(Utilidades.CAMPO_ESTADO_COMPRA, datos.optString("estado"));

                        Long resultadoMasReservas = db.insert(Utilidades.TABLA_VENTAS_TIENDA, null, values);
                    }
                    db.close();

                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerComprasTienda " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerComprasTienda " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", id_usu);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }

    public void traerRazas() {

        String urlCompras = "http://app.animalresort.com.co/traerRazas.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlCompras, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;

                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        values.put(Utilidades.CAMPO_ID_RAZA, datos.optString("id"));
                        values.put(Utilidades.CAMPO_RAZA_SERVER, datos.optString("raza"));
                        values.put(Utilidades.CAMPO_BANNO_SERVER, datos.optString("banno"));
                        values.put(Utilidades.CAMPO_PELUQUERIA_SERVER, datos.optString("peluqueria"));

                        Long result = db.insert(Utilidades.TABLA_RAZAS, null, values);
                    }

                    db.close();

                    if (TIPO_USUARIO.equals("Usuario")) {
                        timeEnd=System.currentTimeMillis();
                        Long timeSub = (timeEnd-timeInicio)/1000;
                        registrarTiempo(timeSub.intValue());
                        Intent intent = new Intent(Login.this, MenuUsuario.class);
                        startActivity(intent);
                        finish();
                    } else {

                        FirebaseMessaging.getInstance().subscribeToTopic("admin");
                        timeEnd=System.currentTimeMillis();

                        Long timeSub = (timeEnd-timeInicio)/1000;
                        registrarTiempo(timeSub.intValue());

                        Intent intent = new Intent(Login.this, Menu_usuAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(Login.this, "en response traerRazas " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "on response error traerRazas " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(request);

    }
    private void registrarTiempo(int tiempo){

        String urlTiempo="http://app.animalresort.com.co/analisisLogin.php";

        StringRequest request = new StringRequest(Request.Method.POST, urlTiempo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error subir tiempo "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("tiempo_login", Integer.toString(tiempo));
                params.put("id_usuario", id_usu);
                params.put("version", version_app);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    private void borrarTablas(String tabla) {

        try {
            SQLiteDatabase db1 = conn.getReadableDatabase();
            db1.execSQL("DELETE  FROM " + tabla);
            db1.close();
        } catch (Exception e) {
            Toast.makeText(Login.this, "Error: " + e.getMessage() + " tabla: " + tabla, Toast.LENGTH_SHORT).show();
        }
    }

    class GestionDescarga implements Runnable{

        private int part;

        public GestionDescarga(int part){
            this.part=part;
        }
        @Override
        public void run() {
            switch (part){
                case 1:
                    traerProductos();
                    break;
                case 2:
                    traerEtiquetas();
                    traerPrecios();
                    break;
                case 3:
                    traerComprasTienda();
                    traerRazas();
                    break;
                case 4:
                    traerDatosMascotas();
                    traerDirecciones();
                    break;
                case 5:
                    traerMascotasReservadas();
                    break;
                case 6:
                    traerReservas();
                    break;
            }
        }
    }
}