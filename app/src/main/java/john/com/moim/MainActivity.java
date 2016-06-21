package john.com.moim;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

import john.com.moim.database.DbOpenHelper;

public class MainActivity extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayMoim();

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlaceActivity.class);
                intent.putExtra("moim_title", mGroupList.get(groupPosition));
                intent.putExtra("place_title", mChildList.get(groupPosition).get(childPosition));
                startActivity(intent);

                return false;
            }
        });

        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition, Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹 및 차일드를 길게 클릭 했을 경우 이벤트
        mListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onPopupButtonClick(view, position, id);
                return true;
            }
        });

    }

    private ExpandableListView mListView;

    private void setLayout() {
        mListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.add_moim) {
            addMoim();
            return true;
        }

        if (id == R.id.add_place) {
            CharSequence[] items2 = new CharSequence[mGroupList.size()];

            for (int i = 0; i < mGroupList.size(); i++) {
                items2[i] = mGroupList.get(i);
            }

            final CharSequence[] items = items2;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // 제목셋팅
            alertDialogBuilder.setTitle(R.string.add_place_alert);
            alertDialogBuilder.setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            addPlace(items[id].toString());
                        }
                    });

            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();

            return true;
        }

        if (id == R.id.address) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddressActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.refresh_moim) {
            displayMoim();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayMoim() {
        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();

        mDbOpenHelper = new DbOpenHelper(MainActivity.this);
        mDbOpenHelper.open();

        Cursor cursor = DbOpenHelper.mDB.query("moim", null, null, null, null, null, "create_date desc");

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));

            mGroupList.add(title);

            Cursor place_cursor = DbOpenHelper.mDB.query("place", null, null, null, null, null, null);

            ArrayList<String> mChildListContent = new ArrayList<String>();

            while (place_cursor.moveToNext()) {
                if (title.equals(place_cursor.getString(place_cursor.getColumnIndex("moim_title")))) {
                    mChildListContent.add(place_cursor.getString(place_cursor.getColumnIndex("title")));
                }
            }

            mChildList.add(mChildListContent);
        }

        mDbOpenHelper.close();

        mListView.setAdapter(new BaseExpandableAdapter(MainActivity.this, mGroupList, mChildList));
    }

    private void addMoim() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle(R.string.add_moim_text);
        alert.setMessage(R.string.add_moim_sample);

        final EditText name = new EditText(MainActivity.this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String moim_name = name.getText().toString();

                mDbOpenHelper = new DbOpenHelper(MainActivity.this);
                mDbOpenHelper.open();

                ContentValues values = new ContentValues();

                values.put("title", moim_name);
                values.put("create_date", System.currentTimeMillis());

                DbOpenHelper.mDB.insert("moim", null, values);

                mDbOpenHelper.close();
                displayMoim();
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void addPlace(final String moim_title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle(R.string.add_place_text);
        alert.setMessage(R.string.add_place_sample);

        final EditText name = new EditText(MainActivity.this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mDbOpenHelper = new DbOpenHelper(MainActivity.this);
                mDbOpenHelper.open();

                ContentValues values = new ContentValues();

                values.put("moim_title", moim_title);
                values.put("title", name.getText().toString());
                values.put("create_date", System.currentTimeMillis());

                DbOpenHelper.mDB.insert("place", null, values);

                mDbOpenHelper.close();
                displayMoim();
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void removeMoim(final int itemType, final String s) {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);

        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            ab.setMessage(Html.fromHtml("<strong><font color=\"#ff0000\"> " + "경고! "
                    + "</font></strong><br>장소 '" + s + "' 를 삭제하면 복구할 수 없습니다. 삭제를 하겠습니까?"));
        } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            ab.setMessage(Html.fromHtml("<strong><font color=\"#ff0000\"> " + "경고!!!!! "
                    + "</font></strong><br>모임 '" + s + "' 을 삭제하면 복구할 수 없습니다. 삭제를 하겠습니까?"));
        }

        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDbOpenHelper = new DbOpenHelper(MainActivity.this);
                mDbOpenHelper.open();
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    DbOpenHelper.mDB.delete("place", "title=?", new String[]{s});
                    DbOpenHelper.mDB.delete("place_result", "place_title=?", new String[]{s});
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    DbOpenHelper.mDB.delete("moim", "title=?", new String[]{s});
                    DbOpenHelper.mDB.delete("place", "moim_title=?", new String[]{s});
                    DbOpenHelper.mDB.delete("place_result", "moim_title=?", new String[]{s});
                }
                mDbOpenHelper.close();
                displayMoim();
            }
        });

        ab.setNegativeButton("cancel", null);
        ab.show();
    }

    public void onPopupButtonClick(View button, final int position, final long id) {
        PopupMenu popup = new PopupMenu(this, button);

        popup.getMenuInflater().inflate(R.menu.main_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_remove:
                        removeMoim(ExpandableListView.getPackedPositionType(id), mListView.getItemAtPosition(position).toString());
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}
