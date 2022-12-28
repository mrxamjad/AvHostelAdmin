package com.example.mrxhostel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mrxhostel.Faculty.AlterFaculty;
import com.example.mrxhostel.Notice.DeleteNotice;
import com.example.mrxhostel.Notice.UploadNotice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView uploadNotice,uploadImage,uploadPdf,addFaculty,deleteNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice=findViewById(R.id.addNotice);
        uploadImage=findViewById(R.id.addGalleryImage);
        uploadPdf=findViewById(R.id.addEbook);
        addFaculty=findViewById(R.id.addFaculty);
        deleteNotice=findViewById(R.id.deleteNotice);

        uploadNotice.setOnClickListener(this); //it will go for on click method that is override below
        uploadImage.setOnClickListener(this);
        uploadPdf.setOnClickListener(this);
        addFaculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        Intent intent;

        switch (id){
            case R.id.addNotice:
            {
                intent=new Intent(MainActivity.this, UploadNotice.class);
                startActivity(intent);
                break;
            }
            case R.id.addGalleryImage:
            {
                intent=new Intent(MainActivity.this,UploadGallery.class);
                startActivity(intent);
                break;
            }
            case R.id.addEbook:
            {
                intent=new Intent(MainActivity.this,UploadPdf.class);
                startActivity(intent);
                break;
            }case R.id.addFaculty:
            {
                intent=new Intent(MainActivity.this, AlterFaculty.class);
                startActivity(intent);
                break;
            }case R.id.deleteNotice:
            {
                intent=new Intent(MainActivity.this, DeleteNotice.class);
                startActivity(intent);
                break;
            }

        }
    }
}