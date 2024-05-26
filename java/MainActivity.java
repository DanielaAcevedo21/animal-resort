package com.example.animalresort;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    ImageView logo;
    private final int DURACION_SPLASH = 3000;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                Toast.makeText(this, "Habilita las notificaciones", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, 123);
            } else {
                splash();
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            splash();
        }
    }
    private void splash(){
         Animation animacion = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
         logo.setAnimation(animacion);

         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 Intent intent = new Intent(MainActivity.this, Login.class);

                 Pair[] pairs = new Pair[1];
                 pairs[0] = new Pair<View, String>(logo, "logo");

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                     ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                     startActivity(intent, options.toBundle());
                     finish();
                 } else {
                     startActivity(intent);
                     finish();
                 }
             }
         }, DURACION_SPLASH);
     }
}