package c.example.bring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

public class CreateListActivity extends AppCompatActivity {

    public static final String LISTPATH = "lists";

    Button save;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_list);

        save = (Button) findViewById(R.id.saveNewListBtn);
        name = (EditText) findViewById(R.id.listNameEdit);


        if(getIntent().getExtras().containsKey("editname")) {
            boolean editname = getIntent().getExtras().getBoolean("editname");
            if (editname) {
                setTitle(getString(R.string.renamelist));
                save.setText(getString(R.string.okay));
                final String filename = getIntent().getExtras().getString("filename");
                name.setText(filename);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File old = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + LISTPATH, filename + ".json");
                        File newf = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + LISTPATH, name.getText().toString() + ".json");
                        if (old.exists()) {
                            old.renameTo(newf);
                            Intent i = new Intent();
                            i.putExtra("newname", name.getText().toString());
                            setResult(RESULT_OK, i);
                            finish();
                        }
                        setResult(RESULT_CANCELED, getIntent());
                        finish();
                    }
                });
            } else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File dir = new File(getApplicationContext().getFilesDir(), LISTPATH);
                        if (dir.exists() && !dir.isDirectory()) dir.delete();
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        File newFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + LISTPATH, name.getText().toString() + ".json");
                        if (newFile.exists())
                            newFile.delete();
                        try {
                            newFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }
                });
            }
        }

        if(getIntent().getExtras().containsKey("download")){

            setTitle(getString(R.string.downloadlist));
            save.setText(getString(R.string.dl));

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = name.getText().toString()+".json";
                    Memo.downloadList(getApplicationContext(), path, new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+LISTPATH+"/"+path));
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        }
    }
}
