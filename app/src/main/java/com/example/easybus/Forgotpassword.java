package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Forgotpassword extends AppCompatActivity {

    EditText mEmail;
    Button mForgotPassword;

    ProgressDialog progressDialog;
    TextView back;

    StringRequest stringRequest;
    String URL ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mEmail = findViewById(R.id.Email);
        mForgotPassword = findViewById(R.id.forgotpassword);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("寄送中......");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //忘記密碼回登入頁
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(Forgotpassword.this,Login3.class);
                startActivity(it1);
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                boolean match =validateEmailAddress(email);
                if(match==true) {
                    progressDialog.show();
                    if (email.isEmpty()) {
                        Toast.makeText(Forgotpassword.this, "輸入email", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        stringRequest = new StringRequest(Request.Method.POST, Urls.url1+"/mysql/forgot.php", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String mail = object.getString("mail");
                                    if (mail.equals("send")) {
                                        Toast.makeText(Forgotpassword.this, "請去查看email!!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(Forgotpassword.this, response, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override

                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Forgotpassword.this, error.toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("email", email);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(Forgotpassword.this);
                        requestQueue.add(stringRequest);
                    }
                }
            }
        });
    }

    private boolean validateEmailAddress(String email){ //加空值判斷
        String emailInput = email;

        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            //Toast.makeText(this,"Email Validated Successfully!",Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(this,"InValid Email Address!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}