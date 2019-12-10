//TODO: parse the JSON, in MyAdapter.java change the onclick to delete from API
//TODO: Somehow get the array of models back to MainActivty
package com.example.recyclerviewpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        models = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton=findViewById(R.id.button);
        getModels=findViewById(R.id.getModels);
        userInput=findViewById(R.id.userInput);

        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        models = getMyList();
        myAdapter = new MyAdapter(this,models);
        mRecyclerView.setAdapter(myAdapter);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model m=new Model();
                m.setTitle("Dynamically created");
                m.setDescription("This is created upon button press");
                m.setId(1);
                m.setImg(R.drawable.ic_launcher_background);
                models.add(m);
                //mRecyclerView.setAdapter(myAdapter);
                String test = generateJSONString();
                //submit(test);
                //models = getModels();
                mRecyclerView.setAdapter(myAdapter);
            }
        });
        getModels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                models=getModels(myMainActivity);
                mRecyclerView.setAdapter(myAdapter);

            }
        });

    }
    private void updateModelMain(ArrayList<Model> newModels){
        models = newModels;
        Toast.makeText(getApplicationContext(),"newModels "+newModels.size(),Toast.LENGTH_LONG).show();
        mRecyclerView.setAdapter(myAdapter);
    }
    /*
    final ArrayList<Model> incomingModels = new ArrayList<>();
    String URL = myURL+"/task";

    //parse JSON object into models
    JSONArray jsonArray1 = new JSONArray(response);
    for(int i=0;i<jsonArray1.length();i++){
        Model m = new Model();
        JSONObject content = jsonArray1.getJSONObject(i);
        String contentString = content.getString("content");
        m.setTitle(contentString);
        incomingModels.add(m);
    }
        */
    private ArrayList<Model> getModels(MainActivity myMainActivity) {
        final String savedata= null;
        final ArrayList<Model> incomingModels = new ArrayList<>();
        String URL = myURL+"/task";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                    //JSONObject objres=new JSONObject(response);
                    //parse JSON object into models
                    JSONArray jsonArray1 = new JSONArray(response);
                    for(int i=0;i<jsonArray1.length();i++){
                        Model m = new Model();
                        JSONObject content = jsonArray1.getJSONObject(i);
                        String contentString = content.getString("content");
                        m.setTitle(contentString);
                        int idVal = content.getInt("id");
                        m.setId(idVal);
                        m.setImg(R.drawable.ic_launcher_background);
                        m.setDescription("This is created upon button press");

                        m.setImg(R.drawable.ic_launcher_background);
                        m.setTitle(contentString);
                        incomingModels.add(m);
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateModelMain(incomingModels);
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                //Log.v("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    //Log.v("Unsupported Encoding while trying to get the bytes", data);
                    return null;
                }
            }

        };
        Toast.makeText(getApplicationContext(),"incomingModels "+incomingModels.size(),Toast.LENGTH_LONG).show();
        requestQueue.add(stringRequest);
        return incomingModels;
    }

    /*private class myAsyncTask extends AsyncTask<URL,Integer,String>{
        private WeakReference<MainActivity> activityWeakReference;
        myAsyncTask(MainActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override protected String doInBackground(URL... urls){
            try{
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(myURL)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String myResponse = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateModelMain();
                                }
                            });
                        }
                    }
                });
            }
        }
    }*/
    //code to add a task to the database
    private void submit(String data) {
        final String savedata= data;
        String URL=myURL+"/task/add";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);
                    Toast.makeText(getApplicationContext(),objres.toString(),Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                //Log.v("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    //Log.v("Unsupported Encoding while trying to get the bytes", data);
                    return null;
                }
            }

        };
        requestQueue.add(stringRequest);
    }

    private String generateJSONString(){
        return "{"+"\"content\":"+"\""+userInput.getText()+"\","+"\"priority\":"+"\"3\"}";
    }
    private ArrayList<Model> getMyList(){
        ArrayList<Model> models = new ArrayList<>();

        Model m=new Model();
        m.setTitle("background");
        m.setDescription("This is background image");
        m.setImg(R.drawable.ic_launcher_background);
        models.add(m);

        m=new Model();
        m.setTitle("foreground");
        m.setDescription("This is foreground");
        m.setImg(R.drawable.ic_launcher_foreground);
        models.add(m);

        return models;
    }


}
