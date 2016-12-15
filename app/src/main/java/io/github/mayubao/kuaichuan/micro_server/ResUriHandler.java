package io.github.mayubao.kuaichuan.micro_server;

/**
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public interface ResUriHandler {

    /**
     * is matches the specify uri
     * @param uri
     * @return
     */
    boolean matches(String uri);


    /**
     * handler the request which matches the uri
     * @param request
     */
    void handler(Request request);

    /**
     * releas some resource when finish the handler
     */
    void destroy();
}
