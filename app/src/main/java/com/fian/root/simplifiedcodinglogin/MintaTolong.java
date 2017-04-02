package com.fian.root.simplifiedcodinglogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fian.root.simplifiedcodinglogin.R.id.choose;
import static com.fian.root.simplifiedcodinglogin.R.id.buttonRegister;
import static com.fian.root.simplifiedcodinglogin.R.id.submit;

public class MintaTolong extends AppCompatActivity implements View.OnClickListener  {

    public static final String URL = "http://tolongin.96.lt/timeline.php";

    public static final String KEY_JUDUL= "judul";
    public static final String KEY_DESKRIPSI= "deskripsi";
    public static final String KEY_SPINNER= "kategori";
    public static final String KEY_LOKASI= "lokasi";
    public static final String KEY_FOTO= "foto";

    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    private Button buttonSubmit;
    private Button buttonChoose;
    private EditText input_judul;
    private EditText input_pertolongan;
    private Spinner input_spinner;
    private EditText input_lokasi;
    private ImageView input_foto;
    private String foto_encode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minta_tolong);

        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"Kecelakaan", "Lalu Lintas", "sakit", "cari alamat"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        input_judul = (EditText) findViewById(R.id.judul);
        input_pertolongan = (EditText) findViewById(R.id.pertolongan);
        input_spinner = (Spinner) findViewById(R.id.spinner1);
        input_lokasi = (EditText) findViewById(R.id.lokasi);
        input_foto = (ImageView) findViewById(R.id.foto);

        buttonSubmit= (Button) findViewById(submit);
        buttonChoose= (Button) findViewById(choose);

        buttonSubmit.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return encodedImage;
//        byte[] encodedBytes = Base64.encode("Test".getBytes());
//        System.out.println("encodedBytes " + new String(encodedBytes));
    }

    private void UploadPertolongan(){
        final String judul = input_judul.getText().toString().trim();
        final String desksripsi = input_pertolongan.getText().toString().trim();
        final String lokasi = input_lokasi.getText().toString().trim();
        final String foto = foto_encode;
        final String spinner = input_spinner.getSelectedItem().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//
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
                        String r_message = null;
                        try {
                            r_message = mainObject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String r_apiKey = null;
                        try {
                            r_apiKey = mainObject.getString("apiKey");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(MintaTolong.this,r_message,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MintaTolong.this,error.toString()+"::"+error.networkResponse.data+"::"+error.networkResponse.headers,Toast.LENGTH_LONG).show();
                    }
                }){


            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put(KEY_JUDUL,judul);
                params.put(KEY_DESKRIPSI,desksripsi);
                params.put(KEY_SPINNER, spinner);
                params.put(KEY_LOKASI, lokasi);
                params.put(KEY_FOTO, foto);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();

                String apiKey = pref.getString("apiKey", null);

                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", apiKey);

                return params;
            }

        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            UploadPertolongan();
        }
        if (v == buttonChoose) {
            getFoto();
        }
    }

    public void getFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        Toast.makeText(MintaTolong.this,"Loading...",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                input_foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        foto_encode = getStringImage(bitmap);
//      String encodebitmap = getStringImage(bitmap);
//      Toast.makeText(MintaTolong.this,"IMAGE : "+encodebitmap,Toast.LENGTH_LONG).show();

    }

}