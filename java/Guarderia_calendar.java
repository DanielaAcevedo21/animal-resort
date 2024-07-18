package com.example.animalresort;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import utilidades.Utilidades;

public class Guarderia_calendar extends AppCompatActivity {

    private Button mSave_btn;
    private TextView  direccion_reserva, desc_xdia_tv, precio_dias_sindesc_tv, dias_selec_tv, num_masc_tv, tv_dias, tv_precio;
    private String id_usuario_mascota, id_direccion = "1", invocador, TipoUsuario, fecha_notificar;

    private ListView listado_mascotas;

    private final ArrayList<String> lista_direcciones = new ArrayList<>();
    private final ArrayList<String> lista_mascotas = new ArrayList<>();
    private final ArrayList<Mascota> mascotaArray = new ArrayList<>();

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> id_mascota_selecc = new ArrayList<>();
    private ArrayList<Integer> posicionesCheck = new ArrayList<>();
    private ArrayList<String> lista_descuentos;
    private ArrayList<String> lista_id_reservas = new ArrayList<>();
    List<View> views = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();
    List<Calendar> fechasReservas = new ArrayList<>();
    ArrayList<Calendar> dates = new ArrayList<>();
    ArrayList<Calendar> fechasPorGrupo = new ArrayList<>();
    private ImageButton mainMenu;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_reservas", null, 1);

    private long mLastClickTime = 0;
    // calendario custom
    private static Double precioDiario;
    private static String grupo;
    private int diaSem;
    private static boolean edicion = false;
    private static int limite = 0;
    private static TextView ulitmaPosView;

