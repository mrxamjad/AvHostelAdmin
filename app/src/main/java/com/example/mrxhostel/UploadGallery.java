package com.example.mrxhostel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrxhostel.Faculty.AlterFaculty;
import com.example.mrxhostel.Faculty.UpdateTeacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Array;

public class UploadGallery extends AppCompatActivity {
    private static final int REQ = 1;
    Spinner imageCategory;
    CardView selectImage;
    Button UploadImage;
    ImageView galleryImageView;
    String category;
    private Bitmap bitmap;
    private ProgressDialog pd;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private String downloadUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gallery);
        selectImage=findViewById(R.id.addGalleryImage);
        imageCategory=findViewById(R.id.spinner);
        UploadImage=findViewById(R.id.uploadImageBtn);
        galleryImageView=findViewById(R.id.galleryImageView);
        pd=new ProgressDialog(this);

        reference= FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference= FirebaseStorage.getInstance().getReference().child("gallery");

        String[] cat= new String[]{"Select Category", "Event","Student Get To gather", "Special Function"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,cat));

        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category=imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //upload image
        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap==null)
                {
                    Toast.makeText(getApplicationContext(),"Plese Select the Image",Toast.LENGTH_SHORT).show();
                }else if(category.equals("Select Category"))
                {
                    Toast.makeText(getApplicationContext(),"Plese Select any Category",Toast.LENGTH_SHORT).show();


                }
                else {
                    pd.setMessage("Uploading");
                    pd.show();

                    uploadImage();

                }
            }
        });



    }

    private void uploadImage() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();

        // setting the path to store the data on Firebase Storage
        final StorageReference filePath;
        filePath=storageReference.child(category).child(finalimage+".jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimage);

        uploadTask.addOnCompleteListener(UploadGallery.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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

    private void uplaodData() {
        reference.child(category);
        final String uniqueKey=reference.push().getKey();

        reference.child(category).child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Uploaded successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UploadGallery.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_SHORT).show();

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
            galleryImageView.setImageBitmap(bitmap);

        }
    }
}