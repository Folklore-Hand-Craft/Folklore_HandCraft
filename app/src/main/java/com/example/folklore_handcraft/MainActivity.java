package com.example.folklore_handcraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.AddProduct;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private List<AddProduct> products;
    private ProductAdapter adapter;

    private ProductDao taskDao;

    private Handler handler;

    public static final String TASK_FILE = "taskFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(message -> {
            notifyDataSetChanged();
            return false;
        });

        configureAmplify();

        ProductDataBase database = Room.databaseBuilder(getApplicationContext(), ProductDataBase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.productDao();


        // Add Task Button listener
        findViewById(R.id.goAddProduct).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(MainActivity.this, com.example.folklore_handcraft.AddProduct.class);
            startActivity(goToAddTask);
        });
//        private void listItemDeleted() {
//            adapter.notifyDataSetChanged();
//        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();




        products = new ArrayList<>();
        getTasksDataFromAPI();

//        if (teamName.equals("")) {
//            getTasksDataFromAPI();
//        } else {
//            getTeamTasksFromAPI(teamName);
//        }



        RecyclerView taskRecyclerView = findViewById(R.id.listTask);
        adapter = new ProductAdapter(products, new ProductAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), ProductDetails.class);
                goToDetailsIntent.putExtra("productName", products.get(position).getName());
                goToDetailsIntent.putExtra("productDescription", products.get(position).getDescription());
                goToDetailsIntent.putExtra("productPrice", products.get(position).getPrice());
                goToDetailsIntent.putExtra("productContact", products.get(position).getContact());
                goToDetailsIntent.putExtra(TASK_FILE, products.get(position).getFileName());
                startActivity(goToDetailsIntent);
            }



        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);

    }



    private void configureAmplify() {

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
        } catch (AmplifyException exception) {
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }
    }

    private void getTasksDataFromAPI() {
        Amplify.API.query(ModelQuery.list(AddProduct.class),
                response -> {
                    for (AddProduct product : response.getData()) {
                        products.add(product);
                        Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + product.getName());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

}

