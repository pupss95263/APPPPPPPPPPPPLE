package com.example.easybus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Page8Activity_caregiver extends AppCompatActivity {
    SelectPicPopupWindow menuWindow; //自訂義的彈出框類別(SelectPicPopupWindow)

    ImageView backBtn,editpassword,qrcode,emergency,mycontact,logout;
    CircleImageView mPforfilepic;

    TextView mEnteredName,myphone;
    String identity;
    public static final int SELECT_PHOTO=1;
    public static final int TAKE_PHOTO = 3;
    private Uri imageUri;
    private Context mContext;
    public String email,getmail,password,getpass,fullname,pic,phone,encodeimage,img;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page8_caregiver);

        //隱藏title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        backBtn=findViewById(R.id.backicon);
        mEnteredName = findViewById(R.id.EnteredName);
        mPforfilepic = findViewById(R.id.profilepic);
        myphone = findViewById(R.id.txt1);
        editpassword=findViewById(R.id.frame2);
        qrcode = findViewById(R.id.frame3);
        mycontact = findViewById(R.id.frame5);
        logout = findViewById(R.id.frame8);
        mContext = Page8Activity_caregiver.this;

        requestQueue = Volley.newRequestQueue(this);
        getmail=mail();
        getpass=pass();
        readUser();
        fetchimage();
        ImageRetriveWithPicasso();
        //我的聯絡人
        mycontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page8Activity_caregiver.this,my_contact.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();
            }
        });
       /* //新增緊急聯絡人
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page8Activity.this,emergency_contact.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();
            }
        });*/
        //登出
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences email = getSharedPreferences("email",MODE_PRIVATE);
                email.edit().clear().commit();
                Intent it = new Intent(Page8Activity_caregiver.this,Login3.class);
                startActivity(it);
            }
        });

        //QRcode
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page8Activity_caregiver.this,qrcode_page.class);
                intent.putExtra("email",getmail);
                startActivity(intent);
                finish();
            }
        });

        //修改密碼
        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page8Activity_caregiver.this,edit_password.class);
                intent.putExtra("email",getmail);
                intent.putExtra("password",getpass);
                startActivity(intent);
                finish();

            }
        });
        //返回健(回選單)
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("requester".equalsIgnoreCase(identity)) {
                    Intent it4 = new Intent(Page8Activity_caregiver.this, Page3Activity.class);
                    startActivity(it4);
                }else if("caregiver".equalsIgnoreCase(identity)){
                    Intent it = new Intent(Page8Activity_caregiver.this,Page4Activity.class);
                    startActivity(it);
                }

            }
        });

        mPforfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //實例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(Page8Activity_caregiver.this, itemsOnClick);
                //設計彈出框
                menuWindow.showAtLocation(Page8Activity_caregiver.this.findViewById(R.id.profilepic), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }
    //抓取使用者基本資料
    public void readUser(){
        String URL =Urls.url1+"/LoginRegister/fetch.php?email="+getmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            fullname = response.getString("fullname");
                            phone = response.getString("userphone");
                            identity = response.getString("identity");
                            myphone.setText(phone);
                            mEnteredName.setText(fullname);
                            img = response.getString("image");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Page8Activity_caregiver.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Page8Activity_caregiver.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    //儲存頭貼
    private void savepic(){
        String URL =Urls.url1+"/LoginRegister/upload.php?email="+getmail;
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Page8Activity_caregiver.this,response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Page8Activity_caregiver.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("image",encodeimage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Page8Activity_caregiver.this);
        requestQueue.add(request);
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
                Toast.makeText(Page8Activity_caregiver.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Page8Activity_caregiver.this);
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
                        Toast.makeText(Page8Activity_caregiver.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        // 圖片讀取失敗
                        //Toast.makeText(Page8Activity_caregiver.this, "失敗拉幹", Toast.LENGTH_SHORT).show();
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
    public String pass(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            password=extras.getString("password");
        }
        return password;
    }
    private View.OnClickListener itemsOnClick=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()){
                //拍照
                case R.id.takePhotoBtn:
                    takePhoto();
                    break;
                //相簿選擇相片
                case R.id.SelectPhotoBtn:
                    openAlbum();
                    break;
                case R.id.cancelBtn:
                    break;
                default:
                    break;
            }
        }
    };

    private void openAlbum() {
        Intent in=new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(in,SELECT_PHOTO);
    }

    public void takePhoto() {
        //時間命名圖片的名稱
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);

        //儲存至DCIM資料夾
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path,filename+".jpg");

        //照片更換
        try {
            //如果上次的照片存在,就刪除
            if (outputImage.exists()) {
                outputImage.delete();
            }
            //創一個新文件
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //如果Android版本大於7.0
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(Page8Activity_caregiver.this, "com.example.EasyBus.fileprovider",outputImage);
        }else{
            imageUri = Uri.fromFile(outputImage);
        }

        //申請動態權限
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)mContext,new String[]{Manifest.permission.CAMERA},100);
        }else{
            //啟動相機程序
            startCamera();
        }
    }

    private void startCamera(){
        //指定圖片輸出地址為imageUri
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri); //指定圖片地址
        startActivityForResult(intent,TAKE_PHOTO); //啟動相機
        //拍完照startActivityForResult() 结果返回onActivityResult()函数
    }

    // 使用startActivityForResult()方法開啟Intent的回調
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        switch(requestCode){
            case TAKE_PHOTO:
                try{
                    //將圖片解析成bitmap對象
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //將圖片顯示出來
                    mPforfilepic.setImageBitmap(bitmap);
                    if(data!=null){
                        System.out.println("turi"+imageUri);
                        System.out.println("tbitmap"+bitmap.toString());
                        pic=imageUri.toString();
                        imageStore(bitmap);
                        savepic();
                    }else{

                        Intent it4 = new Intent(Page8Activity_caregiver.this, Page8Activity_caregiver.class);
                        it4.putExtra("email",getmail);
                        readUser();
                        fetchimage();
                        ImageRetriveWithPicasso();
                        startActivity(it4);
                    }

                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                break;

            case SELECT_PHOTO:
                try {
                    //獲取圖片
                    if(data != null){
                        Uri uri=data.getData();
                        if(uri != null){
                            ContentResolver cr = this.getContentResolver();
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            mPforfilepic.setImageBitmap(bitmap);
                            imageStore(bitmap);
                            System.out.println("suri"+uri);
                            System.out.println("sbitmap"+bitmap.toString());
                            pic=uri.toString();
                            savepic();
                        }
                    }else{
                        Intent it4 = new Intent(Page8Activity_caregiver.this, Page8Activity_caregiver.class);
                        it4.putExtra("email",getmail);
                        readUser();
                        fetchimage();
                        ImageRetriveWithPicasso();
                        startActivity(it4);
                    }

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                }
                super.onActivityResult(requestCode, resultCode, data);
                break;

            default:
                break;

        }
    }
//////到底
    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] imageBytes = stream.toByteArray();
        encodeimage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
////