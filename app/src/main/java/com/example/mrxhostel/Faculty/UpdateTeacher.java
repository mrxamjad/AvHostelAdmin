package com.example.mrxhostel.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateTeacher extends AppCompatActivity {

    private ImageView updateTeacherImage;
     private EditText updateTeacherName,updateTeacherEmail,updateTeacherPost;
     private Spinner spinnerTeacherCategory;
     private Button updateFacultyBtn,deleteFacultyBtn;
    private String name,email,image,category,post,key,teacherCategory,downloadUrl;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> adapter;
    private int REQ=1;
    private AlertDialog alertDialog;
    private Bitmap bitmap;
    private ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        storageReference= FirebaseStorage.getInstance().getReference().child("teacher");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("teacher");
        pd=new ProgressDialog(this);


        updateTeacherImage=findViewById(R.id.updateTeacherImage);
        updateTeacherName=findViewById(R.id.updateTeacherName);
        updateTeacherEmail=findViewById(R.id.updateTeacherEmail);
        updateTeacherPost=findViewById(R.id.updateTeacherPost);
//        spinnerTeacherCategory=findViewById(R.id.spinnerTeacherCategory);
        updateFacultyBtn=findViewById(R.id.updateFacultyBtn);
        deleteFacultyBtn=findViewById(R.id.deleteFacultyBtn);

//getting data from intent
        Intent update= getIntent();
        name=update.getStringExtra("name");
        email=update.getStringExtra("email");
        image=update.getStringExtra("image");
        category=update.getStringExtra("category");
        post=update.getStringExtra("post");
        key=update.getStringExtra("key");

      //  Toast.makeText(this, "name: "+name+"/n Email: "+email+"/n image: "+image+"/nPost: "+post+"/n key: "+key+"/n category: "+category, Toast.LENGTH_SHORT).show();


//        //setting adapter
//        String[] cat= new String[]{"Select Category", "Computer Science and Engineering","Mechanical Engineering", "Biomedical Engineering","Pharmaceutical Engineering"};
//        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,cat);
//        spinnerTeacherCategory.setAdapter(adapter);
//
//
//
//        spinnerTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                teacherCategory=spinnerTeacherCategory.getSelectedItem().toString();
//
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                int spinnerPosition = adapter.getPosition(category);
//                //set the default according to value
//                spinnerTeacherCategory.setSelection(spinnerPosition);
//
//            }
//
//
//        });


//setting the data to their corresponding field
        updateTeacherEmail.setText(email);
        updateTeacherName.setText(name);
        updateTeacherPost.setText(post);

        try{
            if(image!=null) {
                Picasso.get().load(image).into(updateTeacherImage);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

         //set adapter default position





        updateFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=updateTeacherName.getText().toString();
                email=updateTeacherEmail.getText().toString();
                post=updateTeacherPost.getText().toString();
                checkValidation();
            }
        });

        deleteFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               deleteData();
                alertDialog.show();
            }
        });

        updateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });




    }

    private void deleteData() {
        pd.setTitle("Please wait");
        pd.setMessage("Deleting...");
        pd.show();

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setIcon(R.drawable.ic_delete_red);
        builder.setMessage("Are you sure want to delete the Faculty");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child(category).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateTeacher.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UpdateTeacher.this,AlterFaculty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        pd.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTeacher.this, "Failed to delete the data", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                pd.dismiss();
            }
        });

        try {
            alertDialog= builder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void checkValidation() {
        if (updateTeacherName.getText().toString().isEmpty()) {
            updateTeacherName.setError("Empty");
            updateTeacherName.requestFocus();
        } else if (updateTeacherEmail.getText().toString().isEmpty()) {
            updateTeacherEmail.setError("Empty");
            updateTeacherEmail.requestFocus();

        }else if (updateTeacherPost.getText().toString().isEmpty()) {
            updateTeacherPost.setError("Empty");
//        }else if(teacherCategory.equals("Select Category"))
//        {
//            Toast.makeText(this, "Select A Category", Toast.LENGTH_SHORT).show();
        }else if(bitmap==null)
        {

            updateData(image);
        }
        else{
            updateImageData();
        }
    }

    private void updateImageData() {
       // progress dialog
        pd.setTitle("Please Wait");
        pd.setMessage("Uploading..");
        pd.show();


        //compress the Image

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        if(bitmap!=null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
            byte[] finalimage = baos.toByteArray();


        // setting tha path to store the data on Firebase Storage
        final StorageReference filePath;
        filePath=storageReference.child(name+"-"+key+".jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimage);

        uploadTask.addOnCompleteListener(UpdateTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadUrl);

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

    private void updateData(String image) {
        pd.setTitle("Please Wait");
        pd.setMessage("Uploading..");
        pd.show();
        HashMap hp=new HashMap();
        hp.put("name",name);
        hp.put("email",email);
        hp.put("post",post);
        hp.put("image",image);


        databaseReference.child(category).child(key).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Intent intent=new Intent(UpdateTeacher.this,AlterFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                pd.dismiss();

                Toast.makeText(UpdateTeacher.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacher.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                pd.dismiss();
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
            updateTeacherImage.setImageBitmap(bitmap);

        }
    }
}