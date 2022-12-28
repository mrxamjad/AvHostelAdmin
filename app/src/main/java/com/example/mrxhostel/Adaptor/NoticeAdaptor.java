package com.example.mrxhostel.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrxhostel.Notice.NoticeData;
import com.example.mrxhostel.Notice.UpdateNotice;
import com.example.mrxhostel.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdaptor extends RecyclerView.Adapter <NoticeAdaptor.NoticeViewAdaptor>{
    ArrayList<NoticeData> list;
    Context context;
    String profileName,profileImage,noticeDate,noticeTime,noticeKey,noticeTitle,noticeImage;

    public NoticeAdaptor(ArrayList<NoticeData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NoticeViewAdaptor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notice_layout,parent,false);

        return new NoticeViewAdaptor(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdaptor holder, int position) {

        NoticeData data=list.get(position);

       // for better performance put it directly

        noticeTitle=data.getTitle();
        noticeImage=data.getImage();
        noticeDate=data.getDate();
        noticeTime=data.getTime();
        noticeKey=data.getKey();

        holder.title.setText(noticeTitle);
        holder.date.setText(noticeTime+" "+noticeDate);

        try {
            if (noticeImage!=null) {
                Picasso.get().load(noticeImage).into(holder.noticeImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for better put it directly
                noticeTitle=data.getTitle();
                noticeImage=data.getImage();
                noticeKey=data.getKey();

                Intent intent=new Intent(context, UpdateNotice.class);
                intent.putExtra("title",noticeTitle);

                intent.putExtra("image",noticeImage);
                intent.putExtra("key",noticeKey);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NoticeViewAdaptor extends RecyclerView.ViewHolder {
        ImageView profileImage,noticeImage;
        TextView name,date,title;
        Button update;


        public NoticeViewAdaptor(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.noticeProfileImage);
            noticeImage=itemView.findViewById(R.id.noticeImage);

            name=itemView.findViewById(R.id.noticeProfileName);
            date=itemView.findViewById(R.id.noticeDate);
            title=itemView.findViewById(R.id.noticeTitle);
            update=itemView.findViewById(R.id.noticeUpdateBtn);

        }
    }
}


