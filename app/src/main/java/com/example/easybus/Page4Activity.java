package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Page4Activity extends AppCompatActivity {
    String email,getmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page4);
        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getmail = mail();
        //Toast.makeText(Page4Activity.this, getmail, Toast.LENGTH_SHORT).show();
        //跳頁到定位查詢
        Button btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(Page4Activity.this,Page9Activity.class);
                it1.putExtra("email",getmail);
                startActivity(it1);
            }
        });
        //跳頁到歷史足跡
        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = new Intent(Page4Activity.this,Page10Activity.class);
                it2.putExtra("email",getmail);
                startActivity(it2);
            }
        });
        //跳頁到我的帳戶
        Button btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences email = getSharedPreferences("email",MODE_PRIVATE);
                String email2=email.getString("Email","");
                if(email2 != "") {
                    Intent it4 = new Intent(Page4Activity.this, Page8Activity_caregiver.class);
                    it4.putExtra("email", email2);
                    startActivity(it4);
                }else{
                    Intent it = new Intent(Page4Activity.this,Login2.class);
                    startActivity(it);
                }
            }
        });

    }
    public String mail(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            email=extras.getString("email");
        }
        return email;
    }
}