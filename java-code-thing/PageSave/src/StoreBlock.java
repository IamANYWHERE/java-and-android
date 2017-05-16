import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by 我 on 2017/5/16.
 */
public class StoreBlock extends JPanel {
    //内存块容器
    public StoreBlock(){
        setLayout(new GridLayout(2,2));

    }

    public ArrayList<JButton> addBlock(int index){
        //添加内存块
        //一个内存块由10个按钮组成，表示容纳的指令
        JPanel jPanel=new JPanel();
        jPanel.setLayout(new GridLayout(10,1));

        ArrayList<JButton> jButtons=new ArrayList<>();

        for (int i=0;i<10;i++){
            JButton jButton=new JButton("");
            jButton.setEnabled(false);
            jButton.setBackground(Color.WHITE);
            jPanel.add(jButton);
            jButtons.add(jButton);
        }
        Border border=BorderFactory.createEtchedBorder();
        Border title=BorderFactory.createTitledBorder(border,index+"块");
        jPanel.setBorder(title);

        add(jPanel);

        return jButtons;
    }
}
