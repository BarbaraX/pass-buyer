package com.barbara.passbuyer.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.barbara.passbuyer.Download.FileDownloadManager;
import com.barbara.passbuyer.Download.PassFile;
import com.barbara.passbuyer.Download.PassFileStorage;
import com.barbara.passbuyer.Model.Pass;
import com.barbara.passbuyer.Model.PassResponseReader;
import com.barbara.passbuyer.Model.Shop;
import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.BaseUrls;
import com.barbara.passbuyer.Utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class PassListActivity extends Activity {

    private Shop mShop;
    private String mPassListUrl;

    //为了让内部类在访问时，编译器不合成相应的static getter、setter方法，声明为package default
    ListView mPassListView;
    PassAdapter mPassAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//需要放在setContentView之前，否则会报错
        setContentView(R.layout.activity_pass_list);

        if (getShopInfo()) {
            initTitleView();
            mPassListUrl = BaseUrls.PASS_LIST_BASE_URL+"?storeID="+mShop.getId();
        } else {
            Log.e("passBuyer", "intent中没有shop信息");
            finish();
        }

        mPassListView = (ListView) findViewById(R.id.list_view);

    }

    /**
     * 从intent中取出shop信息
     * @return
     */
    private boolean getShopInfo() {
        Intent intent = getIntent();
        mShop = (Shop) intent.getSerializableExtra("shop");
        return mShop!=null;
    }

    /**
     * 初始化Shop相关的View
     */
    private void initTitleView(){

        ((TextView) findViewById(R.id.shop_name_view)).setText(mShop.getName());
        ((TextView) findViewById(R.id.shop_tel_view)).setText(mShop.getPhone());
        ((TextView) findViewById(R.id.shop_addr_view)).setText(mShop.getAddress());

        final TextView mShopInfoView = (TextView) findViewById(R.id.shop_info_view);
        mShopInfoView.setText(mShop.getInfo());
/*        mShopInfoView.setOnClickListener(new View.OnClickListener() {
            boolean isEllipsized = true;

            @Override
            public void onClick(View view) {
                if (isEllipsized) {
                    isEllipsized = false;
                    mShopInfoView.setEllipsize(null); // 展开
                    mShopInfoView.setSingleLine(isEllipsized);
                } else {
                    isEllipsized = true;
                    mShopInfoView.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                }
            }
        });*/

        new AsyncImageLoader(this).loadBitmap(mShop.getLogoUrl(), (ImageView) findViewById(R.id.shop_img));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //创建并执行异步任务获得优惠券列表信息，并从sharedPreferences中还原FileDownloadManager中的PassFile对象
        new PassListLoadTask(mPassListUrl, this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("passBuyer", "PassListActiviy onDestroy()");
        FileDownloadManager.getInstance().stopAllDownloadTask();
    }

    /**
     * 为了避免因为PassListLoadTask的生命周期可能大于Activity的生命而内存泄露，
     * 将PassListLoadTask定义成了static的，但是这种情况就会导致Task没有办法访问Activity的非static变量。
     * 解决办法：
     *      让Task维护一个Activity的弱引用；
     *      Task通过弱引用访问Activity的成员
     *      为了避免编译器在内部类在调用外部类的private成员时合成getter跟setter，被访问的成员声明为package default
     */

    /**
     * 下载优惠券列表异步任务
     * 为了避免内存泄露，声明为static
     */
    private static class PassListLoadTask extends AsyncTask<Void, Void, List<Pass>>{
        private String mUrl;
        private WeakReference<PassListActivity> mActivityWeakRef;
        private ListView listView;
        private PassAdapter adapter;

        public PassListLoadTask(String url, PassListActivity activity) {
            this.mUrl = url;
            mActivityWeakRef = new WeakReference<>(activity);
            if (mActivityWeakRef.get()!=null) {
                listView = mActivityWeakRef.get().mPassListView;
                adapter = mActivityWeakRef.get().mPassAdapter;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //隐藏ListView
            if (listView!=null) {
                listView.setVisibility(View.GONE);
            }
        }

        @Override
        protected List<Pass> doInBackground(Void... voids) {
            String respStr = HttpUtil.sendHttpGetRequest(mUrl);
            Log.i("passBuyer", respStr);
            return PassResponseReader.readPasses(respStr);
        }

        @Override
        protected void onPostExecute(List<Pass> passes) {
            super.onPostExecute(passes);

            //从sharedPreferences中还原FileDownloadManager中的PassFile，放到FileDownloadManager中
            // 需要写成异步任务吗？
            FileDownloadManager downloadManager = FileDownloadManager.getInstance();
            for (Pass pass:passes) {
                String url = pass.getDownloadUrl();
                if (PassFileStorage.hasPassFileOfUrl(url)) {
                    int id = passes.indexOf(pass);
                    PassFile file = PassFileStorage.getPassFileOfUrl(url);
                    Log.i("passBuyer", file.toString());
                    downloadManager.addToDownload(id, file);
                }
            }

            //只能在这里设置adapter，如果在onCreate中设置，pass列表为空
            if (mActivityWeakRef.get()!=null) {
                adapter = new PassAdapter(mActivityWeakRef.get(), R.layout.pass_list_item, passes);
                adapter.setAttachedListView(mActivityWeakRef.get().mPassListView);
                listView.setAdapter(adapter);
            }


            //显示ListView
            listView.setVisibility(View.VISIBLE);
        }
    }
}
