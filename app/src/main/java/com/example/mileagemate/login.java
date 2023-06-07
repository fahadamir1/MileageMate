package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.microedition.khronos.egl.EGLDisplay;

public class login extends AppCompatActivity {
   private EditText email,pass;
   ImageView button;

   FirebaseAuth mauth;
   FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        intitcomponents();
        mauth=FirebaseAuth.getInstance();
        user=mauth.getCurrentUser();
        button.setOnClickListener(view -> {
            String uemail=email.getText().toString();
            String upass=pass.getText().toString();
            progressbar("Please wait","Login",true);
            mauth.signInWithEmailAndPassword(uemail,upass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressbar("Please wait","Login",false);
                Toast.makeText(this,"Signup Successfully",Toast.LENGTH_SHORT).show();
                sendusertodashboard();
            }
            else{
                progressbar("Please wait","login",false);
                Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show();
            }
            });
        });
    }

    private void intitcomponents() {
    email=findViewById(R.id.Email);
    pass=findViewById(R.id.password);
    button=findViewById(R.id.loginbuton);
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

    public void sendusertodashboard(){
        Intent I = new Intent(this, Dashboard.class);
        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(I);
    }

    public void forgetbutton(View view) {
        startActivity(new Intent(getApplicationContext(),forgetPassword.class));
    }

    public void rediretosignup(View view) {
        startActivity(new Intent(getApplicationContext(), signup.class));
    }
}