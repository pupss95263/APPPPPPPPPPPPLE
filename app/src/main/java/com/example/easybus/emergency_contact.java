package com.example.easybus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class emergency_contact extends AppCompatActivity {
    TextView back;
    String email, getmail,phone;
    RequestQueue requestQueue;
    EditText memergency;
    Button madd;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        getmail = mail();
        back = findViewById(R.id.back);
        memergency = findViewById(R.id.emergency_phone);
        madd = findViewById(R.id.add);
        requestQueue = Volley.newRequestQueue(this);
        //隱藏title bar///
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone =memergency.getText().toString().trim();
                flag = isNumeric(phone);
                if(flag == true){
                    UpdateUser(phone);
                }
                else{
                    Toast.makeText(emergency_contact.this, "電話格式不符", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(emergency_contact.this, Page8Activity.class);
                it1.putExtra("email",getmail);
                startActivity(it1);
            }
        });
    }

    public void UpdateUser(final String phone) {
        String URL = Urls.url1+"/LoginRegister/emergency_contact.php?email="+getmail;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")){
                            String s ="已將 "+phone+" 設為緊急聯絡電話";
                            Toast.makeText(emergency_contact.this, s, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(emergency_contact.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put("emergency_contact", phone);
                return parms;
            }
        };
        requestQueue.add(stringRequest);
    }
    public static boolean isNumeric(String str){
        if (str.length()>10){
            return false;
        }else {
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String mail() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }
        return email;
    }
}