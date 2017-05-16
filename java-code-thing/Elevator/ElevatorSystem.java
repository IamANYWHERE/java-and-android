package com.company;

import org.omg.CORBA.PUBLIC_MEMBER;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by 我 on 2017/4/17.
 *这是一个模拟电梯的系统
 */
public class ElevatorSystem
{
    //电梯程序总启动主函数
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            //swing程序中都需带的函数。事件分发机制用于处理所有AWTEvent(以及其子类）的分发
            @Override
            public void run()
            {
                //线程中的run函数用于创建和初始化窗体和图像化界面
                ElevatorFrame elevatorFrame=new ElevatorFrame();               //创建一个电梯框架实例
                elevatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认的关闭按钮
                elevatorFrame.setLocationByPlatform(true);                    //让平台决定窗体的位置
                elevatorFrame.setVisible(true);                               //让窗体显现（绘制）
            }
        });
    }
}

class ElevatorFrame extends JFrame
{
    //整个程序的框架，在上面添加容器，再在容器上添加组件
    private static final int DEFAULT_WIDTH=700;  //默认窗体的宽
    private static final int DEFAULT_HEIGHT=500; //默认宽假的高

    public ElevatorFrame()
    {
        //框架的构造函数
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);//设置大小
        WholePanel wholePanel=new WholePanel();   //创建容器的实例
        setTitle("Elevator");                    //设置框架名
        this.getContentPane().add(wholePanel);    //添加容器
        setResizable(false);                      //设置大小不可调整
        pack();                                    //自动调整组件之间的距离，大小等
    }
}

class WholePanel extends JPanel
{
    //最大的容器，所有其他组件都在里面
    private static final String UP_BUTTON="▲";           //向上按钮的图标
    private static final String DOWN_BUTTON="▼";        //向下按钮的图标
    private static final String OPEN_ALERT_BUTTON="✆"; //开启警报的图标
    private static final String CLOSE_ALERT_BUTTON="☏";//关闭警报的图标

    private JPanel mButtonPanel; //电梯楼层中的上下按钮的容器
    private JPanel mAlertPanel;  //报警按钮的容器
    private ElevatorComponent mElevatorComponent; //包含所有电梯的组件
    private ArrayList<OpenButton> mUpButtons;       //电梯楼层中的向上的按钮数组
    private ArrayList<OpenButton> mDownButtons;     //电梯楼层中的向下的按钮数组
    private Queue<Task> mWaitTasks;                 //等待任务队列，电梯楼层中按钮点击后产生的任务。由电梯外部调度线程维护，电梯调度算法进行调度
    private ArrayList<JButton> mOpenAlertButtons;  //开启警报的按钮数组
    private ArrayList<JButton> mCloseAlertButtons; //关闭警报的按钮数组

    public WholePanel()
    {
        //容器的构造函数，初始化容器
        this.setLayout(new BorderLayout(5,5));  //设置容器的布局
        mButtonPanel=new JPanel();;            //创建一个容器实例
        mElevatorComponent=new ElevatorComponent();//创建放置电梯的组件
        mButtonPanel.setLayout(new GridLayout(20,3));//设置布局
        mUpButtons=new ArrayList<>();              //实例化按钮数组
        mDownButtons=new ArrayList<>();            //实例化按钮数组
        Border border=BorderFactory.createEtchedBorder();//创建蚀灼线
        Border titleBorder=BorderFactory.createTitledBorder(border,"Floor");//创建由标签的蚀灼线
        mButtonPanel.setBorder(titleBorder);           //把带有标签的蚀灼线的给容器

        for (int i=1;i<=20;i++)
        {
            //实例化按钮设置监听器，并添加到容器和按钮数组中
            JButton floorButton=new JButton(""+(21-i));//用来表示楼层的按钮
            floorButton.setEnabled(false);             //设置不可点击
            mButtonPanel.add(floorButton);            //将按钮添加至容器
            final int floor=i-1;                       //代码中的实际楼层是0到19，所以要减1

            OpenButton upButton;                        //声名向上键
            OpenButton downButton;                      //声名向下键
            if (i==1)
            {
                //在最高层时不可能再往上了，所以设置最高层的上按钮不可用
                upButton=new OpenButton("");
                upButton.setEnabled(false);
            }else
            {
                //1到19层都可以往上走，设置按钮和监听器
                upButton = new OpenButton(UP_BUTTON);//设置上按钮图标
                upButton.addActionListener(new ActionListener() {//设置监听器
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Task task=new Task(floor,Task.TYPE_UP);//创建一个标识着楼层和按钮类型的任务
                        mWaitTasks.add(task);                  //将任务添加至等待任务队列
                        upButton.setColor(Color.CYAN);          //将按钮设置为CYAN色表示等待电梯响应
                        upButton.setEnabled(false);             //设置不可点击，因为已经点击了
                    }
                });
            }
                mUpButtons.add(upButton);        //将上按钮添加至上按钮组
                mButtonPanel.add(upButton);      //将上按钮添加到容器
            if (i==20)
            {
                //再最低层时不可能再往下了，所以设置最底层的下按钮不可用
                downButton=new OpenButton(""); //
                downButton.setEnabled(false);
            }else
            {
                downButton = new OpenButton(DOWN_BUTTON);//设置按钮图标
                downButton.addActionListener(new ActionListener() {//设置监听器
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Task task=new Task(floor,Task.TYPE_DOWN);//创建一个标识着楼层和按钮类型的任务
                        mWaitTasks.add(task);                    //将任务添加至等待任务队列
                        downButton.setColor(Color.CYAN);          //将按钮设置为CYAN色，表示等待电梯响应
                        downButton.setEnabled(false);             //将按钮设置不可点击，因为已经点击了
                    }
                });
            }

