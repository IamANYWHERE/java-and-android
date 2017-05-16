import javafx.scene.control.RadioButton;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Created by 我 on 2017/5/14.
 */
public class RadioButtonPanel extends JPanel {
    //用与选择算法的按钮容器
    private ButtonGroup mButtonGroup;//用于管理按钮
    public RadioButtonPanel(){
        mButtonGroup=new ButtonGroup();

        addBorder();//添加边界线
    }
    public JRadioButton addRadioButton(String name,boolean selected){
        //添加按钮
        JRadioButton jRadioButton=new JRadioButton(name,selected);
        mButtonGroup.add(jRadioButton);
        add(jRadioButton);
        return jRadioButton;
    }
    private void addBorder(){
        //添加边界线
        Border etched=BorderFactory.createEtchedBorder();
        Border titled=BorderFactory.createTitledBorder(etched,"algorithm");
        setBorder(titled);
    }
}
