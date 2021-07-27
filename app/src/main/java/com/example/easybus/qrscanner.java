package com.example.easybus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class qrscanner extends AppCompatActivity {
    String f_email,email,mygetmail;
    RequestQueue requestQueue, requestQueue1;
    Dialog dialog,fdialog;
    //判斷是否加好友、判斷是否存在
    Button btnok,btncancle,btngo;
    TextView maddfriend,friendname,myfriend,back;
    //private PopupWindow popupWindow = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(qrscanner.this,Page8Activity.class);
                intent.putExtra("email",mygetmail);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mygetmail=mail();
        requestQueue1 = Volley.newRequestQueue(this);
        requestQueue = Volley.newRequestQueue(this);
        //Initialize intent integrator
        IntentIntegrator intentIntegrator = new IntentIntegrator(qrscanner.this);
        //Set prompt text
        intentIntegrator.setPrompt("請將QRcode對準");
        //Set beep
        intentIntegrator.setBeepEnabled(true);
        //Locked orientation
        intentIntegrator.setOrientationLocked(true);
        //Set capture activity
        intentIntegrator.setCaptureActivity(Capture.class);
        //Initiate scan
        intentIntegrator.initiateScan();
        dialog = new Dialog(qrscanner.this);
        fdialog = new Dialog(qrscanner.this);

        dialog.setContentView(R.layout.activity_scan_dialog);
        //刪除dialog方方的背景
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnok = dialog.findViewById(R.id.okok);
        btncancle = dialog.findViewById(R.id.canclecancle);
        maddfriend = dialog.findViewById(R.id.addfriend);


        fdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fdialog.setContentView(R.layout.myfriend_dialog);
        btngo = fdialog.findViewById(R.id.button10);
        myfriend = fdialog.findViewById(R.id.myfriend);




    }
    public void addfriend(final String f_name,final String f_email,final String f_phone){
        String URL =Urls.url1+"/LoginRegister/addfriend.php?email="+mygetmail;
        //
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")){
                            Toast.makeText(qrscanner.this, "加入成功 !", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(qrscanner.this, "已為聯絡人", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(qrscanner.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                //引號內是key值;
                parms.put("email", mygetmail);
                parms.put("f_name", f_name);
                parms.put("f_email", f_email);
                parms.put("f_phone", f_phone);
                return parms;
            }
        };
        requestQueue1.add(stringRequest);
    }
    //加好友用的readUser
    public void readUser2(final String f_email){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+f_email;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    String f_name,f_phone;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            f_name = response.getString("fullname");
                            f_phone = response.getString("userphone");
                            addfriend(f_name,f_email,f_phone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(qrscanner.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(qrscanner.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

//判斷好柚是否已經存在friendexist

    public void friendexist(final String f_email) {
        String URL =Urls.url1+"/LoginRegister/friend_exist.php?email="+mygetmail+"&f_email="+f_email;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("existed")){
                            btngo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    fdialog.dismiss();
                                    Intent intent = new Intent(qrscanner.this,my_contact.class);
                                    intent.putExtra("email",mygetmail);
                                    startActivity(intent);
                                }
                            });
                            fdialog.show();
                           // Toast.makeText(emergency_contact.this, s, Toast.LENGTH_SHORT).show();
                        }else{
                            readUser(f_email);

                            btnok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    readUser2(f_email);
                                    Intent intent = new Intent(qrscanner.this,Page8Activity.class);
                                    intent.putExtra("email",mygetmail);
                                    startActivity(intent);
                                }
                            });
                            btncancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(qrscanner.this,Page8Activity.class);
                                    intent.putExtra("email",mygetmail);
                                    startActivity(intent);
                                }
                            });

                            dialog.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(qrscanner.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) ;
        requestQueue.add(stringRequest);
    }


    public void readUser(final String f_email){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+f_email;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    String f_name,f_phone;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            f_name = response.getString("fullname");
                            //dialog內的TextView
                            maddfriend.setText("是否加入\n"+f_name+"\n為聯絡人");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(qrscanner.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(qrscanner.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if(intentResult.getContents()!=null){
            //when reuslt content not null
            f_email = intentResult.getContents();
            friendexist(f_email);
            //如果聯絡人存在fdialog
            //如果這個聯絡人不存在 -> else readUser() -> dialog.show() -> 點確認 readUser2() ->返回page8
                                                                  //-> 點取消 -> 返回page8
        }else{
            Toast.makeText(getApplicationContext(), "掃描失敗", Toast.LENGTH_SHORT).show();
        }
    }

    public String mail(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            email=extras.getString("email");
        }
        return email;
    }
//悲劇
}