                mDownButtons.add(downButton);//将下按钮添加到下按钮组
                mButtonPanel.add(downButton);//将下啊你怒添加到容器

        }

        add(mButtonPanel,BorderLayout.WEST);//将按钮容器添加到总的容器中西边
        addAlertPanel();                       //在总的容器中添加开启和解除报警的按钮容器
        add(mElevatorComponent);            //添加包含所有电梯的组件到总的容器中
    }


    private void addAlertPanel(){
        //在总的容器中添加开启和解除报警的按钮容器
        mAlertPanel =new JPanel(); //初始化报警容器
        mAlertPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,20,0)); //设置容器的布局
        JLabel jLabel=new JLabel(); //初始化可以添加文字的组件
        jLabel.setText("Alert");   //设置其文字为Alert
        mAlertPanel.add(jLabel);   //将标签组件添加到容器中

        mOpenAlertButtons=new ArrayList<>(); //初始化开启报警的按钮组
        mCloseAlertButtons=new ArrayList<>();//初始化关闭报警的按钮组
        for (int i=0;i<5;i++)
        {
            JPanel panel=new JPanel();//初始化一个容器，将添加一对开启和关闭报警的按钮

            JButton openAlert=new JButton(OPEN_ALERT_BUTTON); //初始化开启报警的按钮
            openAlert.setBackground(Color.WHITE);//设置颜色为白色
            mOpenAlertButtons.add(openAlert);  //将开启报警的按钮添加到按钮组
            panel.add(openAlert);                 //将按钮添加到容器

            JButton closeAlert=new JButton(CLOSE_ALERT_BUTTON);//初始化关闭报警的按钮
            closeAlert.setBackground(Color.WHITE);//设置颜色为白色
            mCloseAlertButtons.add(closeAlert); //将关闭报警的按钮添加到按钮组
            closeAlert.setEnabled(false);         //设置其不可点击。因为警报未开启
            panel.add(closeAlert);                 //将按钮添加到容器

            ArrayList<Elevator> elevators=mElevatorComponent.getElevators();//得到5部电梯组
            final int index=i; //监听器里用外部变量，必须为final
            openAlert.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    elevators.get(index).setAddTask(false);//将这部电梯的添加任务的功能禁止
                    openAlert.setEnabled(false);           //设置开启警报的按钮不可点击
                    elevators.get(index).openAlert();       //按钮所对应的电梯开启警报
                }
            });

            closeAlert.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closeAlert.setEnabled(false);//设置关闭警报的按钮不可用
                    elevators.get(index).closeAlert();//设置按钮对应的电梯关闭警报
                    openAlert.setEnabled(true);      //设置开启警报的按钮可用
                    elevators.get(index).setAddTask(true);//设置按钮对应的电梯可以添加任务
                }
            });

            Border border=BorderFactory.createEtchedBorder(); //创建一个蚀灼线
            Border titleBorder=BorderFactory.createTitledBorder(border,""+(i+1));//创建一个带有标签的蚀灼线
            panel.setBorder(titleBorder);//将带有标签的蚀灼线添加给容器
            mAlertPanel.add(panel);//把这个小容器添加给警报容器
        }
        add(mAlertPanel,BorderLayout.SOUTH);//将警报容器添加给总容器的下部

        Border border=BorderFactory.createEtchedBorder();//创建一个蚀灼线
        Border titleBorder=BorderFactory.createTitledBorder(border,"♫♬");//创建一个带有标签的蚀灼线
        mAlertPanel.setBorder(titleBorder); //将带有标签的蚀灼线添加到容器
    }


    class ElevatorComponent extends JComponent
    {
        //容纳所有电梯的组件
        private static final int DEFAULT_WIDTH=500; //默认宽500
        private static final int DEFAULT_HEIGHT=400;//默认高400
        private ArrayList<Elevator> mElevators;        //电梯数组

        public ElevatorComponent()
        {
            //构造函数
            setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);//设置大小
            mElevators =new ArrayList<>();          //初始化电梯数组
            mWaitTasks =new LinkedList<>();         //初始化等待队列
            setLayout(new FlowLayout(FlowLayout.RIGHT,10,10));//设置布局

            for (int j=1;j<=5;j++)
            {
                Border border=BorderFactory.createEtchedBorder();//创建一个蚀灼线
                Border titleBorder=BorderFactory.createTitledBorder(border,"Elevator"+j);//创建一个带有标签的蚀灼线
                Elevator elevator=new Elevator(j-1);//初始化电梯，并传入电梯的ID
                elevator.setBorder(titleBorder);    //将带有标签的蚀灼线添加到电梯
                mElevators.add(elevator);         //将电梯添加到电梯数组
                add(elevator);                     //将电梯添加给组件

            }

            new controlThread().start();  //启动电梯调度线程，控制并调度电梯去响应外部的上下按钮
        }

        public ArrayList<Elevator> getElevators(){
            return mElevators;
        }//得到电梯组

        class controlThread extends Thread{
            //电梯外部控制线程
            ArrayList<Elevator> upElevators;  //存储向上的电梯组
            ArrayList<Elevator> waitElevators;//存储等待的电梯组
            ArrayList<Elevator> downElevators;//存储向下的电梯组

            private void scheduleElevator()
            {
                //电梯调度算法，用调度五部电梯，将等待队列里的任务分配给他们
                Task task=mWaitTasks.poll(); //弹出等待队列头部任务
                int floor=task.getFloor();    //得到任务的楼层
                int buttonType=task.getButtonType();//得到触发此任务的按钮类型

                Elevator goalElevator=null;   //初始化目标电梯

                sortElevator();               //将电梯归置，分成三种类型的电梯，向上，向下，等待。
                int curDistance=20;          //初始化电梯离任务的距离为最大
                if (buttonType==Task.TYPE_UP)
                {
                    //按钮类型为楼层按钮的向上按钮时
                    for (int i=waitElevators.size()-1;i>=0;i--)
                    {
                        //查找等待的电梯，如果有，就把距离现在所在楼层最近的电梯，设置为目标电梯
                        int distance=Math.abs(waitElevators.get(i).getCurrentFloor().getFloor()-floor);
                        //计算距离
                        if (distance<=curDistance)
                        {
                            goalElevator=waitElevators.get(i);
                            curDistance=distance;
                        }
                        //得到最小距离电梯
                    }

                    for (int i=0;i<upElevators.size();i++)
                    {
                        //查找向上电梯，如果有，就把距离所在楼层最近的电梯，且比等待电梯还近的设置为目标电梯
                        if (upElevators.get(i).getCurrentFloor().getFloor()>=floor)
                        {
                            //向上的电梯必须在低于或等于按钮所在楼层
                            int distance=upElevators.get(i).getCurrentFloor().getFloor()-floor;
                            //计算距离
                            if (distance<=curDistance)
                            {
                                goalElevator=upElevators.get(i);
                                curDistance=distance;
                            }
                            //得到最小距离电梯
                        }
                    }

                }
                else if (buttonType==Task.TYPE_DOWN)
                {
                    //按钮类型为向下
                    for (int i=waitElevators.size()-1;i>=0;i--)
                    {
                        //查找等待的电梯，如果有，就把距离现在所在楼层最近的电梯，设置为目标电梯
                        int distance=Math.abs(waitElevators.get(i).getCurrentFloor().getFloor()-floor);
                        //计算距离
                        if (distance<=curDistance)
                        {
                            goalElevator=waitElevators.get(i);
                            curDistance=distance;
                        }
                        //得到最小距离电梯
                    }

                    for (int i=0;i<downElevators.size();i++)
                    {
                        //查找向下电梯，如果有，就把距离所在楼层最近的电梯，且比等待电梯还近的设置为目标电梯
                        if (downElevators.get(i).getCurrentFloor().getFloor()<=floor)
                        {
                            //向下的电梯必须在高于或等于按钮所在楼层
                            int distance=floor-downElevators.get(i).getCurrentFloor().getFloor();
                            //计算距离
                            if (distance<=curDistance)
                            {
                                goalElevator=downElevators.get(i);
                                curDistance=distance;
                            }
                            //得到最小距离电梯
                        }
                    }

                }

                if (goalElevator==null)
                {
                    //如果没有合适的电梯作为目标电梯，暂时不处理它，将任务加到队尾
                    mWaitTasks.add(task);
                    task=null;
                }else
                {
                    //有合适的目标电梯，将任务加到目标电梯的就绪任务队列中
                    goalElevator.addTask(task);
                }
            }

            private void sortElevator(){
                //分配电梯到合适的电梯数组中
                upElevators=new ArrayList<>();
                waitElevators=new ArrayList<>();
                downElevators=new ArrayList<>();

                for (int i=0;i<mElevators.size();i++)
                {
                    Elevator elevator=mElevators.get(i);
                    if (elevator.getState()==Elevator.UP_STATE&&elevator.isAddTask())
                    {
                        //如果电梯状态向上，并且可以添加任务，将他加入上电梯数组
                        upElevators.add(elevator);
                    }else if (elevator.getState()==Elevator.DOWN_STATE&&elevator.isAddTask())
                    {
                        //如果电梯状态向下，并且可以添加任务，将他加入下电梯数组
                        downElevators.add(elevator);
                    }else if(elevator.getState()==Elevator.WAIT_STATE&&elevator.isAddTask())
                    {
                        //如果电梯状态为等待，并且可以添加任务，将他加入等待电梯数组
                        waitElevators.add(elevator);
                    }
                }
            }

            @Override
            public void run()
            {
                //调度线程的run函数
                while(true)
                {
                    //每隔一段时间就分配一次等待任务队列中任务，不停的循环检查
                    try
                    {
                        sleep(Elevator.CHECK_TASK_PER_TIME);//睡眠
                        if (!mWaitTasks.isEmpty())
                        {
                            //如果等待任务队列不为空就调度
                            scheduleElevator();
                        }
                    }catch (InterruptedException ie)
                    {
                        ie.printStackTrace();
                    }

                }
            }
        }
    }

    class Elevator extends JPanel{
        //电梯组件
        public static final int UP_STATE=1;   //电梯状态为向上
        public static final int DOWN_STATE=2; //电梯状态为向下
        public static final int WAIT_STATE=3; //电梯状态为等待
        public static final int WARN_STATE=4; //电梯状态为警报

        public static final int FLOOR_NUMBER=20; //电梯楼层为20

        public static final String LABEL_NORMAL="❀";//表示电梯当前的是安全的
        public static final String LABEL_WARN="☠"; //表示电梯当前是不安全的

        //用于显示当前电梯的状态
        public static final String STATE_LABEL_WAIT="Wait";//显示等待
        public static final String STATE_LABEL_UP="Up";     //显示向上
        public static final String STATE_LABEL_DOWN="Down";//显示向下
        public static final String STATE_LABEL_WARN="Warn";//显示警报
        public static final String STATE_LABEL_OPEN="Open";//显示开门

        public static final int CHECK_TASK_PER_TIME=100; //每次检查有无任务时，至少等待的世间
        public static final int MOVE_FLOOR_PER_TIME=200; //每次电梯移动时所等待世间
        public static final int OPEN_DOOR_TIME=1500;  //电梯到达时开门的时间

        public  final int T_ID;  //唯一标识电梯的ID

        private ArrayList<RoomButton> mDigitButtons; //电梯内部的数字按钮数组
        private ArrayList<RoomButton> mRoomButtons;  //用颜色表示电梯所在楼层按钮数组
        private ArrayList<Task> mTasks;               //电梯内的就绪任务队列
        private boolean mAddTask;                   //表示当前电梯可不可以添加任务
        private int mState;                          //表示电梯当前的状态
        private Floor mCurrentFloor;                //表示电梯当前所在的楼层
        private Task mCurrentTask;                  //表示电梯当前需要解决的任务
        private JLabel mLabelFloor;                 //用于显示当前所在楼层
        private JLabel mLabelState;                 //用于显示当前电梯状态
        private JLabel mWarnLabel;                  //用显示电梯当前的安全状态


        public Elevator(int id){
            //电梯的构造函数，创出电梯的ID
            setLayout(new GridLayout(22,2)); //设置布局

            mLabelFloor=new JLabel();  //初始化显示电梯楼层的标签
            mWarnLabel=new JLabel();   //初始化显示电梯安全状态的标签
            mWarnLabel.setHorizontalAlignment(JLabel.CENTER);//设置标签中心显示文字
            mLabelFloor.setHorizontalAlignment(JLabel.CENTER);//设置标签中心显示文字
            mWarnLabel.setText(LABEL_NORMAL);//设置文字
            mLabelFloor.setText("1");//设置文字
            add(mWarnLabel);//将标签添加到电梯组件中
            add(mLabelFloor);//将标签添加到电梯组件中
            mAddTask=true;//设置任务可添加

            mDigitButtons=new ArrayList<>(); //初始化数字按钮数组
            mRoomButtons=new ArrayList<>();  //初始化按钮数组
            mTasks=new ArrayList<>();         //初始化就绪任务队列
            mState=WAIT_STATE;               //设置当前状态为等待状态
            mCurrentFloor=new Floor(FLOOR_NUMBER-1);//设置当前电梯所在楼层为最底层
            mCurrentTask=null;               //当前所需解决的任务为空
            T_ID=id;                           //设置电梯ID

            for(int i=1;i<=FLOOR_NUMBER;i++) {

                RoomButton jDigit = new RoomButton("" + (FLOOR_NUMBER+1-i),Color.WHITE);//初始化数字按钮
                RoomButton jRoom=new RoomButton("",Color.BLACK);//初始化表示电梯的按钮

                if(i==FLOOR_NUMBER) {
                    jRoom.setColor(Color.RED);//最底层电梯设置为红色
                }

                add(jDigit);//将按钮添加到组件
                add(jRoom);//将按钮添加到组件
                mDigitButtons.add(jDigit);//将数字按钮添加到按钮组
                mRoomButtons.add(jRoom);  //将表示电梯的按钮添加到按钮组
                jRoom.setEnabled(false);   //设置表示电梯的按钮不可点击

                final int floor=i-1;      //按钮对应楼层

                jDigit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Task task=new Task(floor,Task.TYPE_DIGIT);//创建一个任务
                        if (isAddTask()) {
                            //如果当前电梯可以添加任务，就添加
                            mTasks.add(task);//添加任务
                            jDigit.setColor(Color.CYAN);//设置其颜色为CYAN，表示等待电梯来响应
                            jDigit.setEnabled(false);   //设置数字按钮暂时不可用
                        }
                    }
                });
            }

            mLabelState=new JLabel();   //初始话显示电梯状态的标签
            JLabel floorLabel=new JLabel();//初始化表示所在楼层的标签

            mLabelState.setText(STATE_LABEL_WAIT);//设置显示状态的标签的文字
            floorLabel.setText("Floor"); //设置文字

            mLabelState.setHorizontalAlignment(JLabel.CENTER);//设置文字中心显示
            floorLabel.setHorizontalAlignment(JLabel.CENTER);//设置文字中心显示

            add(floorLabel); //添加标签给组件
            add(mLabelState);//添加标签给组件

            new ElevatorThread().start(); //启动电梯内部任务调度线程
        }

        public void setAddTask(boolean addTask) {
            mAddTask = addTask;
        }//设置任务是否可以添加

        public boolean isAddTask() {
            return mAddTask;
        }//返回任务是否可以添加

        public void addTask(Task task){
            mTasks.add(task);
        }//向电梯内部的就绪任务队列添加任务

        public int getState() {
            return mState;
        }//返回电梯当前状态

        public Floor getCurrentFloor() {
            return mCurrentFloor;
        }//返回电梯当前楼层

        public void openAlert(){
            //开启电梯报警，删除所有任务，让电梯去一楼
            if (mState==WARN_STATE)//如果电器正在报警状态，则此操作无效
                return;;

            Task task=new Task(19,Task.TYPE_ALERT);//创建一个任务，让电梯去一楼，并标识为警报按钮
            int size=mTasks.size(); //得到就绪任务队列的大小

            for (int i=size-1;i>=0;i--)
            {
                //删除队列里的任务，并让按钮归置
                Task removeTask=mTasks.get(i); //得到任务
                int type=removeTask.getButtonType();//得到任务的按钮类型
                int floor=removeTask.getFloor();   //得到任务楼层
                if (type==Task.TYPE_DOWN)
                {
                    //归置按钮
                    mDownButtons.get(floor).setColor(Color.WHITE);
                    mDownButtons.get(floor).setEnabled(true);
                }else if (type==Task.TYPE_UP)
                {
                    //归置按钮
                    mUpButtons.get(floor).setColor(Color.WHITE);
                    mUpButtons.get(floor).setEnabled(true);
                }
                mTasks.remove(i);//移除任务
            }
            mTasks.add(task);//添加任务

            for (int i=0;i<FLOOR_NUMBER;i++)
            {
                //设置所有按钮不可用
                RoomButton button=mDigitButtons.get(i);
                button.setEnabled(false);
                button.setColor(Color.WHITE);
            }
            //设置标签为警报
            mWarnLabel.setText(LABEL_WARN);
        }

        public void closeAlert(){
            //关闭警报，恢复按钮可用
            if (mState!=WARN_STATE)//状态正常时，此操作无效
                return;
            int size=mTasks.size(); //得到任务队列的大小

            for (int i=size-1;i>=0;i--)
            {
                //移除任务
                mTasks.remove(i);
            }

            for (int i=0;i<FLOOR_NUMBER;i++)
            {
                //设置按钮可用
                RoomButton button=mDigitButtons.get(i);
                button.setEnabled(true);
            }
            //设置标签为安全状态
            mWarnLabel.setText(LABEL_NORMAL);
            mState=WAIT_STATE;//设置电梯状态为等待
        }

        class ElevatorThread extends Thread{
            //电梯线程，用于控制电梯内部的任务调度和执行任务
            private void scheduleTask(){
                //任务调度函数，实现调度任务
                if (mState==WAIT_STATE)
                {
                    //状态为等待时
                    mCurrentTask=mTasks.get(0);//取一个任务为当前任务
                    int taskFloor=mCurrentTask.getFloor();//得到任务楼层
                    int currentFloor=mCurrentFloor.getFloor();//得到电梯当前楼层

                    if (taskFloor<currentFloor)
                    {
                        mState=UP_STATE; //任务楼层高于当前楼层时，向上走，并将状态改为向上
                    }else if(taskFloor>currentFloor)
                    {
                        mState=DOWN_STATE;//任务楼层低于当前楼层时，向下走，并将状态改为向下
                    }

                }else if (mState==UP_STATE)
                {
                    //状态为向上时
                    mCurrentTask=null;  //设置当前任务为空
                    for (Task task:mTasks)
                    {
                        //查找任务
                        if(task.getFloor()<=mCurrentFloor.getFloor())
                        {
                            //任务楼层高于当前楼层时
                            if(mCurrentTask==null||task.getFloor()>mCurrentTask.getFloor())//找到距离最短的任务
                                mCurrentTask=task;
                        }
                    }

                    if(mCurrentTask==null)
                    {
                        //如何当前任务为空，说明没有需要向上的任务
                        mState=DOWN_STATE;//将状态改为向下
                        for (Task task:mTasks)
                        {
                            //查找任务
                            if (task.getFloor()>=mCurrentFloor.getFloor())
                            {
                                //任务楼层低于当前楼层时
                                if(mCurrentTask==null||task.getFloor()<mCurrentTask.getFloor())//找到距离最短的任务
                                    mCurrentTask=task;
                            }
                        }
                    }
                }else if(mState==DOWN_STATE)
                {
                    //状态为向下
                    mCurrentTask=null; //设置当前任务为空
                    for (Task task:mTasks)
                    {
                        //查找任务
                        if (task.getFloor()>=mCurrentFloor.getFloor())
                        {
                            //任务楼层低于当前楼层时
                            if (mCurrentTask==null||task.getFloor()<mCurrentTask.getFloor())//找到距离最短的任务
                                mCurrentTask=task;
                        }
                    }

                    if (mCurrentTask==null)
                    {
                        //当前任务为空，说明没有向下的任务了
                        mState=UP_STATE;//改电梯状态为向上
                        for (Task task:mTasks)
                        {
                            //查找任务
                            if(task.getFloor()<=mCurrentFloor.getFloor())
                            {
                                //任务楼层高于当前楼层时
                                if(mCurrentTask==null||task.getFloor()>mCurrentTask.getFloor())//找到距离最短的任务
                                    mCurrentTask=task;
                            }
                        }
                    }
                }
            }

            private void updateLabelState(){
                //更新显示状态的标签
                if (mState==WAIT_STATE)
                    mLabelState.setText(STATE_LABEL_WAIT);
                else if (mState==UP_STATE)
                    mLabelState.setText(STATE_LABEL_UP);
                else if (mState==DOWN_STATE)
                    mLabelState.setText(STATE_LABEL_DOWN);
            }
            @Override
            public void run() {
                while(true)
                {
                    if (!mTasks.isEmpty()){
                        //当就绪任务队列不为空时，调度任务，执行任务
                        try
                        {
                            sleep(CHECK_TASK_PER_TIME); //睡眠
                            scheduleTask();//调度任务
                            if (mCurrentTask==null)//当前任务为空时跳过这次循环
                                continue;

                            if (mState == UP_STATE)
                            {
                                //电梯状态为向上时
                                sleep(MOVE_FLOOR_PER_TIME); //睡眠
                                int currentFloor=mCurrentFloor.getFloor(); //得到当前楼层
                                int taskFloor=mCurrentTask.getFloor();  //得到任务楼层

                                if(taskFloor<currentFloor)
                                {
                                    //任务楼层高于当前楼层时，将电梯往上一格
                                    mRoomButtons.get(currentFloor).setColor(Color.BLACK);
                                    mRoomButtons.get(currentFloor-1).setColor(Color.RED);
                                    mCurrentFloor.setFloor(currentFloor-1);

                                    if (taskFloor==mCurrentFloor.getFloor())
                                    {
                                        //任务楼层等于当前楼层时，完成任务
                                        mCurrentTask.setFinish(true);
                                    }

                                }else if (taskFloor==currentFloor)
                                {
                                    //任务楼层等于当前楼层时，完成任务
                                    mCurrentTask.setFinish(true);
                                }


                            }else if(mState==DOWN_STATE)
                            {
                                //电梯为向下状态时
                                sleep(MOVE_FLOOR_PER_TIME);//睡眠
                                int currentFloor=mCurrentFloor.getFloor();//得到当前楼层
                                int taskFloor=mCurrentTask.getFloor();   //得到任务楼层

                                if(taskFloor>currentFloor)
                                {
                                    //任务楼层低于当前楼层时，将电梯往下一格
                                    mRoomButtons.get(currentFloor).setColor(Color.BLACK);
                                    mRoomButtons.get(currentFloor+1).setColor(Color.RED);
                                    mCurrentFloor.setFloor(currentFloor+1);

                                    if(taskFloor==mCurrentFloor.getFloor())
                                    {
                                        //任务楼层等于电梯楼层，王朝任务
                                        mCurrentTask.setFinish(true);
                                    }

                                }else if(taskFloor==currentFloor)
                                {
                                    //任务楼层等于电梯楼层时，完成任务
                                    mCurrentTask.setFinish(true);
                                }

                            }else if (mState==WAIT_STATE)
                            {
                                //当前状态为等待时，说明任务楼层等于当前楼层，完成任务
                                mCurrentTask.setFinish(true);
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run()
                                {
                                    mLabelFloor.setText(""+(20-mCurrentFloor.getFloor()));
                                }
                            }); //更新显现当前楼层的标签

                            if (mCurrentTask.isFinish())
                            {
                                //当前任务完成时
                                int floor=mCurrentFloor.getFloor(); //得到当前所在楼层
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRoomButtons.get(floor).setColor(Color.YELLOW);
                                        mRoomButtons.get(floor).setText("O");
                                        mLabelState.setText(STATE_LABEL_OPEN);
                                    }
                                });//任务完成，更新按钮颜色。说明有人在这一层出电梯，将电梯们打开，设置状态为开门

                                sleep(OPEN_DOOR_TIME);//睡眠
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRoomButtons.get(floor).setColor(Color.RED);
                                        mRoomButtons.get(floor).setText("");
                                        updateLabelState();
                                    }
                                });//更新按钮颜色和标签，将门关闭

                                int buttonType=mCurrentTask.getButtonType();//得到任务的按钮类型
                                if(buttonType==Task.TYPE_DIGIT)
                                {
                                    //按钮类型为数字按钮时
                                    SwingUtilities.invokeLater(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        mDigitButtons.get(floor).setColor(Color.WHITE);
                                        mDigitButtons.get(floor).setEnabled(true);
                                    }
                                });//归置对应数字按钮，恢复颜色和点击

                                }else if(buttonType==Task.TYPE_UP)
                                {
                                    //按钮类型为向上按钮时
                                    SwingUtilities.invokeLater(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            mUpButtons.get(floor).setColor(Color.WHITE);
                                            mUpButtons.get(floor).setEnabled(true);
                                        }
                                    });//归置对应数字按钮，恢复颜色和点击

                                }else if(buttonType==Task.TYPE_DOWN)
                                {
                                    //按钮类型为向下时
                                    SwingUtilities.invokeLater(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            mDownButtons.get(floor).setColor(Color.WHITE);
                                            mDownButtons.get(floor).setEnabled(true);
                                        }
                                    });//归置对应数字按钮，恢复颜色和点击

                                }else if (buttonType==Task.TYPE_ALERT)
                                {
                                    //按钮类型为警报时
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLabelState.setText(STATE_LABEL_WARN);
                                            mState=WARN_STATE;
                                            mCloseAlertButtons.get(T_ID).setEnabled(true);
                                        }
                                    });//设置标签状态为警报，电梯状态为警报状态，
                                }

                                mTasks.remove(mCurrentTask);//任务完成移除任务
                                mCurrentTask=null;      //设置当前任务为空
                            }
                            else
                            {
                                //任务没完成时，更新显示状态的标签
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateLabelState();
                                    }
                                });
                            }

                        }catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }
                    }else
                    {
                        //就绪任务队列为空时
                        if (mState!=WARN_STATE)
                        {
                            //电梯状态不为警报时
                            mState = WAIT_STATE;//状态设置为等待
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    mLabelState.setText(STATE_LABEL_WAIT);
                                }
                            });//更新显示电梯状态的标签
                        }else
                        {
                            //电梯状态为报警时
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    mLabelState.setText(STATE_LABEL_WARN);
                                }
                            });//更新显示电梯状态为警报
                        }
                        try
                        {
                            sleep(CHECK_TASK_PER_TIME);//睡眠
                        }catch (InterruptedException ie){
                            ie.printStackTrace();
                        }

                    }
                }
            }
        }


    }
}







