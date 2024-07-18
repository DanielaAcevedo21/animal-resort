package com.example.animalresort;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class Hotel_desc extends AppCompatActivity {

    private ImageButton mainMenu;
    private ArrayList<String> precios;
    private TextView precio1,precio5,precio10;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_desc);

        mainMenu = findViewById(R.id.mainMenu);
        precio1=findViewById(R.id.ht_dia);
        precio5=findViewById(R.id.ht_hasta9dias);
        precio10=findViewById(R.id.ht_10diasoMas);

        precios();

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
                                Intent intent = new Intent(Hotel_desc.this, LoginGoogle.class);
                                startActivity(intent);
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Hotel_desc.this, Mis_reservas.class);
                                startActivity(intent2);
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Hotel_desc.this, Registrar_mascota.class);
                                startActivity(inte);
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Hotel_desc.this, Direcciones.class);
                                startActivity(inten);
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Hotel_desc.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Hotel_desc.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Hotel_desc.this, Mis_compras.class);
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

    public void servicioHotelSeleccionado(View view){

        switch (view.getId()){
            case R.id.hotel1:
                Intent intenNormal = new Intent(Hotel_desc.this, Hotel_reservar.class);
                startActivity(intenNormal);
                break;
            case R.id.hotel5_9:
                Intent intent = new Intent(Hotel_desc.this, Hotel_reservar.class);
                intent.putExtra("servicio","1");
                intent.putExtra("tipo","insert");
                startActivity(intent);
                break;
            case  R.id.hotel10:
                Intent intent2 = new Intent(Hotel_desc.this, Hotel_reservar.class);
                intent2.putExtra("servicio","2");
                intent2.putExtra("tipo","insert");
                startActivity(intent2);
                break;
            default:
        }

    }
    private void precios() {

        precios = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='2'",null);

        try {
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++){

                String valor = cursor.getString(cursor.getColumnIndex("valor_descuento"));

                precios.add(valor);

                cursor.moveToNext();
            }
            cursor.close();

            precio1.setText("$"+precios.get(0));
            precio5.setText("$"+precios.get(1));
            precio10.setText("$"+precios.get(2));

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en precios()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

   }