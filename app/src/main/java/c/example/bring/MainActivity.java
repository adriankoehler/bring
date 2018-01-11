/*package c.example.bring;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}*/

package c.example.bring;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout mainContent, listContent;

    private DrawerLayout drawerLayout;
    private ListView lists;
    private ArrayList<String> listNames;
    private String selectedList;
    private DrawerItemClickLister drawerItemClickLister;

    private Button openListBtn, createListBtn;
    private ListView listItems;
    private MemoAdapter memoAdapter;
    private FloatingActionButton addMemoItemBtn;
    private List<Memo> memos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        /*** INITIALIZING ***/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listNames = refreshDrawerList();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lists = (ListView) findViewById(R.id.drawerlist);

        drawerItemClickLister = new DrawerItemClickLister(getSupportActionBar(), listNames, lists, drawerLayout){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                StringBuilder sb = new StringBuilder();
                sb.append((String)lists.getAdapter().getItem(position));
                selectedList = sb.toString();
                sb.append(".json");
                try {
                    memos = Memo.getMemosFromJsonFile(getApplicationContext(), sb.toString());
                } catch (JSONException e) {
                    memos = new LinkedList<>();
                }
                memoAdapter = new MemoAdapter(getApplicationContext(), memos);
                listItems.setAdapter(memoAdapter);
                mainContent.setVisibility(View.GONE);
                listContent.setVisibility(View.VISIBLE);
            }
        };

        lists.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listNames));
        lists.setOnItemClickListener(drawerItemClickLister);

        openListBtn = (Button) findViewById(R.id.openButton);
        createListBtn = (Button) findViewById(R.id.createButton);

        mainContent = (RelativeLayout) findViewById(R.id.maincontent);
        listContent = (RelativeLayout) findViewById(R.id.listcontent);

        listItems = (ListView) findViewById(R.id.listView);
        addMemoItemBtn = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        /*** MAIN MENU  ***/
        openListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(lists);
            }
        });
        createListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateListActivity.class);
                startActivityForResult(i, 1);
            }
        });


        /*** LIST DISPLAY ***/
        addMemoItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("requestcode", 2);
                startActivityForResult(intent,2 );
            }
        });

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Memo memo = memos.get(position);
                Memo memo = memoAdapter.getItem(position);
                TextView txtMemo = (TextView) view.findViewById(R.id.txtMemo);
                if (memo.isFullDisplayed()) {
                    txtMemo.setText(memo.getShortText());
                    memo.setFullDisplayed(false);
                } else {
                    txtMemo.setText(memo.getText());
                    memo.setFullDisplayed(true);
                }
            }
        });
    }


    private class MemoAdapter extends ArrayAdapter<Memo> {

        public MemoAdapter(Context context, List<Memo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_list_item, parent, false);
            }

            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);
            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtMemo = (TextView) convertView.findViewById(R.id.txtMemo);

            final int pos = position;
            final Memo memo = memoAdapter.getItem(position);
            memo.setFullDisplayed(false);
            txtDate.setText(memo.getDate());
            txtMemo.setText(memo.getShortText());
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.putExtra("time", memo.getTime());
                    intent.putExtra("text", memo.getText());
                    intent.putExtra("pos", pos);
                    intent.putExtra("requestcode", 3);
                    startActivityForResult(intent, 3);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memos.remove(pos);
                    memoAdapter = new MemoAdapter(getApplicationContext(), memos);
                    listItems.setAdapter(memoAdapter);
                    Memo.saveMemoToJsonFile(getApplicationContext(), memos, selectedList);
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /******** REQUEST CODES ***********/
        /*** 1 = new list               ***/
        /*** 2 = new list item          ***/
        /*** 3 = edit list item         ***/
        /**********************************/
        if(requestCode==1){
            Log.e("LIST", "refreshing list");
            listNames = refreshDrawerList();
            drawerItemClickLister.setListNames(listNames);
            lists.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listNames));
            lists.setOnItemClickListener(drawerItemClickLister);
        }
        if(requestCode == 2 && resultCode == RESULT_OK){
            memos.add(new Memo(data.getExtras().getLong("time"), data.getExtras().getString("text")));
            memoAdapter = new MemoAdapter(getApplicationContext(), memos);
            listItems.setAdapter(memoAdapter);
            Memo.saveMemoToJsonFile(getApplicationContext(), memos, selectedList);
        }
        if(requestCode == 3 && resultCode == RESULT_OK){
            memos.remove(data.getExtras().getInt("pos"));
            memos.add(data.getExtras().getInt("pos"),
                    new Memo(data.getExtras().getLong("time"), data.getExtras().getString("text")));
            memoAdapter = new MemoAdapter(getApplicationContext(), memos);
            listItems.setAdapter(memoAdapter);
            Memo.saveMemoToJsonFile(getApplicationContext(), memos, selectedList);
        }
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> refreshDrawerList(){
        ArrayList<String> jsonFiles = new ArrayList<String>();
        File files = new File(getFilesDir().getAbsolutePath() + "/" + CreateListActivity.LISTPATH);
        if(files.isDirectory()){
            File[] found = files.listFiles();
            jsonFiles.clear();
            for(File f : found){
                String s = f.getName();
                jsonFiles.add(s.substring(0, s.length()-5));
            }
        }
        return jsonFiles;
    }

    private void toggleDrawer(){
        if(drawerLayout.isDrawerOpen(lists))
            drawerLayout.closeDrawer(lists);
        else
            drawerLayout.openDrawer(lists);

    }
}
