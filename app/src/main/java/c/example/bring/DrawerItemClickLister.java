package c.example.bring;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by root on 10.01.18.
 */

public class DrawerItemClickLister implements ListView.OnItemClickListener {

    ArrayList<String> names;
    ActionBar actionBar;
    ListView drawerList;
    DrawerLayout dLayout;

    public DrawerItemClickLister(ActionBar b, ArrayList<String> names, ListView drawerList, DrawerLayout layout){
        this.actionBar = b;
        this.names = names;
        this.drawerList = drawerList;
        this.dLayout = layout;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    public void setListNames(ArrayList<String> ln){
        this.names = ln;
    }

    private void selectItem(int pos){
        drawerList.setItemChecked(pos, true);
        actionBar.setTitle(names.get(pos));
        dLayout.closeDrawer(drawerList);
    }
}
