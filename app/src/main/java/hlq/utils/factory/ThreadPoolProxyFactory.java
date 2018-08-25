package hlq.utils.factory;

/**
 * Created by Huanglinqing on 2018/8/25/025 18:26
 * E-Mail Address：1306214077@qq.com
 */
public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ThreadPoolProxy mDownLoadThreadPoolProxy;

    /**
     * 得到普通线程池代理对象mNormalThreadPoolProxy
     */
    public static ThreadPoolProxy getNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    /**
     * 得到下载线程池代理对象mDownLoadThreadPoolProxy
     */
    public static ThreadPoolProxy getDownLoadThreadPoolProxy() {
        if (mDownLoadThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mDownLoadThreadPoolProxy == null) {
                    mDownLoadThreadPoolProxy = new ThreadPoolProxy(3, 3);
                }
            }
        }
        return mDownLoadThreadPoolProxy;
    }
}

