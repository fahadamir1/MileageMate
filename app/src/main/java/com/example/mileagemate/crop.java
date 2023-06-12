package com.example.mileagemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class crop extends AppCompatActivity {
    public static final int CAMARA_REQUEST_CODE=200;
    public static final int STORAGE_REQUEST_CODE=400;
    public static final int IMAGE_PICK_GALLERY_CODE=1000;
    public static final int IMAGE_PICK_CAMARA_CODE=1001;

    FirebaseFirestore dg;

    ArrayList<datamodel> arrayList;
    String text;
    dbhelper db;

    recycleviewadapter adapter;

    RecyclerView container;

    ImageView imagepreview;
    EditText generatedtext;
    Calendar calendar;

    Button user;
    String[] camara_Permission;
    String[] storage_Permission;
    String devicename=getDeviceName();
    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        initcomponents();
        showimageimportdiag();
        user=findViewById(R.id.adduser);
        dg = FirebaseFirestore.getInstance();
        db = new dbhelper(this);
        user.setOnClickListener(view -> {
            String gettextfromimage=generatedtext.getText().toString();
            String currentdate= DateFormat.getInstance().format(calendar.getTime());
            datamodel d=new datamodel();
            d.setTask_date(currentdate);
            d.setTask_name("Add Task");
            d.setTask_desc("Meter Reading:"+gettextfromimage+" ");
            db.insert(d);
            dg.collection(devicename).add(d).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Data is Sync", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Sync-Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, "Data inserted", Toast.LENGTH_SHORT).show();
            refresh();
            Intent i=new Intent(this, Dashboard.class);
            startActivity(i);
        });
    }
    public void refresh() {
        arrayList = db.selectAlldata();
        container.setLayoutManager(new LinearLayoutManager(this));
        adapter = new recycleviewadapter(this, arrayList);
        container.setAdapter(adapter);

    }
    private void showimageimportdiag() {
        String [] s={"Camara","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            /// camara option is click
                            if(!checkcamarapermissionallowd()){
                                requestcamarapersmission();
                            }
                            else{
                                //permission is allowed
                                pickcamara();
                            }
                        }
                        if(i==1){
                            /// gallery option is clicked
                            if(!checkstoragepermissionallowd()){
                                requeststoragepersmission();
                            }
                            else{
                                //permission is allowed
                                pickstorage();
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private void pickstorage() {
        Intent storeagepick=new Intent(Intent.ACTION_PICK);
        storeagepick.setType("image/*");
        startActivityForResult(storeagepick,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickcamara() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"newpic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"image to text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent camaraintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraintent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(camaraintent,IMAGE_PICK_CAMARA_CODE);

    }

    private void requeststoragepersmission() {
        ActivityCompat.requestPermissions(this,storage_Permission,STORAGE_REQUEST_CODE);

    }

    private boolean checkstoragepermissionallowd() {
        boolean STORAGECHECK= ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return  STORAGECHECK;
    }

    private void requestcamarapersmission() {
        ActivityCompat.requestPermissions(this,camara_Permission,CAMARA_REQUEST_CODE);
    }

    private boolean checkcamarapermissionallowd() {
        boolean result= ContextCompat.checkSelfPermission(crop.this,
                android.Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean STORAGECHECK= ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return (result && STORAGECHECK);
    }

    private void initcomponents() {
        imagepreview=findViewById(R.id.imagepreview);
        calendar=Calendar.getInstance();
        generatedtext=findViewById(R.id.setgeneratedtext);
        camara_Permission=new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_Permission=new String[]{android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMARA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean camaraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStoreageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (camaraaccepted && writeStoreageaccepted) {
                        pickcamara();
                    } else {
                        Toast.makeText(this, "Permission Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStoreageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStoreageaccepted) {
                        pickstorage();
                    } else {
                        Toast.makeText(this, "Permission Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //got image from camara

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image  now crop it;

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode == IMAGE_PICK_CAMARA_CODE) {
                //got image now crop it;
                Toast.makeText(this, "Crop activity run", Toast.LENGTH_SHORT).show();
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Crop activity ok", Toast.LENGTH_SHORT).show();

                Uri resultUri = result.getUri(); // getimageuri
                imagepreview.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)imagepreview.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                 TextRecognizer recognizer= new TextRecognizer.Builder(getApplicationContext()).build();
                if(!recognizer.isOperational()){
                    Toast.makeText(this, "Error TryAgain", Toast.LENGTH_SHORT).show();
                }
                else{
                    Frame frame= new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items=recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    // get text from the array
                    for(int i=0;i< items.size();i++){
                        TextBlock T= items.valueAt(i);
                        sb.append(T.getValue());
                        sb.append("\n");
                    }
                     text=sb.toString();
                    generatedtext.setText(sb.toString());
                }
            }
            else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error= result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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