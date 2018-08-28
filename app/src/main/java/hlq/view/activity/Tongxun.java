package hlq.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hlq.base.activity.BaseActivity;
import hlq.base.bean.MessageBean;
import hlq.base.constant.BltContant;
import hlq.bluetooth.R;
import hlq.service.ReceiveSocketService;
import hlq.service.SendSocketService;
import hlq.utils.ToastUtil;
import hlq.utils.factory.ThreadPoolProxyFactory;
import hlq.widget.TitleBar;

public class Tongxun extends BaseActivity {

    @BindView(R.id.titlebar)
    TitleBar titlebar;
    @BindView(R.id.content_ly)
    LinearLayout contentLy;
    @BindView(R.id.go_edit_text)
    EditText goEditText;
    @BindView(R.id.go_text_btn)
    Button goTextBtn;
    @BindView(R.id.go_file_btn)
    Button goFileBtn;
    @BindView(R.id.text)
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        titlebar.setTitle(getIntent().getStringExtra("devicename"));
        titlebar.setBackgroundResource(R.color.blue);
        titlebar.setImmersive(true);
        titlebar.setTitleColor(Color.WHITE);
        //开启消息接收端
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                new ReceiveSocketService().receiveMessage();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tongxun;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.go_text_btn, R.id.go_file_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_text_btn:
                //发送文字消息
                if (TextUtils.isEmpty(goEditText.getText().toString())) {
                    ToastUtil.shortShow("请先输入信息");
                } else {
                    SendSocketService.sendMessage(goEditText.getText().toString());
                }
                break;
            case R.id.go_file_btn:
                SendSocketService.sendMessageByFile(Environment.getExternalStorageDirectory()+"/test.png");
                break;
        }
    }

    /**
     * RECEIVER_MESSAGE:21 收到消息
     * BltContant.SEND_TEXT_SUCCESS:发送消息成功
     *BltContant.SEND_FILE_NOTEXIT:文件不存在
     * BltContant.SEND_FILE_IS_FOLDER:不能发送文件夹
     * @param messageBean
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageBean messageBean) {
        switch (messageBean.getId()) {
            case 21:
                Log.d("收到消息", messageBean.getContent());
                text.append("收到消息:" + messageBean.getContent() + "\n");
                break;
            case BltContant.SEND_TEXT_SUCCESS:
                text.append("我:" + goEditText.getText().toString() + "\n");
                goEditText.setText("");
                break;
            case BltContant.SEND_FILE_NOTEXIT:
                ToastUtil.shortShow("发送的文件不存在，内存根目录下的test.png");
                break;
            case BltContant.SEND_FILE_IS_FOLDER:
                ToastUtil.shortShow("不能传送一个文件夹");
                break;
            default:
                break;
        }
    }
}
