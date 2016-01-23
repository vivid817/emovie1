package j2.com.emovie.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> mobileValues;

    public ImageAdapter(Context context,ArrayList<String> mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        View gridView;
        if (convertView == null) {
            imageView = new ImageView(context);

            String imgUrl = mobileValues.get(position);
            Picasso.with(context).load(imgUrl).into(imageView);
        } else {
            imageView = (ImageView) convertView;
        }
        String imgUrl = mobileValues.get(position);
        Picasso.with(context).load(imgUrl).into(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return mobileValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

