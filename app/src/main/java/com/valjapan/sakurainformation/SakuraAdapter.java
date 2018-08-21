package com.valjapan.sakurainformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBObject;

import java.util.List;

public class SakuraAdapter extends ArrayAdapter<NCMBObject> {

    static class ViewHolder {
        ImageView image;
        TextView name;
        ImageView like;
        TextView likeData;
    }

    public SakuraAdapter(Context context, int resource, List<NCMBObject> objects) {
        super(context, resource, objects);
    }

    private Bitmap getImage(String IMAGE_FILENAME) {

        final NCMBFile file = new NCMBFile(IMAGE_FILENAME);
        Bitmap btm = null;

        try {
//            画像ダウンロード
            file.fetch();
            btm = BitmapFactory.decodeByteArray(file.getFileData(), 0, file.getFileData().length);

        } catch (NCMBException error) {
//            ダウンロード失敗時の処理
            NCMBError(error);

        }
        return btm;
    }

    //    like数の送信
    private void addLike(String objectID, String likeNumber) {
//        初期化
        NCMB.initialize(getContext(), MainActivity.APPLICATAON_KEY, MainActivity.CLIENT_KEY);
        NCMBObject obj = new NCMBObject("SakuraClass");

//        ObjectIDの設定
        obj.setObjectId(objectID);

//        Like数の登録
        obj.put("likeData", likeNumber);

//        Like数の送信
        try {
            obj.save();
        } catch (NCMBException error) {
            NCMBError(error);
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SakuraAdapter.ViewHolder viewHolder;

//        convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.like = (ImageView) convertView.findViewById(R.id.like);
            viewHolder.likeData = (TextView) convertView.findViewById(R.id.likeData);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final NCMBObject item = getItem(position);

        viewHolder.image.setImageBitmap(getImage(item.getString("imageName")));
        viewHolder.name.setText(item.getString("name"));
        viewHolder.likeData.setText(item.getString("likeData"));

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Likeを押した時のLike数の変更
                int Like = Integer.parseInt(viewHolder.likeData.getText().toString()) + 1;
                viewHolder.likeData.setText(String.valueOf(Like));

//                Like数の送信
                addLike(getItem(position).getObjectId(), String.valueOf(Like));

//                Likeを押した時のアニメーション
                AlphaAnimation alpha = new AlphaAnimation(1, 0);
                alpha.setDuration(500);
                viewHolder.like.startAnimation(alpha);

            }
        });
        return convertView;
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
