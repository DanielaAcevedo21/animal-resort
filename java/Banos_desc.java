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

public class Banos_desc extends AppCompatActivity {

   private ArrayList<String> adcionales;
   private RecyclerView rvAdicionales;

   private String cadenaServicios="";

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banos_desc);

        rvAdicionales=findViewById(R.id.rvAdicionalesbanno);
        rvAdicionales.setHasFixedSize(true);
        rvAdicionales.setLayoutManager(new LinearLayoutManager(this));

        ImageButton menuPrincipal = findViewById(R.id.mainMenu);

        adicionalesBanno();

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
                                Intent intent = new Intent(Banos_desc.this, LoginGoogle.class);
                                startActivity(intent);
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Banos_desc.this, Mis_reservas.class);
                                startActivity(intent2);
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Banos_desc.this, Registrar_mascota.class);
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Banos_desc.this, Direcciones.class);
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Banos_desc.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Banos_desc.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Banos_desc.this, Mis_compras.class);
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

    private void adicionalesBanno() {

        adcionales = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM descuentos WHERE id_servicio2='5'",null);

        try {
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++){

                String id_banno= cursor.getString(cursor.getColumnIndex("id_descuentos"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre_descuento"));
                String precio = cursor.getString(cursor.getColumnIndex("valor_descuento"));
                cadenaServicios=cadenaServicios+nombre+"-";
                adcionales.add(nombre+"-"+precio+"-"+id_banno+"-"+i);
                cursor.moveToNext();
            }
            cursor.close();
            Adapter_AdicionalesBanno adapter_adicionalesBanno = new Adapter_AdicionalesBanno(Banos_desc.this,adcionales);
            rvAdicionales.setAdapter(adapter_adicionalesBanno);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en precios()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}