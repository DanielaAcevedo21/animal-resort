package com.example.animalresort;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import utilidades.Utilidades;

public class Hotel_reservar extends AppCompatActivity {

    private ImageButton mainMenu;

    private LinearLayout ly_calendario, ly_fecha1, ly_fecha2;
    private TextView calendarioInicio, calendarioFinal;
    private int contador;
    private TextView check_in;
    private TextView check_out;
    private TextView dias_tv;
    private ListView listView_mascotas;
    private TextView direccion;
    //resumen
    private TextView dias_total;
    private TextView num_masc_tv;
    private TextView desc_xdia_tv;
    private TextView precio_dias_sindesc_tv;
    private Button reservar;
    private TextView precio_total;
    //macotas
    private final ArrayList<String> lista_mascotas = new ArrayList<>();
    private final ArrayList<Mascota>mascotaArray= new ArrayList<>();
    private ArrayAdapter <String> mAdapter;
    private String id_usuario_mascota;
    private ArrayList<String> lista_descuentos;
    private final ArrayList<String> id_mascota_selecc= new ArrayList<>();
    private final ArrayList<Integer> posicionesCheck = new ArrayList<>();
    //calendario
    String F_ini, F_fin;
    long dias;
    private Date fechaIni,fechaFin;
    private final ArrayList<String> lista_direcciones = new ArrayList<>();
    private String id_direccion="1", invocador;

    private final String url="http://app.animalresort.com.co/insertarReservasNew.php";
    private final String url2="http://app.animalresort.com.co/insertarMascotasReservadasNew.php";

    private long mLastClickTime = 0;

