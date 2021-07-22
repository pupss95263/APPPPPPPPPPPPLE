package com.example.easybus;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class edit_password extends AppCompatActivity {
    String email, getmail, password, getpass;
    EditText pas1, pas2, pas3;
    public String pass1, pass2, pass3;
    RequestQueue requestQueue;
    Button btnok;
    TextView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        pas1 = findViewById(R.id.password1);
        pas2 = findViewById(R.id.password2);
        pas3 = findViewById(R.id.password3);
        btnok = findViewById(R.id.okBtn);
        btnback = findViewById(R.id.back);
        requestQueue = Volley.newRequestQueue(this);

        getmail = mail();
        getpass = pass();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readUser(email);
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass1 = pas1.getText().toString().trim();
                pass2 = pas2.getText().toString().trim();
                pass3 = pas3.getText().toString().trim();
                if (pass1.isEmpty() || pass2.isEmpty() || pass3.isEmpty()){
                    Toast.makeText(edit_password.this, "不可空白", Toast.LENGTH_SHORT).show();
                }else{
                    if(!pass1.equals(getpass)){
                        Toast.makeText(edit_password.this, "請輸入正確密碼", Toast.LENGTH_SHORT).show();
                    }else  if(!pass2.equals(pass3)){
                        Toast.makeText(edit_password.this, "請輸入相同密碼", Toast.LENGTH_SHORT).show();
                    }else if(pass2.equals(pass1) && pass3.equals(pass1)){
                        Toast.makeText(edit_password.this, "不可與原密碼相同", Toast.LENGTH_SHORT).show();
                    }else{
                        UpdateUser(pass3);
                    }
                }
            }
        });
    }
    public void UpdateUser(final String pass3) {
        String URL = Urls.url1+"/LoginRegister/edit.php?email="+getmail;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")){
                            Intent intent =new Intent(edit_password.this,Login3.class);
                            Toast.makeText(edit_password.this, "請重新登入", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(edit_password.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put("password", pass3);
                return parms;
            }
        };
        requestQueue.add(stringRequest);
    }

    public String mail() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }
        return email;
    }

    public String pass() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            password = extras.getString("password");
        }
        return password;
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
                            Toast.makeText(edit_password.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(edit_password.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void turnpage(String identity) {

        if("requester".equalsIgnoreCase(identity)) {
            Intent it4 = new Intent(edit_password.this,Page8Activity.class);
            it4.putExtra("email", email);
            startActivity(it4);
        }else if("caregiver".equalsIgnoreCase(identity)){
            Intent it = new Intent(edit_password.this,Page8Activity_caregiver.class);
            it.putExtra("email", email);
            startActivity(it);

        }
    }
}