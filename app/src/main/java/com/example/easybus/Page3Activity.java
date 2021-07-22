package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class Page3Activity extends AppCompatActivity {
    String email,getmail;
    RequestQueue requestQueue;
    com.example.easybus.FloatingActionButton f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);

        requestQueue = Volley.newRequestQueue(this);
        getmail = mail();
        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //跳頁到新增路線
        Button btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(Page3Activity.this,Page5Activity.class);
                it1.putExtra("email",getmail);
                startActivity(it1);
            }
        });

        //跳頁到搭車
        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = new Intent(Page3Activity.this,Page6Activity.class);
                it2.putExtra("email",getmail);
                startActivity(it2);
            }
        });

        //跳頁到注意事項
        Button btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it3 = new Intent(Page3Activity.this,Page7Activity.class);
                it3.putExtra("email",getmail);
                startActivity(it3);
            }
        });

        //跳頁到我的帳戶
        Button btn4 = (Button)findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it4 = new Intent(Page3Activity.this,Page8Activity.class);
                it4.putExtra("email",getmail);
                // Toast.makeText(Page3Activity.this, getmail, Toast.LENGTH_SHORT).show();
                startActivity(it4);
            }
        });

        //浮動按鈕撥打給緊急聯絡人
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readUser();
            }
        });
    }

    @SuppressLint("LongLogTag")
    protected void makeCall(final String phone) {
        //Snackbar.make(v,"打電話給緊急連絡人",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        Intent call = new Intent(Intent.ACTION_DIAL);
        Uri u = Uri.parse("tel:"+phone);
        call.setData(u);

        try {
            startActivity(call);
            finish();
            Log.i("Finished making a call...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Page3Activity.this, ex.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Page3Activity.this,
                    "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
        //startActivity(call        );
    }
    public void readUser(){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+getmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    String emergency_phone;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            emergency_phone = response.getString("emergency_contact");
                            makeCall(emergency_phone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Page3Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Page3Activity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    public String mail(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            email=extras.getString("email");
        }
        return email;
    }
}