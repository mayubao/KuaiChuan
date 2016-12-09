package io.github.mayubao.kuaichuan.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.utils.NavigatorUtils;

/**
 * Created by mayubao on 2016/11/28.
 * Contact me 345269374@qq.com
 */
public class MainActivity extends AppCompatActivity{

    RecyclerView rv;

    String[] strArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });





        StringBuilder sb = new StringBuilder();
        String str = " Hello world ！#";
        for(int i=0; i < 30; i++){
            sb.append(str);
        }

        strArray = sb.toString().split("#");

        rv = (RecyclerView) this.findViewById(R.id.rv);
        // 设置布局显示方式，这里我使用都是垂直方式——LinearLayoutManager.VERTICAL
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        // 设置添加删除item的时候的动画效果
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new MyRecycleViewAdapter());

    }

    public Context getContext(){
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_client) {
//            Intent intent = new Intent(getContext(), ClientActivity.class);
//            startActivity(intent);
            NavigatorUtils.toChooseFileUI(getContext());
            return true;
        }else if (id == R.id.action_server) {
//            Intent intent = new Intent(getContext(), ServerActivity.class);
//            startActivity(intent);
            NavigatorUtils.toReceiverWaitingUI(getContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View contentView = View.inflate(getContext(), R.layout.item_transfer, null);
            return new MyViewHolder(contentView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return strArray.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
