package com.example.ocrapplicationformultilanguage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TranslationFragment extends Fragment {

    private Button translateBtn;
    private AutoCompleteTextView autoCompleteTextViewFrom;
    private AutoCompleteTextView autoCompleteTextViewTo;
    private TextInputEditText sourceEditText;
    private TextInputEditText translatedEditText;

    private String sourceLang, targetLang;


    OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translation, container, false);


        autoCompleteTextViewFrom = view.findViewById(R.id.autoCompleteTextViewFrom);
        autoCompleteTextViewTo = view.findViewById(R.id.autoCompleteTextViewTo);

        // data to inflate the drop-down items
        String[] langauge_dictionary = new String[]{"Afrikaans", "Arabic", "Danish", "English", "German", "Hindi", "Marathi"};

        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, langauge_dictionary);
        autoCompleteTextViewFrom.setAdapter(adapter);
        autoCompleteTextViewTo.setAdapter(adapter);

        autoCompleteTextViewFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourceLang = sourceLanguage(langauge_dictionary);
            }
        });

        autoCompleteTextViewTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                targetLang = targetLanguage(langauge_dictionary);
            }
        });

        client = new OkHttpClient();

        sourceEditText = view.findViewById(R.id.sourceEditText);
        translatedEditText = view.findViewById(R.id.translatedEditText);
        translateBtn = view.findViewById(R.id.translateBtn);

        translateBtn.setOnClickListener(view1 -> {
            post(sourceLang, targetLang);


        });
        return view;
    }


    public String sourceLanguage(String[] langauge_dictionary) {
        String tempSourceLang = " ";
        if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[0])) {

            // for Afrikaans
            tempSourceLang = "af";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[1])) {
            // for Arabic

            tempSourceLang = "ar";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[2])) {
            // for Danish
            tempSourceLang = "da";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[3])) {
            // for English
            tempSourceLang = "en";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[4])) {
            //German
            tempSourceLang = "de";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[5])) {
            //Hindi
            tempSourceLang = "hi";
        } else if (autoCompleteTextViewFrom.getText().toString().equals(langauge_dictionary[6])) {
            //Marathi
            tempSourceLang = "mr";
        } else {
            Toast.makeText(getContext(), "Please select your language!", Toast.LENGTH_SHORT).show();
        }
        return tempSourceLang;
    }

    public String targetLanguage(String[] langauge_dictionary) {
        String tempTargetLang = "";
        if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[0])) {

            // for Afrikaans
            tempTargetLang = "af";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[1])) {
            // for Arabic

            tempTargetLang = "ar";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[2])) {
            // for Danish
            tempTargetLang = "da";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[3])) {
            // for English
            tempTargetLang = "en";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[4])) {
            //German
            tempTargetLang = "de";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[5])) {
            //Hindi
            tempTargetLang = "hi";
        } else if (autoCompleteTextViewTo.getText().toString().equals(langauge_dictionary[6])) {
            //Marathi
            tempTargetLang = "mr";
        } else {
            Toast.makeText(getContext(), "Please select your language!", Toast.LENGTH_SHORT).show();
        }
        return tempTargetLang;
    }

    public void post(String sourceLang, String targetLang) {
        RequestBody body = new FormBody.Builder()
                .add("source_language", sourceLang )
                .add("target_language", targetLang)
                .add("text", Objects.requireNonNull(sourceEditText.getText()).toString())
                .build();

        Request request = new Request.Builder()
                .url("https://text-translator2.p.rapidapi.com/translate")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", "1cbb6a14d5msh02d6df95b649332p165bb5jsn11cd76766d0f")
                .addHeader("X-RapidAPI-Host", "text-translator2.p.rapidapi.com")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String resp = Objects.requireNonNull(response.body()).string();

                            JSONObject jsonObject = new JSONObject(resp);
                            JSONObject data = jsonObject.getJSONObject("data");
                            String result = data.getString("translatedText");
                            translatedEditText.setText(result);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}