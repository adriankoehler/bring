package c.example.bring;

/**
 * Created by Adrian on 26.11.2017.
 */

import android.content.Context;

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

    public static List<Memo> getMemosFromJsonFile(Context con, String path) throws JSONException{
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
        for(int i = 0; i<jsonArray.length(); i++){
            JSONObject o = jsonArray.getJSONObject(i);
            memos.add(new Memo(o.getLong("time"), o.getString("text")));
        }
        return memos;
    }

    public static void saveMemoToJsonFile(Context con, List<Memo> memos, String filename){
        File jsonfile = new File(con.getFilesDir().getAbsolutePath()+"/"+CreateListActivity.LISTPATH+"/"+filename+".json");
        if(jsonfile.exists()) jsonfile.delete();
        JSONArray jsonArray = new JSONArray();
        for(Memo m : memos){
            JSONObject o = new JSONObject();
            try {
                o.put("time", m.getTime());
                o.put("text", m.getText());
            } catch (JSONException e){
                e.printStackTrace();
            }
            jsonArray.put(o);
        }
        String json = jsonArray.toString();
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