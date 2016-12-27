package io.github.mayubao.kuaichuan.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.mayubao.kuaichuan.AppContext;
import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.core.utils.ToastUtils;
import io.github.mayubao.kuaichuan.ui.ChooseFileActivity;
import io.github.mayubao.kuaichuan.ui.adapter.FileInfoAdapter;
import io.github.mayubao.kuaichuan.utils.AnimationUtils;

/**
 * Apk列表Fragment
 *
 * Created by mayubao on 2016/11/24.
 * Contact me 345269374@qq.com
 */
public class FileInfoFragment extends Fragment {

    @Bind(R.id.gv)
    GridView gv;
    @Bind(R.id.pb)
    ProgressBar pb;

    private int mType = FileInfo.TYPE_APK;
    private List<FileInfo> mFileInfoList;
    private FileInfoAdapter mFileInfoAdapter;

    @SuppressLint("ValidFragment")
    public FileInfoFragment(){
        super();
    }

    @SuppressLint("ValidFragment")
    public FileInfoFragment(int type) {
        super();
        this.mType = type;
    }

    public static FileInfoFragment newInstance(int type) {
        FileInfoFragment fragment = new FileInfoFragment(type);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apk, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, rootView);

        if(mType == FileInfo.TYPE_APK){ //应用
            gv.setNumColumns(4);
        }else if(mType == FileInfo.TYPE_JPG){ //图片
            gv.setNumColumns(3);
        }else if(mType == FileInfo.TYPE_MP3){ //音乐
            gv.setNumColumns(1);
        }else if(mType == FileInfo.TYPE_MP4){ //视频
            gv.setNumColumns(1);
        }

        //Android6.0 requires android.permission.READ_EXTERNAL_STORAGE
        init();//初始化界面

        return rootView;
    }

    private void init() {
        if(mType == FileInfo.TYPE_APK){
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_APK).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_JPG){
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_JPG).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        } else if (mType == FileInfo.TYPE_MP3) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_MP3).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        } else if (mType == FileInfo.TYPE_MP4) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_MP4).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mFileInfoList.get(position);
                if (AppContext.getAppContext().isExist(fileInfo)) {
                    AppContext.getAppContext().delFileInfo(fileInfo);
                    updateSelectedView();
                } else {
                    //1.添加任务
                    AppContext.getAppContext().addFileInfo(fileInfo);
                    //2.添加任务 动画
                    View startView = null;
                    View targetView = null;

                    startView = view.findViewById(R.id.iv_shortcut);
                    if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
                        ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
                        targetView = chooseFileActivity.getSelectedView();
                    }
                    AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
                }

                mFileInfoAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        updateFileInfoAdapter();
        super.onResume();
    }

    /**
     * 更新FileInfoAdapter
     */
    public void updateFileInfoAdapter(){
        if(mFileInfoAdapter != null){
            mFileInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新ChoooseActivity选中View
     */
    private void updateSelectedView(){
        if(getActivity() != null && (getActivity() instanceof ChooseFileActivity)){
            ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
            chooseFileActivity.getSelectedView();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 显示进度
     */
    public void showProgressBar(){
        if(pb != null) {
         pb.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏进度
     */
    public void hideProgressBar(){
        if(pb != null && pb.isShown()) {
            pb.setVisibility(View.GONE);
        }
    }


    /**
     * 获取ApkInfo列表任务
     */
    class GetFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>>{
        Context sContext = null;
        int sType = FileInfo.TYPE_APK;
        List<FileInfo> sFileInfoList = null;

        public GetFileInfoListTask(Context sContext, int type) {
            this.sContext = sContext;
            this.sType = type;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(String... params) {
            //FileUtils.getSpecificTypeFiles 只获取FileInfo的属性 filePath与size
            if(sType == FileInfo.TYPE_APK){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{ FileInfo.EXTEND_APK});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_APK);
            }else if(sType == FileInfo.TYPE_JPG){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{ FileInfo.EXTEND_JPG, FileInfo.EXTEND_JPEG});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_JPG);
            }else if(sType == FileInfo.TYPE_MP3){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{ FileInfo.EXTEND_MP3});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_MP3);
            }else if(sType == FileInfo.TYPE_MP4){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{ FileInfo.EXTEND_MP4});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_MP4);
            }

            mFileInfoList = sFileInfoList;

            return sFileInfoList;
        }


        @Override
        protected void onPostExecute(List<FileInfo> list) {
            hideProgressBar();
            if(sFileInfoList != null && sFileInfoList.size() > 0){
                if(mType == FileInfo.TYPE_APK){ //应用
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_APK);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_JPG){ //图片
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_JPG);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP3){ //音乐
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_MP3);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP4){ //视频
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_MP4);
                    gv.setAdapter(mFileInfoAdapter);
                }
            }else{
                ToastUtils.show(sContext, sContext.getResources().getString(R.string.tip_has_no_apk_info));
            }
        }
    }

}
