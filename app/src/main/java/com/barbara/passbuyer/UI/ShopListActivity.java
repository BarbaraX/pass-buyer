package com.barbara.passbuyer.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.barbara.passbuyer.Utils.ViewHelper;
import com.barbara.passbuyer.Model.Shop;
import com.barbara.passbuyer.Model.ShopResponseReader;
import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.BaseUrls;
import com.barbara.passbuyer.Utils.HttpUtil;

import java.util.List;


public class ShopListActivity extends Activity {

    private ListView mListView;
    private View mProgressView;

    private ArrayAdapter<Shop> mAdapter;
    private List<Shop> mShops;//TODO 随着商户数量增加，List所需内存增加，需要考虑解决办法

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        mListView = (ListView)findViewById(R.id.list_view);
        mProgressView = findViewById(R.id.loading_progress);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //创建并执行商户列表加载后台任务
        new ShopListLoadTask(BaseUrls.SHOP_LIST_URL).execute();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new ShopListLoadTask(BaseUrls.SHOP_LIST_URL).execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retailer_list, menu);
        return true;
    }

    public class ShopListLoadTask extends AsyncTask<Void, Void, List<Shop>> {
        private String mUrl;

        public ShopListLoadTask(String url) {
            this.mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ViewHelper.switchShowedView(ShopListActivity.this, mListView, mProgressView);
        }

        @Override
        protected List<Shop> doInBackground(Void... voids) {
            String respStr = HttpUtil.sendHttpGetRequest(mUrl);
            return ShopResponseReader.readShops(respStr);

        }

        @Override
        protected void onPostExecute(List<Shop> shops) {
            super.onPostExecute(shops);

            mShops = shops;

            ViewHelper.switchShowedView(ShopListActivity.this, mProgressView, mListView);

            mAdapter = new ShopAdapter(ShopListActivity.this, R.layout.shop_list_item, mShops);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Shop shop = mShops.get(i);
                    Intent intent = new Intent(ShopListActivity.this, PassListActivity.class);
                    intent.putExtra("shop", shop);
                    startActivity(intent);
                }
            });

        }

/*
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }*/
    }

}
