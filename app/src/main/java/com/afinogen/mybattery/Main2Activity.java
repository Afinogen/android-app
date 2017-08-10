package com.afinogen.mybattery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Main2Activity extends AppCompatActivity {

    DBHelper dbHelper;
    long idBattery;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        idBattery = intent.getLongExtra("idBattery", 0);
        Log.d(LOG_TAG, "idBattery = " + idBattery);
        if (idBattery != 0) {
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("battery", null, "id = ?", new String[]{Long.toString(idBattery)}, null, null, null);
            c.moveToFirst();
            Log.d(LOG_TAG, "query = " + c.getCount());
            if (c.getCount() > 0) {
                // определяем номера столбцов по имени в выборке
                int idColIndex = c.getColumnIndex("id");
                int vendorColIndex = c.getColumnIndex("vendor");
                int capacityColIndex = c.getColumnIndex("capacity");
                int countCellsColIndex = c.getColumnIndex("count_cells");
                int dischargeRateColIndex = c.getColumnIndex("discharge_rate");
                int commentColIndex = c.getColumnIndex("comment");

                ((EditText) findViewById(R.id.akkumVendor)).setText(c.getString(vendorColIndex));
                ((EditText) findViewById(R.id.akkumCapacity)).setText(c.getString(capacityColIndex));
                ((Spinner) findViewById(R.id.akkumCountCells)).setSelection(c.getInt(countCellsColIndex), true);
                ((EditText) findViewById(R.id.akkumDischargeRate)).setText(c.getString(dischargeRateColIndex));
                ((EditText) findViewById(R.id.akkumComment)).setText(c.getString(commentColIndex));
            } else {
                idBattery = 0;
            }
            c.close();
        }

        Button btnAdd = (Button) findViewById(R.id.saveButton);
        // создаем объект для создания и управления версиями БД

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                db.execSQL("drop table battery;");
                // закрываем подключение к БД
//                dbHelper.close();

//                db = dbHelper.getWritableDatabase();
//                dbHelper.onCreate(db);

                //Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                //db.execSQL("drop table battery;");
                //int clearCount = db.delete("battery", null, null);
                //Log.d(LOG_TAG, "deleted rows count = " + clearCount);

                // создаем объект для данных
                ContentValues cv = new ContentValues();

                // получаем данные из полей ввода
                String vendor = ((EditText) findViewById(R.id.akkumVendor)).getText().toString();
                String capacity = ((EditText) findViewById(R.id.akkumCapacity)).getText().toString();
                String countCells = ((Spinner) findViewById(R.id.akkumCountCells)).getSelectedItem().toString();
                String dischargeRate = ((EditText) findViewById(R.id.akkumDischargeRate)).getText().toString();
                String comment = ((EditText) findViewById(R.id.akkumComment)).getText().toString();

                cv.put("vendor", vendor);
                cv.put("capacity", capacity);
                cv.put("count_cells", countCells);
                cv.put("discharge_rate", dischargeRate);
                cv.put("comment", comment);

                long rowID;
                if (idBattery > 0) {
                    int res = db.update("battery", cv, "id = ?", new String[] {Long.toString(idBattery)});
                    Log.d(LOG_TAG, "row updated, ID = " + idBattery + ", res = " + res);
                    rowID = idBattery;
                } else {
                    // вставляем запись и получаем ее ID
                    rowID = db.insert("battery", null, cv);
                    Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                }

                // закрываем подключение к БД
                dbHelper.close();
                Snackbar.make(view, "Сохранено " + rowID, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table battery ("
                    + "id integer primary key autoincrement,"
                    + "vendor text,"
                    + "capacity text,"
                    + "comment text,"
                    + "count_cells text,"
                    + "discharge_rate integer,"
                    + "count_s integer,"
                    + "cycles integer,"
                    + "status integer" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
