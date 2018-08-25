package hlq.view.activity;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hlq.base.activity.BaseActivity;
import hlq.base.bean.BluRxBean;
import hlq.bluetooth.R;
import hlq.widget.TitleBar;

public class Tongxun extends BaseActivity {

    @BindView(R.id.titlebar)
    TitleBar titlebar;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.sendcontent)
    EditText sendcontent;
    @BindView(R.id.sendbutton)
    Button sendbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tongxun;
    }

    @OnClick(R.id.sendbutton)
    public void onViewClicked() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
