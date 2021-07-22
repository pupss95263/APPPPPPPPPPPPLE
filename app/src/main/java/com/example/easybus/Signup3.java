package com.example.easybus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Signup3 extends AppCompatActivity {
    EditText mFullname, mPassword, mEmail,mPassword2,mPhone;
    TextView mRegistertext;
    Button mReg;
    ProgressBar mProgressBar;
    RadioButton mRad1,mRad2;
    RadioGroup mRaG;
    String midentity="";
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mFullname=findViewById(R.id.name);
        mPassword=findViewById(R.id.pass);
        mPassword2=findViewById(R.id.pass2);
        mEmail=findViewById(R.id.email);
        mPhone=findViewById(R.id.cell);
        mRegistertext=findViewById(R.id.Registertext);
        mReg=findViewById(R.id.register);

        mRaG=findViewById(R.id.RadioGroup);
        mRad1=findViewById(R.id.radioButton1);
        mRad2=findViewById(R.id.radioButton2);

        mProgressBar=findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);



        mRegistertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login3.class);
                startActivity(intent);
                finish();
            }
        });
        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fullname, email, password, identity, password2, userphone;
                fullname = String.valueOf(mFullname.getText());
                email = String.valueOf(mEmail.getText());
                password = String.valueOf(mPassword.getText());
                password2 = String.valueOf(mPassword2.getText());
                userphone = String.valueOf(mPhone.getText());

                switch (mRaG.getCheckedRadioButtonId()) {
                    case R.id.radioButton1:
                        midentity = "requester";
                        break;
                    case R.id.radioButton2:
                        midentity = "caregiver";
                        break;
                }
                identity = midentity;
                url = Urls.url1+"/LoginRegister/signup.php";
                Boolean match = validateEmailAddress(email);
                if (match == true) {
                    if (!fullname.equals("") && !email.equals("") && !password.equals("") && !identity.isEmpty() && !password2.equals("") && !userphone.equals("")) {
                        if (password.equals(password2)) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String[] field = new String[5];
                                    field[0] = "fullname";
                                    field[1] = "email";
                                    field[2] = "password";
                                    field[3] = "userphone";
                                    field[4] = "identity";

                                    String[] data = new String[5];
                                    data[0] = fullname;
                                    data[1] = email;
                                    data[2] = password;
                                    data[3] = userphone;
                                    data[4] = identity;


                                    PutData putData = new PutData(url, "POST", field, data);//小高電腦的IP
                                    if (putData.startPut()) {
                                        if (putData.onComplete()) {
                                            mProgressBar.setVisibility(View.GONE);
                                            String result = putData.getResult();
                                            if (result.equals("Sign Up Success")) {
                                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), Login3.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(Signup3.this,"密碼不相符",Toast.LENGTH_SHORT).show();
                        }
                    }//
                }
                else{
                    Toast.makeText(getApplicationContext(),"All filed required",Toast.LENGTH_SHORT).show();
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