package hlq.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothManager bluetoothmanger;
    private BluetoothAdapter bluetoothadapter;
    private Button buttonscan;
    private Button sousuo;
    private TextView localblumessage;
    private TextView bluemessage;
    private TextView scanfinnish;
    private int blueisok = 1;
    private ListView listview;
    private ArrayAdapter<String>  adapter;
    private List<String> list;
    private List<BluetoothDevice> listdevice;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(mReceiver, makeFilter());
        init();
        initblue();

    }

    /**
     * 初始化蓝牙设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initblue() {
        bluetoothmanger = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothadapter == null) {
            Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 组件初始化
     */
    private void init() {
        buttonscan = findViewById(R.id.scan);
        sousuo = findViewById(R.id.sousuo);
        bluemessage = findViewById(R.id.bluemessage);
        scanfinnish = findViewById(R.id.scanfinnish);
        localblumessage = findViewById(R.id.localblumessage);
        listview = findViewById(R.id.listview);
        buttonscan.setOnClickListener(this);
        sousuo.setOnClickListener(this);
        list = new ArrayList<>();
        listdevice = new ArrayList<>();

        /**
         * listview监听事件 即配对
         */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                try {
                    //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                    Method method = BluetoothDevice.class.getMethod("createBond");
                    Log.e(getPackageName(), "开始配对");
                    method.invoke(listdevice.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                //获取本地蓝牙名称
                String name = bluetoothadapter.getName();
                //获取本地蓝牙地址
                String address = bluetoothadapter.getAddress();
                localblumessage.setText("本地蓝牙名称:" + name + "本地蓝牙地址:" + address);
                break;
            case R.id.sousuo:
                if (!blueisenable()) {
                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enabler, 1);
                }else{
                    startscan();
                }
                break;
        }
    }

    /**
     * 开始扫描蓝牙
     */
    private void startscan() {
        Log.d("开始扫描", "开始扫描了");

        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Log.d("来到这里了","来到这里了......");
                        if (bluetoothadapter.isDiscovering()){
                            bluetoothadapter.cancelDiscovery();
                        }
                        bluetoothadapter.startDiscovery();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    /**
     * 判断蓝牙是否开启
     *
     * @return
     */
    public boolean blueisenable() {
        if (bluetoothadapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                startscan();
            }
        }
    }


    /**
     * 蓝牙广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON://蓝牙打开中
                            Log.d("蓝牙状态", "打开了");
                            Log.e("TAG", "TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON://蓝牙打开完成
                            Log.e("TAG", "STATE_ON");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://蓝牙关闭中
                            Log.d("蓝牙状态", "关闭了");
                            Log.e("TAG", "STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF://蓝牙关闭完成
                            Log.e("TAG", "STATE_OFF");
                            break;
                    }
                    break;
                //找到设备
                case BluetoothDevice.ACTION_FOUND:
                    Log.d("找设备","找设备");
                    // 从intent中获取设备
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listdevice.add(device);
                    // 判断是否配对过
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        // 添加到列表
                        bluemessage.append(device.getName() + ":"
                                + device.getAddress() + "\n");
                        list.add(device.getName() + ":" + device.getAddress());
                        String[] strings = new String[list.size()];
                        list.toArray(strings);
                        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,strings);
                        listview.setAdapter(adapter);
                    }
                    break;
                //搜索完成
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    scanfinnish.setText("搜索完成");
                    break;
                //开始扫描
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d("开始扫描","开始扫描");
                    break;
                //状态改变
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice de = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (de.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            Log.e(getPackageName(), "取消配对");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                             Toast.makeText(MainActivity.this,"配对中",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Toast.makeText(MainActivity.this,"配对成功",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

            }
        }
    };

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变的广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);//找到设备的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索完成的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//开始扫描的广播
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        return filter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }
}
