package io.github.mayubao.kuaichuan.core;

/**
 * Created by mayubao on 2016/11/10.
 * Contact me 345269374@qq.com
 */
public interface Transferable {



    /**
     *
     * @throws Exception
     */
    void init() throws Exception;


    /**
     *
     * @throws Exception
     */
    void parseHeader() throws Exception;


    /**
     *
     * @throws Exception
     */
    void parseBody() throws Exception;


    /**
     *
     * @throws Exception
     */
    void finish() throws Exception;
}
