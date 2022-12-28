package com.example.mrxhostel.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UpdateNotice extends AppCompatActivity {
    String title,image,key;
    ImageView updateNoticeImageView;
    EditText noticeTitle;
    Button updateNoticeBtn,deleteNoticeBtn;
    CardView selectImage;
    private Bitmap bitmap;
    private String downloadUrl;
    private final int REQ=1;
    private ProgressDialog pd;
    private AlertDialog alertDialog;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_notice);


        databaseReference= FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference= FirebaseStorage.getInstance().getReference().child("Notice");
        pd=new ProgressDialog(this);



        updateNoticeImageView= findViewById(R.id.updateNoticeImageView);
        updateNoticeBtn=findViewById(R.id.updateNoticeBtn);
        deleteNoticeBtn=findViewById(R.id.deleteNoticeBtn);
        noticeTitle=findViewById(R.id.updateNoticeTitle);
        selectImage=findViewById(R.id.updateNoticeImage);

        Intent intent=getIntent();
       title= intent.getStringExtra("title");
       image= intent.getStringExtra("image");
       key=intent.getStringExtra("key");

       noticeTitle.setText(title);
       if (image!=null){
           try {
               Picasso.get().load(image).into(updateNoticeImageView);
           } catch (Exception e) {
               e.printStackTrace();
           }

       }

       selectImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               openGallery();
           }
       });

       updateNoticeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               title=noticeTitle.getText().toString();
               if (title.isEmpty()){
                   noticeTitle.setError("Empty Title");
                   noticeTitle.requestFocus();
               }else if(bitmap==null)
               {

                   updateNotice(image);
               }else {

                   updateImageNotice();

               }
           }
       });

       deleteNoticeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               deleteNotice();


           }
       });




    }

    private void deleteNotice() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to Delete notice?");
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle("Delete");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                storageReference.child(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateNotice.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UpdateNotice.this,DeleteNotice.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UpdateNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
             @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        try {
            alertDialog= builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }







    }

    private void updateImageNotice() {
        pd.show();

        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bout);
        byte[] finalImage= bout.toByteArray();
        final StorageReference filePath=storageReference.child(key);
        UploadTask uploadTask=filePath.putBytes(finalImage);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);

                                    updateNotice(downloadUrl);


                                }
                            });
                        }
                    });

                }
                else {
                    Toast.makeText(UpdateNotice.this, "SomeThing went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateNotice.this, "Couldn't upload the notice", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateNotice(String image) {
        pd.show();

        HashMap hp=new HashMap();
        hp.put("title",title);
        hp.put("image",image);

        databaseReference.child(key).updateChildren(hp).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Toast.makeText(UpdateNotice.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                 Intent intent=new Intent(UpdateNotice.this,DeleteNotice.class);

                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            updateNoticeImageView.setImageBitmap(bitmap);

        }
    }
}