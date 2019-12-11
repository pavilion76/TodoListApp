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

import org.json.JSONObject;
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
                submit(in);
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
    //THIS WORKS DON"T TOUCH
    private void submit(final String data) {
        final String savedata= data;
        String URL=myURL+"/task/add";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);
                    Toast.makeText(getApplicationContext(),objres.toString(),Toast.LENGTH_LONG).show();
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

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) { return null; }
            }
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String> ();
                params.put("content", "test");
                return params;
            }
        };
        queue.add(stringRequest);

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
    private ArrayList<Model> getMyList(){
        ArrayList<Model> models = new ArrayList<>();
        return models;
    }


}
