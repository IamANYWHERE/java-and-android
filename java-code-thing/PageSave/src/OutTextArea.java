import javax.swing.*;
import java.awt.*;

/**
 * Created by 我 on 2017/5/14.
 */
public class OutTextArea extends JTextArea {
    //文字输出框
    private static final int DEFAULT_COLUMN=20;//默认行
    private static final int DEFAULT_ROW=40; //默认列
    public OutTextArea(){
        super(DEFAULT_COLUMN,DEFAULT_ROW);
        setLineWrap(true); //设置自动换行
        setEditable(false);//设置不可修改内容
    }
}
