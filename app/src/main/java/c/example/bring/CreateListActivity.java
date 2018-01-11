package c.example.bring;

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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getApplicationContext().getFilesDir(), LISTPATH);
                if(dir.exists() && !dir.isDirectory()) dir.delete();
                if(!dir.exists()) {
                    dir.mkdir();
                }
                File newFile = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+LISTPATH, name.getText().toString()+".json");
                if(newFile.exists())
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
