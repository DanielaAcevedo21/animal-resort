package com.example.animalresort;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Mis_compras extends AppCompatActivity {

    private final ArrayList<Compra> lista_compras = new ArrayList<>();
    private ImageButton mainMenu;
    private RecyclerView recyclerCompras;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_compras);

        recyclerCompras=findViewById(R.id.rvCompras);
        recyclerCompras.setHasFixedSize(true);
        recyclerCompras.setLayoutManager(new LinearLayoutManager(this));

        mainMenu=findViewById(R.id.mainMenu);

        traerCompras();

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
                                Intent intent = new Intent(Mis_compras.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Mis_compras.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Mis_compras.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Mis_compras.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Mis_compras.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Mis_compras.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Mis_compras.this, Mis_compras.class);
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

    public void traerCompras() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM ventas_tienda ORDER BY id_venta DESC",null);

        try {

            cursor.moveToFirst();

            if (lista_compras.size()>=1) lista_compras.clear();

            for(int i=0;i<cursor.getCount();i++){

                String id = cursor.getString(cursor.getColumnIndex("id_venta"));
                String nombre = cursor.getString(cursor.getColumnIndex("producto"));
                String direccion = cursor.getString(cursor.getColumnIndex("direccion_venta"));
                String precio = cursor.getString(cursor.getColumnIndex("precio_venta_tienda"));
                String fecha= cursor.getString(cursor.getColumnIndex("fecha_venta"));
                String cantidad= cursor.getString(cursor.getColumnIndex("cantidad_producto"));
                String estado= cursor.getString(cursor.getColumnIndex("estado_compra"));
                lista_compras.add( new Compra(id,nombre,direccion,precio,fecha,cantidad,estado));

                cursor.moveToNext();

            }

            Adapter_Compras adapter = new Adapter_Compras(Mis_compras.this, lista_compras);
            recyclerCompras.setAdapter(adapter);

            cursor.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion traerCompras()  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}