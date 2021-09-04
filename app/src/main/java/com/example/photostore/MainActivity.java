package com.example.photostore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.photostore.Adapter.RecyclerAdapter;
import com.example.photostore.ModelClass.ModelData;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import androidx.core.app.ActivityCompat;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private static final String fetch_image_url = "https://unguruyou.com/divers_club/apis/TestApp/fetch_test_images.php";
    private static final int STORAGE_PERMISSION_CODE = 1;
    private List<ModelData> modelData;
    private RecyclerView rec_view;

    private RecyclerAdapter rec_adapter;
    private JsonArrayRequest json_request;
    private RequestQueue requestQueue;

    @Override
    protected void onStart() {
        super.onStart();

        isStoragePermissionGranted();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rec_view =findViewById(R.id.rec_id);
        rec_view.setHasFixedSize(true);
        rec_view.setLayoutManager(new LinearLayoutManager(this));

        modelData =new ArrayList<>();

        fetchImages();

    }


    private void fetchImages() {
        json_request =new JsonArrayRequest(fetch_image_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null;
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject ob=response.getJSONObject(i);
                        ModelData listData =new ModelData(ob.getString("imageurl"));
                        modelData.add(listData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                setAdater(modelData);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(json_request);



    }

    private void setAdater(List<ModelData> model_data) {

        rec_adapter =new RecyclerAdapter(model_data,this);
        rec_view.setAdapter(rec_adapter);

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

}