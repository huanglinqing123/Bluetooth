package hlq.service;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import hlq.base.constant.BltContant;
import hlq.base.manger.BltManager;

/**
 * Created by Huanglinqing on 2018/8/25/025 18:43
 * E-Mail Address：1306214077@qq.com
 * 蓝牙服务端管理类 单例模式
 */
public class BltService {

    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;

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
    private void createBltService() {
        try {
            if (BltManager.getInstance().getmBluetoothAdapter() != null && BltManager.getInstance().getmBluetoothAdapter().isEnabled()) {
                bluetoothServerSocket = BltManager.getInstance().getmBluetoothAdapter().listenUsingRfcommWithServiceRecord("com.bluetooth.demo", BltContant.SPP_UUID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
