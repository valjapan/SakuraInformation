package com.valjapan.sakurainformation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBAcl;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.io.ByteArrayOutputStream;
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

        //初期化
        NCMB.initialize(this, APPLICATAON_KEY, CLIENT_KEY);
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("SakuraClass");


        //dataというフィールドがsakuraとなっているデータを検索する条件を設定
//      query.whereContainedInArray("data", Arrays.asList("sakura",ORIGINAL_TEXT));

        query.whereContainedInArray("data", Arrays.asList("eXbpFECaEavbYKUxVovx", ORIGINAL_TEXT));


        try {
            //データストアからデータを検索
            List<NCMBObject> NCMBObjects = query.find();
            adapter = new SakuraAdapter(this, R.layout.custom_list_layout, NCMBObjects);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
        } catch (NCMBException error) {
            //検索失敗時の処理
            NCMBError(error);
        }

        //表示データの追加方法
        set("千葉工業大学", "image.jpg", R.drawable.image);

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

    //表示データの追加
    public void set(String placeName, String fileName, int imageID) {
        try {
            //初期化
            NCMB.initialize(this, APPLICATAON_KEY, CLIENT_KEY);
            NCMBObject obj = new NCMBObject("SakuraClass");

            //データの追加
            obj.put("data", "sakura");
            obj.put("likeData", "0");
            obj.put("name", placeName);
            obj.put("imageName", fileName);
            obj.save();

            //画像をサーバーに送信
            upImage(imageID, fileName);
        } catch (Exception error) {

            //追加失敗時の処理
            NCMBError(new NCMBException(error));
        }
    }

    //画像の送信
    public void upImage(int imageID, String fileName) {

        //画像データ取得
        Bitmap image = BitmapFactory.decodeResource(getResources(), imageID);
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream);
        byte[] data = byteArrayStream.toByteArray();

        //ACL 読み込み:可 , 書き込み:不可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(false);

        //画像の送信
        final NCMBFile file = new NCMBFile(fileName, data, acl);
        file.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException error) {
                if (error != null) {
                    //送信失敗時の処理
                    NCMBError(error);
                }

            }
        });
    }

}

