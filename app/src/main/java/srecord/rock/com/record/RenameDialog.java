package srecord.rock.com.record;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.List;

/**
 * Created by RockStar0116 on 2016.09.20.
 */
public class RenameDialog extends Dialog implements View.OnClickListener{
    Context _this;
    Button ok, cancel;
    EditText file_name;
    String file_path;
    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }

    public RenameDialog(Context context, String file_name) {
        super(context);
        _this = context;
        file_path = file_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rename);
        ok = (Button)findViewById(R.id.rename_ok);
        cancel = (Button)findViewById(R.id.rename_cancel);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        file_name = (EditText)findViewById(R.id.file_path_txt);
        file_name.setText(file_path.toString());
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }
    public void changeFileName(){
        File current_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/" + file_path);
        String target_file_name = file_name.getText().toString();
        if (!target_file_name.contains(".")) {
            target_file_name += ".mp3";
        }
        File target_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/" + target_file_name);
        if(current_file.exists()){
            current_file.renameTo(target_file);
        }
    }
    @Override
    public void onClick(View view) {
        int view_id = view.getId();
        switch(view_id)
        {
            case R.id.rename_ok:
                changeFileName();
                this.dismiss();
                break;
            case R.id.rename_cancel:
                this.dismiss();
                break;

        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ((ShowActivity)_this).refreshList();
    }
}
