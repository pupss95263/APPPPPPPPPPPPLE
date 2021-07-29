package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class my_contact extends AppCompatActivity {
    String email,getmail,img;
    TextView mEnteredName;
    ImageView backBtn;
    RequestQueue requestQueue;
    RecyclerView mrecyclerView;
    friendAdapter friendAdapter;
    CircleImageView mPforfilepic;
    List<friend> friendList;
    Dialog dialog;
    Button clickme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        //隱藏title bar///
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        backBtn=findViewById(R.id.backicon);
        mEnteredName = findViewById(R.id.EnteredName);
        dialog = new Dialog(my_contact.this);
        mPforfilepic = findViewById(R.id.profilepic);
        dialog.setContentView(R.layout.nofriend_dialog);
        //刪除dialog方方的背景
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clickme=dialog.findViewById(R.id.button10);

        LoadAllfriend();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mrecyclerView=findViewById(R.id.recyclerview);
        mrecyclerView.setHasFixedSize(true);
        // LinearLayoutManager llm = new LinearLayoutManager(this);
        //llm.setOrientation(LinearLayoutManager.VERTICAL);
        //mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerView.setLayoutManager(linearLayoutManager);

        friendList = new ArrayList<>();
        //friendAdapter = new friendAdapter(my_contact.this,friendList);
        //mrecyclerView.setAdapter(friendAdapter);
        requestQueue = Volley.newRequestQueue(this);
        getmail=mail();
        readUser();
        fetchimage();
        //返回我的資料
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(my_contact.this,Page8Activity.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();
            }
        });

        String URL =Urls.url1+"/LoginRegister/my_contact.php?email="+getmail;
        //Request.Method.GET,URL,null我自己加的
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,URL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {//////
                        if (array.length()==0){
                            dialog.show();
                            clickme.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(my_contact.this,qrcode_page.class);
                                    intent.putExtra("email",getmail);
                                    startActivity(intent);
                                }
                            });
                        }else
                            for(int i =0;i<array.length();i++){
                                try {
                                    JSONObject object = array.getJSONObject(i);
                                    String name = object.getString("F_name").trim();
                                    String phone = object.getString("F_phone").trim();

                                    friend f =new friend();
                                    f.setF_name(name);
                                    f.setF_phone(phone);
                                    friendList.add(f);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        friendAdapter = new friendAdapter(my_contact.this,friendList);
                        mrecyclerView.setAdapter(friendAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(my_contact.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue2 = Volley.newRequestQueue(my_contact.this);
        requestQueue2.add(request);

    }

    private void LoadAllfriend() {

    }

    //抓取使用者基本資料
    private void readUser(){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+getmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String fullname;
                        try {
                            fullname = response.getString("fullname");
                            mEnteredName.setText(fullname);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(my_contact.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(my_contact.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    public  void fetchimage(){
        String URL =Urls.url1+"/LoginRegister/fetchimage.php?email="+getmail;
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    img  = jsonObject.getString("image");

                    ImageRetriveWithPicasso();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(my_contact.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(my_contact.this);
        requestQueue.add(request);
    }
    public void ImageRetriveWithPicasso() {
        String imgurl = Urls.url1+"/LoginRegister/images/"+img;
        Picasso.with(this)

                .load(imgurl)
                .placeholder(R.drawable.profile)
                .fit()
                // .error(R.drawable.ic_error_black_24dp)
                .into(mPforfilepic, new Callback() {
                    @Override
                    public void onSuccess() {
                        // 圖片讀取完成
                        Toast.makeText(my_contact.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        // 圖片讀取失敗
                        //  Toast.makeText(Page8Activity.this, "失敗拉幹", Toast.LENGTH_SHORT).show();
                    }
                });
        System.out.println(imgurl);
    }
    public String mail(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            email=extras.getString("email");
        }
        return email;
    }
}