package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.net.InetAddress;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

public class Login3 extends AppCompatActivity {
    InetAddress ip;
    public static EditText mPassword;
    public static EditText mEmail;
    static String Password;
    static String Email;
    public static String password2;
    public static String email2;
    TextView mRegistertext,mForgetext;
    ImageButton backBtn;
    Button mLoginBtn;
    RequestQueue requestQueue;
    public static ProgressBar mProgressBar;
    public static CheckBox check;
    String email, password,identity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);
        //隱藏title bar///
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mPassword=findViewById(R.id.pass);
        mEmail=findViewById(R.id.email);
        mRegistertext=findViewById(R.id.registertext);
        mLoginBtn=findViewById(R.id.login);
        mForgetext=findViewById(R.id.Forgetext);
        backBtn=findViewById(R.id.back);
        check=findViewById(R.id.checkBox);
        mProgressBar=findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        requestQueue = Volley.newRequestQueue(this);
        //跳到註冊
        mRegistertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Signup3.class);
                startActivity(intent);
                finish();
            }
        });
        //跳到忘記密碼
        mForgetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Forgotpassword.class);
                startActivity(intent);
                finish();
            }
        });
        output();
        SharedPreferences user = getSharedPreferences("remember", MODE_PRIVATE);
        Email = user.getString("email", "");
        Password = user.getString("password", ""); //取得之前註冊好的資料

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                email = mEmail.getText().toString();
                password = mPassword.getText().toString();




                if (!email.equals("") && !password.equals("")) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[2];
                            field[0] = "email";
                            field[1] = "password";

                            String[] data = new String[2];
                            data[0] = email;
                            data[1] = password;

                            PutData putData = new PutData(Urls.url1+"/LoginRegister/login.php", "POST", field, data);//小高電腦的IP
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    mProgressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Login Success")) {
                                        readUser(email);
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "All filed required", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private void input(){
        SharedPreferences user=getSharedPreferences("remember",MODE_PRIVATE);
        SharedPreferences.Editor edit =user.edit();

        if(check.isChecked()){
            edit.putString("Email",mEmail.getText().toString());
            edit.putString("password2",mPassword.getText().toString());
            edit.putBoolean("remember",true);

        }else {
            edit.remove("Email");
            edit.remove("password2");
            edit.putBoolean("remember",false);
        }
        edit.commit();
    }
    private void output(){
        SharedPreferences user=getSharedPreferences("remember",MODE_PRIVATE);
        String email2=user.getString("Email","");  //若沒取得就是沒任何東西 " "
            String password2 = user.getString("password2", "");
            boolean remember = user.getBoolean("remember", false);
            //在input()我是寫成若打勾則讓它remember設為true，所以若沒取到則是false

            mEmail.setText(email2); //有取到先前給予的資料的話就會顯示出來
            mPassword.setText(password2);
            check.setChecked(remember);//並且利用remember的狀態判斷上次是否打勾，有的話就顯示打勾
    }
    public void readUser(final String email){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+email;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    String identity;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            identity = response.getString("identity");
                            turnpage(identity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login3.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login3.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void turnpage(String identity) {

        if("requester".equalsIgnoreCase(identity)) {
            Intent it4 = new Intent(Login3.this,Page8Activity.class);
            it4.putExtra("email", email);
            it4.putExtra("password",password);
            startActivity(it4);
            input();
            SharedPreferences email = getSharedPreferences("email",MODE_PRIVATE);
            SharedPreferences.Editor editor =email.edit();
            editor.putString("Email",mEmail.getText().toString());
            editor.commit();
            finish();
        }else if("caregiver".equalsIgnoreCase(identity)){
            Intent it = new Intent(Login3.this,Page8Activity_caregiver.class);
            it.putExtra("email", email);
            it.putExtra("password",password);
            startActivity(it);
            input();
            SharedPreferences email = getSharedPreferences("email",MODE_PRIVATE);
            SharedPreferences.Editor editor =email.edit();
            editor.putString("Email",mEmail.getText().toString());
            editor.commit();
            finish();
        }
    }

}
