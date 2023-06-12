package com.example.mileagemate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    ArrayList<datamodel> arrayList = new ArrayList<>();
    recycleviewadapter adapter;
    FloatingActionButton addbutton;

    boolean flag = false;

    FirebaseFirestore onlinedb;

    dbhelper db;
    String devicename;
    RecyclerView container;
    Toolbar tool;
    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initcomponents();
        setSupportActionBar(tool);
        getSupportActionBar();
        devicename=getDeviceName();
        getSupportActionBar().setHomeButtonEnabled(true);
        db = new dbhelper(this);
        arrayList = db.selectAlldata();
        container.setLayoutManager(new LinearLayoutManager(this));
        adapter = new recycleviewadapter(this, arrayList);
        container.setAdapter(adapter);


        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        addbutton.setOnClickListener(view -> {
            Dialog dialog = new Dialog(Dashboard.this);
            dialog.setContentView(R.layout.adddeletedialog);
            EditText taskname = dialog.findViewById(R.id.addtaskname);
            EditText date = dialog.findViewById(R.id.adddate);
            EditText dec = dialog.findViewById(R.id.adddesc);
            TextView title = dialog.findViewById(R.id.title1);
            Button add = dialog.findViewById(R.id.addtaskbutton);
            add.setOnClickListener(view1 -> {
                String task_name = "", task_date = "", desc = "";
                if (!taskname.getText().toString().equals("") | !date.getText().toString().equals("") | !dec.getText().toString().equals("")) {
                    task_name = taskname.getText().toString();
                    task_date = date.getText().toString();
                    desc = dec.getText().toString();
                    datamodel user_data = new datamodel();
                    user_data.setTask_name(task_name);
                    user_data.setTask_desc(desc);
                    user_data.setTask_date(task_date);
                    db.insert(user_data);
                    devicename=getDeviceName();
                    if (isconnected()) {
                        onlinedb = FirebaseFirestore.getInstance();
                        onlinedb.collection(devicename).add(user_data).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Data is Sync", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Internet is not Connected", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Sync Data When Internet is Connected", Toast.LENGTH_SHORT).show();
                    }
                    arrayList = db.selectAlldata();
                    adapter = new recycleviewadapter(this, arrayList);
                    container.setAdapter(adapter);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Data Inserted");
                            pDialog.show();
                        }
                    }, 1000);
                    dialog.dismiss();
                    pDialog.dismiss();
                    adapter.notifyItemInserted(arrayList.size() - 1);
                    container.scrollToPosition(arrayList.size() - 1);
                } else {
                    Toast.makeText(this, "Please fill all the values", Toast.LENGTH_SHORT).show();
                }

            });
            dialog.show();
        });
    }

    public boolean isconnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void syndatawithfirebase() {
        if (isconnected()) {
            while (i < arrayList.size()) {
                datamodel d = new datamodel();
                d = arrayList.get(i);
                onlinedb = FirebaseFirestore.getInstance();
                devicename=getDeviceName();
                onlinedb.collection(devicename).add(d).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Run if successful;
                    }
                    else {
                        Toast.makeText(this, "Sync Un-Successful!", Toast.LENGTH_SHORT).show();
                    }
                });
                i++;
            }
            Toast.makeText(this, "Sync Successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "internet is not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid = item.getItemId();
        if (itemid == R.id.ClearAll) {
            db.deleteall();
            refresh();
            Toast.makeText(this, "Clear Data", Toast.LENGTH_SHORT).show();
        } else if (itemid == R.id.SyncOnline) {
            syndatawithfirebase();
        } else if (itemid == R.id.Refresh) {
            refresh();
        } else if (itemid == R.id.camra) {
            Intent i = new Intent(this, crop.class);
            startActivity(i);
        }
        else if (itemid == R.id.lost) {
          getdatafromfirebase();
        }
        return super.onOptionsItemSelected(item);
    }
    public void getdatafromfirebase() {
        String name = getDeviceName();
        onlinedb=FirebaseFirestore.getInstance();
        onlinedb.collection(name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve data from each document
                        datamodel data = documentSnapshot.toObject(datamodel.class);
                        db.insert(data);
                    }
                    Toast.makeText(this, "Dashboard Updated", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failure! Try Again!", Toast.LENGTH_SHORT).show();
                });
    }





    private void initcomponents() {
        container = findViewById(R.id.recyclerView1);
        addbutton = findViewById(R.id.floatingbutton);
        tool = findViewById(R.id.toolbarwidget);
    }

    public void refresh() {
        arrayList = db.selectAlldata();
        adapter = new recycleviewadapter(this, arrayList);
        container.setAdapter(adapter);
    }
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }
}

