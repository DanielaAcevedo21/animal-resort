package com.example.animalresort;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Direcciones extends AppCompatActivity {

    private ImageButton mainMenu;
    private ArrayList<Direccion> listado_Ob_direc = new ArrayList<>();
    private RecyclerView recyclerView;
    private String direccionS, ciudadS, ToF, descripS;

    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_reservas", null, 1);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direcciones);

        Button btnAñadirDirec = findViewById(R.id.AgregarDireccion);
        mainMenu = findViewById(R.id.mainMenu);
        recyclerView = findViewById(R.id.rvDirecciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        traer_direcciones();

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
                                Intent intent = new Intent(Direcciones.this, LoginGoogle.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.popup_reservas:
                                Intent intent2 = new Intent(Direcciones.this, Mis_reservas.class);
                                startActivity(intent2);
                                finish();
                                return true;
                            case R.id.popup_mascotas:
                                Intent inte = new Intent(Direcciones.this, Registrar_mascota.class);
                                startActivity(inte);
                                finish();
                                return true;
                            case R.id.popup_direcciones:
                                Intent inten = new Intent(Direcciones.this, Direcciones.class);
                                startActivity(inten);
                                finish();
                                return true;
                            case R.id.popup_menu_principal:
                                SQLiteDatabase db = conn.getReadableDatabase();
                                Cursor cursor = db.rawQuery("SELECT tipo_usuario FROM usuario", null);
                                cursor.moveToFirst();
                                String tipo_usu = cursor.getString(cursor.getColumnIndex("tipo_usuario"));

                                if (!tipo_usu.equals("Usuario")) {
                                    Intent inten5 = new Intent(Direcciones.this, Menu_usuAdmin.class);
                                    startActivity(inten5);
                                } else {
                                    Intent inten4 = new Intent(Direcciones.this, MenuUsuario.class);
                                    startActivity(inten4);

                                }
                                finish();
                                return true;
                            case R.id.popup_compras:
                                Intent intent6 = new Intent(Direcciones.this, Mis_compras.class);
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

        btnAñadirDirec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Direcciones.this, CUDirecciones.class);
                intent.putExtra("invocador", "crear");
                startActivity(intent);
                finish();
            }
        });

    }

    public void traer_direcciones() {

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mis_direcciones", null);
        try {

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {

                direccionS = cursor.getString(cursor.getColumnIndex("direccion"));
                ciudadS = cursor.getString(cursor.getColumnIndex("ciudad"));
                ToF = cursor.getString(cursor.getColumnIndex("seleccion_defecto"));
                descripS = cursor.getString(cursor.getColumnIndex("descripcion_casa"));

                Direccion direc = new Direccion();
                direc.setDireccion(direccionS);
                direc.setId_direccion(cursor.getString(cursor.getColumnIndex("id_direccion")));
                direc.setNombre_ciudad(ciudadS);
                direc.setDescripcion_casa(descripS);
                listado_Ob_direc.add(direc);

                cursor.moveToNext();
            }
            Adapter_Direccion adapter_direccion = new Adapter_Direccion(Direcciones.this, listado_Ob_direc);
            recyclerView.setAdapter(adapter_direccion);
            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error en funcion direcciones()" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}