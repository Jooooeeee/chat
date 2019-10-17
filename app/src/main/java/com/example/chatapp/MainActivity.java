package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.net.InetSocketAddress;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private Button server=null,client=null,addfile=null;
    private static final int REQUEST_CODE_CHOOSE=23;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    private void init(){
        server=findViewById(R.id.server);
        server.setOnClickListener(this);
        client=findViewById(R.id.client);
        client.setOnClickListener(this);
        addfile=findViewById(R.id.addfile);
        addfile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.server:
                Intent intent1=new Intent(MainActivity.this,ServiceActivity.class);
                startActivity(intent1);
                break;
            case R.id.client:
                Intent intent2=new Intent(MainActivity.this,ClientActivity.class);
                startActivity(intent2);
                break;
            case R.id.addfile:
               request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                default:
                    break;
        }
    }
    public void request(String permission){
        if (ContextCompat.checkSelfPermission(MainActivity.this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{permission},1);
        }
        else {
            matisse();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    matisse();
                }
                else {
                    Toast.makeText(this,"YOU DENIED THE PERMISSION",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void matisse(){
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofImage(),false)
                .countable(true)
                .maxSelectable(9)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                    }
                })
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }


}
