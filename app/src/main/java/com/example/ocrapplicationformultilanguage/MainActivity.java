package com.example.ocrapplicationformultilanguage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    //UI Views
    private MaterialButton inputImageBtn;
    private ShapeableImageView imageIv;
    private EditText recognizedTextEt;
    private AutoCompleteTextView autoCompleteTextView;

    private static final String TAG = "MAIN_TAG";

    //Uri of the image that we will take from Camera/Gallery
    private Uri imageUri = null;

    //to handle the result of Camera/Gallery permission
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //arrays of permission to pick image from Camera, Gallery
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Progress Dialog
    private ProgressDialog progressDialog;

    //TextRecognizer
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputImageBtn = findViewById(R.id.inputImageBtn);
        MaterialButton recognizeTextBtn = findViewById(R.id.recognizeTextBtn);
        imageIv = findViewById(R.id.imageIv);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);


        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        // data to inflate the drop-down items
        String[] langauge_dictionary = new String[]{"Latin", "Devanagari ", "Chinese","Japanese", "Korean "};

        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, langauge_dictionary);
        autoCompleteTextView.setAdapter(adapter);

        //
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                // for latin
                if(autoCompleteTextView.getText().toString().equals(langauge_dictionary[0])){
                    //text recognizer
                    textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                }
                // for Devanagari
                else if(autoCompleteTextView.getText().toString().equals(langauge_dictionary[1])){

                    textRecognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                }
                // for Chinese
                else if(autoCompleteTextView.getText().toString().equals(langauge_dictionary[2])){

                    textRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                }
                // for Japanese
                else if(autoCompleteTextView.getText().toString().equals(langauge_dictionary[3])){

                    textRecognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                }
                // for Korean
                else if(autoCompleteTextView.getText().toString().equals(langauge_dictionary[4])){

                    textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                } else{
                    textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                }
            }
        });

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //text recognizer
//        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //Handle click, show input image dialog
        inputImageBtn.setOnClickListener(view -> showInputImageDialog());

        recognizeTextBtn.setOnClickListener(view -> {
            if (imageUri == null) {
                Toast.makeText(MainActivity.this, "Pick image first...", Toast.LENGTH_SHORT).show();
            } else {
                recognizeTextFromImage();
            }
        });

    }

    private void recognizeTextFromImage() {
        progressDialog.setMessage("Preparing image....");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);

            progressDialog.setMessage("Recognizing text....");

            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        progressDialog.dismiss();
                        String recognizedText = text.getText();

                        recognizedTextEt.setText(recognizedText);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed recognizing text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed preparing image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputImageDialog() {
        Log.d(TAG, "showInputImageDialog: in input image");
        PopupMenu popupMenu = new PopupMenu(this, inputImageBtn);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALLERY");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == 1) {
                if (checkCameraPermission()) {
                    pickImageCamera();
                } else {
                    requestCameraPermission();
                }
            } else if (id == 2) {
                if (checkStoragePermission()) {
                    pickImageGallery();
                } else {
                    requestStoragePermission();
                }
            }
            return false;
        });

    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }


    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if picked
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image picked
                        Intent data = result.getData();

                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        MainActivity.this.getContentResolver(),
                                        selectedImageUri);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageIv.setImageBitmap(
                                    selectedImageBitmap);
                        }

                        //set to imageview
//                        imageIv.setImageURI(imageUri);
                    } else {
                        //cancelled
                        Toast.makeText(MainActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cameraActivityResultLauncher.launch(intent);

    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if taken from camera
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image is taken from camera
                        imageIv.setImageURI(imageUri);
                    } else {
                        //cancelled
                        Toast.makeText(MainActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    //handle permission result

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }
    }
}