package c.example.bring;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by root on 10.01.18.
 */

public class DrawerItemLongClickListener implements ListView.OnItemLongClickListener {

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
