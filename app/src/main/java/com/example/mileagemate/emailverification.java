package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class emailverification extends AppCompatActivity {

    Button verify;
    FirebaseAuth mauth;
    FirebaseUser User;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverification);
        verify=findViewById(R.id.verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mauth=FirebaseAuth.getInstance();
        User = mauth.getCurrentUser();
        verify.setOnClickListener(view -> {
            progressbar("Please Wait...","Verification",true);
            if (User!=null) {
                Intent i = new Intent(getApplicationContext(), introscreen.class);
                startActivity(i);
            }
            else{
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                }
        });
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
            pDialog.dismiss();
        }
    }


    public void backtosignup(View view) {
        Intent i=new Intent(getApplicationContext(), signup.class);
        startActivity(i);
    }


}