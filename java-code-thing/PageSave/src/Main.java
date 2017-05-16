import javax.swing.*;
import java.awt.*;

/**
 * Created by 我 on 2017/5/14.
 */
public class Main {

    public static void main(String[] args){
        //main函数，程序的起始
        EventQueue.invokeLater(new Runnable() {
            //创建并产生图形界面
            @Override
            public void run() {
                MainFrame mainFrame=new MainFrame();
                mainFrame.setTitle("sdsdsd");
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setVisible(true);

            }
        });
    }
}
