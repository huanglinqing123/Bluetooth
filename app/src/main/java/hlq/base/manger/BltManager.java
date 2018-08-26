package hlq.base.manger;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import hlq.APP;
import hlq.base.constant.BltContant;

/**
 * Created by Huanglinqing on 2018/8/25.  项目中用  demo不使用
 * 蓝牙对象管理器
 * 蓝牙4.0 必须在api18 android4.3以上才能运行
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BltManager {
    /**
     * 设置成单例模式
     */
    private BltManager() {
    }

    private static class BltManagers {
        private static BltManager bltManager = new BltManager();
    }

    public static BltManager getInstance() {
        return BltManagers.bltManager;
    }


    /**
     * 蓝牙管理器
     */
    private BluetoothManager bluetoothManager;
    /**
     * 蓝牙适配器
     * BluetoothAdapter是Android系统中所有蓝牙操作都需要的，
     * 它对应本地Android设备的蓝牙模块，
     * 在整个系统中BluetoothAdapter是单例的。
     * 当你获取到它的实例之后，就能进行相关的蓝牙操作了。
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 配对成功后的蓝牙套接字
     */
    private BluetoothSocket mBluetoothSocket;

    /**
     * 蓝牙状态接口
     */
    private OnRegisterBltReceiver onRegisterBltReceiver;

    public interface OnRegisterBltReceiver {
        void onBluetoothDevice(BluetoothDevice device);//搜索到新设备

        void onBltIng(BluetoothDevice device);//连接中

        void onBltEnd(BluetoothDevice device);//连接完成

        void onBltNone(BluetoothDevice device);//未连接
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public BluetoothSocket getmBluetoothSocket() {
        return mBluetoothSocket;
    }

    /**
     * 在使用蓝牙BLE之前，需要确认Android设备是否支持BLE feature(required为false时)，
     * 另外要需要确认蓝牙是否打开。如果发现不支持BLE，则不能使用BLE相关的功能。
     * 如果支持BLE，但是蓝牙没打开，则需要打开蓝牙。
     * api 18以上
     *
     * @param context
     */
    public void initBltManager(Context context) {
        if (bluetoothManager != null) return;
        //首先获取BluetoothManager
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BluetoothAdapter
        if (bluetoothManager != null)
            mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * 注册广播来接收蓝牙配对信息
     * @param context
     */
    public void registerBltReceiver(Context context, OnRegisterBltReceiver onRegisterBltReceiver) {
        this.onRegisterBltReceiver = onRegisterBltReceiver;
        // 用BroadcastReceiver来取得搜索结果
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        context.registerReceiver(searchDevices, intent);
    }

    /**
     * 反注册广播取消蓝牙的配对
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(searchDevices);
        if (getmBluetoothAdapter() != null)
            getmBluetoothAdapter().cancelDiscovery();
    }


    /**
     * 蓝牙接收广播
     */
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        //接收
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e("bluetooth", keyName + ">>>" + String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device;
            // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                onRegisterBltReceiver.onBluetoothDevice(device);
            }
            //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("BlueToothTestActivity", "正在配对......");
                        onRegisterBltReceiver.onBltIng(device);
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("BlueToothTestActivity", "完成配对");
                        onRegisterBltReceiver.onBltEnd(device);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("BlueToothTestActivity", "取消配对");
                        onRegisterBltReceiver.onBltNone(device);
                    default:
                        break;
                }
            }
        }
    };

    public String bltStatus(int status) {
        String a = "未知状态";
        switch (status) {
            case BluetoothDevice.BOND_BONDING:
                a = "连接中";
                break;
            case BluetoothDevice.BOND_BONDED:
                a = "连接完成";
                break;
            case BluetoothDevice.BOND_NONE:
                a = "未连接/取消连接";
                break;
        }
        return a;
    }

    /**
     * 获得系统保存的配对成功过的设备，并尝试连接
     */
    private void getBltList() {
        if (getmBluetoothAdapter() == null) return;
        //获得已配对的远程蓝牙设备的集合
        Set<BluetoothDevice> devices = getmBluetoothAdapter().getBondedDevices();
        if (devices.size() > 0) {
            for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext(); ) {
                BluetoothDevice device = it.next();
                //自动连接已有蓝牙设备
                createBond(device, null);
            }
        }
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param btDev   蓝牙设备对象
     * @param handler 结果回调事件
     * @return
     */
    private void connect(BluetoothDevice btDev, Handler handler) {
        try {
            //通过和服务器协商的uuid来进行连接
            mBluetoothSocket = btDev.createRfcommSocketToServiceRecord(BltContant.SPP_UUID);
            if (mBluetoothSocket != null)
                //全局只有一个bluetooth，所以我们可以将这个socket对象保存在appliaction中
                APP.bluetoothSocket = mBluetoothSocket;
            //通过反射得到bltSocket对象，与uuid进行连接得到的结果一样，但这里不提倡用反射的方法
            //mBluetoothSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
            Log.d("blueTooth", "开始连接...");
            //在建立之前调用
            if (getmBluetoothAdapter().isDiscovering())
                //停止搜索
                getmBluetoothAdapter().cancelDiscovery();
            //如果当前socket处于非连接状态则调用连接
            if (!getmBluetoothSocket().isConnected()) {
                //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                getmBluetoothSocket().connect();
            }
            Log.d("blueTooth", "已经链接");
            if (handler == null) return;
            //结果回调
            Message message = new Message();
            message.what = 4;
            message.obj = btDev;
            handler.sendMessage(message);
        } catch (Exception e) {
            Log.e("blueTooth", "...链接失败");
            try {
                getmBluetoothSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * 尝试配对和连接
     *
     * @param btDev
     */
    public void createBond(BluetoothDevice btDev, Handler handler) {
        if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
            //如果这个设备取消了配对，则尝试配对
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                btDev.createBond();
            }
        } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
            //如果这个设备已经配对完成，则尝试连接
            connect(btDev, handler);
        }
    }


    /**
     * 输入mac地址进行自动配对
     * 前提是系统保存了该地址的对象
     *
     * @param address
     */
    public void autoConnect(String address, Handler handler) {
        if (getmBluetoothAdapter().isDiscovering()) getmBluetoothAdapter().cancelDiscovery();
        BluetoothDevice btDev = getmBluetoothAdapter().getRemoteDevice(address);
        connect(btDev, handler);
    }

    /**
     * 蓝牙操作事件
     *
     * @param context
     * @param status
     */
    public void clickBlt(Context context, int status) {
        switch (status) {
            case BltContant.BLUE_TOOTH_SEARTH://搜索蓝牙设备，在BroadcastReceiver显示结果
                startSearthBltDevice(context);
                break;
            case BltContant.BLUE_TOOTH_OPEN://本机蓝牙启用
                if (getmBluetoothAdapter() != null)
                    getmBluetoothAdapter().enable();//启用
                break;
            case BltContant.BLUE_TOOTH_CLOSE://本机蓝牙禁用
                if (getmBluetoothAdapter() != null)
                    getmBluetoothAdapter().disable();//禁用
                break;
            case BltContant.BLUE_TOOTH_MY_SEARTH://本机蓝牙可以在300s内被搜索到
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                context.startActivity(discoverableIntent);
                break;
            case BltContant.BLUE_TOOTH_CLEAR://本机蓝牙关闭当前连接
                try {
                    if (getmBluetoothSocket() != null)
                        getmBluetoothSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public void checkBleDevice(Context context) {
        if (getmBluetoothAdapter() != null) {
            if (!getmBluetoothAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
            }
        } else {
            Log.i("blueTooth", "该手机不支持蓝牙");
        }
    }

    /**
     * 搜索蓝牙设备
     * 通过调用BluetoothAdapter的startLeScan()搜索BLE设备。
     * 调用此方法时需要传入 BluetoothAdapter.LeScanCallback参数。
     * 因此你需要实现 BluetoothAdapter.LeScanCallback接口，BLE设备的搜索结果将通过这个callback返回。
     * <p/>
     * 由于搜索需要尽量减少功耗，因此在实际使用时需要注意：
     * 1、当找到对应的设备后，立即停止扫描；
     * 2、不要循环搜索设备，为每次搜索设置适合的时间限制。避免设备不在可用范围的时候持续不停扫描，消耗电量。
     * <p/>
     * 如果你只需要搜索指定UUID的外设，你可以调用 startLeScan(UUID[], BluetoothAdapter.LeScanCallback)方法。
     * 其中UUID数组指定你的应用程序所支持的GATT Services的UUID。
     * <p/>
     * 注意：搜索时，你只能搜索传统蓝牙设备或者BLE设备，两者完全独立，不可同时被搜索。
     */
    private boolean startSearthBltDevice(Context context) {
        //开始搜索设备，当搜索到一个设备的时候就应该将它添加到设备集合中，保存起来
        checkBleDevice(context);
        //如果当前发现了新的设备，则停止继续扫描，当前扫描到的新设备会通过广播推向新的逻辑
        if (getmBluetoothAdapter().isDiscovering())
            stopSearthBltDevice();
        Log.i("bluetooth", "本机蓝牙地址：" + getmBluetoothAdapter().getAddress());
        //开始搜索
        getmBluetoothAdapter().startDiscovery();
        return true;
    }

    public boolean stopSearthBltDevice() {
        //暂停搜索设备
        return getmBluetoothAdapter().cancelDiscovery();
    }

    /**
     * 连接GATT Server
     * 两个设备通过BLE通信，首先需要建立GATT连接。这里我们讲的是Android设备作为client端，连接GATT Server。
     * 连接GATT Server，你需要调用BluetoothDevice的connectGatt()方法。
     * 此函数带三个参数：Context、autoConnect(boolean)和BluetoothGattCallback对象。
     * <p/>
     * 是否直接连接到远程设备（false）或自动连接，尽快远程设备成为可用（true）。
     * <p/>
     * 函数成功，返回BluetoothGatt对象，它是GATT profile的封装。通过这个对象，我们就能进行GATT Client端的相关操作。
     * BluetoothGattCallback用于传递一些连接状态及结果。
     * <p/>
     * BluetoothGatt常规用到的几个操作示例:
     * <p/>
     * connect() ：连接远程设备。
     * discoverServices() : 搜索连接设备所支持的service。
     * disconnect()：断开与远程设备的GATT连接。
     * close()：关闭GATT Client端。
     * readCharacteristic(characteristic) ：读取指定的characteristic。
     * setCharacteristicNotification(characteristic, enabled) ：设置当指定characteristic值变化时，发出通知。
     * getServices() ：获取远程设备所支持的services。
     * <p/>
     * 等等。
     * <p/>
     * 注：
     * 1、某些函数调用之间存在先后关系。例如首先需要connect上才能discoverServices。
     * 2、 一些函数调用是异步的，需要得到的值不会立即返回，而会在BluetoothGattCallback的回调函数中返回。
     * 例如
     * discoverServices与onServicesDiscovered回调，
     * readCharacteristic与 onCharacteristicRead回调，
     * setCharacteristicNotification与 onCharacteristicChanged回调等。
     *
     * @param context
     * @param device
     * @param mCallback
     */
    public BluetoothGatt connectBleDevice(Context context, BluetoothDevice device, BluetoothGattCallback mCallback) {
        BluetoothGatt mBluetoothGatt = device.connectGatt(context, false, mCallback);
        return mBluetoothGatt;
    }

}
