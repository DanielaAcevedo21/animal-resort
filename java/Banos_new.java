package com.example.animalresort;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utilidades.Utilidades;

public class Banos_new extends AppCompatActivity {

    private TextView txvfechaBano, diaConsulta, numMascotas, precioTotal, direccionBano;
    private ListView listViewMascotas;

    private final ArrayList<String> lista_mascotas = new ArrayList<>();
    private final ArrayList<Mascota>mascotaArray= new ArrayList<>();
    private ArrayAdapter <String> mAdapter;
    private DatePickerDialog.OnDateSetListener DateSet;
    private static final String TAG="Banos_new";
    private final ArrayList<String> lista_direcciones = new ArrayList<>();
    private ArrayList<String> lista_descuentos;
    private final ArrayList<String> id_mascota_selecc= new ArrayList<>();
    private final ArrayList<Integer> posicionesCheck = new ArrayList<>();
    private Button guardarbtn;
    private final String url="http://app.animalresort.com.co/insertarReservasNew.php";
    private String invocador=" sin invocador";
    private final String url2="http://app.animalresort.com.co/insertarMascotasReservadasNew.php";
    private String id_usuario_mascota, id_direccion="1", F_ini;
    private  int idServicio,añoCal,mesCal,diaCal;
    //adicionales
    private String[]listaAdicionales;
    private String serviciosEspecificos="Baño";
    private ListView listViewAdicionales;
    private double precioAdicionales=0.0;
    private String check="";
    private TextView precioRaza;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banos_new);

        ImageButton menuPrincipal = findViewById(R.id.mainMenu);
        txvfechaBano= findViewById(R.id.fecha_bano);
        diaConsulta=findViewById(R.id.dia_bano);
        numMascotas= findViewById(R.id.num_masc_banos);
        precioTotal=findViewById(R.id.tv_precio_bano);
        direccionBano= findViewById(R.id.direccion_bano);

        precioRaza=findViewById(R.id.precio_banno_razas);
        listViewMascotas=findViewById(R.id.list_mascotas_bano);
        guardarbtn=findViewById(R.id.guardar_bano);

        listViewAdicionales=findViewById(R.id.seleccionar_adicionales_banno);

        //fechas
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        F_ini= dateFormat.format(tomorrow);
        Calendar cal= Calendar.getInstance();
        añoCal= cal.get(Calendar.YEAR);
        mesCal= cal.get(Calendar.MONTH);
        diaCal= cal.get(Calendar.DAY_OF_MONTH);

        txvfechaBano.setText(F_ini);
        diaConsulta.setText(F_ini);

        consultarBD();
        traer_mascotas();
        traer_direcciones();
        posicionesCheck.add(0,0);


        if (lista_mascotas.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una mascota", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Banos_new.this, ModificarMascotas.class);
            intent.putExtra("invocador","banos");
            startActivity(intent);
            finish();
        }
        else  if (lista_direcciones.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una direccion", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Banos_new.this, CUDirecciones.class);
            intent.putExtra("invocador","banos");
            startActivity(intent);
            finish();
        }
        else{
            listViewMascotas.setItemChecked(0,true);

            if( getIntent().getExtras() != null)
            {

                if(getIntent().getStringExtra("tipo").equals("insert")){
                    invocador= getIntent().getStringExtra("servicios");
                    listaAdicionales=invocador.split("-");

                }else if(getIntent().getStringExtra("tipo").equals("update")){
                    invocador= getIntent().getStringExtra("id");
                    listaAdicionales=getIntent().getStringExtra("servicios").split("-");
                    guardarbtn.setText("Actualizar");

                }else if(getIntent().getStringExtra("tipo").equals("preselec")){
                    invocador= getIntent().getStringExtra("servicios");
                    listaAdicionales=invocador.split("-");
                    check=getIntent().getStringExtra("check");

                }
            }

            traer_adicionales();

            if(!check.isEmpty()){
                listViewAdicionales.setItemChecked(Integer.parseInt(check),true);
                precioAdicionales=precioAdicionales+Integer.parseInt(lista_descuentos.get(Integer.parseInt(check)));
            }

            calPrecio();
        }

        listViewAdicionales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (listViewAdicionales.isItemChecked(i)){
                    precioAdicionales=precioAdicionales+Integer.parseInt(lista_descuentos.get(i));

                }else{
                    precioAdicionales=precioAdicionales-Integer.parseInt(lista_descuentos.get(i));
                }

                calPrecio();

            }
        });

        listViewMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                calPrecio();
            }
        });

        menuPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),menuPrincipal);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup_perfil:
                                Intent intent = new Intent(Banos_new.this, LoginGoogle.class);
                                startActivity(intent);
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Banos_new.this, Mis_reservas.class);
                                startActivity(intent2);
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Banos_new.this, Registrar_mascota.class);
                                mascotasChecked();
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Banos_new.this, Direcciones.class);
                                mascotasChecked();
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Banos_new.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Banos_new.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Banos_new.this, Mis_compras.class);
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

        direccionBano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(Banos_new.this, Direcciones.class);
                mascotasChecked();
                startActivity(inten);
            }
        });

        txvfechaBano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog= new DatePickerDialog(
                        Banos_new.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        DateSet,
                        añoCal,mesCal,diaCal);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
            }
        });
        DateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mes, int dia) {
                añoCal=anno;
                mesCal=mes+1;
                diaCal=dia;

                Log.d(TAG,"OnDateSet: Date: "+añoCal+"/"+mesCal+"/"+diaCal);
                F_ini= añoCal+"-"+mesCal+"-"+diaCal;
                txvfechaBano.setText(F_ini);
                diaConsulta.setText(txvfechaBano.getText().toString());
                mesCal--;
            }
        };

        listViewMascotas.setOnTouchListener(new ListView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        listViewAdicionales.setOnTouchListener(new ListView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        guardarbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (id_mascota_selecc.size()>=1) id_mascota_selecc.clear();

                for (int i=0;i<mascotaArray.size();i++){
                    if (listViewMascotas.isItemChecked(i)){
                        id_mascota_selecc.add(mascotaArray.get(i).getId_mas());
                    }
                }
                if(id_mascota_selecc.size()<1){
                    Toast.makeText(getApplicationContext(), "Debes seleccionar al menos una mascota", Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(Banos_new.this,"Un momento \uD83D\uDE0A ",Toast.LENGTH_SHORT).show();

                    if(guardarbtn.getText().equals("Actualizar")){

                        for (int i = 0; i <listaAdicionales.length ; i++) {
                            if (listViewAdicionales.isItemChecked(i)){
                                serviciosEspecificos=serviciosEspecificos+", "+listaAdicionales[i];
                            }
                        }

                        actualizarBanno();
                    }else{

                        for (int i = 0; i <listaAdicionales.length ; i++) {
                            if (listViewAdicionales.isItemChecked(i)){
                                serviciosEspecificos=serviciosEspecificos+", "+listaAdicionales[i];
                            }
                        }
                        verificarDisponibilidad();
                    }
                }

            }

        });
    }



    @Override
    public void onResume(){
        super.onResume();
        traer_mascotas();
        for(int i=0;i<posicionesCheck.size();i++){
            listViewMascotas.setItemChecked(posicionesCheck.get(i),true);
        }
        posicionesCheck.clear();
        calPrecio();
        traer_direcciones();
    }

    public void mascotasChecked(){
        for (int i = 0; i < mascotaArray.size(); i++) {
            if (listViewMascotas.isItemChecked(i)) {
                posicionesCheck.add(i);
            }
        }
    }

    public void actualizarBanno(){

        String urlUp="http://app.animalresort.com.co/upDateReservas.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    upDateReservasLocal();

                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de actualizarBaño "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id",invocador);
                params.put("fecha_inicio",F_ini);
                params.put("fecha_fin",F_ini);
                params.put("direccion",id_direccion);
                params.put("descuento","0");
                params.put("precio_total",precioTotal.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","3");
                params.put("servicio_especifico",serviciosEspecificos);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);

    }
    private  void upDateReservasLocal(){

        int descuento_result=0;

        SQLiteDatabase db2 = conn.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put(Utilidades.CAMPO_FECHA_INICIO,F_ini);
        values2.put(Utilidades.CAMPO_FECHA_FIN,F_ini);
        values2.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        values2.put(Utilidades.CAMPO_DESCUENTO, "0");
        values2.put(Utilidades.CAMPO_PRECIO_TOTAL,precioTotal.getText().toString());
        values2.put(Utilidades.CAMPO_DIAS_RESERVA, "1");
        values2.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO,serviciosEspecificos);
        descuento_result = db2.update(Utilidades.TABLA_RESERVA,values2,"id =?", new String[]{invocador});
        db2.close();

        deleteUpDateMacotasReservadas();

    }
    private void deleteUpDateMacotasReservadas(){

        String url="http://app.animalresort.com.co/DeleteMascotasReservadas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    eliminarMascotasResLocal();
                    actualizarMascotasSelec(id_mascota_selecc);

                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de deleteUpDateMacotasReservadas baños "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id",invocador);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);
    }
    private void eliminarMascotasResLocal(){

        SQLiteDatabase db1 = conn.getReadableDatabase();
        db1.execSQL("DELETE  FROM mascotas_reservadas WHERE id_reservacion="+invocador);
        db1.close();

    }
    public void actualizarMascotasSelec(ArrayList<String>ids){

        try {

            SQLiteDatabase db2 = conn.getWritableDatabase();
            ContentValues values = new ContentValues();

            for (int i = 0; i < ids.size(); i++) {

                values.put(Utilidades.CAMPO_ID_RESERVACION, invocador);
                values.put(Utilidades.CAMPO_ID_MASCOTA, ids.get(i));
                values.put(Utilidades.CAMPO_USUARIO_MASCOTA_RES,id_usuario_mascota);
                Long fechaResult = db2.insert(Utilidades.TABLA_MASCOTAS_RESERVADAS,Utilidades.CAMPO_ID_MASCOTARES, values);

                subirMascotasReservadas(invocador,ids.get(i),id_usuario_mascota);
            }
            db2.close();

            Toast.makeText(getApplicationContext(), "Baño actualizado", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Banos_new.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error actualizarMascotasSelec baños "+ e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    private void calPrecio() {

        double precio =0;
        double precioRegular=0;

        int size= lista_mascotas.size();

        for (int i=0;i<size;i++){
            if (listViewMascotas.isItemChecked(i)){

                SQLiteDatabase db = conn.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT banno_server FROM precios_raza WHERE raza_server='"+mascotaArray.get(i).getRaza_mas()+"'", null);
                cursor.moveToFirst();
                precio= Integer.parseInt(cursor.getString(cursor.getColumnIndex("banno_server")))+precioAdicionales;
                precioRegular= Integer.parseInt(cursor.getString(cursor.getColumnIndex("banno_server")));
                cursor.close();
                db.close();
            }
        }
        precioRaza.setText(""+precioRegular);
        precioTotal.setText(""+precio);
        diaConsulta.setText(txvfechaBano.getText().toString());

    }

    public void verificarDisponibilidad(){

        String urlVerif="http://app.animalresort.com.co/verificarDisponibilidad.php";

        StringRequest request = new StringRequest(Request.Method.POST, urlVerif, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                   // Toast.makeText(Banos_new.this,"response: "+response,Toast.LENGTH_SHORT).show();

                    if (Integer.parseInt(response)<5){
                        subirBanno();
                    }else{
                        Toast.makeText(Banos_new.this,"El cupo está completo en esta fecha, prueba con otra",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de verificarDisponibilidad "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error verificarDisponibilidad "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("fecha_inicio",F_ini);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);

    }

    public void subirBanno(){

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    registrarReservas(response);

                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de subirBanno "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error subirBanno "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("fecha_inicio",F_ini);
                params.put("fecha_fin",F_ini);
                params.put("direccion",id_direccion);
                params.put("descuento","0");
                params.put("precio_total",precioTotal.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","5");
                params.put("estado_reserva", "Reserva exitosa");
               params.put("servicio_especifico",serviciosEspecificos);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);

    }

    private void registrarReservas(String id_servidor) {

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Utilidades.CAMPO_ID,id_servidor);

        values.put(Utilidades.CAMPO_FECHA_INICIO, F_ini);
        values.put(Utilidades.CAMPO_FECHA_FIN, F_ini);
        values.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        values.put(Utilidades.CAMPO_DESCUENTO,"0");
        values.put(Utilidades.CAMPO_ESTADO_RESERVA,"Reserva exitosa");
        values.put(Utilidades.CAMPO_PRECIO_TOTAL,precioTotal.getText().toString());
        values.put(Utilidades.CAMPO_DIAS_RESERVA,"1" );
        values.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO,serviciosEspecificos);

        Long fechaResult = db.insert(Utilidades.TABLA_RESERVA,null, values);

        db.close();

        registrarMascotasSelec(id_mascota_selecc);

    }
    public void registrarMascotasSelec(ArrayList<String>ids){

        try {

            SQLiteDatabase db = conn.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT MAX(id) as id FROM reserva", null);

            cursor.moveToFirst();
            String  id_ultima_reserva = cursor.getString(0);

            db.close();

            SQLiteDatabase db2 = conn.getWritableDatabase();
            ContentValues values = new ContentValues();

            for (int i = 0; i < ids.size(); i++) {

                values.put(Utilidades.CAMPO_ID_RESERVACION, id_ultima_reserva);
                values.put(Utilidades.CAMPO_ID_MASCOTA, ids.get(i));
                values.put(Utilidades.CAMPO_USUARIO_MASCOTA_RES,id_usuario_mascota);
                Long fechaResult = db2.insert(Utilidades.TABLA_MASCOTAS_RESERVADAS,Utilidades.CAMPO_ID_MASCOTARES, values);

                subirMascotasReservadas(id_ultima_reserva,ids.get(i),id_usuario_mascota);
            }
            db2.close();
            notificarVenta();
            Toast.makeText(getApplicationContext(), "Baño reservado", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Banos_new.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error registrarMascotasSelec "+ e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    private void notificarVenta(){

        String url="http://app.animalresort.com.co/Notificacion.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    notificarVentaCorreo();

                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de notificarVenta "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error notificarVenta "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("titulo", "Baño Animal Resort");
                params.put("cuerpo", "Fecha servicio baño: "+F_ini);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);
    }

    private void notificarVentaCorreo(){

        String url="http://app.animalresort.com.co/enviarCorreoServicios.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"en response de notificarVentaCorreo "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"on response error notificarVentaCorreo "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("fecha_inicio",F_ini);
                params.put("fecha_fin",F_ini);
                params.put("direccion",id_direccion);
                params.put("descuento","0");
                params.put("precio_total",precioTotal.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","5");
                params.put("estado_reserva", "Reserva exitosa");
                params.put("servicio_especifico",serviciosEspecificos);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
    public void subirMascotasReservadas(String idReserva, String id_mascota, String id_usuario){

        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                }catch (Exception e){
                    Toast.makeText(Banos_new.this,"en response de subirMascotasReservadas baños "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Banos_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id_reservacion",idReserva);
                params.put("id_mascota", id_mascota);
                params.put("usuario_mascota_res",id_usuario);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Banos_new.this);
        requestQueue.add(request);

    }
    private void consultarBD() {

        lista_descuentos = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='5'",null);

        try {
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++){

                String valor = cursor.getString(cursor.getColumnIndex("valor_descuento"));
                lista_descuentos.add(valor);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_descuento baño "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void traer_mascotas() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM reg_mascota WHERE activo_mascota='activo'",null);
        try {
            cursor.moveToFirst();
            if (lista_mascotas.size()>=1) lista_mascotas.clear();
            if (mascotaArray.size()>=1) mascotaArray.clear();

            for(int i=0;i<cursor.getCount();i++){

                String id_mascotas = cursor.getString(cursor.getColumnIndex("id_masc"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre_mascota"));
                String años = cursor.getString(cursor.getColumnIndex("anios_masc"));
                String meses=cursor.getString(cursor.getColumnIndex("meses_masc"));
                String raza = cursor.getString(cursor.getColumnIndex("raza_masc"));
                String tamanno= cursor.getString(cursor.getColumnIndex("tamanno_mascota"));
                String bano= cursor.getString(cursor.getColumnIndex("precio_banno"));
                String peluqueria= cursor.getString(cursor.getColumnIndex("precio_peluqueria"));

                id_usuario_mascota= cursor.getString(cursor.getColumnIndex("usuario_mascota"));

                lista_mascotas.add(nombre);

                Mascota m = new Mascota();
                m.setId_mas(id_mascotas);
                m.setNombre_mas(nombre);
                m.setAños_mas(años);
                m.setMeses_mas(meses);
                m.setTamaño_mas(tamanno);
                m.setRaza_mas(raza);
                m.setBanno(bano);
                m.setPeluqueria(peluqueria);
                mascotaArray.add(m);

                cursor.moveToNext();
            }
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,lista_mascotas);
            listViewMascotas.setAdapter(mAdapter);
            cursor.close();
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error traer_mascotas baños"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void traer_adicionales() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_multiple2,listaAdicionales);
        listViewAdicionales.setAdapter(adapter);
    }

    public void traer_direcciones() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT direccion,ciudad, id_direccion FROM mis_direcciones WHERE seleccion_defecto='true'",null);
        try {
            cursor.moveToFirst();

            if (lista_direcciones.size()>=1) lista_direcciones.clear();

            for(int i=0;i<cursor.getCount();i++){

                id_direccion= cursor.getString(cursor.getColumnIndex("id_direccion"));
                String direccion = cursor.getString(cursor.getColumnIndex("direccion"));
                String ciudad = cursor.getString(cursor.getColumnIndex("ciudad"));
                lista_direcciones.add(ciudad+", "+direccion);
                direccionBano.setText(ciudad+"-"+direccion);

                cursor.moveToNext();

            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error traer_direcciones baños"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}