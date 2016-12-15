package io.github.mayubao.kuaichuan.micro_server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO Stream Utils
 *
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class IOStreamUtils {

    /**
     * get the string line and the line is end with '\r\n'
     * @param is
     * @return
     * @throws IOException
     */
    public static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int a = 0, b = 0;
        while((b != -1) && !(a == '\r' && b == '\n')){
            a = b;
            b = is.read();
            sb.append((char)(b));
        }

        String line = sb.toString();
//        if(line == null || line.equals(" ")){
//            return null;
//        }

        if(line == null || line.equals("\r\n")){
            return null;
        }

        return line;
    }


    /**
     * convert inputstream to string
     * @param is
     * @return
     */
    public static String inputStreamToString(InputStream is){
        if(is == null){
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] bytes = new byte[2048];
        try {
            while((len = is.read(bytes)) != -1){
                baos.write(bytes, 0, len);
            }

            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
