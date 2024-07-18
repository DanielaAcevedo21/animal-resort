package com.example.animalresort;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class Guarderia_desc extends AppCompatActivity{

    private ArrayList<String> precios;
    private ImageButton mainMenu;
    private TextView precioDia,precio5,precio10,precio15;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarderia_desc);

        mainMenu=findViewById(R.id.mainMenu);
        precioDia= findViewById(R.id.tv_precio_por_dia);
        //precioDiaL = findViewById(R.id.precioDiaL);
        precio5= findViewById(R.id.tv_precio_hasta_dia9);
        precio10= findViewById(R.id.tv_precio_hasta_dia14);
        precio15= findViewById(R.id.tv_precio_16dias_o_mas);


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
                                Intent intent = new Intent(Guarderia_desc.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Guarderia_desc.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Guarderia_desc.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Guarderia_desc.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor =db.rawQuery("SELECT tipo_usuario FROM usuario",null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if(!tipo_usu.equals("Usuario")){
                                    Intent inten5 = new Intent(Guarderia_desc.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                }
                                else {
                                    Intent inten4 = new Intent(Guarderia_desc.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Guarderia_desc.this, Mis_compras.class);
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

    public void servicioGuarderiaSeleccionado(View view){

        switch (view.getId()){

            case R.id.precioDiaL:
                Intent intentB = new Intent(Guarderia_desc.this, Guarderia_calendar.class);
                startActivity(intentB);
                break;
            case R.id.dias5_9:
                Intent intent = new Intent(Guarderia_desc.this, Guarderia_calendar.class);
                intent.putExtra("servicio","1");
                intent.putExtra("tipo","insert");
                startActivity(intent);
                break;
            case  R.id.dias10_14:
                Intent intent2 = new Intent(Guarderia_desc.this, Guarderia_calendar.class);
                intent2.putExtra("servicio","2");
                intent2.putExtra("tipo","insert");
                startActivity(intent2);
                break;
            case  R.id.dias15:
                Intent intent3 = new Intent(Guarderia_desc.this, Guarderia_calendar.class);
                intent3.putExtra("servicio","3");
                intent3.putExtra("tipo","insert");
                startActivity(intent3);
                break;

            default:
        }

    }

    private void precios() {

        precios = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT valor_descuento FROM descuentos WHERE id_servicio2='1'",null);

        try {
            cursor.moveToFirst();

        //    Toast.makeText(getApplicationContext(), ""+cursor.getCount(), Toast.LENGTH_SHORT).show();// 5 en bd
            for(int i=0;i<cursor.getCount();i++){

                String valor = cursor.getString(cursor.getColumnIndex("valor_descuento"));

                precios.add(valor);

                cursor.moveToNext();
            }
            cursor.close();

            precioDia.setText("$"+precios.get(0));

            precio5.setText("$"+precios.get(1));

            precio10.setText("$"+precios.get(2));
            precio15.setText("$"+precios.get(3));

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en precios()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}