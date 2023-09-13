package com.aguadeoro.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;

public class ImagesAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<String> picNames;
    private final int count;

    public ImagesAdapter(Context c, String filename, ArrayList<String> picNames) {
        mContext = c;
        this.picNames = picNames;
        count = picNames.size();
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final String fullPicName = picNames.get(position);
        final String picID = fullPicName.substring(fullPicName.lastIndexOf("_") + 1);
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(700, 700));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setPadding(8, 8, 8, 8);
        //load image
        Options op = new Options();
        op.inPreferredConfig = Config.RGB_565;
        Bitmap myBitmap = BitmapFactory.decodeFile(fullPicName, op);
        imageView.setImageBitmap(myBitmap);
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new DeleteImageTask().execute(picID);
                return true;
            }
        });
        return imageView;
    }

    public class DeleteImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            Query q = new Query("delete from CustomerHistoryPic where PicID = " + args[0]);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(mContext,
                        mContext.getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            Toast.makeText(mContext,
                    mContext.getString(R.string.deleted),
                    Toast.LENGTH_LONG).show();

        }
    }

}