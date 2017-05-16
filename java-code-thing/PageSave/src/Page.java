import java.util.ArrayList;

/**
 * Created by 我 on 2017/5/15.
 */
public class Page {
    //表示一个页
    private int mIndex;//表示逻辑页号

    public Page(int index){
        mIndex=index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}
