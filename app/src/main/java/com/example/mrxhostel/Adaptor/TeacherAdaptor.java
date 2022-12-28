package com.example.mrxhostel.Adaptor;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrxhostel.Faculty.TeacherData;
import com.example.mrxhostel.Faculty.UpdateTeacher;
import com.example.mrxhostel.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdaptor extends RecyclerView.Adapter<TeacherAdaptor.TeacherViewAdaptor> {
    private List<TeacherData> list=new ArrayList<>();
    private Context context;
    private String name,email,image,category,post,key;

    public TeacherAdaptor(List<TeacherData> list, Context context, String category) {
        this.list = list;
        this.context = context;
        this.category=category;
    }

    @NonNull
    @Override
    public TeacherViewAdaptor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.faculty_item_layout,parent,false);
        return new TeacherViewAdaptor(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewAdaptor holder, int position) {


        TeacherData item=list.get(position);

        name=item.getName();
        email=item.getEmail();
        post=item.getPost();
        image=item.getImage();
        key=item.getKey();

        holder.name.setText(name);
        holder.email.setText(email);
        holder.post.setText(post);


        //set image by Picasso library
        try {
            if(image!=null)
                Picasso.get().load(image).into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=item.getName();
                email=item.getEmail();
                post=item.getPost();
                image=item.getImage();
                key=item.getKey();

                Intent update= new Intent(context, UpdateTeacher.class);
                update.putExtra("name",name);
                update.putExtra("email",email);
                update.putExtra("post",post);
                update.putExtra("image",image);
                update.putExtra("key",key);
                update.putExtra("category",category);
                context.startActivity(update);
            }
        });




    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    public class TeacherViewAdaptor extends RecyclerView.ViewHolder {

        TextView name,email,post;
        ImageView imageView;
        Button updateInfo;

        public TeacherViewAdaptor(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.teacherName);
            email=itemView.findViewById(R.id.teacherEmail);
            post=itemView.findViewById(R.id.teacherPost);
            imageView=itemView.findViewById(R.id.teacherImage);
            updateInfo=itemView.findViewById(R.id.updateInfoBtn);
        }
    }
}
