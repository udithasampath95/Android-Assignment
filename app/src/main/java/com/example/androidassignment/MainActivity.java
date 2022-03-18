package com.example.androidassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidassignment.adapter.UserAdapter;
import com.example.androidassignment.model.Data;
import com.example.androidassignment.model.User;
import com.example.androidassignment.network.APIService;
import com.example.androidassignment.network.RetrofitInstance;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, UserAdapter.OnLoadMoreListener {
    private static final String TAG = "MainActivity ";
    private SearchView nameSearchView;
    private RecyclerView imageRecycleView;
    private UserAdapter userAdapter;
    private Button doneBtn;
    private String inputValue = "";
    private int index = 1;
    private ArrayList<Data> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListners();

    }

    private void setListners() {
        imageRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager llManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (userAdapter.getItemCount() - 1)) {
                    userAdapter.showLoading();
                }
            }
        });
        nameSearchView.setOnQueryTextListener(this);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputValue == null || inputValue.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Text", Toast.LENGTH_SHORT).show();
                } else {
                    if(isOnline()){
                        startService(null, index);
                    }else{
                        Toast.makeText(MainActivity.this, "App Need Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void startService(String s, int index) {
        Log.i(TAG, "CALL_GET_USER_RESULT");
        APIService getNoticeDataService = RetrofitInstance.getRetroClient().create(APIService.class);
        Call<User> getUsers = getNoticeDataService.getUserList(index);
        getUsers.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null) {
                    if (response.body() == null) {
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_BODY_NULL");
                        Toast.makeText(MainActivity.this, "Server temporary down!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_" + response.code());
                        if (response.code() == 200) {
                            if (s == null) {
                                Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_VALUE" + response.body().toString());
                                User userDataRespo = response.body();
                                userAdapter = new UserAdapter(MainActivity.this, userDataRespo.getData(), MainActivity.this);
                                imageRecycleView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                                imageRecycleView.setAdapter(userAdapter);
                            } else {
                                ArrayList<Data> userResponsesTempArray = new ArrayList<>();
                                for (int i = 0; i < response.body().getData().size(); i++) {
                                    userResponsesTempArray.add(response.body().getData().get(i));
                                }
                                list = new ArrayList<>();
                                if (userResponsesTempArray != null) {
                                    for (int j = 0; j < userResponsesTempArray.size(); j++) {
                                        list.add(userResponsesTempArray.get(j));
                                    }
                                    userAdapter.notifyItemInserted(response.body().getData().size());
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            userAdapter.dismissLoading();
                                            userAdapter.addItemMore(list);
                                            userAdapter.setMore(true);
                                        }
                                    }, 1000);
                                }
                            }
                        }
                    }

                } else {
                    Log.i(TAG, "CALL_GET_USER_RESULT_ON_RESPONSE_INVALID_REQUEST");
                    Toast.makeText(MainActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i(TAG, "CALL_GET_USER_RESULT_FAILURE_" + t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        doneBtn = findViewById(R.id.doneBtn);
        nameSearchView = findViewById(R.id.nameSearchView);
        imageRecycleView = findViewById(R.id.imageRecycleView);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null) {
            inputValue = "";
        } else {
            inputValue = newText;
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        index = index + 1;
        Log.i(TAG, "CALL_LOAD_MORE_" + index);
        startService("refresh", index);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}