package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class qrcode_page extends AppCompatActivity {
    String email,getmail,fullname,getfullname;
    ImageView qrcode,backBtn,qrscan;
    TextView back;
    String identity;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_page);

        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        qrscan = findViewById(R.id.qrscan_btn);
        qrcode=findViewById(R.id.qrimage);
        back=findViewById(R.id.back);
        requestQueue = Volley.newRequestQueue(this);
        getmail = mail();
        getfullname=fullname();
        readUser();
        //前往掃描
        qrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(qrcode_page.this,qrscanner.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();
            }
        });
        //返回健(基本資料)
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnpage(identity);
                /*Intent intent = new Intent(qrcode_page.this,Page8Activity.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();*/
            }
        });
        MultiFormatWriter writer = new MultiFormatWriter();
        try{
            BitMatrix martix = writer.encode(getmail, BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(martix);
            qrcode.setImageBitmap(bitmap);
        }catch (WriterException e) {
            e.printStackTrace();
        }

    }
    public String mail(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            email=extras.getString("email");
        }
        return email;
    }
    public String fullname(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            fullname=extras.getString("fullname");
        }
        return fullname;
    }
    public void readUser(){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+email;
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
                            Toast.makeText(qrcode_page.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(qrcode_page.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void turnpage(String identity) {

        if("requester".equalsIgnoreCase(identity)) {
            Intent it4 = new Intent(qrcode_page.this,Page8Activity.class);
            it4.putExtra("email", email);
            startActivity(it4);
            finish();
        }else if("caregiver".equalsIgnoreCase(identity)){
            Intent it = new Intent(qrcode_page.this,Page8Activity_caregiver.class);
            it.putExtra("email", email);
            startActivity(it);
            finish();
        }
    }
}