package com.valjapan.sakurainformation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SakuraAdapter adapter;
    ListView listView;

    static final String APPLICATAON_KEY = "3a4cc58db19cc70dc7cb0a0bc2db7c7ca72f7bf85c70c697ab0595b9ac2bb406";
    static final String CLIENT_KEY = "8bf5c3328e4f1bd8d4154621774aac66f7033b93533683cabff51f9982b4e16a";
    static final String ORIGINAL_TEXT = "EVzRQBXjAeXzQFyhggqS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        初期化
        NCMB.initialize(this, APPLICATAON_KEY, CLIENT_KEY);
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("SakuraClass");

//        dataというフィールドがsakura・ORIZINAL_TEXTとなってるデータを検索する条件を設定
        query.whereContainedInArray("data", Arrays.asList("sakura", ORIGINAL_TEXT));

        try {
//            データすろあからデータを検索
            List<NCMBObject> NCMBObjects = query.find();
            adapter = new SakuraAdapter(this, R.layout.custom_list_layout, NCMBObjects);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

        } catch (NCMBException error) {
//            検索失敗時の処理
            NCMBError(error);
        }

//        String originalText = RandomStringUtils.randomAlphabetic(20);
//        Log.e("originalText",originalText);
    }

    //    エラー処理
    private void NCMBError(NCMBException error) {

        StringBuilder sb = new StringBuilder("【Failure】 \n");
        if (error.getCode() != null) {
            sb.append("StatusCode : ").append(error.getCode()).append("\n");
        }
        if (error.getMessage() != null) {
            sb.append("Message : ").append(error.getMessage()).append("\n");
        }
        Log.e("error", sb.toString());
    }

}

