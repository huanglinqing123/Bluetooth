package hlq.utils;

import android.widget.Toast;

import hlq.APP;

/**
 * ToastUtils
 */
public class ToastUtil {

    private ToastUtil() {
        throw new AssertionError();
    }

    //长
    public static void show(String message) {
        Toast.makeText(APP.context, message, Toast.LENGTH_LONG).show();
    }

    //短
    public static void shortShow(String message) {
        Toast.makeText(APP.context, message, Toast.LENGTH_SHORT).show();
    }
}
