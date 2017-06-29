package com.example.fushuang.lyricview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "11111111111";
    private EditText mEditText;
    private Button mButton;
    private ListView mListView;
    private List<String> mStr;
    private ArrayAdapter<String> mAdapter;
    private List<MusicSearchResult.ShowapiResBodyBean.PagebeanBean.ContentlistBean> mContentlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mEditText = (EditText) findViewById(R.id.search);

        mButton = (Button) findViewById(R.id.button);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        mStr = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStr);

        mListView.setAdapter(mAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitHelper
                        .getInstance()
                        .createApi(QQMusicService.class)
                        .getSearchResult(31490, mEditText.getText().toString(), 1, "274e07e744d7457bbc3e6c60682327e2")
                        .enqueue(new Callback<MusicSearchResult>() {
                            @Override
                            public void onResponse(Call<MusicSearchResult> call, Response<MusicSearchResult> response) {
                                    mStr.clear();
                                mContentlist = response.body().getShowapi_res_body().getPagebean().getContentlist();
                                for (MusicSearchResult.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean : mContentlist) {
                                    mStr.add(bean.getSongname()+"   " +bean.getSingername());
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<MusicSearchResult> call, Throwable t) {
                                Log.d(TAG, "onFailure: ");
                            }
                        });
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MusicSearchResult.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean = mContentlist.get(i);

        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("been",bean);
        startActivity(intent);
    }

}
