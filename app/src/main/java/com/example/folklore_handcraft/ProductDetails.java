package com.example.folklore_handcraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URL;

public class ProductDetails extends AppCompatActivity {

    private static final String TAG = "TaskDetail";
    private URL url =null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();

        String productName = intent.getExtras().getString("productName");
        String productDescription = intent.getExtras().getString("productDescription");
        String productPrice = intent.getExtras().getString("productPrice");
        String productContact = intent.getExtras().getString("productContact");

        ((TextView)findViewById(R.id.taskTitle)).setText(productName);
        ((TextView)findViewById(R.id.taskBody)).setText(productDescription);
        ((TextView)findViewById(R.id.taskState)).setText(productPrice);
        ((TextView)findViewById(R.id.contactId)).setText(productContact);


        String fileName = intent.getExtras().get(MainActivity.TASK_FILE).toString();




        ImageView imageView = findViewById(R.id.imageViewfile);

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Glide.with(getBaseContext())
                            .load(url.toString())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .centerCrop()
                            .into(imageView);
                    return false;
                });

        getFileFromS3Storage(fileName);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String linkedText = String.format("<a href=\"%s\">download File</a> ", url);
        TextView test = findViewById(R.id.uploadfile);
        test.setText(Html.fromHtml(linkedText));
        test.setMovementMethod(LinkMovementMethod.getInstance());

    }
    private void getFileFromS3Storage(String key) {
        Amplify.Storage.downloadFile(
                key,
                new File(getApplicationContext().getFilesDir() + key),
                result -> {
                    Log.i(TAG, "Successfully downloaded: " + result.getFile().getAbsoluteFile());
                },
                error -> Log.e(TAG,  "Download Failure", error)
        );

        Amplify.Storage.getUrl(
                key,
                result -> {
                    Log.i(TAG, "Successfully generated: " + result.getUrl());
                    url= result.getUrl();
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "URL generation failure", error)
        );
    }


}