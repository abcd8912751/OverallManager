package com.furja.overall.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.furja.common.Preferences;
import com.furja.overall.R;
import com.furja.utils.AutoUpdateUtils;
import com.furja.utils.MyCrashHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.sentry.Sentry;

import static com.furja.utils.Utils.showLog;

public class MasterActivity extends BaseActivity {

    Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_qc, R.id.navigation_own)
                .build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navController.navigate(R.id.navigation_qc);
        vertifyPermission();
        silentLogin();
    }
    public void vertifyPermission(){
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE};
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(disposable!=null)
            disposable.dispose();
        new AutoUpdateUtils(this,true).checkUpdate();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        disposable = Observable.timer(10,TimeUnit.MINUTES)
                .subscribe(event->{ //30S未操作就自行退出
                    System.exit(0);
                },error->{
            });    }
}
