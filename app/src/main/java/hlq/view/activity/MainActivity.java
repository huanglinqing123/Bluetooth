package hlq.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hlq.base.activity.BaseActivity;
import hlq.base.bean.BluRxBean;
import hlq.bluetooth.R;
import hlq.receivers.BlueToothReceiver;
import hlq.utils.ToastUtil;
import hlq.utils.factory.ThreadPoolProxyFactory;
import recycleview.huanglinqing.com.dialogutils.DialogUtils;

/**
 * MainACtivity功能
 * 1.获取本地蓝牙名称
 * 2.搜索蓝牙设备
 * 3.蓝牙配对
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.scan)
    Button scan;
    @BindView(R.id.localblumessage)
    TextView localblumessage;
    @BindView(R.id.sousuo)
    Button sousuo;
    @BindView(R.id.bluemessage)
    TextView bluemessage;
    @BindView(R.id.scanfinnish)
    TextView scanfinnish;
    @BindView(R.id.listview)
    ListView listview;

    private BluetoothManager bluetoothmanger;
    private BluetoothAdapter bluetoothadapter;
    private SimpleAdapter adapter;
    private List<Map<String, String>> list;
    private List<BluetoothDevice> listdevice;
    private AlertDialog alertDialog;
    private BlueToothReceiver blueToothReceiver = new BlueToothReceiver();
    private String boundDevice = "DeviceName";
    private int connectionSuccess = 5;//配对成功显示设备

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        registerReceiver(blueToothReceiver, blueToothReceiver.makeFilter());
        init();
        initblue();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 初始化蓝牙设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initblue() {
        bluetoothmanger = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter = bluetoothmanger.getAdapter();
        if (bluetoothadapter == null) {
            Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 组件初始化
     */
    private void init() {
        list = new ArrayList<>();
        listdevice = new ArrayList<>();
        /**
         * listview监听事件 即配对
         */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                //如果已经配对过了进行连接
                if (list.get(position).getOrDefault("statue", "未配对").equals("已配对")) {
                    alertDialog = DialogUtils.dialogloading(MainActivity.this,"正在连接",false,false);
                    ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } else {
                    try {
                        //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                        Method method = BluetoothDevice.class.getMethod("createBond");
                        Log.e(getPackageName(), "开始配对");
                        method.invoke(listdevice.get(position));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 开始扫描蓝牙
     */
    private void startscan() {
        Log.d("开始扫描", "开始扫描了");
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Log.d("来到这里了", "来到这里了......");
                        if (bluetoothadapter.isDiscovering()) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blueToothReceiver);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 监听事件
     *
     * @param view
     */
    @OnClick({R.id.scan, R.id.sousuo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                } else {
                    startscan();
                }
                break;
        }
    }

    /**
     * EventBus 异步
     * 1:找到设备
     * 2：扫描完成
     * 3：开始扫描
     * 4.配对成功跳转到通讯页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BluRxBean bluRxBean) {
        switch (bluRxBean.getId()) {
            case 1:
                listdevice.add(bluRxBean.getBluetoothDevice());
                // 添加到列表
                bluemessage.append(bluRxBean.getBluetoothDevice().getName() + ":"
                        + bluRxBean.getBluetoothDevice().getAddress() + "\n");
                Map<String, String> map = new HashMap<>();
                map.put("deviceName", bluRxBean.getBluetoothDevice().getName() + ":" + bluRxBean.getBluetoothDevice().getAddress());
                if (bluRxBean.getBluetoothDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                    map.put("statue", "未配对");
                } else {
                    map.put("statue", "已配对");
                }
                list.add(map);
                adapter = new SimpleAdapter(MainActivity.this, list, R.layout.devices,
                        new String[]{"deviceName", "statue"}, new int[]{R.id.devicename, R.id.statue});

                listview.setAdapter(adapter);
                break;
            case 2:
                DialogUtils.dimissloading(alertDialog);
                break;
            case 3:
                alertDialog = DialogUtils.dialogloading(MainActivity.this, "正在扫描", false, false);
                break;
            case 4:
                Intent intent = new Intent(MainActivity.this, Tongxun.class);
                startActivity(intent);
                break;
            case 5:
                Intent intent1 = new Intent(MainActivity.this, Tongxun.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
