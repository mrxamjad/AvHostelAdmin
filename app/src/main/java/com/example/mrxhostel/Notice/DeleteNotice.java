package com.example.mrxhostel.Notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mrxhostel.Adaptor.NoticeAdaptor;
import com.example.mrxhostel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNotice extends AppCompatActivity {

    private RecyclerView recyclerView;
    ArrayList<NoticeData> list;
    DatabaseReference databaseReference;
    NoticeAdaptor noticeAdaptor;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        databaseReference= FirebaseDatabase.getInstance().getReference("Notice");

        recyclerView = findViewById(R.id.DeleteNoticeRecycler);
        progressBar=findViewById(R.id.deleteNoticeProgressBar);


        progressBar.setVisibility(View.VISIBLE);
        LoadData();


    }

    private void LoadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list=new ArrayList<>();
                if(!snapshot.exists())
                {

                }else {
                    for (DataSnapshot snap :snapshot.getChildren())
                          {
                              NoticeData data=snap.getValue(NoticeData.class);
                              list.add(data);

                              recyclerView.setHasFixedSize(true);
                              recyclerView.setLayoutManager(new LinearLayoutManager(DeleteNotice.this));
                              noticeAdaptor=new NoticeAdaptor(list,DeleteNotice.this);
                              recyclerView.setAdapter(noticeAdaptor);
                              progressBar.setVisibility(View.GONE);



                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DeleteNotice.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }
}