package com.example.textreg2;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.textreg2.searchword.DictionaryActivity;
import com.example.textreg2.searchword.SearchingActivity;
import com.example.textreg2.searchword.WordMeaningActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class imageLabel extends AppCompatActivity {
    public static final String EXTRA_TEXT="com.example.textreg2.EXTRA_TEXT";
    //EditText mResultEt;
    //TextView mTranslatedTv;
    ImageView mPreviewIv2;
    //Button mTranslateBtn;
    GridLayout layout;

    private static final int CAMERA_REQUEST_CODE =200;
    private static final int STORAGE_REQUEST_CODE =400;
    private static final int IMAGE_PICK_GALLERY_CODE =1000;
    private static final int IMAGE_PICK_CAMERA_CODE =1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_label);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click + button to insert Image");

        mPreviewIv2 = findViewById(R.id.imageIv2);

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    //action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_reg_mainmenu,menu);
        Log.w("MainActivity","menu_main");
        return true;
    }
    //handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if (id== R.id.addImage){
            showImageImportDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //item to display in dialog
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which ==0){
                    //camera option clicked
                    if(!checkCameraPermission()){
                        //camera permission not allowed, request it
                        requestCameraPermission();
                    }
                    else{
                        //permission allowed, take picture
                        pickCamera();
                    }
                }
                else if(which ==1){
                    //gallery option clicked
                    if(!checkStoragePermission()){
                        //storage permission not allowed, request it
                        requestStoragePermission();
                    }
                    else{
                        //permission allowed, take picture
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show(); //show dialog
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera, it will also be saved to storage to get high quality image
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to label"); //description
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //handle permission result

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccept=grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccept){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccept=grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccept){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //TextRecognizer recognizer=new TextRecognizer.Builder(getApplicationContext()).build();
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                        .start(this);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                        .start(this);
            }
        }
        //get cropped image
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri =result.getUri(); //get image uri
                //set image to image view
                mPreviewIv2.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable=(BitmapDrawable)mPreviewIv2.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();

                InputImage image = InputImage.fromBitmap(bitmap,0); //new

                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);    //new
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                // Task completed successfully

                                layout=(GridLayout)findViewById(R.id.labelContainer);
                                layout.removeAllViews();
                                for (ImageLabel label : labels) {
                                    final String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();
                                    final Button button=new Button(imageLabel.this);
                                    button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    button.setId(label.getIndex());
                                    button.setText(text);
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(imageLabel.this, ""+text, Toast.LENGTH_SHORT).show();
                                            String word=button.getText().toString();

                                            Intent intent = new Intent(imageLabel.this, WordMeaningActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("word",word);

                                            bundle.putInt("type", 1);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                    layout.addView(button);
                                }
                                //mResultEt.setText(allLabels);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                Toast.makeText(imageLabel.this, "ERROR: Fail label "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
//
            }
            else if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //if there is any error show it
                Exception error=result.getError();
                Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();
            }
        }
    }

}