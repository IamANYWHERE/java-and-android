import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by 我 on 2017/5/14.
 */
public class MainFrame extends JFrame {

    private static final int NUMBER_INSTRUCTION=320; //指令个数
    private static final int BOUND=10000;  //随机数产生范围

    private JRadioButton mLRUButton;   //LRU选择按钮
    private JRadioButton mFIFOButton;  //FIFO选择按钮

    private JButton mQuickButton;   //快速完成按钮
    private JButton mSlowButton;    //慢速完成按钮
    private JLabel mJLabel;          //显示缺页率

    private int mPageMissed;     //记录缺页数
    private boolean mIsLRU;      //是否选择的是LRU算法

    private OutTextArea mTextArea;//文字输出框

    private ArrayList<Page> mPages;//4个内存块存储的页
    private LinkedList<Page> mLinkedList;//用数据结构来表示页使用的先后，最后一个数据是最近使用的，第一个数据是最久未使用的
    private ArrayList<ArrayList<JButton>> mButtonsLists;//用于显示4个内存块的存储的指令

    public MainFrame(){

        mLinkedList=new LinkedList<>();
        mPages=new ArrayList<>();
        mButtonsLists=new ArrayList<>();
        //初始化变量

        for (int i=0;i<4;i++){
            Page page=new Page(-1);
            mLinkedList.add(page);
            mPages.add(page);
        }//将4个初始页放入内存，表示的是空页

        mPageMissed=0; //初始缺页率为0
        mIsLRU=false;  //初始使用FIFO算法

        setTextArea();  //设置文字输出框
        setRadioButtons();//设置算法选择按钮
        setSelectButtons();//设置执行速度选择按钮
        setStoreBlock();//设置内存显示图
        setListener();//设置监听器

        pack();
    }
    private void setTextArea(){
        mTextArea=new OutTextArea(); //初始化
        JScrollPane scrollPane=new JScrollPane(mTextArea);//将textArea放入滚动容器，使其有滚动条
        add(scrollPane,BorderLayout.CENTER);//将其放入总视图中
    }
    private void setRadioButtons(){
        RadioButtonPanel radioButtonPanel=new RadioButtonPanel();//初始化
        mFIFOButton=radioButtonPanel.addRadioButton("FIFO",true);//添加算法按钮
        mLRUButton=radioButtonPanel.addRadioButton("LRU",false);
        add(radioButtonPanel,BorderLayout.NORTH);
    }
    private void setSelectButtons(){
        SelectButtonPanel selectButtonPanel=new SelectButtonPanel();
        mQuickButton=selectButtonPanel.addButton("Quick");//添加速度选择按钮
        mSlowButton=selectButtonPanel.addButton("Slow");
        mJLabel =selectButtonPanel.addLabel("000/320");//缺页率文本初始化
        add(selectButtonPanel,BorderLayout.SOUTH);
    }

    private void setStoreBlock(){
        StoreBlock storeBlock=new StoreBlock();
        for (int i=0;i<4;i++) {
            ArrayList<JButton> jButtons = storeBlock.addBlock(i);//添加一个内存块
            mButtonsLists.add(jButtons);//将内存块中的按钮添加到按钮表中以便使用
        }
        add(storeBlock,BorderLayout.WEST);
    }

    private void setListener(){
        //用lambda设置监听器
        mFIFOButton.addActionListener(e->{
            System.out.println("FIFO");
            mIsLRU=false;//选择FIFO算法
        });
        mLRUButton.addActionListener(e->{
            System.out.println("LRU");
            mIsLRU=true;//选择LRU算法
        });

        mSlowButton.addActionListener(e->{
            System.out.println("Slow");
            System.out.println("Clicked");
            startProcess(100);//每0.1秒执行一个指令
        });
        mQuickButton.addActionListener(e->{
            System.out.println("Quick");
            System.out.println("Clicked");
            startProcess(0);//无限制执行指令
        });
    }

