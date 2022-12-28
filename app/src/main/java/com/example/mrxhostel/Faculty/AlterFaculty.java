package com.example.mrxhostel.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mrxhostel.Adaptor.TeacherAdaptor;
import com.example.mrxhostel.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AlterFaculty extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;

    private List<TeacherData> list1,list2,list3,list4;

    private RecyclerView cseDept,mechDept, bmeDept,pharmaDept;
    private LinearLayout cseNoData,mechNoData,bmeNoData,pharmaNoData;
    private ProgressBar progressBar;

    private DatabaseReference reference,dbRef;

    private TeacherAdaptor teacherAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_faculty);


        floatingActionButton=findViewById(R.id.floatingBtn);
        cseDept=findViewById(R.id.cseDept);
        mechDept=findViewById(R.id.mechDept);
        bmeDept=findViewById(R.id.bmeDept);
        pharmaDept=findViewById(R.id.pharmaDept);
        progressBar=findViewById(R.id.alterFacultyProgressBar);

        cseNoData=findViewById(R.id.cseNoData);
        mechNoData=findViewById(R.id.mechNoData);
        bmeNoData=findViewById(R.id.bmeNoData);
        pharmaNoData=findViewById(R.id.pharmaNoData);


        reference=FirebaseDatabase.getInstance().getReference().child("teacher");
        progressBar.setVisibility(View.VISIBLE);







        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iAddFaculty = new Intent(getApplicationContext(), AddFaculty.class);
                startActivity(iAddFaculty);

            }
        });

        cseDept();
        mechDept();
        bmeDept();
        pharmaDept();

    }

    private void cseDept() {
        dbRef=reference.child("Computer Science and Engineering");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1= new ArrayList<>();
                if(!snapshot.exists())
                {
                    cseNoData.setVisibility(View.VISIBLE);
                    cseDept.setVisibility(View.GONE);
                }else
                {
                    cseNoData.setVisibility(View.GONE);
                    cseDept.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list1.add(data);
                    }
                    cseDept.setHasFixedSize(true);
                    cseDept.setLayoutManager(new LinearLayoutManager(AlterFaculty.this));
                    teacherAdaptor = new TeacherAdaptor(list1,AlterFaculty.this,"Computer Science and Engineering");
                    cseDept.setAdapter(teacherAdaptor);
                    progressBar.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlterFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void mechDept() {
        dbRef=reference.child("Mechanical Engineering");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2= new ArrayList<>();
                if(!snapshot.exists())
                {
                    mechNoData.setVisibility(View.VISIBLE);
                    mechDept.setVisibility(View.GONE);
                }else
                {
                    mechNoData.setVisibility(View.GONE);
                    mechDept.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    mechDept.setHasFixedSize(true);
                    mechDept.setLayoutManager(new LinearLayoutManager(AlterFaculty.this));
                    teacherAdaptor = new TeacherAdaptor(list2,AlterFaculty.this,"Mechanical Engineering");
                    mechDept.setAdapter(teacherAdaptor);
                    progressBar.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlterFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void bmeDept() {
        dbRef=reference.child("Biomedical Engineering");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list3= new ArrayList<>();
                if(!snapshot.exists())
                {
                    bmeNoData.setVisibility(View.VISIBLE);
                    bmeDept.setVisibility(View.GONE);
                }else
                {
                    bmeNoData.setVisibility(View.GONE);
                    bmeDept.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    bmeDept.setHasFixedSize(true);
                    bmeDept.setLayoutManager(new LinearLayoutManager(AlterFaculty.this));
                    teacherAdaptor = new TeacherAdaptor(list3,AlterFaculty.this,"Biomedical Engineering");
                    bmeDept.setAdapter(teacherAdaptor);
                    progressBar.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlterFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void pharmaDept() {
        dbRef=reference.child("Pharmaceutical Engineering");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list4= new ArrayList<>();
                if(!snapshot.exists())
                {
                    pharmaNoData.setVisibility(View.VISIBLE);
                    pharmaDept.setVisibility(View.GONE);
                }else
                {
                    pharmaNoData.setVisibility(View.GONE);
                    pharmaDept.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list4.add(data);
                    }
                    pharmaDept.setHasFixedSize(true);
                    pharmaDept.setLayoutManager(new LinearLayoutManager(AlterFaculty.this));
                    teacherAdaptor = new TeacherAdaptor(list4,AlterFaculty.this,"Biomedical Engineering");
                    pharmaDept.setAdapter(teacherAdaptor);
                    progressBar.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlterFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}