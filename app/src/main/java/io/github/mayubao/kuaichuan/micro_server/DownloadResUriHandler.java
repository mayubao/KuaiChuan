package io.github.mayubao.kuaichuan.micro_server;

import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;

/**
 * the iamge resource uri handler
 * 1.match the uri like: http://hostname:port/image/xxxx.xx
 *
 * Created by mayubao on 2016/12/15.
 * Contact me 345269374@qq.com
 */
public class DownloadResUriHandler implements ResUriHandler {

    public static final String DOWNLOAD_PREFIX = "/download/";

    private Activity mActivity;


    public DownloadResUriHandler(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(DOWNLOAD_PREFIX);
    }

    @Override
    public void handler(Request request) {
        //1.get the image file name from the uri
        String uri = request.getUri();
        String fileName = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        //bug :resolve chinese incorrect code
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        FileInfo fileInfo = FileUtils.getFileInfo(this.mActivity, fileName);

        //2.check the local system has the file. if has, return the image file, else return 404 to the client
        Socket socket = request.getUnderlySocket();
        OutputStream os = null;
        PrintStream printStream = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(fileInfo == null){//not exist this file
            printStream.println("HTTP/1.1 404 NotFound");
            printStream.println();
        }else{
            printStream.println("HTTP/1.1 200 OK");
//            image/jpeg
            printStream.println("Content-Length:" + fileInfo.getSize());
//            printStream.println("Content-Type:image/png");
//            multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*
            printStream.println("Content-Type:application/octet-stream");
//            printStream.println("Content-Type:multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*");
            printStream.println();


            File file = null;
            FileInputStream fis = null;
            try {
                if(fileName.trim().equals("") || fileName.trim().equals("/")){
                    file = new File(mActivity.getPackageCodePath());
                }else{
                    file = new File(fileInfo.getFilePath());
                }
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            // send the file to the client
            try {
                int len = 0;
                byte[] bytes = new byte[2048];
                while((len = fis.read(bytes)) != -1){
                    printStream.write(bytes, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(fis != null){
                    try {
                        fis.close();
                        fis = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        printStream.flush();
        printStream.close();

    }

    @Override
    public void destroy() {
        this.mActivity = null;
    }

}
