package hlq.view.activity;

import android.os.Bundle;
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
import hlq.bluetooth.R;
import hlq.service.ReceiveSocketService;
import hlq.service.SendSocketService;
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
                SendSocketService.sendMessage(goEditText.getText().toString());
                break;
            case R.id.go_file_btn:
                break;
        }
    }

    /**
     * RECEIVER_MESSAGE:21 收到消息
     *
     * @param messageBean
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageBean messageBean) {
        switch (messageBean.getId()) {
            case 21:
                Log.d("收到消息",messageBean.getContent());
                text.append(messageBean.getContent());
                break;
            default:
                break;
        }
    }
}
