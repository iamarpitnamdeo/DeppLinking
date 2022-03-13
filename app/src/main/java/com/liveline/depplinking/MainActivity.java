package com.liveline.depplinking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONObject;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import android.net.Uri;

import android.provider.Settings;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity {

    Intent i;
    String main,description,temp,maxTemp,minTemp,feelLike;
    EditText edt1,edt2;
    TextView m,d,t,max,min,fl;
    Long lat,longi;
    MaterialButton m1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Branch.enableLogging();
        m=findViewById(R.id.main);
        d=findViewById(R.id.description);
        t=findViewById(R.id.temp);
        max=findViewById(R.id.maxTemp);
        min=findViewById(R.id.minTemp);
        fl=findViewById(R.id.feelLike);
        // Branch object initialization
        Branch.getAutoInstance(this);
        m1=findViewById(R.id.materialButton);
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + android_id);

        edt1=findViewById(R.id.edt1);
        edt2=findViewById(R.id.edt2);

        i=getIntent();
        Uri deeplink = i.getData();
        Log.d("deep", "onCreate: "+deeplink);



        m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(edt1.getText().toString().trim().length()==0)
              {
                  edt1.requestFocus();
                  edt1.setError("Please Enter Latitude");

              }
              else if(edt2.getText().toString().trim().length()==0)
              {
                  edt2.requestFocus();
                  edt2.setError("Please Enter Longitude");
              }
              else
              {
                  lat=Long.parseLong(edt1.getText().toString());
                  longi=Long.parseLong(edt2.getText().toString());
                  hitApi(lat,longi);
              }
            }
        });

       // ii3r2.test-app.link


    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener =new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(@Nullable JSONObject referringParams, @Nullable BranchError error) {
            if (error == null) {
                Log.i("BranchDeepLink", "" + referringParams.toString());
                try {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(referringParams.toString());
                    Share shareScreenDataModel = gson.fromJson(json, Share.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
    };



    private void hitApi(long lat,long longi)
    {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longi+"&appid=aeb34c86dcc7189ee864dd464352287f";
        Log.d("AG", "hitBookApi: "+url);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "onResponse: " + response);

                        JSONArray jsonArray=response.optJSONArray("weather");
                        Log.d("weather", "onResponse: " + jsonArray);
                       for(int i=0;i<jsonArray.length();i++)
                       {
                           main=jsonArray.optJSONObject(i).optString("main");
                           description=jsonArray.optJSONObject(i).optString("description");
                       }
                       JSONObject jsonObject=response.optJSONObject("main");

                      temp= jsonObject.opt("temp").toString();
                      feelLike=  jsonObject.opt("feels_like").toString();
                      minTemp=  jsonObject.opt("temp_min").toString();
                      maxTemp=  jsonObject.opt("temp_max").toString();



                      m.setText("Weather :-"+main);
                      d.setText("Description :-"+description);
                      t.setText("Temperature :-"+temp);
                      fl.setText("Feels_like :-"+feelLike);
                      min.setText("Min Temperature :-"+minTemp);
                      max.setText("Max Temperature :-"+maxTemp);
                        Log.d("jobj", "onResponse: "+temp+" "+feelLike+" "+minTemp+" "+maxTemp);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(MainActivity.this, "Please Enter Valid Input", Toast.LENGTH_SHORT).show();
                        Log.d("okss", "Please Enter Valid Input");
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}




