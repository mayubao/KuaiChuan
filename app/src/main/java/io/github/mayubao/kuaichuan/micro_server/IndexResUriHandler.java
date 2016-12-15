package io.github.mayubao.kuaichuan.micro_server;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * the index res uri handler
 *
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class IndexResUriHandler implements ResUriHandler {

    private static final String TAG = IndexResUriHandler.class.getSimpleName();

    private Activity mActivity;


    public IndexResUriHandler(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public boolean matches(String uri) {
        if(uri == null || uri.equals("") || uri.equals("/")){
            return true;
        }
        return false;
    }

    @Override
    public void handler(Request request) {
        //1.get the local index.html
        String indexHtml = null;
        try {
            InputStream is = this.mActivity.getAssets().open("index.html");
            indexHtml = IOStreamUtils.inputStreamToString(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //2.send the data to the client like http protocal

        if(request.getUnderlySocket() != null && indexHtml != null){
            OutputStream outputStream = null;
            PrintStream printStream = null;

            try {
                outputStream = request.getUnderlySocket().getOutputStream();
                printStream = new PrintStream(outputStream);
                printStream.println("HTTP/1.1 200 OK");
//                printStream.println("Content-Length:" + indexHtml.length());
                printStream.println("Content-Type:text/html");
                printStream.println("Cache-Control:no-cache");
                printStream.println("Pragma:no-cache");
                printStream.println("Expires:0");
                printStream.println();

                indexHtml = convert(indexHtml);


                byte[] bytes = indexHtml.getBytes("UTF-8");
                printStream.write(bytes);

                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                if(outputStream != null){
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(printStream != null){
                    try {
                        printStream.close();
                        printStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void destroy() {
        this.mActivity = null;
    }

    /**
     * convert html with further proccessing
     * @param indexHtml
     * @return
     */
    public String convert(String indexHtml) {
        return indexHtml;
    }
}
