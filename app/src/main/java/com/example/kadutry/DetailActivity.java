package com.example.kadutry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
TextView mTitleTv,mDetailTv;
ImageView mImageIv;
Button mSaveBtn,mShareBtn,mWallBtn;
private static final int WRITE_EXTERNAL_STORAGE_CODE=1;
Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Post Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mTitleTv=findViewById(R.id.titleTv);
        mDetailTv=findViewById(R.id.descriptionTv);
        mImageIv=findViewById(R.id.imageView);
        String image=getIntent().getStringExtra("image");
        String title=getIntent().getStringExtra("title");
        String desc=getIntent().getStringExtra("description");

        mTitleTv.setText(title);
        mDetailTv.setText(desc);
        Picasso.get().load(image).into(mImageIv);




        mSaveBtn=(Button)findViewById(R.id.saveBtn);
        mShareBtn=(Button)findViewById(R.id.shareBtn);
        mWallBtn=(Button)findViewById(R.id.WallBtn) ;

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                } else {
                    saveImage();
                }
                }
            else
                {
                    saveImage();
                }
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
        mWallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
}
private void shareImage() {
        try
        {
            bitmap=((BitmapDrawable)mImageIv.getDrawable()).getBitmap();
            String s=mTitleTv.getText().toString()+"\n"+mDetailTv.getText().toString();
            File file=new File(getExternalCacheDir(),"sample.png");
            FileOutputStream fOut=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true,false);
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(intent.EXTRA_TEXT,s);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent,"Share Via"));



        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImage() {
        bitmap=((BitmapDrawable)mImageIv.getDrawable()).getBitmap();
String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) ;
        File path= Environment.getExternalStorageDirectory();
        File dir=new File(path+"/Firebase/");
        dir.mkdir();
        String imageName=timeStamp+".PNG";
        File file=new File(dir,imageName);
        OutputStream out;
        try{
            out =new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,out);
            out.flush();
            out.close();
            Toast.makeText(this, imageName+"Saved into"+dir, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemlongClick(View view,int position);

}

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    saveImage();

                }
                else
                {
                    Toast.makeText(this, "Enable Permission to save image!", Toast.LENGTH_SHORT).show();
                }
            }



        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
