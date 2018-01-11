package c.example.bring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditActivity extends AppCompatActivity {
    private EditText etText;
    private Button btnSave;
    private Button btnCancel;
    private Memo memo;

    private int requestCode;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.etText = (EditText) findViewById(R.id.etText);
        this.btnSave = (Button) findViewById(R.id.btnSave);
        this.btnCancel = (Button) findViewById(R.id.btnCancel);

        requestCode = getIntent().getExtras().getInt("requestcode");
        memo = new Memo();

        if(requestCode == 3){
            memo = new Memo(getIntent().getExtras().getLong("time"),
                    getIntent().getExtras().getString("text"));
            etText.setText(memo.getText());
            position = getIntent().getExtras().getInt("pos");
        }

        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
    }

    public void onSaveClicked() {
        Intent res = new Intent();
        res.putExtra("time", memo.getTime());
        res.putExtra("text", etText.getText().toString());
        res.putExtra("pos", position);
        setResult(RESULT_OK, res);
        finish();
    }

    public void onCancelClicked() {
        this.finish();
    }
}
