package com.example.kadutry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.InvalidMarkException;

public class AddPostActivity extends AppCompatActivity {
EditText mTitleEt,mDescrEt;
ImageView mPostIv;
Button mUploadBtn;
String mStoragePath="uploads/";
String mDatabasePath="Data";
Uri mFilePathUri;
StorageReference mStorageReference;
DatabaseReference mDatabaseReference;
ProgressDialog mProgressDialog;
int IMAGE_REQUEST_CODE=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Add New Post");
        mTitleEt=(EditText)findViewById(R.id.pTitleEt);
        mDescrEt=(EditText)findViewById(R.id.pDescrEt);
        mPostIv=(ImageView)findViewById(R.id.pImageIv);
        mUploadBtn=(Button)findViewById(R.id.pUploadBtn);

        mPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),IMAGE_REQUEST_CODE);

            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
uploadImage();
            }
        });
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference(mDatabasePath);
        mProgressDialog=new ProgressDialog(AddPostActivity.this);

    }

    private void uploadImage() {
        if (mFilePathUri!=null){
            mProgressDialog.setTitle(" Uploading..");
            mProgressDialog.show();
            StorageReference storageReference2nd=mStorageReference.child(mStoragePath+System.currentTimeMillis()+"."+getFileExtension(mFilePathUri));
            storageReference2nd.putFile(mFilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri>urlTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUri=urlTask.getResult();
                    String mPostTitle=mTitleEt.getText().toString().trim();
                    String mPostDescr=mDescrEt.getText().toString().trim();
                    mProgressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                   ImageUploadInfo imageUploadInfo=new ImageUploadInfo(mPostTitle,mPostDescr,downloadUri.toString(),mPostTitle.toLowerCase());
                   String imageUploadId=mDatabaseReference.push().getKey();
                   mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
               }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressDialog.setTitle("Uploading..");

                }
            });
        }
        else
        {
            Toast.makeText(this, "Please Select Image to Upload ", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            mFilePathUri=data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),mFilePathUri);
                mPostIv.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
