package com.barbara.passbuyer.UI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.barbara.passbuyer.Model.Shop;
import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.HttpUtil;
import com.barbara.passbuyer.Utils.ImageCache;
import com.barbara.passbuyer.Utils.MD5;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by barbara on 6/26/15.
 */
public class ShopAdapter extends ArrayAdapter<Shop> {

    private int mResouceId;
    private Context mContext;
    private AsyncImageLoader mImageLoader;

    public ShopAdapter(Context context, int resource, List<Shop> objects) {
        super(context, resource, objects);
        mResouceId = resource;
        mContext = context;
        mImageLoader = new AsyncImageLoader(mContext);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Shop shop = getItem(position);//getItem取出objects链表中的第position个元素
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResouceId, null);

            viewHolder = new ViewHolder();

            viewHolder.shopName = (TextView) view.findViewById(R.id.shop_name_view);
            viewHolder.shopTel = (TextView) view.findViewById(R.id.shop_tel_view);
            viewHolder.shopAddr = (TextView) view.findViewById(R.id.shop_addr_view);
            viewHolder.shopInfo = (TextView) view.findViewById(R.id.shop_info_view);
            viewHolder.shopLogo = (ImageView) view.findViewById(R.id.shop_img);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.shopName.setText(shop.getName());
        viewHolder.shopTel.setText(shop.getPhone());
        viewHolder.shopAddr.setText(shop.getAddress());
        viewHolder.shopInfo.setText(shop.getInfo());

        //异步加载图片
        mImageLoader.loadBitmap(shop.getLogoUrl(), viewHolder.shopLogo);

        return view;
    }

    class ViewHolder {
        ImageView shopLogo;
        TextView shopName;
        TextView shopTel;
        TextView shopAddr;
        TextView shopInfo;
    }

}
