package c.example.bring;

import java.util.List;

/**
 * Created by root on 15.01.18.
 */


enum ListType { MEMO, COUNT }

public class ListObject {


    private List<Memo> memos;
    private boolean isOnline;
    private ListType type;

    public ListObject(List<Memo> m, boolean online, ListType t){
        memos = m;
        isOnline = online;
        type = t;
    }

    public List<Memo> getMemos(){
        return memos;
    }
    public boolean isOnline(){
        return isOnline;
    }
    public ListType getType(){
        return type;
    }

    public void setMemos(List<Memo> m){
        memos = m;
    }
    public void setOnline(boolean b){
        isOnline = b;
    }

}
