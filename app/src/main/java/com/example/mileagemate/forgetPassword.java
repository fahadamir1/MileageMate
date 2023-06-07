package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPassword extends AppCompatActivity {

    EditText email;
    TextView backtologin,redirecttosignup;
    ImageView button;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initcomponents();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth=FirebaseAuth.getInstance();
        button.setOnClickListener(view -> {
            String gemail=email.getText().toString();
            if(gemail.isEmpty()){
                email.setError("Enter a valid Email");
                Toast.makeText(getApplicationContext(),gemail,Toast.LENGTH_SHORT).show();
            }
            else {
                progressbar("Please Wait...", "Forget Password", true);
                auth.sendPasswordResetEmail(gemail).addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        progressbar("Please Wait...", "Forget Password", false);
                        Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), login.class));
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),""+task.getException(),Toast.LENGTH_SHORT).show();
                        progressbar("Please Wait...", "Forget Passwod", false);
                    }

                });
            }
        });

        backtologin.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), login.class));
        });
        redirecttosignup.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), signup.class));
        });
    }




    private void initcomponents() {
        email=findViewById(R.id.resetemail);
        backtologin=findViewById(R.id.backtologin);
        redirecttosignup=findViewById(R.id.redirecttosignup);
        button=findViewById(R.id.forgetbuton);
    }
    public void progressbar(String message,String title,boolean flag){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        if(flag){
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF0000"));
            pDialog.setTitleText(title);
            pDialog.setMessage(message);
            pDialog.show();
        }
        else {
            pDialog.dismiss();
        }
    }
}