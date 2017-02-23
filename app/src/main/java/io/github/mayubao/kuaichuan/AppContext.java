package io.github.mayubao.kuaichuan;

import android.app.Application;

import com.tencent.bugly.Bugly;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.github.mayubao.kuaichuan.core.entity.FileInfo;

/**
 * 全局的Application Context
 *
 * Created by mayubao on 2016/11/25.
 * Contact me 345269374@qq.com
 */
public class AppContext extends Application {

    /**
     * Bugly App Id
     */
    public static final String BUGLY_APP_ID = "fc178c6e5c";

    /**
     * 主要的线程池
     */
    public static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);

    /**
     * 文件发送单线程
     */
    public static Executor FILE_SENDER_EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 全局应用的上下文
     */
    static AppContext mAppContext;


    //文件发送方
    Map<String, FileInfo> mFileInfoMap = new HashMap<String, FileInfo>(); //采用HashMap结构，文件地址--->>>FileInfo 映射结构，重复加入FileInfo

    Map<String, FileInfo> mReceiverFileInfoMap = new HashMap<String, FileInfo>();

    @Override
    public void onCreate() {
        super.onCreate();
        this.mAppContext = this;

        //初始化Bugly
        Bugly.init(getApplicationContext(), BUGLY_APP_ID, true);
    }

    /**
     * 获取全局的AppContext
     * @return
     */
    public static AppContext getAppContext(){
        return mAppContext;
    }

    //==========================================================================
    //==========================================================================
    //发送方
    /**
     * 添加一个FileInfo
     * @param fileInfo
     */
    public void addFileInfo(FileInfo fileInfo){
//        if(!mFileInfoSet.contains(fileInfo)){
//            mFileInfoSet.add(fileInfo);
//        }

        if(!mFileInfoMap.containsKey(fileInfo.getFilePath())){
            mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     * @param fileInfo
     */
    public void updateFileInfo(FileInfo fileInfo){
        mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     * @param fileInfo
     */
    public void delFileInfo(FileInfo fileInfo){
//        if(mFileInfoSet.contains(fileInfo)){
//            mFileInfoSet.remove(fileInfo);
//        }
        if(mFileInfoMap.containsKey(fileInfo.getFilePath())){
            mFileInfoMap.remove(fileInfo.getFilePath());
        }
    }

    /**
     * 是否存在FileInfo
     * @param fileInfo
     * @return
     */
    public boolean isExist(FileInfo fileInfo){
        if(mFileInfoMap == null) return false;
        return mFileInfoMap.containsKey(fileInfo.getFilePath());
    }

    /**
     * 判断文件集合是否有元素
     * @return 有返回true， 反之
     */
    public boolean isFileInfoMapExist(){
        if(mFileInfoMap == null || mFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     * @return
     */
    public Map<String, FileInfo> getFileInfoMap(){
        return mFileInfoMap;
    }

    /**
     * 获取即将发送文件列表的总长度
     * @return
     */
    public long getAllSendFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : mFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }

    //==========================================================================
    //==========================================================================



    //==========================================================================
    //==========================================================================
    //发送方
    /**
     * 添加一个FileInfo
     * @param fileInfo
     */
    public void addReceiverFileInfo(FileInfo fileInfo){
        if(!mReceiverFileInfoMap.containsKey(fileInfo.getFilePath())){
            mReceiverFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     * @param fileInfo
     */
    public void updateReceiverFileInfo(FileInfo fileInfo){
        mReceiverFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     * @param fileInfo
     */
    public void delReceiverFileInfo(FileInfo fileInfo){
        if(mReceiverFileInfoMap.containsKey(fileInfo.getFilePath())){
            mReceiverFileInfoMap.remove(fileInfo.getFilePath());
        }
    }

    /**
     * 是否存在FileInfo
     * @param fileInfo
     * @return
     */
    public boolean isReceiverInfoExist(FileInfo fileInfo){
        if(mReceiverFileInfoMap == null) return false;
        return mReceiverFileInfoMap.containsKey(fileInfo.getFilePath());
    }

    /**
     * 判断文件集合是否有元素
     * @return 有返回true， 反之
     */
    public boolean isReceiverFileInfoMapExist(){
        if(mReceiverFileInfoMap == null || mReceiverFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     * @return
     */
    public Map<String, FileInfo> getReceiverFileInfoMap(){
        return mReceiverFileInfoMap;
    }


    /**
     * 获取即将接收文件列表的总长度
     * @return
     */
    public long getAllReceiverFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : mReceiverFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }

    //==========================================================================
    //==========================================================================
}
