package hlq.service;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import hlq.APP;
import hlq.base.bean.BluRxBean;
import hlq.base.constant.BltContant;
import hlq.base.manger.BltManager;

/**
 * Created by Huanglinqing on 2018/8/25/025 18:43
 * E-Mail Address：1306214077@qq.com
 * 蓝牙服务端管理类 单例模式
 */
public class BltService {

    public static int SERVER_ACCEPT = 11;//回调标记

    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public BluetoothServerSocket getBluetoothServerSocket() {
        return bluetoothServerSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    private BltService() {
        createBltService();
    }

    private static class BlueToothServices {
        private static BltService bltService = new BltService();
    }

    public static BltService getInstance() {
        return BlueToothServices.bltService;
    }

    /**
     * 从蓝牙适配器中创建一个蓝牙服务作为服务端，在获得蓝牙适配器后创建服务器端
     */
    //服务器端的bltsocket需要传入uuid和一个独立存在的字符串，以便验证，通常使用包名的形式
    private void createBltService() {
        try {
            if (BltManager.getInstance().getmBluetoothAdapter() != null) {
                bluetoothServerSocket = BltManager.getInstance().getmBluetoothAdapter().listenUsingRfcommWithServiceRecord("hlq.bluetooth", BltContant.SPP_UUID);
            }
        } catch (IOException e) {
        }
    }

    /**
     * 开启服务端
     */
    public void startBluService() {

        while (true) {
            try {
                if (getBluetoothServerSocket() == null){
                    Log.e("在这里获取的为空","在这里获取的为空");
                }
                bluetoothSocket = getBluetoothServerSocket().accept();
                if (bluetoothSocket != null) {
                    APP.bluetoothSocket = bluetoothSocket;
                    EventBus.getDefault().post(new BluRxBean(SERVER_ACCEPT, bluetoothSocket.getRemoteDevice()));
                    //如果你的蓝牙设备只是一对一的连接，则执行以下代码
                    getBluetoothServerSocket().close();
                    //如果你的蓝牙设备是一对多的，则应该调用break；跳出循环
                    //break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭服务端
     */
    public void cancel() {
        try {
            getBluetoothServerSocket().close();
        } catch (IOException e) {
            Log.e("blueTooth", "关闭服务器socket失败");
        }
    }

}
