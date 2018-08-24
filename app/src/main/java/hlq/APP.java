package hlq;

import android.app.Application;
import android.content.Context;

/**
 * Created by  Huanglinqing on 2018/8/24/024.
 * APPlication 类
 * 初始化something
 */

public class APP extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
