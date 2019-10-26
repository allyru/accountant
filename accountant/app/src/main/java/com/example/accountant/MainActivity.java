package com.example.accountant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.util.Log;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.CalendarView;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "Logs";
    final String FILENAME = "statisticIN";

    private Button Bin, Bout,Bsave,Bcancel, Bstatistic;
    private TextView Tsum;
    private ConstraintLayout CLsum, CLmain, CLcalendar;
    private TableLayout TLstatistic;
    private EditText EdTnum;
    private Menu menu;
    private Spinner spinner;
    private Spinner spinDate;
    private Spinner spinTime;

    DBHelper dbHelper;

    boolean x;
    String[][] mass = new String[10][2];
    int massIndexROWS = 0;
    int massIndexCOLM = 0;
    String selectedDate;

    static int ROWS = 2;
    static int COLM = 2;

    String[] dataIn = {"Зарплата", "Премия"};
    String[] dataOut = {"Еда", "Хозтовары", "Электроника", "Развлечение", "Разное"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.spinner); //выподающее меню
        spinDate = (Spinner) findViewById(R.id.spinDate); //дата
        spinTime = (Spinner) findViewById(R.id.spinTime); //время

        Tsum = (TextView) findViewById(R.id.Tsum); // Баланс
        Bin = (Button) findViewById(R.id.Bin); // Кнопка для внесения суммы
        Bout = (Button) findViewById(R.id.Bout); // Кнопка для снятия суммы
        Bsave = (Button) findViewById(R.id.Bsave); // Кнопка для подтверждения результата
        Bcancel = (Button) findViewById(R.id.Bcancel); // Кнопка для отмены
        Bstatistic = (Button) findViewById(R.id.bStatistic); // Кнопка для демонстрации статистики
        EdTnum = (EditText) findViewById(R.id.EdTnum); // Ввод суммы

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        CLsum = (ConstraintLayout) findViewById(R.id.CLsum);
        CLmain = (ConstraintLayout) findViewById(R.id.CLmain);
        CLcalendar = (ConstraintLayout) findViewById(R.id.CLcalendar);
        TLstatistic = (TableLayout) findViewById(R.id.TLstatistic);

        TLstatistic.setVisibility(View.INVISIBLE);
        CLmain.setVisibility(View.VISIBLE);
        CLsum.setVisibility(View.INVISIBLE);
        CLcalendar.setVisibility(View.INVISIBLE);

        Bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = true;
                funcSelect();
                CLmain.setVisibility(View.INVISIBLE);
                CLsum.setVisibility(View.VISIBLE);

            }
        });

        Bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = false;
                funcSelect();
                CLmain.setVisibility(View.INVISIBLE);
                CLsum.setVisibility(View.VISIBLE);

            }
        });

        Bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test();
                CLmain.setVisibility(View.VISIBLE);
                CLsum.setVisibility(View.INVISIBLE);
            }
        });

        Bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdTnum.setText("");
                CLmain.setVisibility(View.VISIBLE);
                CLsum.setVisibility(View.INVISIBLE);
            }
        });

        Bstatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLmain.setVisibility(View.INVISIBLE);
                TLstatistic.setVisibility(View.VISIBLE);
                funcTable();
            }
        });

        //Вызывает календарь по нажатию на выподающее меню
        spinDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CLcalendar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        //Создаёт календарь
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
                                            int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                selectedDate = new StringBuilder().append(mMonth + 1)
                        .append("-").append(mDay).append("-").append(mYear)
                        .append(" ").toString();
                funcSelectData();
                CLcalendar.setVisibility(View.INVISIBLE);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        menu.add("Главное меню");
        menu.add("Настройки");
        menu.add("menu3");
        menu.add("menu4");

        return super.onCreateOptionsMenu(menu);
    }



    public void test()
    {
        String sum = Tsum.getText().toString();
        String num = EdTnum.getText().toString();

        //mass[massIndexROWS][massIndexCOLM] = (selectedDate + " " + spinTime.getSelectedItem() + "");
        mass[massIndexROWS][massIndexCOLM] = spinner.getSelectedItem().toString();
        massIndexCOLM++;
        mass[massIndexROWS][massIndexCOLM] = num + " RUB";
        massIndexCOLM = 0;
        massIndexROWS++;

        int a;
        if(x)
        {
            a = Integer.parseInt(sum) + Integer.parseInt(num);
        }else
        {
            a = Integer.parseInt(sum) - Integer.parseInt(num);
        }

        sum = a + "";
        EdTnum.setText("");
        Tsum.setText(sum);
        CLsum.setVisibility(View.INVISIBLE);
    }

    public void funcSelect() //Заполнение выпадающего меню. Отвечает за доходы/расходы
    {
        if(x)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, dataIn);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
        }else
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, dataOut);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
        }
    }

    public void funcSelectData() //Заполнение выподающего меню. Дата
    {
        String[] date = new String[1];
        date[0] = selectedDate;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, date);
        spinDate.setAdapter(adapter);
        spinDate.setSelection(0);
    }

    public void funcTable() //Создание и заполнение таблицы
    {
        for (int i = 0; i < mass.length; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.setGravity(Gravity.CENTER_VERTICAL);
            TLstatistic.setStretchAllColumns(true);
            TLstatistic.setShrinkAllColumns(true);

            TextView textView1 = new TextView(this);
            textView1.setText(mass[i][0]);
            textView1.setTextSize(24);

            TextView textView2 = new TextView(this);
            textView2.setGravity(Gravity.CENTER);
            textView2.setText(mass[i][1]);
            textView2.setTextSize(24);

            tableRow.addView(textView1, 0);
            tableRow.addView(textView2, 1);

            TLstatistic.addView(tableRow, i);
        }

    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "statisticDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table incomeTable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text" + ");");
            db.execSQL("create table costsTable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
