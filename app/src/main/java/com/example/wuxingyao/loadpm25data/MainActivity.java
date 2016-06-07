package com.example.wuxingyao.loadpm25data;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView pmData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pmData = (TextView) findViewById(R.id.PMdata);

        findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });
    }

    private void reloadData() {
        pmData.setText("Loading...");

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://aqicn.org/publishingdata/json").openStream(), "UTF-8"));
                    String line = null;
                    StringBuffer content = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        content.append(line);
                    }
                    br.close();
                    return content.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    try {
                        JSONArray jsonArr = new JSONArray(s);
                        JSONObject jsonObj = jsonArr.getJSONObject(0);
                        JSONArray pollutants = jsonObj.getJSONArray("pollutants");
                        JSONObject firstPollutant = pollutants.getJSONObject(0);
                        //System.out.print("cityName:"+jsonObj.getString("cityName")+",local="+jsonObj.getString("localname"));
                        pmData.setText(String.format("%s %s:%f", jsonObj.getString("cityName"), jsonObj.getString("localName"), firstPollutant.getDouble("value")));
                    } catch (JSONException jsone    ) {
                        jsone.printStackTrace();
                    }
                }
            }
        }.execute();
    }


}

