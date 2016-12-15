package io.github.mayubao.kuaichuan.micro_server;

import java.net.Socket;
import java.util.HashMap;

/**
 * the request object include < uri,header and socket (which get from serversocket.accept() ) >
 *
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class Request {

    private String mUri;
    private HashMap<String, String> mHeaderMap = new HashMap<String, String>();
    private Socket mUnderlySocket;


    public Request(){

    }

    //================ Getter and Setter  start=======
    public Socket getUnderlySocket() {
        return mUnderlySocket;
    }

    public void setUnderlySocket(Socket mUnderlySocket) {
        this.mUnderlySocket = mUnderlySocket;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }
    //================ Getter and Setter  end=======

    /**
     * add the header
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value){
        this.mHeaderMap.put(key, value);
    }
}
