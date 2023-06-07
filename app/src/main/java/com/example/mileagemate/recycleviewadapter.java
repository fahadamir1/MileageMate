package com.example.mileagemate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycleviewadapter extends RecyclerView.Adapter<recycleviewadapter.Viewholder> {
    @NonNull
    Context context;

    ArrayList<datamodel> array;

    int index;

    datamodel dm;

    boolean flag;



    recycleviewadapter(Context context,ArrayList<datamodel> arrayList){
    this.context=context;
    this.array=arrayList;

    }

    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.cardviewlayout,parent,false);
        Viewholder V=new Viewholder(view);
        return V;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
    holder.image.setImageResource(R.drawable.baseline_access_time_24);
    holder.name.setText(array.get(position).getTask_name());
    holder.date1.setText(array.get(position).getTask_date());
    holder.desc1.setText(array.get(position).getTask_desc());
    animation(holder.itemView,position);
    holder.layout.setOnClickListener(view -> {
        index= holder.getLayoutPosition();
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.adddeletedialog);
        EditText taskname=dialog.findViewById(R.id.addtaskname);
        EditText date=dialog.findViewById(R.id.adddate);
        EditText dec=dialog.findViewById(R.id.adddesc);
        TextView title=dialog.findViewById(R.id.title1);
        Button add=dialog.findViewById(R.id.addtaskbutton);
        title.setText("Update Task");
        add.setText("Update");
        flag=true;
        taskname.setText(array.get(position).getTask_name());
        date.setText(array.get(position).getTask_date());
        dec.setText(array.get(position).getTask_desc());
        add.setOnClickListener(view1 -> {
            String task_name="",task_date="",desc="";
            if(!taskname.getText().toString().equals("") | !date.getText().toString().equals("")| !dec.getText().toString().equals("")) {
                task_name = taskname.getText().toString();
                task_date = date.getText().toString();
                desc = dec.getText().toString();
                datamodel user_data = new datamodel();
                user_data.setTask_name(task_name);
                user_data.setTask_desc(desc);
                user_data.setTask_date(task_date);
                dm=user_data;
                dbhelper db=new dbhelper(context);
                int index = getIndex();
                datamodel d = getupdateduser();
                if (db.updatedata(d, index)) {
                    Toast.makeText(context, "Data is updated", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(context,"Please fill all the values",Toast.LENGTH_SHORT).show();
            }
            array.set(position,new datamodel(task_name,task_date,desc));
            notifyItemChanged(position);
            dialog.dismiss();
        });
        dialog.show();

    });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dbhelper db=new dbhelper(context);
                index=holder.getLayoutPosition();
                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setTitle("Delete Contact")
                        .setMessage("Are you sure you want to Delete this?")
                        .setIcon(R.drawable.baseline_delete_24)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               array.remove(position);
                               db.deleterow(getIndex());
                                notifyItemRemoved(position);
                                Toast.makeText(context,"Item Deleted",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
                return true;
            }
        });
    }

    public datamodel getupdateduser(){
        return dm;
    }


    public int getIndex(){
        return index+1;
    }

    public int getdatabaseid(int position){
        dbhelper db=new dbhelper(context);
        ArrayList<datamodel> data=new ArrayList<>();
        data=db.selectAlldata();
        datamodel d=new datamodel();
        d=data.get(position);
        int id;
        id=d.getId();
        return id;
    }
    @Override
    public int getItemCount() {
        return array.size();
    }

    public boolean getflag() {
        return flag;
    }

    public void animation(View viewtoanimate,int position){
        Animation slide= AnimationUtils.loadAnimation(context,R.anim.alpha_anim);
        viewtoanimate.setAnimation(slide);
    }

    public class Viewholder extends  RecyclerView.ViewHolder{

        TextView name,desc1,date1;
        ImageView image;
        RelativeLayout layout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.task_name);
            desc1=itemView.findViewById(R.id.desc);
            date1=itemView.findViewById(R.id.date);
            image=itemView.findViewById(R.id.clock);
            layout=itemView.findViewById(R.id.cardlayoutclick);
            flag=false;
        }
    }
}
