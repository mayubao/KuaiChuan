package io.github.mayubao.kuaichuan.ui;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.mayubao.kuaichuan.AppContext;
import io.github.mayubao.kuaichuan.Constant;
import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.common.BaseActivity;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.receiver.SeletedFileListChangedBroadcastReceiver;
import io.github.mayubao.kuaichuan.core.utils.ToastUtils;
import io.github.mayubao.kuaichuan.ui.fragment.FileInfoFragment;
import io.github.mayubao.kuaichuan.ui.view.ShowSelectedFileInfoDialog;
import io.github.mayubao.kuaichuan.utils.NavigatorUtils;

public class ChooseFileActivity extends BaseActivity {


    /**
     * 获取文件的请求码
     */
    public static final int  REQUEST_CODE_GET_FILE_INFOS = 200;

    /**
     * Topbar相关UI
     */
    @Bind(R.id.tv_back)
    TextView tv_back;
    @Bind(R.id.iv_search)
    ImageView iv_search;
    @Bind(R.id.search_view)
    SearchView search_view;
    @Bind(R.id.tv_title)
    TextView tv_title;

    /**
     * BottomBar相关UI
     */
    @Bind(R.id.btn_selected)
    Button btn_selected;
    @Bind(R.id.btn_next)
    Button btn_next;

    /**
     * 其他UI
     */
    @Bind(R.id.tab_layout)
    TabLayout tab_layout;
    @Bind(R.id.view_pager)
    ViewPager view_pager;


    /**
     * 应用，图片，音频， 视频 文件Fragment
     */
    FileInfoFragment mCurrentFragment;
    FileInfoFragment mApkInfoFragment;
    FileInfoFragment mJpgInfoFragment;
    FileInfoFragment mMp3InfoFragment;
    FileInfoFragment mMp4InfoFragment;

    /**
     * 选中文件列表的对话框
     */
    ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;

    /**
     * 更新文件列表的广播
     */
    SeletedFileListChangedBroadcastReceiver mSeletedFileListChangedBroadcastReceiver = null;


    /**
     * 网页传标识
     */
    private boolean mIsWebTransfer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onDestroy() {
        if(mSeletedFileListChangedBroadcastReceiver != null){
            unregisterReceiver(mSeletedFileListChangedBroadcastReceiver);
            mSeletedFileListChangedBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    /**
     * 初始化
     */
    private void init(){
        tv_title.setText(getResources().getString(R.string.title_choose_file));
        tv_title.setVisibility(View.VISIBLE);

        iv_search.setVisibility(View.INVISIBLE);

        search_view.setVisibility(View.GONE);

        mIsWebTransfer = getIntent().getBooleanExtra(Constant.KEY_WEB_TRANSFER_FLAG, false);

        //Android6.0 requires android.permission.READ_EXTERNAL_STORAGE
        //TODO
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GET_FILE_INFOS);
        }else{
            initData();//初始化数据
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_GET_FILE_INFOS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                // Permission Denied
                ToastUtils.show(this, getResources().getString(R.string.tip_permission_denied_and_not_get_file_info_list));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mApkInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(), titles));
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                <item>应用</item>
//                <item>图片</item>
//                <item>音乐</item>
//                <item>视频</item>
                if (position == 0) { //应用

                } else if (position == 1) { //图片

                } else if (position == 2) { //音乐

                } else if (position == 3) { //视频

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        view_pager.setOffscreenPageLimit(4);

        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

        setSelectedViewStyle(false);

        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(getContext());

        mSeletedFileListChangedBroadcastReceiver = new SeletedFileListChangedBroadcastReceiver() {
            @Override
            public void onSeletecdFileListChanged() {
                //TODO udpate file list
                update();
                Log.i(TAG, "======>>>udpate file list");
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SeletedFileListChangedBroadcastReceiver.ACTION_CHOOSE_FILE_LIST_CHANGED);
        registerReceiver(mSeletedFileListChangedBroadcastReceiver, intentFilter);
    }

    /**
     * 更新选中文件列表的状态
     */
    private void update(){
        if(mApkInfoFragment != null) mApkInfoFragment.updateFileInfoAdapter();
        if(mJpgInfoFragment != null) mJpgInfoFragment.updateFileInfoAdapter();
        if(mMp3InfoFragment != null) mMp3InfoFragment.updateFileInfoAdapter();
        if(mMp4InfoFragment != null) mMp4InfoFragment.updateFileInfoAdapter();

        //更新已选中Button
        getSelectedView();
    }

    @OnClick({R.id.tv_back, R.id.btn_selected, R.id.btn_next, R.id.iv_search})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_back:{
                this.finish();
                break;
            }
            case R.id.btn_selected:{
//                btn_selected.setEnabled(false);
//                new ShowSelectedFileInfoDialog(getContext()).show();
                if(mShowSelectedFileInfoDialog != null){
                    mShowSelectedFileInfoDialog.show();
                }
                break;
            }
            case R.id.btn_next:{
//                btn_selected.setEnabled(false);
//                btn_selected.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
//                btn_selected.setTextColor(getResources().getColor(R.color.darker_gray));
                if(!AppContext.getAppContext().isFileInfoMapExist()){//不存在选中的文件
                    ToastUtils.show(getContext(), getContext().getString(R.string.tip_please_select_your_file));
                    return;
                }

                if(mIsWebTransfer){ //跳转到网页传
                    NavigatorUtils.toWebTransferUI(getContext());
                    return;
                }
                //跳转到应用间传输
                NavigatorUtils.toChooseReceiverUI(getContext());
                break;
            }

            case R.id.iv_search:{
                btn_selected.setEnabled(true);
                btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
                btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
        }
    }

    /**
     * 获取选中文件的View
     * @return
     */
    public View getSelectedView(){
        //获取SelectedView的时候 触发选择文件
        if(AppContext.getAppContext().getFileInfoMap() != null && AppContext.getAppContext().getFileInfoMap().size() > 0 ){
            setSelectedViewStyle(true);
            int size = AppContext.getAppContext().getFileInfoMap().size();
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected_detail, size));
        }else{
            setSelectedViewStyle(false);
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected));
        }
        return btn_selected;
    }

    /**
     * 设置选中View的样式
     * @param isEnable
     */
    private void setSelectedViewStyle(boolean isEnable){
        if(isEnable){
            btn_selected.setEnabled(true);
            btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
            btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            btn_selected.setEnabled(false);
            btn_selected.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
            btn_selected.setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter {
        String[] sTitleArray;

        public ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] sTitleArray) {
            this(fm);
            this.sTitleArray = sTitleArray;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){ //应用
                mCurrentFragment = mApkInfoFragment;
            }else if(position == 1){ //图片
                mCurrentFragment = mJpgInfoFragment;
            }else if(position == 2){ //音乐
                mCurrentFragment = mMp3InfoFragment;
            }else if(position == 3){ //视频
                mCurrentFragment = mMp4InfoFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return sTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sTitleArray[position];
        }
    }

}
