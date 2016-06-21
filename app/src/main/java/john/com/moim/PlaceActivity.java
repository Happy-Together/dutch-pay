package john.com.moim;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.util.ArrayList;

import john.com.moim.database.DbOpenHelper;
import john.com.moim.navercafe.NaverCafe;

public class PlaceActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private String moim_title;
    private String place_title;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private String result;

    private final String APPID = "john.com.moim";

    private final String CAFE_URL = "2039seoul";

    private int REQ_PICK_MEDIA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_main);

        Intent intent = getIntent();
        moim_title = intent.getExtras().getString("moim_title");
        place_title = intent.getExtras().getString("place_title");

        setBtnEvent();

        loadDb();

        result = "";
    }

    private void loadDb() {
        mDbOpenHelper = new DbOpenHelper(PlaceActivity.this);
        mDbOpenHelper.open();

        String where[] = {moim_title, place_title};
        Cursor cursor = DbOpenHelper.mDB.query("place_result", null, "moim_title=? and place_title=?", where, null, null, "create_date desc");

        if (cursor.moveToNext()) {
            textView = (TextView) findViewById(R.id.textView4);
            textView2 = (TextView) findViewById(R.id.textView6);
            textView3 = (TextView) findViewById(R.id.textView8);
            textView4 = (TextView) findViewById(R.id.textView10);
            textView.setText(cursor.getString(cursor.getColumnIndex("people")));
            textView2.setText(cursor.getString(cursor.getColumnIndex("unit")));
            textView3.setText(cursor.getString(cursor.getColumnIndex("total")));
            textView4.setText(cursor.getString(cursor.getColumnIndex("remainder")));
        }

        mDbOpenHelper.close();
    }

    private void setBtnEvent() {
        TextView text_address = (TextView) findViewById(R.id.textView3);
        text_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbOpenHelper = new DbOpenHelper(PlaceActivity.this);
                mDbOpenHelper.open();

                ArrayList<String> list = new ArrayList<String>();

                Cursor cursor = DbOpenHelper.mDB.query("address", null, null, null, null, null, "name");

                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor.getColumnIndex("name")));
                }

                CharSequence names2[] = new CharSequence[list.size()];

                for (int i = 0; i < list.size(); i++) {
                    names2[i] = list.get(i);
                }

                final CharSequence[] names = names2;

                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                builder.setTitle("Pick a people");
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView textView = (TextView) findViewById(R.id.textView4);
                        String somePeople = textView.getText().toString();

                        if (somePeople.equals("")) {
                            textView.setText(names[which]);
                        } else {
                            textView.setText(somePeople + "," + names[which]);
                        }
                    }
                });
                builder.show();
            }
        });

        text_address.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                onPopupButtonClick(v);
                return false;
            }
        });

        TextView text_unit = (TextView) findViewById(R.id.textView5);
        text_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = {"1", "100", "500", "1000", "2000", "3000", "5000", "10000", "20000", "50000"};
                AlertDialog.Builder ab = new AlertDialog.Builder(PlaceActivity.this);
                ab.setTitle(R.string.divide_unit_text);
                ab.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TextView textView = (TextView) findViewById(R.id.textView6);
                                textView.setText(items[whichButton]);
                                dialog.cancel();
                            }
                        });
                ab.show();
            }
        });

        TextView text_total = (TextView) findViewById(R.id.textView7);
        text_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);

                alert.setTitle(R.string.add_total);

                final EditText name = new EditText(PlaceActivity.this);
                name.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(name);

                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String total = name.getText().toString();

                        if (total.equals("")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage(R.string.warning_total);
                            alert.show();
                            return;
                        }

                        TextView textView = (TextView) findViewById(R.id.textView8);
                        textView.setText(total);
                    }
                });

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        TextView text_remainder = (TextView) findViewById(R.id.textView9);
        text_remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);

                alert.setTitle(R.string.add_remainder_text);

                final EditText name = new EditText(PlaceActivity.this);
                name.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(name);

                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String remainder = name.getText().toString();

                        if (remainder.equals("")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage(R.string.warning_remainder);
                            alert.show();
                            return;
                        }

                        TextView textView = (TextView) findViewById(R.id.textView10);
                        textView.setText(remainder);
                    }
                });

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        Button button_divide = (Button) findViewById(R.id.button3);
        button_divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTableLayout();
            }
        });

        Button button_save = (Button) findViewById(R.id.button4);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = (TextView) findViewById(R.id.textView4);
                textView2 = (TextView) findViewById(R.id.textView6);
                textView3 = (TextView) findViewById(R.id.textView8);

                mDbOpenHelper = new DbOpenHelper(PlaceActivity.this);
                mDbOpenHelper.open();

                ContentValues values = new ContentValues();

                values.put("moim_title", moim_title);
                values.put("place_title", place_title);
                values.put("create_date", System.currentTimeMillis());
                values.put("people", textView.getText().toString());
                values.put("unit", textView2.getText().toString());
                values.put("total", textView3.getText().toString());
                values.put("remainder", textView4.getText().toString());
                values.put("result", System.currentTimeMillis());

                DbOpenHelper.mDB.insert("place_result", null, values);

                mDbOpenHelper.close();
            }
        });

        ImageButton button_send_kakao = (ImageButton) findViewById(R.id.imageButton);
        button_send_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = (TextView) findViewById(R.id.textView4);
                textView2 = (TextView) findViewById(R.id.textView6);
                textView3 = (TextView) findViewById(R.id.textView8);

                final KakaoLink kakaoLink;
                try {
                    kakaoLink = KakaoLink.getKakaoLink(PlaceActivity.this);
                    final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    kakaoTalkLinkMessageBuilder.addText(result);
                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, PlaceActivity.this);
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton button_send_naver = (ImageButton) findViewById(R.id.imageButton2);
        button_send_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = (TextView) findViewById(R.id.textView4);
                textView2 = (TextView) findViewById(R.id.textView6);
                textView3 = (TextView) findViewById(R.id.textView8);

                new NaverCafe(PlaceActivity.this, APPID).write(CAFE_URL, "64", moim_title, result);
            }
        });
    }

    private void createTableLayout() {
        textView = (TextView) findViewById(R.id.textView4);
        textView2 = (TextView) findViewById(R.id.textView6);
        textView3 = (TextView) findViewById(R.id.textView8);
        textView4 = (TextView) findViewById(R.id.textView10);

        if (textView.getText().equals("") || textView2.getText().equals("") || textView3.getText().equals("")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage(R.string.warning_select);
            alert.show();
            return;
        }

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table); // 테이블 id 명

        tableLayout.removeAllViews();

        String[] split = textView.getText().toString().split(",");

        int incoming = 0;
        int number_people = split.length;
        int total = Integer.valueOf(textView3.getText().toString());
        int divide_unit = Integer.valueOf(textView2.getText().toString());
        int remainder = Integer.valueOf(textView4.getText().toString());
        int people_raw_pay = (total - remainder) / number_people;
        int cut_money = people_raw_pay % divide_unit;

        int people_pay = people_raw_pay;

        if (divide_unit != 1) {
            if (cut_money == 0) {
                people_pay = people_raw_pay;
            } else {
                people_pay = people_raw_pay - cut_money + divide_unit;
            }
        }

        for (String aSplit : split) {
            // Creation row
            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            final TextView text = new TextView(this);

            incoming += people_pay;

            text.setText(aSplit + " " + people_pay);
            result += aSplit + " " + people_pay + "\n";
            text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.addView(text);
            tableLayout.addView(tableRow);
        }

        final TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        final TextView text = new TextView(this);
        text.setText("\n걷은돈 : " + incoming + "\n미리 걷은돈 : " + remainder + "\n총지출 : " + total + "\n남은돈 : " + (incoming + remainder - Integer.valueOf(textView3.getText().toString())));
        result += "\n걷은돈 : " + incoming + "\n미리 걷은돈 : " + remainder + "\n총지출 : " + total + "\n남은돈 : " + (incoming + remainder - Integer.valueOf(textView3.getText().toString()));
        text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.addView(text);
        tableLayout.addView(tableRow);
    }

    public void onPopupButtonClick(View view) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.people_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.people_add:
                        AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);

                        alert.setTitle(R.string.add_people);

                        final EditText name = new EditText(PlaceActivity.this);
                        alert.setView(name);

                        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String people = name.getText().toString();

                                if (people.equals("")) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(PlaceActivity.this);
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();     //닫기
                                        }
                                    });
                                    alert.setMessage(R.string.warning_people);
                                    alert.show();
                                    return;
                                }

                                TextView textView = (TextView) findViewById(R.id.textView4);
                                String somePeople = textView.getText().toString();

                                if (somePeople.equals("")) {
                                    textView.setText(people);
                                } else {
                                    textView.setText(somePeople + "," + people);
                                }
                            }
                        });

                        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();
                        break;
                    case R.id.people_reset:
                        TextView textView = (TextView) findViewById(R.id.textView4);
                        textView.setText("");
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

}
