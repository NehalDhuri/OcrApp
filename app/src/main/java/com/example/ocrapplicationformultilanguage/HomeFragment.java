package com.example.ocrapplicationformultilanguage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.Locale;
import java.util.Objects;


public class HomeFragment extends Fragment {
    //UI Views
    private MaterialButton inputImageBtn;
    private MaterialButton copyButton;
    private MaterialButton ttsButton;
    private MaterialButton stopTtsButton;
    private TextToSpeech tts;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        super.onCreate(savedInstanceState);
        inputImageBtn = view.findViewById(R.id.inputImageBtn);
        MaterialButton recognizeTextBtn = view.findViewById(R.id.recognizeTextBtn);
        imageIv = view.findViewById(R.id.imageIv);
        recognizedTextEt = view.findViewById(R.id.recognizedTextEt);


        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        // data to inflate the drop-down items
        String[] langauge_dictionary = new String[]{"Latin", "Devanagari ", "Chinese","Japanese", "Korean "};

        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, langauge_dictionary);
        autoCompleteTextView.setAdapter(adapter);

        //
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "" + autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
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

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //Handle click, show input image dialog

        inputImageBtn.setOnClickListener(view1 -> showInputImageDialog());

        recognizeTextBtn.setOnClickListener(view1 -> {
            if (imageUri == null) {
                Toast.makeText(getContext(), "Pick image first...", Toast.LENGTH_SHORT).show();
            } else {
                recognizeTextFromImage();
            }
        });

        // Copy Button
        copyButton = view.findViewById(R.id.copy_btn);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", recognizedTextEt.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        //share button
        Button shareButton = view.findViewById(R.id.share_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the message");
                shareIntent.putExtra(Intent.EXTRA_TEXT, recognizedTextEt.getText());
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        //listen
        ttsButton = view.findViewById(R.id.listen_btn);
        stopTtsButton = view.findViewById(R.id.stop_btn);

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(recognizedTextEt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                ttsButton.setVisibility(View.GONE);
                stopTtsButton.setVisibility(View.VISIBLE);

            }
        });


        stopTtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
                stopTtsButton.setVisibility(View.GONE);
                ttsButton.setVisibility(View.VISIBLE);

            }
        });

        return view;
    }


    private void recognizeTextFromImage() {
        progressDialog.setMessage("Preparing image....");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(requireContext(), imageUri);

            progressDialog.setMessage("Recognizing text....");

            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        progressDialog.dismiss();
                        String recognizedText = text.getText();

                        recognizedTextEt.setText(recognizedText);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed recognizing text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed preparing image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputImageDialog() {
        Log.d(TAG, "showInputImageDialog: in input image");
        PopupMenu popupMenu = new PopupMenu(getContext(), inputImageBtn);

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
        intent.setAction(Intent.ACTION_GET_CONTENT);
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
                            imageUri = data.getData();
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        requireContext().getContentResolver(),
                                        imageUri);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageIv.setImageBitmap(
                                    selectedImageBitmap);
                        }

                    } else {
                        //cancelled
                        Toast.makeText(getContext(), "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
                        Toast.makeText(getContext(), "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean cameraResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
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
                        Toast.makeText(getContext(), "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(getContext(), "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }
    }
}