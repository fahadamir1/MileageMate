package com.example.mileagemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    ArrayList<datamodel> arrayList=new ArrayList<>();
    recycleviewadapter adapter;
    FloatingActionButton addbutton;

    boolean flag=false;

    FirebaseFirestore onlinedb;

    dbhelper db;

    RecyclerView container;
    Toolbar tool;
    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initcomponents();
        setSupportActionBar(tool);
        getSupportActionBar();
         db=new dbhelper(this);
        arrayList=db.selectAlldata();
        container.setLayoutManager(new LinearLayoutManager(this));
         adapter=new recycleviewadapter(this,arrayList);
        container.setAdapter(adapter);
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        addbutton.setOnClickListener(view -> {
            Dialog dialog=new Dialog(Dashboard.this);
            dialog.setContentView(R.layout.adddeletedialog);
            EditText taskname=dialog.findViewById(R.id.addtaskname);
            EditText date=dialog.findViewById(R.id.adddate);
            EditText dec=dialog.findViewById(R.id.adddesc);
            TextView title=dialog.findViewById(R.id.title1);
            Button add=dialog.findViewById(R.id.addtaskbutton);
            add.setOnClickListener(view1 -> {
                String task_name="",task_date="",desc="";
                if(!taskname.getText().toString().equals("") | !date.getText().toString().equals("")| !dec.getText().toString().equals("")){
                    task_name=taskname.getText().toString();
                    task_date=date.getText().toString();
                    desc=dec.getText().toString();
                    datamodel user_data=new datamodel();
                    user_data.setTask_name(task_name);
                    user_data.setTask_desc(desc);
                    user_data.setTask_date(task_date);
                    db.insert(user_data);
                    arrayList=db.selectAlldata();
                    adapter=new recycleviewadapter(this,arrayList);
                    container.setAdapter(adapter);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Data Inserted");
                            pDialog.show();
                        }
                    },1000);
                    dialog.dismiss();
                    pDialog.dismissWithAnimation();
                    adapter.notifyItemInserted(arrayList.size()-1);
                    container.scrollToPosition(arrayList.size()-1);
                }
                else{
                    Toast.makeText(this,"Please fill all the values",Toast.LENGTH_SHORT).show();
                }

            });
            dialog.show();
        });
    }

    public boolean isconnected(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            if(networkInfo.isConnectedOrConnecting()){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public void syndatawithfirebase(){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        if(isconnected()){
            while(i< arrayList.size()) {
                datamodel d = new datamodel();
                d=arrayList.get(i);
                onlinedb = FirebaseFirestore.getInstance();
                onlinedb.collection("User_data").add(d).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Syncing.....");
                        pDialog.show();
                    }
                    else{
                        Toast.makeText(this,"Sync Un-Successful!", Toast.LENGTH_SHORT).show();
                    }
                });
                i++;
            }
            Toast.makeText(this,"Sync Successful!",Toast.LENGTH_SHORT).show();
            pDialog.dismissWithAnimation();
        }
        else{
            Toast.makeText(this,"internet is not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==R.id.ClearAll){
            db.deleteall();
            Toast.makeText(this, "Clear Data", Toast.LENGTH_SHORT).show();
        } else if (itemid==R.id.SyncOnline) {
            syndatawithfirebase();
        } else if (itemid==R.id.Refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initcomponents() {
        container=findViewById(R.id.recyclerView1);
        addbutton=findViewById(R.id.floatingbutton);
        tool=findViewById(R.id.toolbarwidget);
    }
    public void refresh(){
        arrayList=db.selectAlldata();
        adapter=new recycleviewadapter(this,arrayList);
        container.setAdapter(adapter);
    }
}