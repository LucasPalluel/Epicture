package com.example.epiflex;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.util.AbstractList;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private OkHttpClient httpClient;
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 126587413;
    public static String AccessToken = "";
    private static String username = "";


    private void GetAccountInformations(){
        httpClient = new OkHttpClient.Builder().build();
        Request request  = new Request.Builder()
                .url("https://api.imgur.com/3/account/" + username)
                .header("Authorization", "Client-ID 352915728d5e2d4")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject data = null;
                try {
                    data = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                User me = new User();
                try{
                    me.url = data.getJSONObject("data").getString("url");
                    me.avatar = data.getJSONObject("data").getString("avatar");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                runOnUiThread(() -> UserRender(me));
            }
        });
    }

    private void UserRender(User user){
        ImageView iv = findViewById(R.id.imageView);
        Picasso.with(MainActivity.this).load(user.avatar).into(iv);
        TextView tv = findViewById(R.id.username);
        tv.setText(user.url);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (AccessToken == "") {
            FloatingActionButton fab = findViewById(R.id.add);
            fab.setVisibility(View.INVISIBLE);
        } else {
            FloatingActionButton fab = findViewById(R.id.add);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View it) {
                    androidx.appcompat.widget.PopupMenu popup = new PopupMenu((Context) MainActivity.this, (View) fab);
                    popup.inflate(R.menu.pop_add);
                    popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) (new PopupMenu.OnMenuItemClickListener() {
                        public final boolean onMenuItemClick(MenuItem it) {
                            Intrinsics.checkExpressionValueIsNotNull(it, "it");
                            if (Intrinsics.areEqual(it.getTitle(), "Camera")) {
                                OpenCamera();
                            }

                            if (Intrinsics.areEqual(it.getTitle(), "Galerie")) {
                                OpenGalery();
                            }
                            return true;
                        }
                    }));
                    popup.show();
                }
            });
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }
    public void SignOut(View view){
        AccessToken = "";
        finish();
        startActivity(getIntent());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AccessToken != "") {
            Button sign_out = findViewById(R.id.signout);
            sign_out.setVisibility(View.VISIBLE);
            ImageView iv = findViewById(R.id.imageView);
            TextView tv = findViewById(R.id.username);
            iv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
            Button sign_in = findViewById(R.id.signin);
            sign_in.setVisibility(View.INVISIBLE);
        } else {
            NavigationMenuItemView galery = findViewById(R.id.nav_gallery);
            galery.setVisibility(View.INVISIBLE);
            Button sign_out = findViewById(R.id.signout);
            sign_out.setVisibility(View.INVISIBLE);
            ImageView iv = findViewById(R.id.imageView);
            TextView tv = findViewById(R.id.username);
            Button sign_in = findViewById(R.id.signin);
            iv.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            sign_in.setVisibility(View.VISIBLE);
            sign_in.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View it) {
                    androidx.appcompat.widget.PopupMenu popup = new PopupMenu((Context) MainActivity.this, (View) sign_in);
                    popup.inflate(R.menu.sign_in);
                    popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) (new PopupMenu.OnMenuItemClickListener() {
                        public final boolean onMenuItemClick(MenuItem it) {
                            Intrinsics.checkExpressionValueIsNotNull(it, "it");
                            if (Intrinsics.areEqual(it.getTitle(), "Sign In with Imgur")) {
                                WebView webView = new WebView(MainActivity.this);
                                setContentView(webView);
                                webView.setBackgroundColor(Color.TRANSPARENT);
                                webView.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=352915728d5e2d4&response_type=token");
                                webView.getSettings().setJavaScriptEnabled(true);
                                webView.setWebViewClient(new WebViewClient() {

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                        String url = request.getUrl().toString();
                                        if (url.contains("access_token")) {
                                            splitUrl(url, view);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            MainActivity.this.startActivity(intent);
                                            return false;
                                        } else {
                                            view.loadUrl(url);
                                        }
                                        return true;
                                    }
                                });
                            }
                            return true;
                        }
                    }));
                    popup.show();
                }
            });
        }
        if (AccessToken != "") {
            GetAccountInformations();
        }
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void splitUrl(String url, WebView view) {
        String[] outerSplit = url.split("#")[1].split("&");
        String refreshToken = null;

        int index = 0;

        for (String s : outerSplit) {
            String[] innerSplit = s.split("=");

            switch (index) {
                // Access Token
                case 0:
                    AccessToken = innerSplit[1];
                    break;

                // Refresh Token
                case 3:
                    refreshToken = innerSplit[1];
                    break;

                // Username
                case 4:
                    username = innerSplit[1];
                    break;
                default:
            }
            index++;
        }
    }

    public void OpenGalery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick an image"), GALLERY_REQUEST_CODE);
    }

    public void OpenCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public static String convertToBase64(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(byteArrayImage, android.util.Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                    Uri imageData = data.getData();
                    String path = com.example.handyopinion.UriUtils.getPathFromUri(MainActivity.this, imageData);
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("image", path,
                                    RequestBody.create(MediaType.parse("application/octet-stream"),
                                            new File(path)))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://api.imgur.com/3/upload")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer " + AccessToken)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
                    String img64 = convertToBase64((Bitmap) data.getExtras().get("data"));
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("image", img64)
                            .addFormDataPart("type", "base64")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://api.imgur.com/3/upload")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer " + AccessToken)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

        }.execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




}



