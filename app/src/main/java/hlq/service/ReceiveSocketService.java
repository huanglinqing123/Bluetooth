package hlq.service;

import android.os.Environment;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hlq.APP;
import hlq.base.bean.MessageBean;

/**
 * Created by Huanglinqing on 2018/8/25/025 20:54
 * E-Mail Address：1306214077@qq.com
 * 接收消息服务端
 *
 */
public class ReceiveSocketService {

    private int RECEIVER_MESSAGE =  21;//收到消息
    private int RECEIVER_FILE =  22;//收到文件

    public  void  receiveMessage(){
          if (APP.bluetoothSocket == null){
              return;
          }
        try {
            InputStream inputStream = APP.bluetoothSocket.getInputStream();
            // 从客户端获取信息
            BufferedReader bff = new BufferedReader(new InputStreamReader(inputStream));
            String json;
            while (true) {
                while ((json = bff.readLine()) != null) {
                    EventBus.getDefault().post(new MessageBean(RECEIVER_MESSAGE,json));
                }
                if ("file".equals(json)) {
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.gif");
                    int length;
                    int fileSzie = 0;
                    byte[] b = new byte[1024];
                    // 2、把socket输入流写到文件输出流中去
                    while ((length = inputStream.read(b)) != -1) {
                        fos.write(b, 0, length);
                        fileSzie += length;
                        System.out.println("当前大小：" + fileSzie);
                        //这里通过先前传递过来的文件大小作为参照，因为该文件流不能自主停止，所以通过判断文件大小来跳出循环
                    }
                    fos.close();
                    EventBus.getDefault().post(new MessageBean(RECEIVER_FILE,"文件保存成功"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