    private void startProcess(int sleep){
        //sleep是每条指令执行的时间
        setButtonEnable(false);//设置所有按钮不可用
        mTextArea.setText("");//初始化文字输出框
        if (mIsLRU){//判断选择的算法
            mTextArea.setText(mTextArea.getText()+"开始LRU\n");
        }else {
            mTextArea.setText(mTextArea.getText()+"开始FIFO\n");
        }
        mPageMissed=0;
        for (int i=0;i<4;i++){
            mPages.get(i).setIndex(-1);
        }
        mJLabel.setText("000/320");
        new Thread(()->{
            //耗时操作放入其他线程完成
            int m;   //m代表当前运行的指令
            int left=0;//用于产生随机指令时的左界限
            int right=319;//用于产生随机指令时的右界限
            m=getRandomNumber(BOUND,left,right);//获取一条随机指令
            dispatch(m);//将指令分配给内存
            Sleep(sleep);//停止sleep毫秒
            for (int i=1;i<NUMBER_INSTRUCTION;){
                //50%的顺序指令，25%随机前部分指令，25%随机后部分指令
                right=m-1;
                if (right<0)
                    right=0;
                left=0;
                m=getRandomNumber(BOUND,left,right);
                i++;
                dispatch(m);
                Sleep(sleep);
                //前一个指令的前部分随机指令

                if (i>=NUMBER_INSTRUCTION)
                    break;
                m=(m+1)%320;
                i++;
                dispatch(m);
                Sleep(sleep);
                //顺序指令

                if (i>=NUMBER_INSTRUCTION)
                    break;
                left=m+1;
                if (left>319)
                    left=319;
                right=319;
                m=getRandomNumber(BOUND,left,right);
                i++;
                dispatch(m);
                Sleep(sleep);
                //前一个指令的后部分指令

                if (i>=NUMBER_INSTRUCTION)
                    break;
                m=(m+1)%320;
                i++;
                dispatch(m);
                Sleep(sleep);
                //顺序指令

            }
            SwingUtilities.invokeLater(()->{
                mJLabel.setText(mPageMissed+"/320");
            });
            //更新缺页率文本
            setButtonEnable(true);//设置按钮可用
            System.out.println("###");
        }).start();
    }
    private void setButtonEnable(boolean b){
        //设置按钮可否用
        mQuickButton.setEnabled(b);
        mSlowButton.setEnabled(b);
        mFIFOButton.setEnabled(b);
        mLRUButton.setEnabled(b);
    }
    private void Sleep(int sleep){
        //设置睡眠sleep毫秒
        try {
            Thread.sleep(sleep);
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    private int getRandomNumber(int bound,int left,int right){
        //获取随机数
        Random random=new Random();
        int number=random.nextInt(bound);
        return number%(right-left+1)+left;
    }

    private void dispatch(int number){
        //在内存寻找指令，没有则插入或置换
        int index=number/10;//指令所在逻辑页
        boolean isFind=false;//指令是否找到
        String text=mTextArea.getText();//获取文字框中的文字
        for (int i=0;i<4;i++){
            //查询4个内存块中是否存在次指令或者内存块为空
            Page page=mPages.get(i);//获取内存块
            if(page.getIndex()==index){
                //指令是否在内存中
                isFind=true;

                if (mIsLRU==true) {
                    mLinkedList.remove(page);
                    mLinkedList.addLast(page);
                }
                changeColor(number,i);
                mTextArea.setText(text+"第"+number+"条指令执行，其已存在内存中，地址为第"+i+"块，第"+number%10+"条指令\n");
                break;
            }else if (page.getIndex()==-1){
                //指令不存在，内存右空位插入
                isFind=true;

                BeanDo(page,index,i);
                changeColor(number,i);

                mTextArea.setText(text+"第"+number+"条指令执行，第"+index+"号页插入第"+i+"号块\n");
                break;
            }


        }

        if (isFind==true)
            return;

        //内存没有找到指令和空位，和某一个页置换
        Page page=mLinkedList.getFirst();
        int i=mPages.indexOf(page);
        int transferIndex=page.getIndex();

        BeanDo(page,index,i);
        changeColor(number,i);

        mTextArea.setText(text+"第"+number+"条指令执行，第"+index+"号页置换给物理内存块中的第"+i+"块中的"+transferIndex+"号页\n");
    }

    private void BeanDo(Page page,int index,int i){
        //调整新的页为最近使用的
        page.setIndex(index);

        ArrayList<JButton> jButtons=mButtonsLists.get(i);
        for (int j=0;j<10;j++){
            jButtons.get(j).setText(index*10+j+"");
        }

        mLinkedList.remove(page);
        mLinkedList.addLast(page);

        mPageMissed+=1;//缺页加一
    }
    private void changeColor(int number,int i){
        //改变内存块中按钮的颜色，用于表示当前运行的指令
        int piece=number%10;
        ArrayList<JButton> jButtons=mButtonsLists.get(i);
        for (int j=0;j<10;j++){
            final int n=j;
            if (j!=piece) {
                SwingUtilities.invokeLater(() -> {
                    jButtons.get(n).setBackground(Color.WHITE);
                });
            }else{
                SwingUtilities.invokeLater(()->{
                    jButtons.get(n).setBackground(Color.ORANGE);
                });
            }
        }
    }
}
