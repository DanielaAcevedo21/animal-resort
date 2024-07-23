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
import android.widget.Spinner;
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

public class Peluqueria_new extends AppCompatActivity {
    private ImageButton mainMenu;

    private TextView fechacorte, dia_corte, numMascotas, precio_total, direccionCorte;
    private ListView listView_mascotas;
    private final ArrayList<String> lista_mascotas = new ArrayList<>();
    private final ArrayList<Mascota>mascotaArray= new ArrayList<>();
    private final ArrayList<Integer> posicionesCheck = new ArrayList<>();
    private ArrayAdapter <String> mAdapter;
    private DatePickerDialog.OnDateSetListener DateSet;
    private static final String TAG="Peluqueria_new";
    private final ArrayList<String> lista_direcciones = new ArrayList<>();
    private ArrayList<String> lista_descuentos;
    private final ArrayList<String> id_mascota_selecc= new ArrayList<>();
    private Button reservar;
    private final String url="http://app.animalresort.com.co/insertarReservasNew.php";
    private final String url2="http://app.animalresort.com.co/insertarMascotasReservadasNew.php";
    private String id_usuario_mascota, id_direccion="1", F_ini,invocador="sin invocador";
    private  int idServicio, añoCal,mesCal,diaCal;
    //adicionales
    private String[]listaAdicionales;
    private String serviciosEspecificos="Corte";
    private ListView listViewAdicionales;
    private double precioAdicionales=0.0;
    private String check="";

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peluqueria_new);


        mainMenu=findViewById(R.id.mainMenu);
        fechacorte=findViewById(R.id.fecha_corte);
        listView_mascotas=findViewById(R.id.list_mascotas_corte);
        direccionCorte=findViewById(R.id.direccion_corte);
        dia_corte=findViewById(R.id.dia_corte);
        numMascotas=findViewById(R.id.num_masc_corte);
        reservar=findViewById(R.id.guardar_corte);
        precio_total=findViewById(R.id.tv_precio_corte);

        listViewAdicionales=findViewById(R.id.seleccionar_adicionales_peluqueria);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        F_ini= dateFormat.format(tomorrow);

        fechacorte.setText(F_ini);

        Calendar cal= Calendar.getInstance();
        añoCal= cal.get(Calendar.YEAR);
        mesCal= cal.get(Calendar.MONTH);
        diaCal= cal.get(Calendar.DAY_OF_MONTH);


        consultarBD();
        traer_mascotas();
        traer_direcciones();
        posicionesCheck.add(0,0);

        if (lista_mascotas.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una mascota", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Peluqueria_new.this, ModificarMascotas.class);
            intent.putExtra("invocador","peluqueria");
            startActivity(intent);
            finish();
        }
        else  if (lista_direcciones.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una direccion", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Peluqueria_new.this, CUDirecciones.class);
            intent.putExtra("invocador","peluqueria");
            startActivity(intent);
            finish();
        }
        else{
            listView_mascotas.setItemChecked(0,true);


            if( getIntent().getExtras() != null)
            {

                if(getIntent().getStringExtra("tipo").equals("insert")){
                    invocador= getIntent().getStringExtra("servicios");
                    listaAdicionales=invocador.split("-");

                }else if(getIntent().getStringExtra("tipo").equals("update")){
                    invocador= getIntent().getStringExtra("id");
                    listaAdicionales=getIntent().getStringExtra("servicios").split("-");
                    reservar.setText("Actualizar");

                }else if(getIntent().getStringExtra("tipo").equals("preselec")){
                    invocador= getIntent().getStringExtra("servicios");
                    listaAdicionales=invocador.split("-");
                    check=getIntent().getStringExtra("check");

                }
            }


            traer_adicionales();

            if(!check.isEmpty()){
                listViewAdicionales.setItemChecked(Integer.parseInt(check),true);
                precioAdicionales=Integer.parseInt(lista_descuentos.get(Integer.parseInt(check)));
            }

            calPrecio();
        }

        listViewAdicionales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                precioAdicionales=Integer.parseInt(lista_descuentos.get(i));

                calPrecio();

            }
        });

        listView_mascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                calPrecio();

            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),mainMenu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup_perfil:
                                Intent intent = new Intent(Peluqueria_new.this, LoginGoogle.class);
                                startActivity(intent);
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Peluqueria_new.this, Mis_reservas.class);
                                startActivity(intent2);
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Peluqueria_new.this, Registrar_mascota.class);
                                mascotasChecked();
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Peluqueria_new.this, Direcciones.class);
                                mascotasChecked();
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Peluqueria_new.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Peluqueria_new.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Peluqueria_new.this, Mis_compras.class);
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

        direccionCorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(Peluqueria_new.this, Direcciones.class);
                mascotasChecked();
                startActivity(inten);
            }
        });

        fechacorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog= new DatePickerDialog(
                        Peluqueria_new.this,
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
                fechacorte.setText(F_ini);
                dia_corte.setText(fechacorte.getText().toString());
                mesCal--;

            }
        };

        listView_mascotas.setOnTouchListener(new ListView.OnTouchListener() {

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

        reservar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (id_mascota_selecc.size()>=1) id_mascota_selecc.clear();

                for (int i=0;i<mascotaArray.size();i++){
                    if (listView_mascotas.isItemChecked(i)){
                        id_mascota_selecc.add(mascotaArray.get(i).getId_mas());
                    }
                }
                if(id_mascota_selecc.size()<1){
                    Toast.makeText(getApplicationContext(), "Debes seleccionar al menos una mascota", Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(Peluqueria_new.this,"Un momento \uD83D\uDE0A ",Toast.LENGTH_SHORT).show();

                    if(reservar.getText().equals("Actualizar")){

                        for (int i = 0; i <listaAdicionales.length ; i++) {
                            if (listViewAdicionales.isItemChecked(i)){
                                serviciosEspecificos=serviciosEspecificos+", "+listaAdicionales[i];
                            }
                        }

                        actualizarCorte();
                    }else{
                        for (int i = 0; i <listaAdicionales.length ; i++) {
                            if (listViewAdicionales.isItemChecked(i)){
                                serviciosEspecificos=serviciosEspecificos+", "+listaAdicionales[i];
                            }
                        }
                        subirCorte();
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
            listView_mascotas.setItemChecked(posicionesCheck.get(i),true);
        }
        posicionesCheck.clear();
        calPrecio();
        traer_direcciones();
    }
    public void mascotasChecked(){
        for (int i = 0; i < mascotaArray.size(); i++) {
            if (listView_mascotas.isItemChecked(i)) {
                posicionesCheck.add(i);
            }
        }
    }
    public void actualizarCorte(){

        String urlUp="http://app.animalresort.com.co/upDateReservas.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    upDateReservasLocal();

                }catch (Exception e){
                    Toast.makeText(Peluqueria_new.this,"en response de subirConcultaMedica "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Peluqueria_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
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
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","4");
                params.put("servicio_especifico",serviciosEspecificos);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Peluqueria_new.this);
        requestQueue.add(request);

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

            Toast.makeText(getApplicationContext(), "Corte actualizado", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Peluqueria_new.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    private  void upDateReservasLocal(){

        int descuento_result=0;

        SQLiteDatabase db2 = conn.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put(Utilidades.CAMPO_FECHA_INICIO,F_ini);
        values2.put(Utilidades.CAMPO_FECHA_FIN,F_ini);
        values2.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        values2.put(Utilidades.CAMPO_DESCUENTO, "0");
        values2.put(Utilidades.CAMPO_PRECIO_TOTAL,precio_total.getText().toString());
        values2.put(Utilidades.CAMPO_DIAS_RESERVA, "1");
        values2.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO, serviciosEspecificos);
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
                    Toast.makeText(Peluqueria_new.this,"en response de upDateReserva "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Peluqueria_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Peluqueria_new.this);
        requestQueue.add(request);
    }
    private void eliminarMascotasResLocal(){

        SQLiteDatabase db1 = conn.getReadableDatabase();
        db1.execSQL("DELETE  FROM mascotas_reservadas WHERE id_reservacion="+invocador);
        db1.close();

    }
    private void calPrecio() {

        double precio =0;

        int size= lista_mascotas.size();

        for (int i=0;i<size;i++){
            if (listView_mascotas.isItemChecked(i)){
                precio= Integer.parseInt( mascotaArray.get(i).getPeluqueria())+precioAdicionales;
            }
        }

        precio_total.setText(""+precio);
        dia_corte.setText(fechacorte.getText().toString());

    }

    public void traer_mascotas() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM reg_mascota WHERE activo_mascota='activo' ",null);
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
            listView_mascotas.setAdapter(mAdapter);
            cursor.close();
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_mascotas()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void traer_adicionales() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,listaAdicionales);
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
                direccionCorte.setText(ciudad+"-"+direccion);

                cursor.moveToNext();

            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_direcciones()  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void consultarBD() {

        lista_descuentos = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='4'",null);

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
            Toast.makeText(getApplicationContext(), "Error en funcion traer_descuento"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void subirCorte(){

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    registrarReservas(response);

                }catch (Exception e){
                    Toast.makeText(Peluqueria_new.this,"en response de subirConcultaMedica "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Peluqueria_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
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
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","4");
                params.put("estado_reserva", "Reserva exitosa");
                params.put("servicio_especifico",serviciosEspecificos);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Peluqueria_new.this);
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
        values.put(Utilidades.CAMPO_PRECIO_TOTAL,precio_total.getText().toString());
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

            Toast.makeText(getApplicationContext(), "Corte reservado", Toast.LENGTH_SHORT).show();
            notificarVenta();
            Intent intent = new Intent(Peluqueria_new.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Peluqueria_new.this,"en response de notificarVenta "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Peluqueria_new.this,"on response error notificarVenta "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("titulo", "Peluqueria Animal Resort");
                params.put("cuerpo", "Fecha servicio peluqueria: "+F_ini);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Peluqueria_new.this);
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
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva","1");
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","4");
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
                    Toast.makeText(Peluqueria_new.this,"en response de subirMascotasReservadas "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Peluqueria_new.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Peluqueria_new.this);
        requestQueue.add(request);

    }
}