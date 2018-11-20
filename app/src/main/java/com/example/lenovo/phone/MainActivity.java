package com.example.lenovo.phone;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "aaaimage";
    private static String imgPath;
    private static final int TAKR_PHOTO=1;
    private Button button;
    private static final String TEMP_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "temp_card.jpg";
    private static final String COMPRESSED_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator;
    private static final String imageName="micong.jpg";
    private ImageView imageView;
    private Uri imageUri;
    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case  TAKR_PHOTO:
                if(resultCode==RESULT_OK){

//                        File file=uri2File(imageUri);
                      /*  if(imageUri!=null){
                            try {
                                Bitmap  bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                                imageView.setImageBitmap(bitmap);
                                if(!bitmap.isRecycled()){
                                    bitmap.recycle();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }*/
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    File file=new File(COMPRESSED_FILE_PATH+"micong.jpg");
                                    if(file.exists()){
                                        file.delete();
                                    }
                                    file.createNewFile();
                                    final Bitmap bitmap=BitmapUtil.decodeSampledBitmapFromFilePath(TEMP_FILE_PATH,COMPRESSED_FILE_PATH+"micong.jpg",380,480);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    });
                       //             sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
                                    Log.d("aaaaaaa", "onActivityResult: "+TEMP_FILE_PATH);
                                    Log.d("aaaaaaa", "onActivityResult: "+COMPRESSED_FILE_PATH+"micong.jpg");
                                    ExifInterface exifInterface=new ExifInterface(COMPRESSED_FILE_PATH+"micong.jpg");
                                    exifInterface.setAttribute(ExifInterface.TAG_ARTIST,"HIKVISION_V1.0.1_build181025");
                                    exifInterface.saveAttributes();
                                   sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            permissionForM();
        }
        button=findViewById(R.id.takePhoto);
        imageView=findViewById(R.id.picture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(TEMP_FILE_PATH);

                try {
                    if(file.exists()){
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(MainActivity.this,"com.example.lenovo.phone.fileprovider",file);

                }else{
                    imageUri=Uri.fromFile(file);
                }
                /*Intent intents = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri=Uri.fromFile(file);
                intents.setData(uri);
                sendBroadcast(intents);*/
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKR_PHOTO);
               /* ExifInterface exifInterface= null;
                try {
                    exifInterface = new ExifInterface(TEMP_FILE_PATH);
                    exifInterface.setAttribute(ExifInterface.TAG_ARTIST,"HIKVISION_V1.0.1_build181025");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/



            }
        });

    }

    protected void saveFile(final InputStream is){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File f = null;

                if (storagePath.equals("")) {
                    storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
                    imgPath = storagePath + "/" +"aaa" + ".jpg";
                    f = new File(TEMP_FILE_PATH);
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    OutputStream outputStream = new FileOutputStream(f);
                    int bytesWritten = 0;
                    int byteCount = 1024;
                   Bitmap bitmap =BitmapFactory.decodeFile(TEMP_FILE_PATH);
                    byte[] bytes = new byte[byteCount];
                    try {
                        int totaltype=is.read();
                        if(bytesWritten<totaltype &&(bytesWritten+byteCount)<=totaltype){
                            while ((byteCount = is.read(bytes,bytesWritten,byteCount)) != -1) {
                                outputStream.write(bytes, bytesWritten, byteCount);
                                bytesWritten += byteCount;
                            }
                        }else{
                            while ((byteCount = is.read(bytes,bytesWritten,totaltype-bytesWritten)) != -1) {
                                outputStream.write(bytes, bytesWritten, totaltype-bytesWritten);
                                bytesWritten += (totaltype-bytesWritten);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        is.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }
    private void permissionForM() {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() > 0) {
            String[] strings = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, strings, 1);
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length>0 && requestCode==1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults.length > 0) {//安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        switch (grantResult){
                            case PackageManager.PERMISSION_GRANTED://同意授权0
                                break;
                            case PackageManager.PERMISSION_DENIED://拒绝授权-1
                                break;
                        }
                    }

                }else{

                }
            }
        }
    }
}
