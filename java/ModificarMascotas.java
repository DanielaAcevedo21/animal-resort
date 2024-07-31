package com.example.animalresort;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utilidades.Utilidades;

public class ModificarMascotas extends AppCompatActivity {

    private EditText nombre_mascota, anios_masc, meses_masc ;
    private Spinner genero_masc, peso_mascota,raza_masc;
    private Spinner especie_mascota;
    private Button UpdateMascota;
    private ImageButton mainMenu;
    private TextView tvSelecFecha;
    private String fecha,usuario_mascota,nombre_mas,anios_mas,meses_mas,raza_mas,genero_mas,tammano_mas,id_mascota_servidor;
    private String invocador="no hay invocador";
    private String[]datosMascotaIntent= new String[6];
    private String precioBanno;
    private String precioPeluqueria;

    /**
     * un array por tipo de mascota
     */
    ArrayList<String>razas= new ArrayList<>();
    ArrayList<String>ids= new ArrayList<>();

    private DatePickerDialog.OnDateSetListener DateSet;
    private static final String TAG="ModificarMascotas";
    private final String url= "http://app.animalresort.com.co/upDateDatosMascotas.php";
    private final String url2 = "http://app.animalresort.com.co/insertarMascotaNew.php";

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_mascota);

        nombre_mascota = findViewById(R.id.nombreAct);
        anios_masc = findViewById(R.id.aniosAct);
        meses_masc = findViewById(R.id.mesesAct);
        raza_masc = findViewById(R.id.razaAct);
        genero_masc =  findViewById(R.id.generoAct);
        peso_mascota =  findViewById(R.id.peso_mascotaAct);
        UpdateMascota = findViewById(R.id.btn_act_masc);
        mainMenu = findViewById(R.id.mainMenu);
        tvSelecFecha= findViewById(R.id.calendario_tambor);
        especie_mascota=findViewById(R.id.spinner_especies);

        traerRazas();

        meses_masc.setText("0");
        anios_masc.setText("0");
        fechaNacimiento();

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ModificarMascotas.this, R.layout.spinner_item,
                getResources().getStringArray(R.array.genero));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero_masc.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(ModificarMascotas.this, R.layout.spinner_item,
                getResources().getStringArray(R.array.peso_mascota));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        peso_mascota.setAdapter(myAdapter2);

        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(ModificarMascotas.this, R.layout.spinner_item,
                getResources().getStringArray(R.array.especies));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especie_mascota.setAdapter(myAdapter3);


        anios_masc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fechaNacimiento();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        meses_masc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fechaNacimiento();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        especie_mascota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                traerRazas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        UpdateMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre_mascota.getText().toString().trim().equalsIgnoreCase("") || anios_masc.getText().toString().trim().equalsIgnoreCase("") ||
                        meses_masc.getText().toString().trim().equalsIgnoreCase("") ){

                    Toast.makeText(getApplicationContext(), "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                }else {

                    if(UpdateMascota.getText().toString().equals("CREAR")){
                        Register();

                    }else{
                        upDateServeer();
                        actualizarDatos();
                        Toast.makeText(getApplicationContext(), nombre_mascota.getText().toString()+" Se ha actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(ModificarMascotas.this, Registrar_mascota.class);
                        startActivity(inte);
                        finish();
                    }

                }
            }
        });

        tvSelecFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal= Calendar.getInstance();
                int anno= cal.get(Calendar.YEAR);
                int mes= cal.get(Calendar.MONTH)+1;
                int dia= cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog= new DatePickerDialog(
                        ModificarMascotas.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        DateSet,
                        anno,mes,dia);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        DateSet = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mes, int dia) {

                mes=mes+1;
                Log.d(TAG,"OnDateSet: Date: "+anno+"/"+mes+"/"+dia);

                fechaNacimientoCalendar(dia,mes,anno);

            }
        };

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
                                Intent intent = new Intent(ModificarMascotas.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(ModificarMascotas.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(ModificarMascotas.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(ModificarMascotas.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(ModificarMascotas.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(ModificarMascotas.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(ModificarMascotas.this, Mis_compras.class);
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

    }

    @TargetApi(Build.VERSION_CODES.O)
    public void fechaNacimientoCalendar(int dia, int mes, int anno){

        ZoneId defaultZoneId = ZoneId.systemDefault();

        LocalDate fechaActual= LocalDate.now();
        LocalDate fechaNacimiento= LocalDate.of(anno,mes,dia);

        Period periodo= Period.between(fechaNacimiento,fechaActual);

        Date fechaa= Date.from(fechaNacimiento.atStartOfDay(defaultZoneId).toInstant());

        DateFormat dateFormat = new SimpleDateFormat("MMM|yyyy");
        String fechaFormat = dateFormat.format(fechaa);

        anios_masc.setText(""+periodo.getYears());
        meses_masc.setText(""+periodo.getMonths());
        tvSelecFecha.setText(fechaFormat);

    }

    public void setDatosMascotaActualizar(String[]datos){

        nombre_mascota.setText(datos[1]);
        anios_masc.setText(datos[2]);
        meses_masc.setText(datos[3]);

        for (int i=0;i<razas.size();i++){
            if (datos[4].equals(razas.get(i))){
                raza_masc.setSelection(i);
            }
        }

        fechaNacimiento();

        if(datos[5].equals("Pequeño")){
            peso_mascota.setSelection(0);
        }else if(datos[5].equals("Mediano")){
            peso_mascota.setSelection(1);
        }else if(datos[5].equals("Grande")){
            peso_mascota.setSelection(2);
        }

        if(datos[6].equals("Macho")){
            genero_masc.setSelection(0);
        }else if(datos[6].equals("Hembra")){
            genero_masc.setSelection(1);
        }
    }

    public void upDateServeer(){

     String nombre_mas = nombre_mascota.getText().toString();
        String anios_mas = anios_masc.getText().toString();
        String meses_mas = meses_masc.getText().toString().trim();
        String raza_mas = ids.get(raza_masc.getSelectedItemPosition());
        String genero_mas = genero_masc.getSelectedItem().toString();
        String tammano_mas = peso_mascota.getSelectedItem().toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ModificarMascotas.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id", datosMascotaIntent[0]);
                params.put("nombre_mascota", nombre_mas);
                params.put("anios", anios_mas);
                params.put("meses", meses_mas);
                params.put("raza", raza_mas);
                params.put("genero", genero_mas);
                params.put("peso_mascota", tammano_mas);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ModificarMascotas.this);
        requestQueue.add(request);
    }

    public void registrarMascota() {

        try {

            SQLiteDatabase db= conn.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Utilidades.CAMPO_ID_MASC,id_mascota_servidor);
            values.put(Utilidades.CAMPO_NOMBRE, nombre_mascota.getText().toString());
            values.put(Utilidades.CAMPO_ANIOS, anios_masc.getText().toString());
            values.put(Utilidades.CAMPO_MESES, meses_masc.getText().toString());
            values.put(Utilidades.CAMPO_RAZA, raza_masc.getSelectedItem().toString());
            values.put(Utilidades.CAMPO_GENERO, genero_masc.getSelectedItem().toString());
            values.put(Utilidades.CAMPO_TAMANNO, peso_mascota.getSelectedItem().toString());
            values.put(Utilidades.CAMPO_PRECIO_BANNO,precioBanno);
            values.put(Utilidades.CAMPO_PRECIO_PELUQUERIA,precioPeluqueria);
            values.put(Utilidades.CAMPO_ACTIVO_MASCOTA,"activo");
            values.put(Utilidades.CAMPO_USUARIO_MASCOTA, usuario_mascota);

            Long registroMascota = db.insert(Utilidades.TABLA_REG_MASCOTA, Utilidades.CAMPO_NOMBRE, values);

            db.close();
            Toast.makeText(getApplicationContext(), nombre_mascota.getText().toString()+" registrado exitosamente", Toast.LENGTH_SHORT).show();

            if(invocador.equals("guarderia")){
                Intent intent = new Intent(ModificarMascotas.this, Guarderia_calendar.class);
                startActivity(intent);
                finish();
            } else  if(invocador.equals("hotel")){
                Intent intent = new Intent(ModificarMascotas.this, Hotel_reservar.class);
                startActivity(intent);
                finish();
            } else  if(invocador.equals("peluqueria")){
                Intent intent = new Intent(ModificarMascotas.this, Peluqueria_new.class);
                startActivity(intent);
                finish();
            } else  if(invocador.equals("consulta")){
                Intent intent = new Intent(ModificarMascotas.this, Consulta_medica.class);
                startActivity(intent);
                finish();
            } else  if(invocador.equals("banos")){
                Intent intent = new Intent(ModificarMascotas.this, Banos_new.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(ModificarMascotas.this, Registrar_mascota.class);
                startActivity(intent);
                finish();
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error registro mascotas local!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void obtenerBannoPeluqueria(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM precios_raza WHERE raza_server='"+raza_masc.getSelectedItem().toString()+"'",null);
        try {
            cursor.moveToFirst();
            precioBanno= cursor.getString(cursor.getColumnIndex("banno_server"));
            precioPeluqueria= cursor.getString(cursor.getColumnIndex("peluqueria_server"));
            Toast.makeText(getApplicationContext(), precioBanno+"-"+precioPeluqueria, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "obtenerBannoPeluqueria: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void actualizarDatos(){

        String idMascota= datosMascotaIntent[0];
        int actResult=0;

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_NOMBRE,nombre_mascota.getText().toString());
        values.put(Utilidades.CAMPO_ANIOS,anios_masc.getText().toString());
        values.put(Utilidades.CAMPO_MESES,meses_masc.getText().toString());
        values.put(Utilidades.CAMPO_GENERO,meses_masc.getText().toString());
        values.put(Utilidades.CAMPO_RAZA, raza_masc.getSelectedItem().toString());
        values.put(Utilidades.CAMPO_GENERO, genero_masc.getSelectedItem().toString());
        values.put(Utilidades.CAMPO_TAMANNO, peso_mascota.getSelectedItem().toString());

        actResult = db.update(Utilidades.TABLA_REG_MASCOTA,values," id_masc=?", new String[]{idMascota});
        db.close();

    }

    public void fechaNacimiento(){
        Calendar c = Calendar.getInstance();

        if(meses_masc.getText().toString().equals("") && anios_masc.getText().toString().equals("")){
            c.add(Calendar.YEAR,-(0));
            c.add(Calendar.MONTH,-(0));
            Date fechaa= c.getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM|yyyy");
            fecha = dateFormat.format(fechaa);
        }
        else if(meses_masc.getText().toString().equals("")){
            c.add(Calendar.YEAR,-(Integer.parseInt(anios_masc.getText().toString())));
            c.add(Calendar.MONTH,-(0));
            Date fechaa= c.getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM|yyyy");
            fecha = dateFormat.format(fechaa);
        }
        else  if(anios_masc.getText().toString().equals("")){

            c.add(Calendar.YEAR,-(0));
            c.add(Calendar.MONTH,-(Integer.parseInt(meses_masc.getText().toString())));
            Date fechaa= c.getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM|yyyy");
            fecha = dateFormat.format(fechaa);
        }
        else{
            c.add(Calendar.YEAR,-(Integer.parseInt(anios_masc.getText().toString())));
            c.add(Calendar.MONTH,-(Integer.parseInt(meses_masc.getText().toString())));
            Date fechaa= c.getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM|yyyy");
            fecha = dateFormat.format(fechaa);
        }
        tvSelecFecha.setText(fecha);
    }
    public void Register(){

        SQLiteDatabase db1 = conn.getReadableDatabase();
        Cursor cursorUsuario= db1.rawQuery("SELECT id_usuario FROM usuario",null);
        cursorUsuario.moveToFirst();
        usuario_mascota=cursorUsuario.getString(cursorUsuario.getColumnIndex("id_usuario"));
        db1.close();

        nombre_mas = nombre_mascota.getText().toString();
        anios_mas = anios_masc.getText().toString();
        meses_mas = meses_masc.getText().toString().trim();
        raza_mas = ids.get(raza_masc.getSelectedItemPosition());
        genero_mas = genero_masc.getSelectedItem().toString();
        tammano_mas = peso_mascota.getSelectedItem().toString();

        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    id_mascota_servidor=response;
                    obtenerBannoPeluqueria();
                    registrarMascota();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ModificarMascotas.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("nombre_mascota", nombre_mas);
                params.put("anios", anios_mas);
                params.put("meses", meses_mas);
                params.put("raza", raza_mas);
                params.put("genero", genero_mas);
                params.put("peso_mascota", tammano_mas);
                params.put("usuario_mascota", usuario_mascota);
                params.put("estadoMascota", "activo");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ModificarMascotas.this);
        requestQueue.add(request);
    }



    private void traerRazas(){

        String url="http://app.animalresort.com.co/traerRazas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    ids.clear();
                    razas.clear();

                    JSONObject datosBajados = new JSONObject(response);

                    JSONArray jsonArray = datosBajados.optJSONArray("datos");

                    JSONObject datos=null;

                    for (int i=0;i<jsonArray.length();i++){
                        datos= jsonArray.getJSONObject(i);

                        switch (especie_mascota.getSelectedItem().toString()){

                            case "perro":
                                if(datos.optString("tipo_mascota").equals(especie_mascota.getSelectedItem().toString())){
                                    razas.add(datos.optString("raza"));
                                    ids.add(datos.optString("id"));
                                }
                                break;
                            case "gato":
                                if(datos.optString("tipo_mascota").equals(especie_mascota.getSelectedItem().toString())){
                                    razas.add(datos.optString("raza"));
                                    ids.add(datos.optString("id"));
                                }
                                break;
                            case "loro":
                                if(datos.optString("tipo_mascota").equals(especie_mascota.getSelectedItem().toString())){
                                    razas.add(datos.optString("raza"));
                                    ids.add(datos.optString("id"));
                                }
                                break;
                        }
                    }

                    ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ModificarMascotas.this, R.layout.spinner_item, razas);
                    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    raza_masc.setAdapter(myAdapter);

                    if( getIntent().getExtras() != null) {
                        invocador= getIntent().getStringExtra("invocador");

                        if(invocador.equals("guarderia")||invocador.equals("hotel")||invocador.equals("consulta")||invocador.equals("banos")||invocador.equals("peluqueria")){
                            UpdateMascota.setText("CREAR");
                            Toast.makeText(getApplicationContext(), "Registra tu mascota", Toast.LENGTH_SHORT).show();
                        }else{
                            setDatosMascotaActualizar( datosMascotaIntent=invocador.split(","));
                        }

                    }else{
                        UpdateMascota.setText("CREAR");
                    }

                }catch (Exception e){
                    Toast.makeText(ModificarMascotas.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ModificarMascotas.this);
        requestQueue.add(request);

    }

}