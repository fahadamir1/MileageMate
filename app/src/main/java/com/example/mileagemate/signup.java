package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {
    EditText email,password;
     ImageView signupbutton;


    FirebaseAuth mauth;
    FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        initcomponents();
        mauth=FirebaseAuth.getInstance();
        signupbutton.setOnClickListener(view -> {
            String uemail=email.getText().toString();
            String upass=password.getText().toString();
            if(!(emailchecker(email.getText().toString()))){
                email.setError("Enter valid Email");
            } else if (password.getText().toString().isEmpty()) {
                email.setError("Enter a valid Password");
            } else if ( password.getText().toString().length()<8) {
                password.setError("Password should be 8 character Long");
            }
            else{
                progressbar("Please wait While Registration..","Registration",true);
                mauth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User=mauth.getCurrentUser();
                        progressbar("Please wait While Registration..", "Registration", false);
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        emailverification();
                    } else {
                        progressbar("Please wait While Registration..", "Registration", false);
                        Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void initcomponents() {
        email=findViewById(R.id.Email);
        password=findViewById(R.id.password);
        signupbutton = findViewById(R.id.signupbutton);

    }

    public boolean emailchecker(String email){
        String regex="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern p= Pattern.compile(regex);
        Matcher M=p.matcher(email);
        boolean matcher=M.find();
        if(matcher){
            return  true;
        }
        else{
            return false;
        }
    }


    public void progressbar(String message,String title,boolean flag){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        if(flag){
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        pDialog.setMessage(message);
        pDialog.show();
    }
       else {
            pDialog.dismissWithAnimation();
        }
    }


    public void emailverification(){
        Intent i=new Intent(this,emailverification.class);
        startActivity(i);
    }

    public void sentologin(View view) {
        Intent i=new Intent(signup.this, login.class);
        startActivity(i);
    }
}