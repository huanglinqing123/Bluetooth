package hlq;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.util.logging.Logger;

/**
 * Created by  Huanglinqing on 2018/8/24/024.
 * APPlication 类
 * 初始化something
 */

public class APP extends Application {

    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static BluetoothSocket bluetoothSocket;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        com.orhanobut.logger.Logger.init();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
