import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by 我 on 2017/5/14.
 */
public class SelectButtonPanel extends JPanel {

    //选择速度的按钮
    private JPanel mButtonPanel;//按钮容器
    private JPanel mLabelPanel;//文本容器

    public SelectButtonPanel(){
        mButtonPanel=new JPanel();
        mLabelPanel=new JPanel();

        mButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT,20,7));
        mLabelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,30,10));
        Border border=BorderFactory.createEtchedBorder();
        Border title=BorderFactory.createTitledBorder(border,"select speed");
        mButtonPanel.setBorder(title);//添加边界线
        add(mButtonPanel); //添加到容器中

        Border title2=BorderFactory.createTitledBorder(border,"missing rate");
        mLabelPanel.setBorder(title2);
        add(mLabelPanel);//添加容器中
    }
    public JButton addButton(String name){
        //添加按钮
        JButton jButton=new JButton(name);
        jButton.setBackground(Color.WHITE);//设置为白色
        mButtonPanel.add(jButton);
        return jButton;
    }
    public JLabel addLabel(String name){
        //添加文本
        JLabel label=new JLabel(name);
        mLabelPanel.add(label);
        return label;
    }
}