    private static int descuento=0;
    private ArrayList<String> lista_descuentos_mascota= new ArrayList<>();

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_reservar);

        mainMenu=findViewById(R.id.mainMenu);
        ly_calendario=findViewById(R.id.ly_calendar);
        calendarioInicio = findViewById(R.id.fecha_1);
        calendarioFinal = findViewById(R.id.fecha_2);

        ly_fecha1 = findViewById(R.id.ly_fecha1);
        ly_fecha2 = findViewById(R.id.ly_fecha2);

        check_in=findViewById(R.id.fecha_1);
        check_out=findViewById(R.id.fecha_2);
        dias_tv=findViewById(R.id.tv_dias_hotel);
        listView_mascotas=findViewById(R.id.list_mascotas);
        direccion=findViewById(R.id.txv_direccion);
        dias_total=findViewById(R.id.dias_selec_tv);
        num_masc_tv=findViewById(R.id.num_masc_tv);
        desc_xdia_tv=findViewById(R.id.desc_xdia_tv);
        precio_dias_sindesc_tv=findViewById(R.id.precio_dias_sindesc_tv);
        reservar=findViewById(R.id.save_btn);
        precio_total=findViewById(R.id.tv_precio_total);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("EEEE MMM d");
        String tomorrowAsString = dateFormat.format(tomorrow);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        F_ini= df.format(tomorrow);
        check_in.setText(tomorrowAsString);
        F_fin= df.format(tomorrow);
        check_out.setText(tomorrowAsString);
        dias = (int) ((tomorrow.getTime() - tomorrow.getTime()));
        dias = (dias / 86400000) + 1;
        dias_tv.setText(Long.toString(dias));

        if( getIntent().getExtras() != null) {
            if(getIntent().getStringExtra("tipo").equals("insert")){

            invocador= getIntent().getStringExtra("servicio");
            if(invocador.equals("1")){

                calendar.add(Calendar.DAY_OF_YEAR, 8);
                Date dias9 = calendar.getTime();
                String dias9String = dateFormat.format(dias9);
                df.setTimeZone(TimeZone.getTimeZone("gmt"));

                F_fin=df.format(dias9);
                check_out.setText(dias9String);
                dias = (int) ((dias9.getTime() - tomorrow.getTime()));
                dias = (dias / 86400000) + 1;
                dias_tv.setText(Long.toString(dias));

            } else if(invocador.equals("2")){
                calendar.add(Calendar.DAY_OF_YEAR, 9);
                Date dias10 = calendar.getTime();
                String dias14String = dateFormat.format(dias10);
                df.setTimeZone(TimeZone.getTimeZone("gmt"));

                F_fin=df.format(dias10);
                check_out.setText(dias14String);
                dias = (int) ((dias10.getTime() - tomorrow.getTime()));
                dias = (dias / 86400000) + 1;
                dias_tv.setText(Long.toString(dias));
            }

        } else if(getIntent().getStringExtra("tipo").equals("update")){
            invocador= getIntent().getStringExtra("id");
            reservar.setText("Actualizar");
        }
        }

        consultarBD();
        traer_mascotas();
        traer_direcciones();
        posicionesCheck.add(0,0);

        if (lista_mascotas.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una mascota", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Hotel_reservar.this, ModificarMascotas.class);
            intent.putExtra("invocador","hotel");
            startActivity(intent);
            finish();
        }
        else  if (lista_direcciones.size()==0){
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una direccion", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Hotel_reservar.this, CUDirecciones.class);
            intent.putExtra("invocador","hotel");
            startActivity(intent);
            finish();
        }
        else{
            listView_mascotas.setItemChecked(0,true);
           // calPrecio();
        }

        MaterialDatePicker.Builder<Pair<Long, Long>> builderRange = MaterialDatePicker.Builder.dateRangePicker();
        builderRange.setCalendarConstraints(limitRange().build());
        builderRange.setTitleText("Dias de tu mascota en hotel");
        builderRange.setTheme(R.style.MaterialCalendarTheme);
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        MaterialDatePicker <Pair<Long, Long>> pickerRange = builderRange.build( );

        listView_mascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                calPrecio();

            }
        });
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
                                //aqui se dala orden para el click
                                Intent intent = new Intent(Hotel_reservar.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Hotel_reservar.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Hotel_reservar.this, Registrar_mascota.class);
                                mascotasChecked();
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Hotel_reservar.this, Direcciones.class);
                                mascotasChecked();
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Hotel_reservar.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Hotel_reservar.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Hotel_reservar.this, Mis_compras.class);
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

        direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(Hotel_reservar.this, Direcciones.class);
                mascotasChecked();
                startActivity(inten);
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){

                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                id_mascota_selecc.clear();

                for (int i=0;i<mascotaArray.size();i++){
                    if (listView_mascotas.isItemChecked(i)){
                        id_mascota_selecc.add(mascotaArray.get(i).getId_mas());
                    }
                }
                if(id_mascota_selecc.size()<1){
                    Toast.makeText(getApplicationContext(), "Debes seleccionar al menos una mascota", Toast.LENGTH_SHORT).show();
                }else{

                    if(reservar.getText().equals("Actualizar")){
                        upDateReserva();
                    }else {
                        Toast.makeText(getApplicationContext(), "Verificando información", Toast.LENGTH_SHORT).show();
                        subirReservas();
                    }
                }

            }
        });

        ly_fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickerRange.show(getSupportFragmentManager(), pickerRange.toString());
                pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")

                    @Override
                    public void onPositiveButtonClick(Pair<Long,Long> selection){
                        Long startDate = selection.first;
                        Long endDate = selection.second ;

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        DateFormat df2 = new SimpleDateFormat("EEEE MMM d", Locale.getDefault());

                        df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        df2.setTimeZone(TimeZone.getTimeZone("gtm"));

                        fechaIni = new Date(startDate);
                        fechaFin = new Date(endDate);

                        String inicioFecha =df2.format(fechaIni);
                        String finFecha =df2.format(fechaFin);

                        F_ini= df.format(fechaIni);
                        F_fin=df.format(fechaFin);

                        check_in.setText(inicioFecha);
                        check_out.setText(finFecha);

                        dias = ((fechaFin.getTime() - fechaIni.getTime()));

                        dias=(dias/86400000)+1;

                        dias_tv.setText(Long.toString(dias));
                        calPrecio();

                    }
                });

            }
        });
        ly_fecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickerRange.show(getSupportFragmentManager(), pickerRange.toString());
                pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")

                    @Override
                    public void onPositiveButtonClick(Pair<Long,Long> selection){
                        Long startDate = selection.first;
                        Long endDate = selection.second ;

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        DateFormat df2 = new SimpleDateFormat("EEEE MMM d", Locale.getDefault());

                        df.setTimeZone(TimeZone.getTimeZone("gmt"));
                        df2.setTimeZone(TimeZone.getTimeZone("gtm"));

                        fechaIni = new Date(startDate);
                        fechaFin = new Date(endDate);

                        String inicioFecha =df2.format(fechaIni);
                        String finFecha =df2.format(fechaFin);

                        F_ini= df.format(fechaIni);
                        F_fin=df.format(fechaFin);

                        check_in.setText(inicioFecha);
                        check_out.setText(finFecha);

                        dias = ((fechaFin.getTime() - fechaIni.getTime()));

                        dias=(dias/86400000)+1;

                        dias_tv.setText(Long.toString(dias));
                        calPrecio();

                    }
                });

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
        descuentos_mascota();
        traer_direcciones();
    }

    public void mascotasChecked(){
        for (int i = 0; i < mascotaArray.size(); i++) {
            if (listView_mascotas.isItemChecked(i)) {
                posicionesCheck.add(i);
            }
        }
    }
    public void upDateReserva(){

        String url="http://app.animalresort.com.co/upDateReservas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    upDateReservasLocal();


                }catch (Exception e){
                    Toast.makeText(Hotel_reservar.this,"en response de upDateReserva "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Hotel_reservar.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id",""+invocador);
                params.put("fecha_inicio",F_ini);
                params.put("fecha_fin", F_fin);
                params.put("direccion",id_direccion);
                params.put("descuento",desc_xdia_tv.getText().toString());
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva",dias_total.getText().toString());
                params.put("servicio_especifico","hotel");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Hotel_reservar.this);
        requestQueue.add(request);

    }
    private  void upDateReservasLocal(){

        int descuento_result=0;

        SQLiteDatabase db2 = conn.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put(Utilidades.CAMPO_FECHA_INICIO,check_in.getText().toString());
        values2.put(Utilidades.CAMPO_FECHA_FIN, check_out.getText().toString());
        values2.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        values2.put(Utilidades.CAMPO_DESCUENTO, desc_xdia_tv.getText().toString());
        values2.put(Utilidades.CAMPO_PRECIO_TOTAL, precio_total.getText().toString());
        values2.put(Utilidades.CAMPO_DIAS_RESERVA, dias_total.getText().toString());
        values2.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO, "hotel");
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
                    Toast.makeText(Hotel_reservar.this,"en response de upDateReserva "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Hotel_reservar.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("id",""+invocador);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Hotel_reservar.this);
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

            Toast.makeText(getApplicationContext(), "Hotel actualizado", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Hotel_reservar.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private CalendarConstraints.Builder limitRange() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDate = calendar.get(Calendar.DAY_OF_MONTH);

        int endMonth = 12;
        int endDate = 31;
        calendarStart.set(year, startMonth, startDate - 1);
        calendarEnd.set(year, endMonth +4, endDate);

        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();

        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);
        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));

        return constraintsBuilderRange;
    }

    static class RangeValidator implements CalendarConstraints.DateValidator {

        long minDate, maxDate;

        RangeValidator(long minDate, long maxDate) {
            this.minDate = minDate;
            this.maxDate = maxDate;
        }

        RangeValidator(Parcel parcel) {
            minDate = parcel.readLong();
            maxDate = parcel.readLong();
        }

        @Override
        public boolean isValid(long date) {
            return !(minDate > date || maxDate < date);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(minDate);
            dest.writeLong(maxDate);
        }

        public static final Parcelable.Creator<RangeValidator> CREATOR = new Parcelable.Creator<RangeValidator>() {

            @Override
            public RangeValidator createFromParcel(Parcel parcel) {
                return new RangeValidator(parcel);
            }

            @Override
            public RangeValidator[] newArray(int size) {
                return new RangeValidator[size];
            }
        };
    }

    public void traer_mascotas() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT id_masc,nombre_mascota,anios_masc,meses_masc,raza_masc,tamanno_mascota,usuario_mascota FROM reg_mascota",null);
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
                id_usuario_mascota= cursor.getString(cursor.getColumnIndex("usuario_mascota"));

                lista_mascotas.add(nombre);

                Mascota m = new Mascota();
                m.setId_mas(id_mascotas);
                m.setNombre_mas(nombre);
                m.setAños_mas(años);
                m.setMeses_mas(meses);
                m.setTamaño_mas(tamanno);
                m.setRaza_mas(raza);
                mascotaArray.add(m);

                cursor.moveToNext();

            }
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,lista_mascotas);
            listView_mascotas.setAdapter(mAdapter);
            cursor.close();
            db.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error traer_mascotas Hotel "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void calPrecio() {

        int contador =0;
        int size= lista_mascotas.size();
        for (int i=0;i<size;i++){
            if (listView_mascotas.isItemChecked(i)){
                contador++;

            }
        }

        descuento=(contador>0)? Integer.parseInt(lista_descuentos_mascota.get(contador-1)): 0;

        if(dias<=4){
            String desc = lista_descuentos.get(0);

            precio_total.setText(""+(contador*(dias*Integer.parseInt(desc))-descuento));

            desc_xdia_tv.setText("0");
        }
        else if (dias>=5 && dias<=9){
            String desc_fijo = lista_descuentos.get(0);
            String desc = lista_descuentos.get(1);

            precio_total.setText(""+(contador*(dias*Integer.parseInt(desc))-descuento));

            Integer p=  ((Integer.parseInt(desc_fijo))-(Integer.parseInt(desc)));
            desc_xdia_tv.setText(""+p);



        }else if (dias>=10) {
            String desc_fijo = lista_descuentos.get(0);
            String desc = lista_descuentos.get(2);

            precio_total.setText(""+(contador*(dias*Integer.parseInt(desc))-descuento));
            desc_xdia_tv.setText("" + (Integer.parseInt(desc_fijo) - Integer.parseInt(desc)));

        }else{

        }

        num_masc_tv.setText(""+contador);
        dias_total.setText(""+dias);

        String desc = lista_descuentos.get(0);
        precio_dias_sindesc_tv.setText(""+Integer.parseInt(desc));

    }

    private void descuentos_mascota(){

        String url="http://app.animalresort.com.co/Descuentos.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        datos = jsonArray.getJSONObject(i);
                        lista_descuentos_mascota.add(datos.optString("descuento"));

                    }
                    calPrecio();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"en response de descuentos_mascota "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"on response error descuentos_mascota "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
    private void consultarBD() {

        lista_descuentos = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='2'",null);

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
            Toast.makeText(getApplicationContext(), "Error traer_descuento Hotel "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void subirReservas(){

        String listaIds = new Gson().toJson(id_mascota_selecc);// esta es la razon por la que duplicaba como en los otros no mando lista no hace foreach en
        //ningun if del php insertarReservasNew

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if(response.equals("error:1")){
                        Toast.makeText(Hotel_reservar.this, "Error: hay mascotas ya reservadas" , Toast.LENGTH_LONG).show();
                    }else{
                        registrarReservas(response);
                    }

                }catch (Exception e){
                    Toast.makeText(Hotel_reservar.this,"en response subirReservas Hotel "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Hotel_reservar.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("fecha_inicio",F_ini);
                params.put("fecha_fin", F_fin);
                params.put("direccion",id_direccion);
                params.put("descuento",desc_xdia_tv.getText().toString());
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva",dias_total.getText().toString());
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","2");
                params.put("estado_reserva", "Reserva exitosa");
                params.put("servicio_especifico","hotel");
                params.put("listaIdsMascotas", listaIds);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Hotel_reservar.this);
        requestQueue.add(request);

    }

    private void registrarReservas(String id_servidor) {

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Utilidades.CAMPO_ID,id_servidor);
        values.put(Utilidades.CAMPO_FECHA_INICIO, check_in.getText().toString());
        values.put(Utilidades.CAMPO_FECHA_FIN, check_out.getText().toString());
        values.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        values.put(Utilidades.CAMPO_DESCUENTO,desc_xdia_tv.getText().toString());
        values.put(Utilidades.CAMPO_PRECIO_TOTAL,precio_total.getText().toString());
        values.put(Utilidades.CAMPO_ESTADO_RESERVA,"Reserva exitosa");
        values.put(Utilidades.CAMPO_DIAS_RESERVA, dias_total.getText().toString() );
        values.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO,"hotel");

        Long fechaResult = db.insert(Utilidades.TABLA_RESERVA,null, values);

        db.close();

        registrarMascotasLocal(id_mascota_selecc);

    }

    public void registrarMascotasLocal(ArrayList<String>ids){

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
            }
            db2.close();
            notificarVenta();
            Toast.makeText(getApplicationContext(), "Hotel reservado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Hotel_reservar.this, Mis_reservas.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error registrarMascotasSelec Hotel "+ e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Hotel_reservar.this,"en response de notificarVenta "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Hotel_reservar.this,"on response error notificarVenta "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("titulo", "Hotel Animal Resort");
                params.put("cuerpo", "Fecha servicio hotel: "+F_ini);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Hotel_reservar.this);
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
                params.put("fecha_fin", F_fin);
                params.put("direccion",id_direccion);
                params.put("descuento",desc_xdia_tv.getText().toString());
                params.put("precio_total",precio_total.getText().toString());
                params.put("dias_reserva",dias_total.getText().toString());
                params.put("id_usuario_reserva",id_usuario_mascota);
                params.put("id_pro_servi","2");
                params.put("estado_reserva", "Reserva exitosa");
                params.put("servicio_especifico","hotel");

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
                    Toast.makeText(Hotel_reservar.this,"en response de subirMascotasReservadas "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Hotel_reservar.this,"on response error"+ error.getMessage(),Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Hotel_reservar.this);
        requestQueue.add(request);

    }

    public void traer_direcciones() {

        SQLiteDatabase db = conn.getReadableDatabase();

        Cursor cursor =db.rawQuery("SELECT direccion,ciudad, id_direccion FROM mis_direcciones WHERE seleccion_defecto='true'",null);
        try {
            cursor.moveToFirst();

            if (lista_direcciones.size()>=1) lista_direcciones.clear();

            for(int i=0;i<cursor.getCount();i++){

                id_direccion= cursor.getString(cursor.getColumnIndex("id_direccion"));
                String Direccion = cursor.getString(cursor.getColumnIndex("direccion"));
                String ciudad = cursor.getString(cursor.getColumnIndex("ciudad"));
                lista_direcciones.add(ciudad+", "+Direccion);
                direccion.setText(ciudad+"-"+Direccion);

                cursor.moveToNext();

            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error traer_direcciones registrarMascotasSelec Hotel"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}