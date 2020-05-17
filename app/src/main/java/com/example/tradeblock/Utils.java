package com.example.tradeblock;

import android.content.Context;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class Utils {
    public static void getImageAndPlaceInto(String url, ImageView imageView, int px, Context context) {
        Picasso.get()
                .load(url)
                .resize(px, px)
                .centerCrop()
                .into(imageView);
    }
}
