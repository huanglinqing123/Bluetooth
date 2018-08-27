package hlq.base.constant;

import java.util.UUID;

/**
 * Created by Huanglinqing on 2018/8/25/025 16:40
 * E-Mail Address：1306214077@qq.com
 * 蓝牙常量控制类
 */
public class BltContant {
    /**
     * 蓝牙UUID
     */
    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //启用蓝牙
    public static final int BLUE_TOOTH_OPEN = 1000;
    //禁用蓝牙
    public static final int BLUE_TOOTH_CLOSE = BLUE_TOOTH_OPEN + 1;
    //搜索蓝牙
    public static final int BLUE_TOOTH_SEARTH = BLUE_TOOTH_CLOSE + 1;
    //被搜索蓝牙
    public static final int BLUE_TOOTH_MY_SEARTH = BLUE_TOOTH_SEARTH + 1;
    //关闭蓝牙连接
    public static final int BLUE_TOOTH_CLEAR = BLUE_TOOTH_MY_SEARTH + 1;


    /**
     * 通讯返回值
     */
    public static final int SEND_TEXT_SUCCESS = 50;//发送文字消息成功
    public static final int SEND_FILE_SUCCESS = 51;//发送文件信息成功
    public static final int SEND_FILE_NOTEXIT = 52;//发送的文件不存在
    public static final int SEND_FILE_IS_FOLDER = 53;//发送的诗歌文件夹
}
