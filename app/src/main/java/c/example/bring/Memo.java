package c.example.bring;

/**
 * Created by Adrian on 26.11.2017.
 */

import android.content.Context;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Memo implements Serializable {
    private Date date;
    private String text;
    private boolean fullDisplayed;
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy 'at' hh:mm aaa");

    public Memo() {
        this.date = new Date();
    }

    public Memo(long time, String text) {
        this.date = new Date(time);
        this.text = text;
    }

    public String getDate() {
        return dateFormat.format(date);
    }

    public long getTime() {
        return date.getTime();
    }

    public void setTime(long time) {
        this.date = new Date(time);
    }

    public void setText(String text) {
        this.text = text;
    }
//    TODO: anzahl hinzufügen; wenn anzahl=0 -> löschen

    public String getText() {
        return this.text;
    }

    public String getShortText() {
        String temp = text.replaceAll("\n", " ");
        if (temp.length() > 25) {
            return temp.substring(0, 25) + "...";
        } else {
            return temp;
        }
    }

    public void setFullDisplayed(boolean fullDisplayed) {
        this.fullDisplayed = fullDisplayed;
    }

    public boolean isFullDisplayed() {
        return this.fullDisplayed;
    }
    @Override
    public String toString() {
        return this.text;
    }

    public static ListObject getListObject(Context con, String path) throws JSONException{
        ListObject o = getListObjectFromJsonFile(con, path);
        if(o.isOnline()){
            downloadList(con, path, new File(con.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+path));
            o = getListObjectFromJsonFile(con, path);
        }
        return o;
    }

    public static void downloadList(final Context c, final String path, final File jsonFile){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClient con = null;
                try
                {
                    con = new FTPClient();
                    con.connect("200644.webhosting67.1blu.de");

                    if (con.login("ftp200644-lists", "sicherespasswort"))
                    {
                        con.enterLocalPassiveMode(); // important!
                        con.setFileType(FTP.BINARY_FILE_TYPE);
                        File f = new File(c.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/downloaded.json");
                        if(f.exists()) f.delete();
                        OutputStream out = new FileOutputStream(f);
                        boolean success = con.retrieveFile("/"+path, out);
                        out.close();
                        if(!success){
                            f.delete();
                        }
                        else{
                            jsonFile.delete();
                            f.renameTo(jsonFile);
                        }
                        con.logout();
                        con.disconnect();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        while (t.isAlive()){}
    }

    private static ListObject getListObjectFromJsonFile(Context con, String path) throws JSONException{
        List<Memo> memos = new LinkedList<>();
        File jsonFile = new File(con.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+path);
        String json = "";
        try {
            FileInputStream reader = new FileInputStream(jsonFile);
            byte[] bytes = new byte[(int)jsonFile.length()];
            reader.read(bytes);
            json = new String(bytes);
        } catch (Exception e){
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray(json);
        JSONObject online = jsonArray.getJSONObject(0);
        JSONObject type = jsonArray.getJSONObject(1);
        JSONArray memoArray = jsonArray.optJSONArray(2);
        for(int i = 0; i<memoArray.length(); i++){
            JSONObject o = memoArray.getJSONObject(i);
            memos.add(new Memo(o.getLong("time"), o.getString("text")));
        }
        ListType t = ListType.MEMO;
        if(type.getInt("type") == 1) t = ListType.COUNT;
        return new ListObject(memos, online.getBoolean("online"), t);
    }

    public static void saveMemoToJsonFile(Context con, ListObject listObject, String filename){
        File jsonfile = new File(con.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+filename+".json");
        writeJsonToFile(jsonfile, getJsonStringFromListObject(listObject));
        if(listObject.isOnline()){
            uploadList(con, filename);
        }
    }

    public static void makeOnline(Context c, String filename, ListObject o){
        o.setOnline(true);
        String json = getJsonStringFromListObject(o);
        File jsonFile = new File(c.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+filename+".json");
        writeJsonToFile(jsonFile, json);
    }
    public static void uploadList(final Context c, String filename){
        final File jsonFile = new File(c.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+filename+".json");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClient con = null;
                try
                {
                    con = new FTPClient();
                    con.connect("200644.webhosting67.1blu.de");

                    if (con.login("ftp200644-lists", "sicherespasswort"))
                    {
                        con.enterLocalPassiveMode(); // important!
                        con.setFileType(FTP.BINARY_FILE_TYPE);
                        FileInputStream in = new FileInputStream(jsonFile);
                        boolean success = con.storeFile("/"+jsonFile.getName(), in);
                        in.close();
                        if(!success)
                            Toast.makeText(c, c.getString(R.string.error), Toast.LENGTH_LONG).show();
                        con.logout();
                        con.disconnect();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private static String getJsonStringFromListObject(ListObject o){
        JSONArray jsonArray = new JSONArray();
        JSONObject online = new JSONObject();
        JSONObject type = new JSONObject();
        int i = 0;
        switch (o.getType()){
            case COUNT:
                i = 1;
                break;
        }
        try {
            type.put("type", i);
            online.put("online", o.isOnline());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JSONArray memoArray = new JSONArray();
        for(Memo m : o.getMemos()){
            JSONObject jo = new JSONObject();
            try {
                jo.put("time", m.getTime());
                jo.put("text", m.getText());
            } catch (JSONException e){
                e.printStackTrace();
            }
            memoArray.put(jo);
        }
        jsonArray.put(online);
        jsonArray.put(type);
        jsonArray.put(memoArray);
        String json = jsonArray.toString();
        return json;
    }
    private static void writeJsonToFile(File jsonfile, String json){
        if(jsonfile.exists()) jsonfile.delete();
        try {
            jsonfile.createNewFile();
            FileOutputStream writer = new FileOutputStream(jsonfile);
            writer.write(json.getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}