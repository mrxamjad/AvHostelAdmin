package com.example.mrxhostel.Faculty;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddFaculty extends AppCompatActivity {
    private static final int REQ = 1;
    private ImageView addTeacherImage;
    EditText addTeacherName,addTeacherEmail,addTeacherPost;
    Spinner spinnerTeacherCategory;
    Button addFacultyBtn;
    Bitmap bitmap;

    private StorageReference storageReference;
    private DatabaseReference databaseReference,dbRef;
    private ProgressDialog pd;

    String teacherCategory,downloadUrl,name,email,post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference= FirebaseStorage.getInstance().getReference().child("teacher");
        pd=new ProgressDialog(this);



        addTeacherImage=findViewById(R.id.addTeacherImage);
        addTeacherName=findViewById(R.id.addTeacherName);
        addTeacherEmail=findViewById(R.id.addTeacherEmail);
        addTeacherPost=findViewById(R.id.addTeacherPost);
        spinnerTeacherCategory=findViewById(R.id.spinnerTeacherCategory);
        addFacultyBtn=findViewById(R.id.addFacultyBtn);

        String[] cat= new String[]{"Select Category", "Computer Science and Engineering","Mechanical Engineering", "Biomedical Engineering","Pharmaceutical Engineering"};
        spinnerTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,cat));

        spinnerTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                teacherCategory=spinnerTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });


        addTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });




    }

    private void checkValidation() {
        name=addTeacherName.getText().toString();
        email=addTeacherEmail.getText().toString();
        post=addTeacherPost.getText().toString();

        if(name.isEmpty()){
            addTeacherName.setError(" name is Empty");
            addTeacherName.requestFocus();
        }
        else if(email.isEmpty()){
            addTeacherEmail.setError(" email is Empty");
            addTeacherEmail.requestFocus();
        }
        else if(post.isEmpty()){
            addTeacherPost.setError(" Post is Empty");
            addTeacherPost.requestFocus();
        }
        else if(teacherCategory.equals("Select Category"))
        {
            Toast.makeText(this, "Select A Category", Toast.LENGTH_SHORT).show();
        }
        else if(bitmap==null)
        {

            uploadData();
        }
        else{
            UploadImageData();
        }

    }

    private void uploadData() {
        pd.setMessage("Uploading..");
        pd.show();

        dbRef=databaseReference.child(teacherCategory);
        final String uniquiKey=dbRef.push().getKey();




        //create an object that store all the data as an object format
        TeacherData data=new TeacherData(name,email,post,downloadUrl, uniquiKey);

        //upload data to Firebase Storage and add success and failure Listner
        dbRef.child(uniquiKey).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Added Successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddFaculty.this,AlterFaculty.class);
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

    private void UploadImageData() {
        //progress dialog
        pd.setMessage("Uploading..");
        pd.show();


        //compress the Image

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();

        // setting tha path to store the data on Firebase Storage
        final StorageReference filePath;
        filePath=storageReference.child(finalimage+".jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimage);

        uploadTask.addOnCompleteListener(AddFaculty.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    uploadData();

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
            addTeacherImage.setImageBitmap(bitmap);

        }
    }
}