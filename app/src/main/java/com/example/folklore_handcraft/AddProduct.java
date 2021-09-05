package com.example.folklore_handcraft;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProduct extends AppCompatActivity {
    private static final String TAG = "AddTaskActivity";
    private ProductDataBase database;
    private ProductDao productDao;

//    private String teamId = "";

//    private final List<Team> teams = new ArrayList<>();

    static String pattern = "yyMMddHHmmssZ";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private static String FileUploadName= simpleDateFormat.format(new Date());
    private static String fileUploadExtention = null;
    private static File uploadFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Add Product");


        //Upload File
        Button uploadFile = findViewById(R.id.UploadImgId);
        uploadFile.setOnClickListener(v1 -> getFileFromDevice());


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();


         database = Room.databaseBuilder(getApplicationContext(), ProductDataBase.class, "task_List")
                .allowMainThreadQueries().build();
        productDao = database.productDao();


        // Add Product
        findViewById(R.id.AddProductId).setOnClickListener(view -> {
            String productName = ((EditText) findViewById(R.id.inputName)).getText().toString();
            String productDescription = ((EditText) findViewById(R.id.inputDescription)).getText().toString();
            String productPrice = ((EditText) findViewById(R.id.inputPrice)).getText().toString();
            String productContact = ((EditText) findViewById(R.id.inputContact)).getText().toString();




            Product newTask = new Product(productName, productDescription, productPrice,productContact);
            productDao.insertOne(newTask);


            addTaskToDynamoDB(productName,
                    productDescription,
                    productPrice,
                    productContact);

            Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
            toast.show();
        });


    }

    public void addTaskToDynamoDB(String productName, String productDescription, String productPrice,String productContact ) {
        com.amplifyframework.datastore.generated.model.AddProduct product = com.amplifyframework.datastore.generated.model.AddProduct.builder()
                .name(productName)
                .description(productDescription)
                .price(productPrice)
                .contact(productContact)
                .fileName(FileUploadName +"."+ fileUploadExtention.split("/")[1])
                .build();

        Amplify.API.mutate(ModelMutation.create(product),
                success -> Log.i(TAG, "Saved item: " + product.getName()),
                error -> Log.e(TAG, "Could not save item to API", error));

        Amplify.Storage.uploadFile(
                FileUploadName +"."+ fileUploadExtention.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );

        Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
        toast.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            fileUploadExtention = getContentResolver().getType(uri);

            Log.i(TAG, "onActivityResult: gg is " +fileUploadExtention);
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());
            Log.i(TAG, "onActivityResult:  data => " + data.getType());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

        }
    }

    private void getFileFromDevice() {
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("image/*");
        upload = Intent.createChooser(upload, "Choose a File");
        startActivityForResult(upload, 999); // deprecated
    }

}