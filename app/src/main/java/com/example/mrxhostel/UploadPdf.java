package com.example.mrxhostel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrxhostel.Faculty.AlterFaculty;
import com.example.mrxhostel.Faculty.UpdateTeacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {

    EditText pdfTitle;
    Button uploadPdfBtn;
    CardView selectPdf;
    TextView pdfNotice;

    private  final int REQ=1;
    private Uri pdfUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    String pdfName="",title;

    private ProgressDialog pd;  // to Show the process



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

//initialize Firebase data base
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();

        pd=new ProgressDialog(this);


        //Initialize the view


        pdfTitle=findViewById(R.id.PdfTitle);
        uploadPdfBtn=findViewById(R.id.uploadPdfBtn);
        selectPdf=findViewById(R.id.addPdf);
        pdfNotice=findViewById(R.id.pdfNotice);

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title=pdfTitle.getText().toString();


                if (title.isEmpty())
                {
                    pdfTitle.setError("Title is Empty");
                    pdfTitle.requestFocus();
                }else if(pdfUri==null){
                    Toast.makeText(UploadPdf.this, "Please select a pdf", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadPdf();
                }

            }
        });



    }

    private void uploadPdf() {
        pd.setTitle("Please wait...");
        pd.setMessage("Uploading...");
        pd.show();

        StorageReference reference=storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri=uriTask.getResult();
                uploadData(String.valueOf(uri));


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Something went wrong while uploading", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String downloadUrl) {
        String uniqueKey=databaseReference.child("pdf").push().getKey();
        HashMap data=new HashMap();
        data.put("pdfTitle", title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UploadPdf.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Failed to Upload Pdf", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method for Open Your Gallery
    private void openGallery() {

        Intent intent=new Intent();
        intent.setType("application/pdf"); //put * for all kind of file and put pdf/docs/ppt
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select PDF File"),REQ);


    }

    //Method to Handle the Image that is picked from the gallery
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
           pdfUri=data.getData();

            if (pdfUri.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getApplicationContext().getContentResolver().query(pdfUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }

           }else if(pdfUri.toString().startsWith("file//"))
           {
               pdfName=new File(pdfUri.toString()).getName();
           }



            Toast.makeText(getApplicationContext(),pdfName,Toast.LENGTH_SHORT).show();
            pdfNotice.setText(pdfName);


        }
    }
}