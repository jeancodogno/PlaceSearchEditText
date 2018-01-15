package io.github.jeancodogno.placesearchedittext;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlaceSearchEditText extends android.support.v7.widget.AppCompatMultiAutoCompleteTextView{

    private static final String TAG = PlaceSearchEditText.class.getName();
    private int INIT_SEARCH = 2;
    private final Context context;
    private String key = "INPUT_YOUR_API_KEY";

    public PlaceSearchEditText(Context context){
        super(context);
        this.context = context;
        this.initialize();


    }

    public PlaceSearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.initialize();
    }

    public PlaceSearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.initialize();
    }

    public PlaceSearchEditText setInitSearch(int  init_search){
        this.INIT_SEARCH = init_search;
        return this;
    }

    private void initialize() {
        this.getApiKey();
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().length() > PlaceSearchEditText.this.INIT_SEARCH) {

                    new Thread() {

                        public void run() {

                            String url = null;
                            try {
                                url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + URLEncoder.encode(charSequence.toString(), "UTF-8") + "&types=geocode&components=country:br&language=pt-br&sensor=false&key=" + PlaceSearchEditText.this.key;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Log.e("URL", url);
                            JSONObject jsonObj = null;
                            try {

                                jsonObj = PlaceSearchEditText.this.getJSONObjectFromURL(url);
                                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                                final ArrayList<String> resultList = new ArrayList<>();

                                for (int i = 0; i < predsJsonArray.length(); i++) {
                                    String new_String = predsJsonArray.getJSONObject(i).getString("description");
                                    resultList.add(new_String);

                                }
                                ((Activity) PlaceSearchEditText.this.context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PlaceSearchEditText.this.setAdapter(new PlaceAdapter(PlaceSearchEditText.this.context, R.layout.places_list, R.id.text_place, resultList));
                                        PlaceSearchEditText.this.setTokenizer(new CommaTokenizer());
                                        PlaceSearchEditText.this.setThreshold(1);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();

        return new JSONObject(jsonString);
    }


    private void getApiKey(){

        try {

            ApplicationInfo appInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(),PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;

            if (bundle != null) {
                this.key = bundle.getString("com.google.android.geo.API_KEY");
            }else{
                Log.e(TAG, "API KEY not Found");
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "API KEY not Found");
        }

    }
}
