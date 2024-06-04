package com.example.animalresort;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MenuUsuario extends AppCompatActivity  {

    private ImageButton mainMenu;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper( this, "bd_reservas", null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        Button guarderia = (Button) findViewById(R.id.guarderia);
        Button hotel = (Button)  findViewById(R.id.hotel);
        Button peluqueria = (Button) findViewById(R.id.peluqueria);
        Button banos = (Button) findViewById(R.id.banos);
        Button consultas = (Button) findViewById(R.id.consultas);
        Button tienda = (Button) findViewById(R.id.tienda);
        //tienda.setVisibility(View.INVISIBLE);
        ImageButton menuPrincipal = findViewById(R.id.mainMenu);
        registerForContextMenu(menuPrincipal);

        mainMenu=findViewById(R.id.mainMenu);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder= new AlertDialog.Builder(MenuUsuario.this);
                builder.setTitle("Atencion");
                builder.setMessage("Estas seguro que deseas salir?");

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog dialog= builder.create();
                dialog.show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        verificarBaseDatos();

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
                                Intent intent = new Intent(MenuUsuario.this, LoginGoogle.class);
                                startActivity(intent);

                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(MenuUsuario.this, Mis_reservas.class);
                                startActivity(intent2);

                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(MenuUsuario.this, Registrar_mascota.class);
                                startActivity(inte);

                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(MenuUsuario.this, Direcciones.class);
                                startActivity(inten);

                                return true;
                            case R.id.popup_menu_principal:

                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(MenuUsuario.this, Mis_compras.class);
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

       guarderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Guarderia_desc.class);
                startActivity(intent);
            }
        });

        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Hotel_desc.class);
                startActivity(intent);
            }
        });

        peluqueria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Peluqueria_desc.class);
                startActivity(intent);
            }
        });

        banos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Banos_desc.class);
                startActivity(intent);
            }
        });

        consultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Consulta_desc.class);
                startActivity(intent);
            }
        });

        tienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUsuario.this,Tienda.class);
                startActivity(intent);
            }
        });
    }
 /*
    public static void main(String[] args) { // no borrar ojo
        Login login = new Login();
        login.LoginU();
    }  */

    private void verificarBaseDatos(){

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT documento_usuario FROM usuario",null);
        try {

            cursor.moveToFirst();
            String ToF= cursor.getString(cursor.getColumnIndex("documento_usuario"));

            if(ToF.equals("vacio")){

                Intent intent = new Intent(MenuUsuario.this,LoginGoogle.class);
                startActivity(intent);

            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error en funcion usuario()"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}