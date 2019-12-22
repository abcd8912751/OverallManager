package com.furja.utils;

/**
 * 输入框 输入完成监听
 */
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.furja.utils.SharpBus;
import com.furja.utils.Utils;
import java.util.regex.Pattern;
import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 输入框输入完成的监听,当输入完成调用其中方法
 */
public class TextInputListener implements View.OnKeyListener,EditText.OnEditorActionListener {
    private long lastTimeMillis;
    SharpBus sharpBus;
    public static String INPUT_ERROR="条码格式错误";
    public TextInputListener() {
        sharpBus = SharpBus.getInstance();
    }
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            TextView textView = (TextView) view;
            excuteInput(textView.getText()+"");
        }
        return false;
    }
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE)
            excuteInput(textView.getText()+"");
        return false;
    }

    public static void bind(EditText editText){
        TextInputListener listener = new TextInputListener();
        if(editText!=null){
            editText.addTextChangedListener(TextChanger.flat(text->{
                if(text.endsWith("\n"))
                    listener.excuteInput(text);
            }));
            editText.setOnKeyListener(listener);
            editText.setOnEditorActionListener(listener);
        }
    }

    /**
     * 处理输入的字符
     * @param input
     */
    private void excuteInput(String input) {
        input = getPureString(input);
        if(input.isEmpty())
            return;
        String pattern="^[0-9]+[A-Z]+$";
        input = Utils.formatBarCode(input);
        sharpBus.post(TAG_SCAN_BARCODE,input);
    }

    /**
     * 获取去除 回车/换行符的字符串
     * @param input
     * @return
     */
    private String getPureString(String input) {
        input=input.toUpperCase();
        input = input.replace("\n", "");
        input = input.replace("\r", "");
        return input;
    }
}
