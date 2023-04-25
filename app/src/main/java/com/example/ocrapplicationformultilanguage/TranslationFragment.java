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
        String[] language_dictionary = new String[]{"Afrikaans",
                "Albanian",
                "Amharic",
                "Arabic",
                "Armenian",
                "Azerbaijani",
                "Basque",
                "Belarusian",
                "Bengali",
                "Bosnian",
                "Bulgarian",
                "Catalan",
                "Cebuano",
                "Chichewa",
                "Chinese (Simplified)",
                "Chinese (Traditional)",
                "Corsican",
                "Croatian",
                "Czech",
                "Danish",
                "Dutch",
                "English",
                "Esperanto",
                "Estonian",
                "Filipino",
                "Finnish",
                "French",
                "Frisian",
                "Galician",
                "Georgian",
                "German",
                "Greek",
                "Gujarati",
                "Haitian Creole",
                "Hausa",
                "Hawaiian",
                "Hebrew",
                "Hindi",
                "Hmong",
                "Hungarian",
                "Icelandic",
                "Igbo",
                "Indonesian",
                "Irish",
                "Italian",
                "Japanese",
                "Javanese",
                "Kannada",
                "Kazakh",
                "Khmer",
                "Kinyarwanda",
                "Korean",
                "Kurdish (Kurmanji)",
                "Kyrgyz",
                "Lao",
                "Latin",
                "Latvian",
                "Lithuanian",
                "Luxembourgish",
                "Macedonian",
                "Malagasy",
                "Malay",
                "Malayalam",
                "Maltese",
                "Maori",
                "Marathi",
                "Mongolian",
                "Myanmar (Burmese)",
                "Nepali",
                "Norwegian",
                "Odia (Oriya)",
                "Pashto",
                "Persian",
                "Polish",
                "Portuguese",
                "Punjabi",
                "Romanian",
                "Russian",
                "Samoan",
                "Scots Gaelic",
                "Serbian",
                "Sesotho",
                "Shona",
                "Sindhi",
                "Sinhala",
                "Slovak",
                "Slovenian",
                "Somali",
                "Spanish",
                "Sundanese",
                "Swahili",
                "Swedish",
                "Tajik",
                "Tamil",
                "Tatar",
                "Telugu",
                "Thai",
                "Turkish",
                "Turkmen",
                "Ukrainian",
                "Urdu",
                "Uyghur",
                "Uzbek",
                "Vietnamese",
                "Welsh",
                "Xhosa",
                "Yiddish",
                "Yoruba",
                "Zulu",
                "Hebrew",
                "Chinese (Simplified)"};

        String[] languages_code = new String[]{"af",
                "sq",
                "am",
                "ar",
                "hy",
                "az",
                "eu",
                "be",
                "bn",
                "bs",
                "bg",
                "ca",
                "ceb",
                "ny",
                "zh-CN",
                "zh-TW",
                "co",
                "hr",
                "cs",
                "da",
                "nl",
                "en",
                "eo",
                "et",
                "tl",
                "fi",
                "fr",
                "fy",
                "gl",
                "ka",
                "de",
                "el",
                "gu",
                "ht",
                "ha",
                "haw",
                "iw",
                "hi",
                "hmn",
                "hu",
                "is",
                "ig",
                "id",
                "ga",
                "it",
                "ja",
                "jw",
                "kn",
                "kk",
                "km",
                "rw",
                "ko",
                "ku",
                "ky",
                "lo",
                "la",
                "lv",
                "lt",
                "lb",
                "mk",
                "mg",
                "ms",
                "ml",
                "mt",
                "mi",
                "mr",
                "mn",
                "my",
                "ne",
                "no",
                "or",
                "ps",
                "fa",
                "pl",
                "pt",
                "pa",
                "ro",
                "ru",
                "sm",
                "gd",
                "sr",
                "st",
                "sn",
                "sd",
                "si",
                "sk",
                "sl",
                "so",
                "es",
                "su",
                "sw",
                "sv",
                "tg",
                "ta",
                "tt",
                "te",
                "th",
                "tr",
                "tk",
                "uk",
                "ur",
                "ug",
                "uz",
                "vi",
                "cy",
                "xh",
                "yi",
                "yo",
                "zu",
                "he",
                "zh"};
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, language_dictionary);
        autoCompleteTextViewFrom.setAdapter(adapter);
        autoCompleteTextViewTo.setAdapter(adapter);

        autoCompleteTextViewFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourceLang = sourceLanguage(language_dictionary,languages_code);
            }
        });

        autoCompleteTextViewTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                targetLang = targetLanguage(language_dictionary,languages_code);
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


    public String sourceLanguage(String[] language_dictionary, String[] language_code) {
        String tempSourceLang = " ";

        for (int i = 0; i < language_dictionary.length; i++) {
            if (autoCompleteTextViewFrom.getText().toString().equals(language_dictionary[i])) {

                return language_code[i];
            }
        }

        return tempSourceLang;
    }

    public String targetLanguage(String[] language_dictionary,String[] language_code) {
        String tempTargetLang = "";

        for (int i = 0; i < language_dictionary.length; i++) {
            if (autoCompleteTextViewTo.getText().toString().equals(language_dictionary[i])) {
                return language_code[i];
            }
        }

        return tempTargetLang;
    }

    public void post(String sourceLang, String targetLang) {
        RequestBody body = new FormBody.Builder()
                .add("source_language", sourceLang)
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