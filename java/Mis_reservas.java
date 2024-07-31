package com.example.animalresort;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Mis_reservas extends AppCompatActivity {

    private final ArrayList<Reserva> lista_reservas = new ArrayList<>();
    private final ArrayList<String> lista_direcciones = new ArrayList<>();
    private ImageButton mainMenu;
    private Button historial_res;
    static String id_usu;
    ArrayAdapter<String> adapter;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        historial_res = findViewById(R.id.historial_res);
        recyclerView=findViewById(R.id.rvAnimals);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainMenu=findViewById(R.id.mainMenu);

        traer_reservas();

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
                                Intent intent = new Intent(Mis_reservas.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Mis_reservas.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Mis_reservas.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Mis_reservas.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Mis_reservas.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Mis_reservas.this, MenuUsuario.class);
                                    startActivity(inten4);
                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Mis_reservas.this, Mis_compras.class);
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

        historial_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                traerIdUsu();
                try {
                    SQLiteDatabase db1 = conn.getReadableDatabase();
                    //db1.execSQL("DELETE  FROM reserva");
                    //db1.close();
                } catch (Exception e) {
                    Toast.makeText(Mis_reservas.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                llamar_historial();

            }
        });

    }
    public void llamar_historial(){

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM reserva ORDER BY id ASC",null);
     //   Toast.makeText(Mis_reservas.this,"Entro al metodo del historial ",Toast.LENGTH_SHORT).show();
        try {
            cursor.moveToFirst();
         //   Toast.makeText(Mis_reservas.this,"Cursor: "+cursor.getCount(),Toast.LENGTH_SHORT).show();

            if (lista_reservas.size()>=1) lista_reservas.clear();

            for(int i=0;i<cursor.getCount();i++){
           //     Toast.makeText(Mis_reservas.this,"Entro al FOR ",Toast.LENGTH_SHORT).show();
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String fecha_ini = cursor.getString(cursor.getColumnIndex("fecha_inicio"));
                String fecha_fin = cursor.getString(cursor.getColumnIndex("fecha_fin"));
                String direccionPorId = cursor.getString(cursor.getColumnIndex("ciudad"));
                String descuento = cursor.getString(cursor.getColumnIndex("descuento"));
                String precioTotal = cursor.getString(cursor.getColumnIndex("precio_total"));
                String estado= cursor.getString(cursor.getColumnIndex("estado_reserva"));
                String dias = cursor.getString(cursor.getColumnIndex("dias_reserva"));
                String servicio = cursor.getString(cursor.getColumnIndex("servicio_especifico"));

                String infoDireccion = traer_direcciones(direccionPorId);

                String[] Vector = infoDireccion.split(",");

                String direccion= Vector[0]+"-"+Vector[1];
                String descripsion= Vector[2];

                lista_reservas.add( new Reserva(id,fecha_ini,fecha_fin, direccion, descripsion,
                        "$"+precioTotal,estado,"$"+(Integer.parseInt(descuento)*Integer.parseInt(dias)),dias, servicio, traerMascotasReservadas(id)) );
                cursor.moveToNext();
            }
            Adapter adapterReservas = new Adapter(Mis_reservas.this, lista_reservas);
            recyclerView.setAdapter(adapterReservas);
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion Historial()  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void traerIdUsu(){
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_usuario FROM usuario" , null);
        try {

            if (cursor.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "el cursor es 0", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToFirst();
                id_usu = cursor.getString(cursor.getColumnIndex("id_usuario"));
                cursor.close();
                db.close();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error en traer id del usuario " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void traer_reservas() {
      //  Toast.makeText(Mis_reservas.this, "Entro a Traer_Reservas", Toast.LENGTH_SHORT).show();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM reserva WHERE estado_reserva = 'Reserva exitosa' AND fecha_inicio >= '"+fechaHoy+"' ORDER BY fecha_inicio ASC",null);

        try {

            cursor.moveToFirst();

            if (lista_reservas.size()>=1) lista_reservas.clear();

            for(int i=0;i<cursor.getCount();i++){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String fecha_ini = cursor.getString(cursor.getColumnIndex("fecha_inicio"));
                String fecha_fin = cursor.getString(cursor.getColumnIndex("fecha_fin"));
                String direccionPorId = cursor.getString(cursor.getColumnIndex("ciudad"));
                String descuento = cursor.getString(cursor.getColumnIndex("descuento"));
                String precioTotal = cursor.getString(cursor.getColumnIndex("precio_total"));
                String estado= cursor.getString(cursor.getColumnIndex("estado_reserva"));
                String dias = cursor.getString(cursor.getColumnIndex("dias_reserva"));
                String servicio = cursor.getString(cursor.getColumnIndex("servicio_especifico"));

                String infoDireccion = traer_direcciones(direccionPorId);
               // Toast.makeText(getApplicationContext(), "Info direcciones: "+infoDireccion, Toast.LENGTH_SHORT).show();

                String[] Vector = infoDireccion.split(",");

                String direccion= Vector[0]+"-"+Vector[1];
                String descripsion= Vector[2];

              lista_reservas.add( new Reserva(id,fecha_ini,fecha_fin, direccion, descripsion,
                    "$"+precioTotal,estado,"$"+(Integer.parseInt(descuento)*Integer.parseInt(dias)),dias, servicio, traerMascotasReservadas(id)) );
              cursor.moveToNext();
            }
           Adapter adapterReservas = new Adapter(Mis_reservas.this, lista_reservas);
          recyclerView.setAdapter(adapterReservas);
            cursor.close();
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_reservas() "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public String traer_direcciones(String direccionCompleta ) {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT id_direccion, direccion,ciudad, descripcion_casa FROM mis_direcciones",null);
        try {
            cursor.moveToFirst();
            if (lista_direcciones.size()>=1) lista_direcciones.clear();

            for(int i=0;i<cursor.getCount();i++){

                String direc = cursor.getString(cursor.getColumnIndex("direccion"));
                String ciudad = cursor.getString(cursor.getColumnIndex("ciudad"));
                String id = cursor.getString(cursor.getColumnIndex("id_direccion"));
                String desc= cursor.getString(cursor.getColumnIndex("descripcion_casa"));
                lista_direcciones.add(id+","+ciudad+","+direc);

              if(id.equals(direccionCompleta)){
                  direccionCompleta=ciudad+","+direc+","+desc;
              }

              cursor.moveToNext();

            }
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traer_direcciones()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return direccionCompleta;

    }

    public ArrayAdapter<String> traerMascotasReservadas(String id){

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT nombre_mascota FROM reg_mascota m INNER JOIN mascotas_reservadas r on m.id_masc = r.id_mascota where r.id_reservacion='"+id+"'",null);

        try {
            ArrayList <String> mascotas_res = new ArrayList<>();
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++) {
                String nombre_masc = cursor.getString(cursor.getColumnIndex("nombre_mascota"));
                mascotas_res.add(nombre_masc);
                cursor.moveToNext();
            }
            adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item, mascotas_res);
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traerMascotasReservadas!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return adapter;
    }
}