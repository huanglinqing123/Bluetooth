package hlq.base.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by  Huanglinqing on 2018/8/24/024.
 * Eventbus 蓝牙实体类
 */

public class BluRxBean {

    public int id;
    public BluetoothDevice bluetoothDevice;

    public BluRxBean(int id, BluetoothDevice bluetoothDevice) {
        this.id = id;
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluRxBean(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