    private static int descuento=0;
    private ArrayList<String> lista_descuentos_mascota= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarderia_calendar);

        mSave_btn = findViewById(R.id.save_btn);
        listado_mascotas = findViewById(R.id.list_mascotas);
        tv_dias = findViewById(R.id.tv_dias);
        mainMenu = findViewById(R.id.mainMenu);

        tv_precio = findViewById(R.id.tv_precio);
        num_masc_tv = findViewById(R.id.num_masc_tv);
        dias_selec_tv = findViewById(R.id.dias_selec_tv);
        precio_dias_sindesc_tv = findViewById(R.id.precio_dias_sindesc_tv);
        desc_xdia_tv = findViewById(R.id.desc_xdia_tv);
        direccion_reserva = findViewById(R.id.direccion_reserva);

        getTipoUsuario();

        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra("tipo").equals("insert")) {

                invocador = getIntent().getStringExtra("servicio");

                if (invocador.equals("1")) {

                    tv_dias.setText("5");

                } else if (invocador.equals("2")) {

                    tv_dias.setText("10");

                } else if (invocador.equals("3")) {

                    tv_dias.setText("15");
                }

            } else if (getIntent().getStringExtra("tipo").equals("update")) {
                edicion = true;
                invocador = getIntent().getStringExtra("id");
                mSave_btn.setText("Actualizar");
                if (TipoUsuario.equals("Tester")) {
                    traerFechasGrupo(true);
                }else{
                    traerFechasGrupo(false);
                }
            }
        }
        rellenar_fechas();
        selectPaquetesDescuento(Integer.parseInt(tv_dias.getText().toString()));
        traerPrecios();
        traer_mascotas();
        traer_direcciones();
        if (TipoUsuario.equals("Tester")) {
            traerGrupo(true);
        }else{
            traerGrupo(false);
        }


        posicionesCheck.add(0, 0);

        if (lista_mascotas.size() == 0) {
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una mascota", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Guarderia_calendar.this, ModificarMascotas.class);
            intent.putExtra("invocador", "guarderia");
            startActivity(intent);
            finish();
        } else if (lista_direcciones.size() == 0) {
            Toast.makeText(getApplicationContext(), "Debes crear por lo menos una direccion", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Guarderia_calendar.this, CUDirecciones.class);
            intent.putExtra("invocador", "guarderia");
            startActivity(intent);
            finish();
        } else {
            listado_mascotas.setItemChecked(0, true);
            //calPrecio();
        }

        listado_mascotas.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        direccion_reserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(Guarderia_calendar.this, Direcciones.class);
                mascotasChecked();
                startActivity(inten);
            }
        });

        listado_mascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                calPrecio();
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
                                Intent intent = new Intent(Guarderia_calendar.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Guarderia_calendar.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Guarderia_calendar.this, Registrar_mascota.class);
                                mascotasChecked();
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Guarderia_calendar.this, Direcciones.class);
                                mascotasChecked();
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor = db.rawQuery("SELECT tipo_usuario FROM usuario", null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if (!tipo_usu.equals("Usuario")) {
                                    Intent inten5 = new Intent(Guarderia_calendar.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                } else {
                                    Intent inten4 = new Intent(Guarderia_calendar.this, MenuUsuario.class);
                                    startActivity(inten4);
                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Guarderia_calendar.this, Mis_compras.class);
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

        mSave_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                /**
                 * esto es para evitar el spam al clickear
                 */
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                id_mascota_selecc.clear();
                /**
                 * agregamos a una lista las mascotas seleccionadas por el usuario
                 */
                for (int i = 0; i < mascotaArray.size(); i++) {
                    if (listado_mascotas.isItemChecked(i)) {
                        id_mascota_selecc.add(mascotaArray.get(i).getId_mas());
                    }
                }

                if (id_mascota_selecc.size() < 1) {
                    Toast.makeText(getApplicationContext(), "Debes seleccionar al menos una mascota", Toast.LENGTH_SHORT).show();
                } else {

                    if (mSave_btn.getText().equals("Actualizar")) {

                        if (TipoUsuario.equals("Tester")) {
                            upDateReserva(true);
                        } else {
                            upDateReserva(false);
                        }

                    } else { //insert

                        Toast.makeText(getApplicationContext(), "Verificando información", Toast.LENGTH_SHORT).show();

                        if (TipoUsuario.equals("Tester")) {

                            for (int i = 0; i < fechasReservas.size(); i++) {

                                hiloServidor p= new hiloServidor(calendarServer(fechasReservas.get(i)), true);
                                Thread hilo= new Thread(p);
                                hilo.start();
                                try {
                                    hilo.join();
                                }catch (Exception exception){

                                }
                            }

                        } else { /** este es el que se usa actualmente
                            // **/

                            Toast.makeText(getApplicationContext(), "Procesando", Toast.LENGTH_SHORT).show();
                            fecha_notificar=calendarServer(fechasReservas.get(0));
                            for (int i = 0; i < fechasReservas.size(); i++) {

                                hiloServidor p= new hiloServidor(calendarServer(fechasReservas.get(i)), false);
                                Thread hilo= new Thread(p);
                                hilo.start();
                                try {
                                    hilo.join();
                                }catch (Exception exception){

                                }
                            }

                        }
                    }
                }
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int posicion = 0;
                boolean nuevoDia = false;
                for (View view2 : views) {

                    if (v.equals(view2)) {
                        if (v instanceof TextView) {
                            if (posicion > diaSem - 2) {
                                if (fechasReservas.size() >= 1) {
                                    int posicionArrayDates = (posicion - diaSem + 1);

                                    for (int i = 0; i < fechasReservas.size(); i++) {
                                        if (fechasReservas.get(i) == dates.get(posicionArrayDates)) {
                                            if(edicion){
                                                Toast.makeText(getApplicationContext(), "Fecha no permitida", Toast.LENGTH_SHORT).show();

                                            }else {
                                                TextView textView = (TextView) view2;
                                                textView.setBackgroundResource(R.drawable.estilo_descuentos1);
                                                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                textView.setText(formatearCalendar(dates.get(posicionArrayDates)));
                                                fechasReservas.remove(i);
                                                tv_dias.setText(fechasReservas.size() + "");

                                            }
                                            nuevoDia = false;
                                            break;

                                        } else {
                                            nuevoDia = true;
                                        }
                                    }

                                    if (nuevoDia) {
                                        TextView textView = (TextView) view2;
                                        if (edicion && fechasReservas.size() == limite) {
                                            TextView textViewUnpint = ulitmaPosView;
                                            fechasReservas.remove(fechasReservas.size() - 1);
                                            textViewUnpint.setBackgroundResource(R.drawable.estilo_descuentos1);
                                            textViewUnpint.setTextColor(getResources().getColor(R.color.colorPrimary));

                                        }
                                        textView.setText(formatearCalendar(dates.get(posicionArrayDates)));
                                        textView.setBackgroundResource(R.drawable.estilo_textview);
                                        textView.setTextColor(getResources().getColor(R.color.white));
                                        fechasReservas.add(dates.get(posicion - diaSem + 1));
                                        tv_dias.setText(fechasReservas.size() + "");
                                        ulitmaPosView = textView;

                                    }
                                } else {
                                    TextView textView = (TextView) view2;
                                    int posicionArrayDates = (posicion - diaSem + 1);

                                    if (edicion && fechasReservas.size() == limite) {
                                        TextView textViewUnpint = ulitmaPosView;
                                        fechasReservas.remove(fechasReservas.size() - 1);
                                        textViewUnpint.setBackgroundResource(R.drawable.estilo_descuentos1);
                                        textViewUnpint.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    }

                                    textView.setBackgroundResource(R.drawable.estilo_textview);
                                    textView.setTextColor(getResources().getColor(R.color.white));

                                    textView.setText(formatearCalendar(dates.get(posicionArrayDates)));
                                    fechasReservas.add(dates.get(posicionArrayDates));
                                    tv_dias.setText(fechasReservas.size() + "");
                                    ulitmaPosView = textView;
                                }
                            }
                        }
                    }
                    posicion++;
                }
                calPrecio();
            }
        };

        for (int i = 0; i < views.size(); i++) {
            views.get(i).setOnClickListener(clickListener);
        }

    }

    /**
     * Todos los metodos referentes a realizar una reserva
     */
    @Override
    public void onResume() {
        super.onResume();
        traer_mascotas();
        for (int i = 0; i < posicionesCheck.size(); i++) {
            listado_mascotas.setItemChecked(posicionesCheck.get(i), true);
        }
        posicionesCheck.clear();
        descuentos_mascota();
        traer_direcciones();
    }

    /**
     * guarda en un array las mascotas que he seleccionado
     */

    private void selectPaquetesDescuento(int cantidad) {
        for (int j = 0; j < cantidad; j++) {
            fechasReservas.add(dates.get(j));
            TextView textView = (TextView) views.get(j + diaSem - 1);
            textView.setBackgroundResource(R.drawable.estilo_textview);
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void mascotasChecked() {
        for (int i = 0; i < mascotaArray.size(); i++) {
            if (listado_mascotas.isItemChecked(i)) {
                posicionesCheck.add(i);
            }
        }
    }

    private void pintarFechas() {
        for (int i = 0; i < fechasPorGrupo.size(); i++) {
            Calendar calendar = fechasPorGrupo.get(i);

            for (int j = 0; j < dates.size(); j++) {

                String fecha1 = calendarServer(calendar);
                String fecha2 = calendarServer(dates.get(j));

                if (fecha1.equals(fecha2)) {
                    fechasReservas.add(dates.get(j));
                    TextView textView = (TextView) views.get(j + diaSem - 1);
                    textView.setBackgroundResource(R.drawable.estilo_fechabloqueada);
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
            }
            tv_dias.setText(fechasReservas.size() + "");
        }
    }
    /**
     * traer todas las fechas de ese grupo
     */
    public void traerFechasGrupo(boolean isTester) {

        String urlFechasGrupo =(isTester)? "http://app.animalresort.com.co/traerFechasGrupoTest.php" :"http://app.animalresort.com.co/traerFechasGrupo.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlFechasGrupo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject datosBajados = new JSONObject(response);
                    JSONArray jsonArray = datosBajados.optJSONArray("datos");
                    JSONObject datos = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        datos = jsonArray.getJSONObject(i);
                        fechasPorGrupo.add(fechasString(datos.optString("fecha_inicio")));
                    }
                    limite = fechasPorGrupo.size() + 1;
                    pintarFechas();
                } catch (Exception e) {
                    Toast.makeText(Guarderia_calendar.this, "en response de traerFechasGrupo " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Guarderia_calendar.this, "on errorresponse traerFechasGrupo " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", invocador);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Guarderia_calendar.this);
        requestQueue.add(request);
    }

    public void traerGrupo(boolean isTester) {

        String urlTest =(isTester)? "http://app.animalresort.com.co/traerUltimoGrupoTest.php" : "http://app.animalresort.com.co/traerUltimoGrupo.php";
        StringRequest request = new StringRequest(Request.Method.POST, urlTest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    grupo = (response.equals("") ? "1" : Integer.toString(Integer.parseInt(response) + 1));
                } catch (Exception e) {
                    Toast.makeText(Guarderia_calendar.this, "en response traerGrupo " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Guarderia_calendar.this, "error traerGrupo " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) ;
        RequestQueue requestQueue = Volley.newRequestQueue(Guarderia_calendar.this);
        requestQueue.add(request);
    }
    /**
     * metodos reserva-update
     */
    class hiloServidor implements Runnable{

        private String fecha;
        private boolean tester;
        public hiloServidor(String fecha, boolean tester){
            this.fecha=fecha;
            this.tester=tester;
        }

        @Override
        public synchronized void run() {
            String listaIds = new Gson().toJson(id_mascota_selecc);
            String urlTest = (tester)?"http://app.animalresort.com.co/TestinsertarReservasNew.php":  "http://app.animalresort.com.co/insertarReservasNew.php";
            StringRequest request = new StringRequest(Request.Method.POST, urlTest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                            lista_id_reservas.add(response);
                            registrarReservaEnLocal(response, fecha);
                            if (lista_id_reservas.size()==fechasReservas.size()){
                                registrarMascotasEnLocal();
                            }

                    } catch (Exception e) {
                        Toast.makeText(Guarderia_calendar.this, "en response de testsubirReservas GuarderiaCalendar " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Guarderia_calendar.this, "error subirReservas GuarderiaCalendar " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("fecha_inicio", fecha);
                    params.put("fecha_fin", fecha);
                    params.put("direccion", id_direccion);
                    params.put("descuento", desc_xdia_tv.getText().toString());
                    params.put("precio_total", precioDiario.toString());
                    params.put("dias_reserva", tv_dias.getText().toString());
                    params.put("id_usuario_reserva", id_usuario_mascota);
                    params.put("id_pro_servi", "1");
                    params.put("estado_reserva", "Reserva exitosa");
                    params.put("servicio_especifico", "guarderia");
                    params.put("listaIdsMascotas", listaIds);
                    params.put("grupo", grupo);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(Guarderia_calendar.this);
            requestQueue.add(request);
            try {
                Thread.sleep(250);
            }catch (InterruptedException exc){
                Toast.makeText(getApplicationContext(), "Hilo interrumpido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarReservaEnLocal(String id_servidor, String fecha) {//tester admin y normal

        if (id_servidor.equals("error:1")) {
            Toast.makeText(getApplicationContext(), fecha+"ya está reservado", Toast.LENGTH_SHORT).show();
        }else{
            SQLiteDatabase db = conn.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Utilidades.CAMPO_ID, id_servidor);
            values.put(Utilidades.CAMPO_FECHA_INICIO, fecha);
            values.put(Utilidades.CAMPO_FECHA_FIN, fecha);
            values.put(Utilidades.CAMPO_CIUDAD, id_direccion);
            values.put(Utilidades.CAMPO_DESCUENTO, desc_xdia_tv.getText().toString());
            values.put(Utilidades.CAMPO_PRECIO_TOTAL, precioDiario.toString());
            values.put(Utilidades.CAMPO_ESTADO_RESERVA, "Reserva exitosa");
            values.put(Utilidades.CAMPO_DIAS_RESERVA, tv_dias.getText().toString());
            values.put(Utilidades.CAMPO_SERVICIO_ESPECIFICO, "guarderia");

            Long fechaResult = db.insert(Utilidades.TABLA_RESERVA, null, values);
            db.close();
        }
    }

    public void registrarMascotasEnLocal() {//tester admin y normal

        try {
            SQLiteDatabase db2 = conn.getWritableDatabase();
            ContentValues values = new ContentValues();

            for (int index=0;index<id_mascota_selecc.size();index++){

                    String idMascota = id_mascota_selecc.get(index);

                    for (int i = 0; i < lista_id_reservas.size(); i++) {

                         if (lista_id_reservas.get(i).equals("error:1")) {

                }else {

                             values.put(Utilidades.CAMPO_ID_RESERVACION, lista_id_reservas.get(i));
                             values.put(Utilidades.CAMPO_ID_MASCOTA, idMascota);
                             values.put(Utilidades.CAMPO_USUARIO_MASCOTA_RES, id_usuario_mascota);
                             Long result = db2.insert(Utilidades.TABLA_MASCOTAS_RESERVADAS, null, values);
                         }
                    }

            }
            db2.close();
            notificarVenta();
            Toast.makeText(getApplicationContext(), "Guarderia reservada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Guarderia_calendar.this, Mis_reservas.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error registrarMascotasSelec GuarderiaCalendar " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Guarderia_calendar.this,"en response de notificarVenta "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Guarderia_calendar.this,"on response error notificarVenta "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("titulo", "Guarderia Animal Resort");
                params.put("cuerpo", "Fecha servicio guarderia: "+ fecha_notificar);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Guarderia_calendar.this);
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

                params.put("fecha_inicio", calendarServer(fechasReservas.get(0)));
                params.put("fecha_fin", calendarServer(fechasReservas.get(fechasReservas.size()-1)));
                params.put("direccion", id_direccion);
                params.put("descuento", desc_xdia_tv.getText().toString());
                params.put("precio_total", precioDiario.toString());
                params.put("dias_reserva", tv_dias.getText().toString());
                params.put("id_usuario_reserva", id_usuario_mascota);
                params.put("id_pro_servi", "1");
                params.put("estado_reserva", "Reserva exitosa");
                params.put("servicio_especifico", "guarderia");


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public void upDateReserva(boolean isTester) {

        String url = (isTester)?"http://app.animalresort.com.co/TestupDateReservas.php":"http://app.animalresort.com.co/upDateReservas.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    upDateReservasLocal();
                } catch (Exception e) {
                    Toast.makeText(Guarderia_calendar.this, "en response de upDateReserva " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Guarderia_calendar.this, "on response error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", ""+invocador);
                params.put("fecha_inicio", calendarServer(fechasReservas.get(fechasReservas.size()-1)));//
                params.put("fecha_fin", calendarServer(fechasReservas.get(fechasReservas.size()-1)));//
                params.put("direccion", id_direccion);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Guarderia_calendar.this);
        requestQueue.add(request);
    }

    private void upDateReservasLocal() {

        int descuento_result = 0;
        SQLiteDatabase db2 = conn.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put(Utilidades.CAMPO_FECHA_INICIO, calendarServer(fechasReservas.get(fechasReservas.size()-1)));
        values2.put(Utilidades.CAMPO_FECHA_FIN, calendarServer(fechasReservas.get(fechasReservas.size()-1)));
        values2.put(Utilidades.CAMPO_CIUDAD, id_direccion);
        descuento_result = db2.update(Utilidades.TABLA_RESERVA, values2, "id =?", new String[]{invocador});
        db2.close();
        Toast.makeText(getApplicationContext(), "Guarderia actualizada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Guarderia_calendar.this, Mis_reservas.class);
        startActivity(intent);
        finish();
    }
    /** Metodos iniciales del activity
     */
    private void calPrecio() {

        int contador = 0;
        int size = lista_mascotas.size();
        for (int i = 0; i < size; i++) {
            if (listado_mascotas.isItemChecked(i)) {
                contador++;
            }
        }

        if(fechasReservas.size()>0){
            descuento=(contador>0)? Integer.parseInt(lista_descuentos_mascota.get(contador-1)): 0;
        }

        if (fechasReservas.size() <= 4) {
            String desc = lista_descuentos.get(0);
            tv_precio.setText("" + (contador * (fechasReservas.size() * Integer.parseInt(desc))-descuento));
            desc_xdia_tv.setText("0");

        } else if (fechasReservas.size() >= 5 && fechasReservas.size() <= 9) {
            String desc_fijo = lista_descuentos.get(0);
            String desc = lista_descuentos.get(1);
            tv_precio.setText("" + (contador * (fechasReservas.size() * Integer.parseInt(desc))-descuento));
            Integer p = ((Integer.parseInt(desc_fijo)) - (Integer.parseInt(desc)));
            desc_xdia_tv.setText("" + p);

        } else if (fechasReservas.size() >= 10 && fechasReservas.size() <= 14) {
            String desc_fijo = lista_descuentos.get(0);
            String desc = lista_descuentos.get(2);
            tv_precio.setText("" + (contador * (fechasReservas.size() * Integer.parseInt(desc))-descuento));
            desc_xdia_tv.setText("" + (Integer.parseInt(desc_fijo) - Integer.parseInt(desc)));

        } else if (fechasReservas.size() >= 15) {
            String desc_fijo = lista_descuentos.get(0);
            String desc = lista_descuentos.get(3);
            tv_precio.setText("" + (contador * (fechasReservas.size() * Integer.parseInt(desc))-descuento));
            desc_xdia_tv.setText("" + (Integer.parseInt(desc_fijo) - Integer.parseInt(desc)));

        }
        num_masc_tv.setText("" + contador);
        dias_selec_tv.setText("" + fechasReservas.size());

        String desc = lista_descuentos.get(0);
        precio_dias_sindesc_tv.setText("" + Integer.parseInt(desc));
        precioDiario = Double.parseDouble(tv_precio.getText().toString()) / fechasReservas.size();
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

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public void traer_mascotas() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_masc,nombre_mascota,anios_masc,meses_masc,raza_masc,tamanno_mascota,usuario_mascota,genero FROM reg_mascota WHERE activo_mascota='activo' ", null);
        try {
            cursor.moveToFirst();
            if (lista_mascotas.size() >= 1) lista_mascotas.clear();
            if (mascotaArray.size() >= 1) mascotaArray.clear();
            for (int i = 0; i < cursor.getCount(); i++) {

                String id_mascotas = cursor.getString(cursor.getColumnIndex("id_masc"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre_mascota"));
                String años = cursor.getString(cursor.getColumnIndex("anios_masc"));
                String meses = cursor.getString(cursor.getColumnIndex("meses_masc"));
                String raza = cursor.getString(cursor.getColumnIndex("raza_masc"));
                String tamanno = cursor.getString(cursor.getColumnIndex("tamanno_mascota"));
                String genero = cursor.getString(cursor.getColumnIndex("genero"));
                id_usuario_mascota = cursor.getString(cursor.getColumnIndex("usuario_mascota"));

                lista_mascotas.add(nombre);

                Mascota m = new Mascota();
                m.setId_mas(id_mascotas);
                m.setNombre_mas(nombre);
                m.setAños_mas(años);
                m.setMeses_mas(meses);
                m.setTamaño_mas(tamanno);
                m.setRaza_mas(raza);
                m.setGenero_mas(genero);
                mascotaArray.add(m);

                cursor.moveToNext();

            }
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lista_mascotas);
            listado_mascotas.setAdapter(mAdapter);
            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error traer_mascotas GuarderiaCalendar " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void traerPrecios() {

        lista_descuentos = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='1'", null);

        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String valor = cursor.getString(cursor.getColumnIndex("valor_descuento"));
                lista_descuentos.add(valor);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error consultarBD GuarderiaCalendar " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void traer_direcciones() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT direccion,ciudad, id_direccion FROM mis_direcciones WHERE seleccion_defecto='true'", null);
        try {
            cursor.moveToFirst();
            if (lista_direcciones.size() >= 1) lista_direcciones.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                id_direccion = cursor.getString(cursor.getColumnIndex("id_direccion"));
                String direccion = cursor.getString(cursor.getColumnIndex("direccion"));
                String ciudad = cursor.getString(cursor.getColumnIndex("ciudad"));
                lista_direcciones.add(ciudad + ", " + direccion);
                direccion_reserva.setText(ciudad + "-" + direccion);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error traer_direcciones GuarderiaCalendar " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getTipoUsuario() {
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo_usuario FROM usuario", null);
        cursor.moveToFirst();
        TipoUsuario = cursor.getString(cursor.getColumnIndex("tipo_usuario"));
        cursor.close();
        db.close();
    }
    /** calendar
     */

    public void rellenar_fechas() {

        Calendar c = Calendar.getInstance();
        /**
         * esta es la marca del incremento
         */
        c.add(Calendar.DAY_OF_YEAR, 1);
        diaSem = c.get(Calendar.DAY_OF_WEEK);
      //  Toast.makeText(getApplicationContext(), "dia sem mes 1 "+diaSem, Toast.LENGTH_SHORT).show();

        int max_textviews = 40;

        for (int i = 1; i <= max_textviews; i++) {
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(c.getTime());
            dates.add(tmp);
            c.add(Calendar.DAY_OF_YEAR, 1);
            views.add((TextView) findViewById(getResources().getIdentifier("dia_" + i, "id", getPackageName())));
            textViews.add((TextView) findViewById(getResources().getIdentifier("dia_" + i, "id", getPackageName())));
        }
        int j = 0;
        Calendar calendarioMesAct = Calendar.getInstance();
        calendarioMesAct.add(Calendar.MONTH, 0);
        TextView tv1 = (TextView) findViewById(R.id.mesMostrar);
        tv1.setText(formatearCalendario(calendarioMesAct));
        Boolean pintaMes = false;

        for (int i = (diaSem - 1); i < max_textviews; i++) {
            textViews.get(i).setText(formatearCalendar(dates.get(j)));
            String cambioMes = formatearCalendario(dates.get(j));

            if (!cambioMes.equals(formatearCalendario(calendarioMesAct))) {

                if (!pintaMes) {
                    pintaMes = true;
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, 1);

                    TextView tvMesSiguiente = (TextView) findViewById(R.id.mesSig);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvMesSiguiente.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, textViews.get(i - 1).getId());

                    tvMesSiguiente.setLayoutParams(layoutParams);
                    tvMesSiguiente.setText(formatearCalendario(calendar));

                    Calendar cal = Calendar.getInstance();
                    /**
                     * para hacer que el calendario se incremente x dias al inicio de la funcion donde marqué el incremento
                     * debe tener el mismo valor que le sumo a j, si son diferentes la vista se daña, creanme
                     */
                    cal.add(Calendar.DAY_OF_YEAR, (j+1));

                    int dayW = cal.get(Calendar.DAY_OF_WEEK);
                //    Toast.makeText(getApplicationContext(), "dia sem mes 2 "+dayW, Toast.LENGTH_SHORT).show();
                    int maxCicle = 8 - dayW;

                    for (int x = 0; x < maxCicle; x++) {
                        RelativeLayout.LayoutParams layoutDias = (RelativeLayout.LayoutParams) textViews.get(i + x).getLayoutParams();
                        layoutDias.addRule(RelativeLayout.BELOW, tvMesSiguiente.getId());
                        textViews.get(i + x).setLayoutParams(layoutDias);
                    }

                    int comp = 7 - maxCicle;
                    comp = maxCicle + comp;

                    for (int y = maxCicle; y <= comp; y++) {
                        RelativeLayout.LayoutParams layoutDias = (RelativeLayout.LayoutParams) textViews.get(i + y).getLayoutParams();
                        layoutDias.addRule(RelativeLayout.BELOW, textViews.get(i).getId());
                        textViews.get(i + y).setLayoutParams(layoutDias);
                    }
                }
            }
            j++;
        }
    }

    public String formatearCalendar(Calendar c) {
        SimpleDateFormat format1 = new SimpleDateFormat("dd");
        String formatted = format1.format(c.getTime());
        return formatted;
    }
    public String formatearCalendario(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM");
        String formatted = format.format(calendar.getTime());
        return formatted;
    }
    public String calendarServer(Calendar c) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        format1.setTimeZone(TimeZone.getTimeZone("gmt"));
        String formatted = format1.format(c.getTime());
        return formatted;
    }
    public Calendar fechasString(String fechaParsear) {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            format1.setTimeZone(TimeZone.getTimeZone("gmt"));
            c.setTime(format1.parse(fechaParsear));
        } catch (Exception e) {
        }
        return c;
    }
}