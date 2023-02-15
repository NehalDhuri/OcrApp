package com.example.ocrapplicationformultilanguage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

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

    private TextView textView;
    private Button translateBtn;
    private AutoCompleteTextView autoCompleteTextViewFrom;
    private AutoCompleteTextView autoCompleteTextViewTo;
    private TextInputEditText sourceEditText;
    private TextInputEditText translatedEditText;



    OkHttpClient client;

    String postURL = "https://google-translate1.p.rapidapi.com/language/translate/v2";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translation, container, false);


        autoCompleteTextViewFrom = view.findViewById(R.id.autoCompleteTextViewFrom);
        autoCompleteTextViewTo = view.findViewById(R.id.autoCompleteTextViewTo);

        // data to inflate the drop-down items
        String[] langauge_dictionary = new String[]{"Afrikaans", "Arabic", "Danish","English", "German","Hindi","Marathi"};
//        String[] langauge_dictionary = new String[]{"Afrikaans", "Arabic", "Danish","English", "German","Hindi","Marathi"};

        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, langauge_dictionary);
        autoCompleteTextViewFrom.setAdapter(adapter);
        autoCompleteTextViewTo.setAdapter(adapter);

//        super.onCreate(savedInstanceState);

        client = new OkHttpClient();

        sourceEditText = view.findViewById(R.id.sourceEditText);
        translatedEditText = view.findViewById(R.id.translatedEditText);

        translateBtn = view.findViewById(R.id.translateBtn);

        translateBtn.setOnClickListener(view1 -> {
            post();
        });
        return view;
    }

    public void post(){
        RequestBody body = new FormBody.Builder()
                .add("q", "Hello, world!")
                .add("target", "es")
                .add("source", "en")
                .build();

        Request request = new Request.Builder()
                .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Accept-Encoding", "application/gzip")
                .addHeader("X-RapidAPI-Key", "1cbb6a14d5msh02d6df95b649332p165bb5jsn11cd76766d0f")
                .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .build();

//        Response response = client.newCall(request).execute();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                textView.setText("fail");

                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    String resp = Objects.requireNonNull(response.body()).string();

                    JSONObject jsonObject = new JSONObject(resp);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray translations = data.getJSONArray("translations");
                    JSONObject translatedText = translations.getJSONObject(0);
                    String result = translatedText.getString("translatedText");
                    textView.setText(result);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}