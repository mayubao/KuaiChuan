package io.github.mayubao.kuaichuan.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.core.utils.MLog;
import io.github.mayubao.kuaichuan.core.utils.TimeUtils;

/**
 * Created by mayubao on 2016/11/10.
 * Contact me 345269374@qq.com
 */
public class FileReceiver extends BaseTransfer implements Runnable {

    private static final String TAG = FileReceiver.class.getSimpleName();

    /**
     * Socket的输入输出流
     */
    private Socket mSocket;
    private InputStream mInputStream;

    /**
     * 传送文件的信息
     */
    private FileInfo mFileInfo;

    /**
     * 控制线程暂停 恢复
     */
    private final Object LOCK = new Object();
    boolean mIsPaused = false;

    /**
     * 文件接收的监听
     */
    OnReceiveListener mOnReceiveListener;


    public FileReceiver(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public void setOnReceiveListener(OnReceiveListener mOnReceiveListener) {
        this.mOnReceiveListener = mOnReceiveListener;
    }

    @Override
    public void run() {
        //初始化
        try {
            if(mOnReceiveListener != null) mOnReceiveListener.onStart();
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MLog.i(TAG, "FileReceiver init() --->>> occur expection");
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //解析头部
        try {
            parseHeader();
        } catch (Exception e) {
            e.printStackTrace();
            MLog.i(TAG, "FileReceiver parseHeader() --->>> occur expection");
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }


        //解析主体
        try {
            parseBody();
        } catch (Exception e) {
            e.printStackTrace();
            MLog.i(TAG, "FileReceiver parseBody() --->>> occur expection");
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //结束
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MLog.i(TAG, "FileReceiver finish() --->>> occur expection");
            if(mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }


    }

    @Override
    public void init() throws Exception{
        if(this.mSocket != null){
            this.mInputStream = mSocket.getInputStream();
        }
    }

    @Override
    public void parseHeader() throws IOException {
        MLog.i(TAG, "parseHeader######>>>start");

        //Are you sure can read the 1024 byte accurately?
        //读取header部分
        byte[] headerBytes = new byte[BYTE_SIZE_HEADER];
        int headTotal = 0;
        int readByte = -1;
        //开始读取header
        while((readByte = mInputStream.read()) != -1){
            headerBytes[headTotal] = (byte) readByte;

            headTotal ++;
            if(headTotal == headerBytes.length){
                break;
            }
        }
        MLog.i(TAG, "FileReceiver receive header size------>>>" + headTotal);
        MLog.i(TAG, "FileReceiver receive header------>>>" + new String(headerBytes, UTF_8));


        //读取缩略图部分
        byte[] screenshotBytes = new byte[BYTE_SIZE_SCREENSHOT];
        int screenshotTotal = 0;
        int sreadByte = -1;
        //开始读取缩略图
        while((sreadByte = mInputStream.read()) != -1){
            screenshotBytes[screenshotTotal] = (byte) sreadByte;

            screenshotTotal ++;
            if(screenshotTotal == screenshotBytes.length){
                break;
            }
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(screenshotBytes, 0, screenshotBytes.length);
        if(mOnReceiveListener != null)mOnReceiveListener.onGetScreenshot(bitmap);

        MLog.i(TAG, "FileReceiver receive screenshot size------>>>" + screenshotTotal);
//        MLog.i(TAG, "FileReceiver receive screenshot------>>>" + new String(headerBytes, UTF_8));


        //解析header
        String jsonStr = new String(headerBytes, UTF_8);
        String[] strArray = jsonStr.split(SPERATOR);
        jsonStr = strArray[1].trim();
        mFileInfo = FileInfo.toObject(jsonStr);
        mFileInfo.setBitmap(bitmap);
        if(mOnReceiveListener != null) mOnReceiveListener.onGetFileInfo(mFileInfo);
//        String fileName = getFileName(fileInfo.getFilePath());
//        int fileSize = fileInfo.getSize();
        MLog.i(TAG, "parseHeader######>>>end");
    }

    @Override
    public void parseBody() throws Exception {
        MLog.i(TAG, "parseBody######>>>start");

        //写入文件
        long fileSize = mFileInfo.getSize();
        OutputStream bos = new FileOutputStream(FileUtils.gerateLocalFile(mFileInfo.getFilePath()));

        //记录文件开始写入时间
        long startTime = System.currentTimeMillis();

        byte[] bytes = new byte[BYTE_SIZE_DATA];
        long total = 0;
        int len = 0;

        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while((len=mInputStream.read(bytes)) != -1){
            synchronized(LOCK) {
                if (mIsPaused) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                bos.write(bytes, 0, len);
                total = total + len;
                eTime = System.currentTimeMillis();
                if(eTime - sTime > 200) { //大于500ms 才进行一次监听
                    sTime = eTime;
                    if(mOnReceiveListener != null) mOnReceiveListener.onProgress(total, fileSize);
                }
            }

//            bos.write(bytes, 0, len);
//            total = total + len;
//            if(mOnReceiveListener != null) mOnReceiveListener.onProgress(total, fileSize);
        }
        //记录文件结束写入时间
        long endTime = System.currentTimeMillis();

        MLog.i(TAG, "FileReceiver body receive######>>>" + (TimeUtils.formatTime(endTime - startTime)));
        MLog.i(TAG, "FileReceiver body receive######>>>" + total);

        MLog.i(TAG, "parseBody######>>>end");

        if(mOnReceiveListener != null) mOnReceiveListener.onSuccess(mFileInfo);
    }

    @Override
    public void finish() {
        //TODO 实现一些资源的关闭

        if(mInputStream != null){
            try {
                mInputStream.close();
            } catch (IOException e) {

            }
        }

        if(mSocket != null && mSocket.isConnected()){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        MLog.i(TAG, "FileReceiver close socket######>>>");
    }

    /**
     * 停止线程下载
     */
    public void pause() {
        synchronized(LOCK) {
            mIsPaused = true;
            LOCK.notifyAll();
        }
    }

    /**
     * 重新开始线程下载
     */
    public void resume() {
        synchronized(LOCK) {
            mIsPaused = false;
            LOCK.notifyAll();
        }
    }

    /**
     * 文件接收的监听
     */
    public interface OnReceiveListener{
        void onStart();
        void onGetFileInfo(FileInfo fileInfo);
        void onGetScreenshot(Bitmap bitmap);
        void onProgress(long progress, long total);
        void onSuccess(FileInfo fileInfo);
        void onFailure(Throwable t, FileInfo fileInfo);
    }

}