class Task{
    //表示任务的类
    public static final int TYPE_UP=1;    //button的类型为向上
    public static final int TYPE_DOWN=-1;//button的类型为向下
    public static final int TYPE_DIGIT=0;//button的类型为数字
    public static final int TYPE_ALERT=2;//button的类型为警报

    private int mFloor;     //表示任务中要去的楼层
    private boolean mFinished; //表示任务是否完成
    private final int mButtonType;//存储触发任务中button的类型

    public Task(int floor,int type){
        //任务的构造函数
        mFloor=floor;//设置楼层
        mFinished=false;//设置任务未完成
        mButtonType=type;//设置button类型
    }

    public int getFloor() {
        return mFloor;
    }//返回楼层

    public boolean isFinish() {
        return mFinished;
    }//返回是否完成任务

    public void setFinish(boolean finish) {
        mFinished = finish;
    }//设置任务的完成

    public int getButtonType() {
        return mButtonType;
    }//返回button类型
}


class Floor{
    //表示楼层的类
    protected int mFloor;//存储楼层

    public Floor(int floor){
        mFloor=floor;
    }//构造函数，传递floor

    public int getFloor() {
        return mFloor;
    }//获取楼层

    public void setFloor(int floor){
        mFloor=floor;
    }//设置楼层
}

class RoomButton extends JButton{
    //继承JButton
    private Color mColor;//存储button的颜色

    public RoomButton(String str,Color color){
        super(str);
        mColor=color;
        setBackground(color);
    }//构造函数，传递名字和颜色

    public void setColor(Color color){
        mColor=color;
        setBackground(color);
    }//设置颜色

    public Color getColor(){
        return mColor;
    }//获取颜色

}

class OpenButton extends RoomButton{
//继承RoomButton，用来作为上下按钮
    public OpenButton(String str){
        super(str,Color.WHITE);
    }//传递button的名字，并设置颜色为白色
}


