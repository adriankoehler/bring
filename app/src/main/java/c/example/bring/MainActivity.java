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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private ListView lists;
    private String selectedList;
    private DrawerItemClickLister drawerItemClickLister;

    private Button openListBtn, createListBtn, downloadListBtn;
    private ListView listItems;
    private MemoAdapter memoAdapter;
    private FloatingActionButton addMemoItemBtn;
    private ListObject listObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        /*** INITIALIZING ***/
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lists = (ListView) findViewById(R.id.drawerlist);

        drawerItemClickLister = new DrawerItemClickLister(getSupportActionBar(), new ArrayList<String>(), lists, drawerLayout){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                StringBuilder sb = new StringBuilder();
                sb.append((String) lists.getAdapter().getItem(position));
                selectedList = sb.toString();
                sb.append(".json");
                try {
                    listObject = Memo.getListObject(getApplicationContext(), sb.toString());
                } catch (JSONException e) {
                    listObject = new ListObject(new LinkedList<Memo>(), false, ListType.MEMO);
                }
                memoAdapter = new MemoAdapter(getApplicationContext(), listObject.getMemos());
                listItems.setAdapter(memoAdapter);
                mainContent.setVisibility(View.GONE);
                listContent.setVisibility(View.VISIBLE);
                toolbar.getMenu().clear();
                if(!listObject.isOnline())
                    getMenuInflater().inflate(R.menu.listmenu, toolbar.getMenu());
                else
                    getMenuInflater().inflate(R.menu.onlinelistmenu, toolbar.getMenu());
            }
        };

        renewDrawer();

        openListBtn = (Button) findViewById(R.id.openButton);
        createListBtn = (Button) findViewById(R.id.createButton);
        downloadListBtn = (Button) findViewById(R.id.downloadButton);

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
                i.putExtra("editname", false);
                startActivityForResult(i, 1);
            }
        });
        downloadListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateListActivity.class);
                i.putExtra("download", true);
                startActivityForResult(i, 5);
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
                    listObject.getMemos().remove(pos);
                    memoAdapter = new MemoAdapter(getApplicationContext(), listObject.getMemos());
                    listItems.setAdapter(memoAdapter);
                    Memo.saveMemoToJsonFile(getApplicationContext(), listObject, selectedList);
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
        /*** 4 = rename list            ***/
        /**  5 = download list          ***/
        /**********************************/
        if(requestCode==1){
            renewDrawer();
        }
        if(requestCode == 2 && resultCode == RESULT_OK){
            listObject.getMemos().add(new Memo(data.getExtras().getLong("time"), data.getExtras().getString("text")));
            memoAdapter = new MemoAdapter(getApplicationContext(), listObject.getMemos());
            listItems.setAdapter(memoAdapter);
            Memo.saveMemoToJsonFile(getApplicationContext(), listObject, selectedList);
        }
        if(requestCode == 3 && resultCode == RESULT_OK){
            listObject.getMemos().remove(data.getExtras().getInt("pos"));
            listObject.getMemos().add(data.getExtras().getInt("pos"),
                    new Memo(data.getExtras().getLong("time"), data.getExtras().getString("text")));
            memoAdapter = new MemoAdapter(getApplicationContext(), listObject.getMemos());
            listItems.setAdapter(memoAdapter);
            Memo.saveMemoToJsonFile(getApplicationContext(), listObject, selectedList);
        }
        if(requestCode == 4 && resultCode == RESULT_OK){
            renewDrawer();
            String n = data.getExtras().getString("newname");
            selectedList = n;
            getSupportActionBar().setTitle(n);
        }
        if(requestCode == 5 && resultCode == RESULT_OK){
            renewDrawer();
        }
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer();
                return true;
            case R.id.action_rename:
                Intent i = new Intent(MainActivity.this, CreateListActivity.class);
                i.putExtra("editname", true);
                i.putExtra("filename", selectedList);
                startActivityForResult(i, 4);
                break;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //delete current list
                                StringBuilder fileName = new StringBuilder();
                                fileName.append(selectedList).append(".json");
                                File f = new File(getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+fileName.toString());
                                if(f.exists()) f.delete();
                                renewDrawer();
                                //got to menu
                                goToMenu();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.deleteconfirmation)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                break;
            case R.id.action_home:
                goToMenu();
                break;
            case R.id.action_upload:
                //make list online
                Memo.makeOnline(getApplicationContext(), selectedList, listObject);
                //then upload
                Memo.uploadList(getApplicationContext(), selectedList);
                //change action bar
                toolbar.getMenu().clear();
                getMenuInflater().inflate(R.menu.onlinelistmenu, toolbar.getMenu());
                break;
            case R.id.action_refresh:
                try {
                    listObject = Memo.getListObject(getApplicationContext(), selectedList + ".json");
                    memoAdapter = new MemoAdapter(getApplicationContext(), listObject.getMemos());
                    listItems.setAdapter(memoAdapter);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharetext, selectedList));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));

                break;
            default:
                return MainActivity.super.onMenuItemSelected(item.getItemId(), item);
        }
        switch (item.getItemId()){
        }
        return super.onOptionsItemSelected(item);
    }

    private void renewDrawer(){
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
        drawerItemClickLister.setListNames(jsonFiles);
        lists.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, jsonFiles));
        lists.setOnItemClickListener(drawerItemClickLister);
    }

    private void toggleDrawer(){
        if(drawerLayout.isDrawerOpen(lists))
            drawerLayout.closeDrawer(lists);
        else
            drawerLayout.openDrawer(lists);
    }

    private void goToMenu(){
        mainContent.setVisibility(View.VISIBLE);
        listContent.setVisibility(View.GONE);
        toolbar.getMenu().clear();
        toolbar.setTitle(R.string.app_name);
    }
}
