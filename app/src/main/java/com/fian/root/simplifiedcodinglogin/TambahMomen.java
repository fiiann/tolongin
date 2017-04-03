package com.fian.root.simplifiedcodinglogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
//import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
//import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.Spinner;
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
//import java.sql.Date;
//import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.fian.root.simplifiedcodinglogin.R.id.choose;
import static com.fian.root.simplifiedcodinglogin.R.id.submit;

/**
 * Created by root on 4/2/17.
 */

public class TambahMomen extends AppCompatActivity implements View.OnClickListener{
    public static final String URL = "http://tolongin.96.lt/momen.php";

    public static final String KEY_JUDUL= "judul";
    public static final String KEY_DESKRIPSI= "deskripsi";
    public static final String KEY_PENOLONG= "penolong";
    public static final String KEY_DITOLONG= "ditolong";
//    public static final String KEY_TANGGAL="tanggal";
    public static final String KEY_FOTO= "foto";

    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    private Button buttonSubmit;
    private Button buttonChoose;
    private EditText input_judul;
    private EditText input_pertolongan;
    private EditText input_penolong;
    private EditText input_ditolong;
//    private DatePicker inputTanggal;
//    private SimpleDateFormat input_tanggal;
    private ImageView input_foto;
    private String foto_encode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_momen);

        input_judul = (EditText) findViewById(R.id.judul);
        input_pertolongan = (EditText) findViewById(R.id.pertolongan); // deskripsi
        input_penolong = (EditText) findViewById(R.id.penolong);
        input_ditolong = (EditText) findViewById(R.id.ditolong);
//        inputTanggal= (DatePicker) findViewById(R.id.tanggal);

//        int day  = inputTanggal.getDayOfMonth();
//        int month= inputTanggal.getMonth();
//        int year = inputTanggal.getYear();
//        input_tanggal= new SimpleDateFormat("dd-MM-yyyy");

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

    private void UploadMomen(){
        final String judul = input_judul.getText().toString().trim();
        final String desksripsi = input_pertolongan.getText().toString().trim();
        final String penolong = input_penolong.getText().toString().trim();
        final String ditolong = input_ditolong.getText().toString().trim();
//        String inputTanggal1 = input_tanggal.format(new Date(year, month, day));
        final String foto = foto_encode;

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

                        Toast.makeText(TambahMomen.this,r_message,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahMomen.this,error.toString()+"::"+error.networkResponse.data+"::"+error.networkResponse.headers,Toast.LENGTH_LONG).show();
                    }
                }){


            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put(KEY_JUDUL,judul);
                params.put(KEY_DESKRIPSI,desksripsi);
                params.put(KEY_PENOLONG,penolong);
                params.put(KEY_DITOLONG,ditolong);
//                params.put(KEY_TANGGAL,tanggal);
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
            UploadMomen();
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
        Toast.makeText(TambahMomen.this,"Loading...",Toast.LENGTH_LONG).show();
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
