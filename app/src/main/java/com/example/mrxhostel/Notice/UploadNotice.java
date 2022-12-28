package com.example.mrxhostel.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrxhostel.Faculty.AlterFaculty;
import com.example.mrxhostel.Faculty.UpdateTeacher;
import com.example.mrxhostel.MainActivity;
import com.example.mrxhostel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {
    private CardView addImage;

    ImageView noticeImageView;
    EditText noticeTitle;
    Button uploadNoticeBtn;

    private  final int REQ=1;
    private Bitmap bitmap;
    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;
    String downloadUrl="",date,time;

    private ProgressDialog pd;  // to Show the process





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        //initialize Firebase data base
        reference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();

        pd=new ProgressDialog(this);


        //Initialize the view
        addImage=findViewById(R.id.addImage);
        noticeImageView=findViewById(R.id.noticeImageView);
        noticeTitle=findViewById(R.id.noticeTitle);
        uploadNoticeBtn=findViewById(R.id.uploadNoticeBtn);

        //method to add Pick Image from the gallery

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        //Upload Noctice Button on click listenning button

        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noticeTitle.getText().toString().isEmpty())
                {
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();

                }
                else if (bitmap==null)
                {
                    uplaodData();
                }
                else{
                    uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        //progress dialog
        pd.setMessage("Uploading..");
        pd.show();

        //compress the Image

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();

        // setting tha path to store the data on Firebase Storage
        final StorageReference filePath;
        filePath=storageReference.child("Notice").child(finalimage+"jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimage);

        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uplaodData();

                                }
                            });
                        }
                    });
                }else
                {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
// Upload the date into Forebase Reatime database
    private void uplaodData() {
        dbRef=reference.child("Notice");
        final String uniquiKey=reference.push().getKey();
        String title=noticeTitle.getText().toString();

    // get the current date and time
        //Current Time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm");
            LocalDateTime now = LocalDateTime.now();
            date=dateFormat.format(now);
            time = timeFormat.format(now);
        }

        //create an object that store all the data as an object format
        NoticeData noticeData=new NoticeData(title,downloadUrl,date,time,uniquiKey);

        //upload data to Firebase Storage and add success and failure Listner
        dbRef.child(uniquiKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Notice Uploaded Successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UploadNotice.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Something went Wrong",Toast.LENGTH_SHORT).show();
            }
        });


    }


    // Method for Open Your Gallery
    private void openGallery() {

        Intent pickImage= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);

    }

    //Method to Handle the Image that is picked from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            noticeImageView.setImageBitmap(bitmap);

        }
    }
}