package com.fian.root.simplifiedcodinglogin;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    public static final String URL = "http://tolongin.96.lt/login.php";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";


    private EditText editTextUsername;
    private EditText editTextPassword;

    private Button buttonLogin;
    private Button buttonKlik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);


        buttonLogin.setOnClickListener(this);

    }

    private void loginUser(){
//        final String username = editTextUsername.getText().toString().trim();
//        final String password = editTextPassword.getText().toString().trim();
            final String username = "tolongin";
            final String password= "tolongin";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject mainObject = null;
                        try {
                            mainObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String r_error = null;
                        try {
                            r_error = mainObject.getString("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String r_apiKey = null;
                        try {
                            r_apiKey = mainObject.getString("apiKey");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();

//                        editor.putBoolean("key_name", true); // Storing boolean - true/false
                        editor.putString("apiKey", r_apiKey); // Storing string
//                        editor.putInt("key_name"`, "int value"); // Storing integer
//                        editor.putFloat("key_name", "float value"); // Storing float
//                        editor.putLong("key_name", "long value"); // Storing long

                        editor.commit(); // commit changes

                        String apiKey = pref.getString("apiKey", null); // getting String
//                        pref.getInt("key_name", null); // getting Integer
//                        pref.getFloat("key_name", null); // getting Float
//                        pref.getLong("key_name", null); // getting Long
//                        pref.getBoolean("key_name", null); // getting boolean
//
//                        editor.remove("name"); // will delete key name
//                        editor.remove("email"); // will delete key email

//                        editor.commit(); // commit changes

//                        editor.clear();
//                        editor.commit(); // commit changes


//                        Toast.makeText(ActivityLogin.this,apiKey,Toast.LENGTH_LONG).show();

                        if(apiKey!=null){
                            Intent intent = new Intent(ActivityLogin.this,TimelineActivity.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityLogin.this,error.toString()+"::"+error.networkResponse.data+"::"+error.networkResponse.headers,Toast.LENGTH_LONG).show();
                    }
                }){




            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put(KEY_USERNAME,username);
                params.put(KEY_PASSWORD,password);
                return params;
            }

        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            loginUser();
        }

    }

    public void register(View view){
        Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
        startActivity(intent);
    }
}