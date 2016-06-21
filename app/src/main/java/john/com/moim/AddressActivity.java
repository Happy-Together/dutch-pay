package john.com.moim;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import john.com.moim.database.DbOpenHelper;

public class AddressActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_main);
        listView = (ListView) findViewById(R.id.listView);
        loadListView();
        registerForContextMenu(listView);

        Button button_add = (Button) findViewById(R.id.button10);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String s = editText.getText().toString();

                mDbOpenHelper = new DbOpenHelper(AddressActivity.this);
                mDbOpenHelper.open();

                ContentValues values = new ContentValues();

                values.put("name", s);
                values.put("create_date", System.currentTimeMillis());

                DbOpenHelper.mDB.insert("address", null, values);

                mDbOpenHelper.close();

                adapter.add(s);
                listView.setAdapter(adapter);

                editText.setText("");
            }
        });

    }

    private void loadListView() {
        mDbOpenHelper = new DbOpenHelper(AddressActivity.this);
        mDbOpenHelper.open();

        Cursor cursor = DbOpenHelper.mDB.query("address", null, null, null, null, null, "name");

        list = new ArrayList<String>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(name);
        }

        mDbOpenHelper.close();

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.BLACK);

                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.address_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.address_remove:
                remove_address(((TextView) info.targetView).getText().toString());
                loadListView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void remove_address(String name) {
        mDbOpenHelper = new DbOpenHelper(AddressActivity.this);
        mDbOpenHelper.open();
        DbOpenHelper.mDB.delete("address", "name=?", new String[]{name});
        mDbOpenHelper.close();
    }

    private void edit_address(String bname, String aname) {
        mDbOpenHelper = new DbOpenHelper(AddressActivity.this);
        mDbOpenHelper.open();
        ContentValues args = new ContentValues();
        args.put("name", aname);
        DbOpenHelper.mDB.update("address", args, "name=?", new String[]{bname});
        mDbOpenHelper.close();
    }

}
