//TODO: parse the JSON

package com.example.recyclerviewpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.recyclerviewpractice.MyAdapter;
import com.example.recyclerviewpractice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private EditText userInput;
    private Button myButton;
    private Button getModels;
    public ArrayList<Model> models;
    private MainActivity myMainActivity = this;
    private static String myURL ="http://192.168.0.127:5000";
    public Context  c= this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        models = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton=findViewById(R.id.button);
        userInput=findViewById(R.id.userInput);
        getModels = findViewById(R.id.getModels);

        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        models = getMyList();


        myAdapter = new MyAdapter(getApplicationContext(),models);
        getModels();
        mRecyclerView.setAdapter(myAdapter);

        //submit a new thing into the API
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter = new MyAdapter(getApplicationContext(),models);
                mRecyclerView.setAdapter(myAdapter);
                String in =userInput.getText().toString();
                submit();
            }
        });
        //get the data from the API and put it in the models array
        getModels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter = new MyAdapter(getApplicationContext(),models);
                getModels();
                mRecyclerView.setAdapter(myAdapter);

            }
        });

    }
    //get models from database
    //This works don't touch
    private void getModels(){
        String URL=myURL+"/task";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    //Toast.makeText(getApplicationContext(),jsonArray.toString(),Toast.LENGTH_LONG).show();
                    models = parseJSONArrayToModels(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        queue.add(stringRequest);
    }

    //code to add a task to the database
    //This works Don't touch
    private void submit(){
        final String myContent = userInput.getText().toString();
        String URL=myURL+"/task/add";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, convertStringToJsonObject(generateJSONString()), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(request);
    }

    private ArrayList<Model> parseJSONArrayToModels(JSONArray jsonArray){
        ArrayList<Model> tempModels= new ArrayList<>();
        try{
            for(int i=0;i<jsonArray.length();i++){
                Model m = new Model();
                JSONObject object= jsonArray.getJSONObject(i);
                m.setTitle(object.getString("content"));
                m.setDescription(""+object.getInt("id"));
                m.setId(object.getInt("id"));
                m.setImg(R.drawable.ic_launcher_background);
                tempModels.add(m);
            }
        }catch (JSONException e){

        }
        return tempModels;
    }
    private String generateJSONString(){
        return "{"+"\"content\":"+"\""+userInput.getText()+"\","+"\"priority\":"+"\"3\"}";
    }
    private JSONObject convertStringToJsonObject(String in){
        try{
            return new JSONObject(in);
        }catch (Throwable t){

        }
        return new JSONObject();
    }
    private ArrayList<Model> getMyList(){
        ArrayList<Model> models = new ArrayList<>();
        return models;
    }


}
