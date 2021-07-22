package com.example.easybus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    String[] permissionsArray=new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE};
    List<String> permissionsList=new ArrayList<>();
    private static final int REQUEST_CODE_ASK_PERMISSION = 1;
    String email2,identity,email;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        Button btn1 = (Button)findViewById(R.id.btn1);

        SharedPreferences email = getSharedPreferences("email",MODE_PRIVATE);
        email2=email.getString("Email","");
        readUser();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MainActivity.this,identity,Toast.LENGTH_LONG).show();
                readUser();
                if(email2 != "") {
                    if("requester".equalsIgnoreCase(identity)) {
                        Intent it4 = new Intent(MainActivity.this,Page3Activity.class);
                        it4.putExtra("email",email2);
                        startActivity(it4);
                    }else if("caregiver".equalsIgnoreCase(identity)){
                        Intent it = new Intent(MainActivity.this,Page4Activity.class);
                        it.putExtra("email",email2);
                        startActivity(it);
                    }
                }else{
                    Intent it = new Intent(MainActivity.this,Login3.class);
                    startActivity(it);
                }

            }
        });

        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //定位權限
        checkRequiredPermission(this);
    }

    private void checkRequiredPermission(Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size()>0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION:
                for (int i=0; i<permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "!!!!!!"+permissions[i], Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "本App需允許授權，才能 "+permissions[i], Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri u = Uri.fromParts("package", getPackageName(), null);
                        it.setData(u);
                        startActivity(it);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void readUser(){

        final String URL =Urls.url1+"/LoginRegister/fetch.php?email="+email2;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            identity = response.getString("identity");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(MainActivity.this,"錯誤拉幹\n"+URL,Toast.LENGTH_LONG).show();
                       // Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
