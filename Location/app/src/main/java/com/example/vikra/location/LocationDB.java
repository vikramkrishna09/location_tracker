package com.example.vikra.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikra on 11/10/2017.
 */

public class LocationDB extends SQLiteOpenHelper implements Serializable {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "CheckIns";
    private static final String TABLE_Check_Ins = "CheckInsTable";
    private static final String Table_ParentTable = "Parent_Table";
    private static final String Table_ChildTable = "Child_Table";
    private static final String Key_ID = "ID";
    private static final String Key_Geoid = "GeoId";
    private static final String Key_Lat = "Lat";
    private static final String Key_Lng = "Lng";
    private static final String Key_Time = "Time";
    private static final String Key_Address = "Address";
    private static final String Key_Name = "Name";
    private static final String Key_Sp = "Sp";
    private int flag = 0;
    private int i = 0;
    public LocationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        i = 1;
        if(i == 1)
        {
            String CreateTable1 = "CREATE TABLE " + Table_ChildTable + "("
                    + Key_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Key_Lat + " REAL,"
                    + Key_Lng + " REAL,"
                    + Key_Sp + " INTEGER,"
                    + Key_Time + " INTEGER,"
                    + Key_Geoid + " Integer " + ")";
            db.execSQL(CreateTable1);

            String CreateTable2 = "CREATE TABLE " + Table_ParentTable + "("
                    + Key_Geoid + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Key_Address + " TEXT,"
                    + Key_Name + " TEXT " + ")";
            db.execSQL(CreateTable2);


        }
        i = 0;
        if(i == 0) {
            String CreateTable = "CREATE TABLE " + TABLE_Check_Ins + "("
                    + Key_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Key_Lat + " REAL,"
                    + Key_Lng + " REAL,"
                    + Key_Time + " INTEGER,"
                    + Key_Address + " TEXT,"
                    + Key_Name + " TEXT " + ")";

            db.execSQL(CreateTable);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Check_Ins);
        db.execSQL("DROP TABLE IF EXISTS " + Table_ChildTable);
        db.execSQL("DROP TABLE IF EXISTS " + Table_ParentTable);
        onCreate(db);
    }




    int addNewCheckIn(CheckIn newCheckIn, int v, int c)
    {
        i = 1;
        int Geocode = 0;
        if(i == 1){
            SQLiteDatabase db = this.getWritableDatabase();

            if(v == 1 || v == 3) {
                ContentValues values = new ContentValues();
                values.put(Key_Address, newCheckIn.returnAddress());
                values.put(Key_Name, newCheckIn.returnName());

                db.insert(Table_ParentTable, null, values);
                // db.close();

                String query = "select last_insert_rowid()";
                Cursor cursor = db.rawQuery(query, null);
                // Geocode = 0;
                if (cursor.moveToFirst()) {
                    do {
                        Geocode = Integer.parseInt(cursor.getString(0));


                    } while (cursor.moveToNext());
                }
                db.close();
            }
            else
            {
                Geocode = c;
            }

            db = this.getWritableDatabase();
          ContentValues values1 = new ContentValues();
            values1.put(Key_Lat, newCheckIn.returnLat());
            values1.put(Key_Lng, newCheckIn.returnLng());
            values1.put(Key_Sp,v);
            values1.put(Key_Time, newCheckIn.returnepochtime());
            //values1.put(Key_StartingPoint,v);
            values1.put(Key_Geoid,Geocode);
            db.insert(Table_ChildTable,null,values1);
            db.close();


        }

        //i = 0;
        if(i == 0) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Key_Lat, newCheckIn.returnLat());
            values.put(Key_Lng, newCheckIn.returnLng());
            values.put(Key_Time, newCheckIn.returnepochtime());
            values.put(Key_Address, newCheckIn.returnAddress());
            values.put(Key_Name, newCheckIn.returnName());

            db.insert(TABLE_Check_Ins, null, values);
            db.close();

        }
        return Geocode;
    }



    public List<CheckIn> getCheckIns()
    {
        i = 1;
        List<CheckIn> CheckIn = null;
        if(i == 1)
        {
           CheckIn = new ArrayList<CheckIn>();
            String selectQuery = "SELECT * FROM "
                    + Table_ParentTable + " INNER JOIN "
                    + Table_ChildTable + " ON "
                    + Table_ParentTable + "." + Key_Geoid + " = "
                    + Table_ChildTable + "." + Key_Geoid +
                    " WHERE " +
                    Table_ChildTable + "." + Key_Sp + " != 3;";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if(cursor.moveToFirst())
            {
                do {

                    double Lat = Double.parseDouble(cursor.getString(4));
                    double Lng = Double.parseDouble(cursor.getString(5));
                    long time = Long.parseLong(cursor.getString(7));

                    String address = cursor.getString(1);
                    String name = cursor.getString(2);

                    CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                    CheckIn.add(newCheckin);







                }while(cursor.moveToNext());
            }
            return CheckIn;
        }

        if(i == 0) {
             CheckIn = new ArrayList<CheckIn>();
            String selectQuery = "SELECT  * FROM " + TABLE_Check_Ins;


            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    double Lat = Double.parseDouble(cursor.getString(1));
                    double Lng = Double.parseDouble(cursor.getString(2));
                    long time = Long.parseLong(cursor.getString(3));
                    String address = cursor.getString(4);
                    String name = cursor.getString(5);

                    CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                    CheckIn.add(newCheckin);

                } while (cursor.moveToNext());
            }

            return CheckIn;
        }

        return CheckIn;

    }

    public List<CheckIn> getStartingPoints()
    {
        List<CheckIn> checkIn = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db == null)
            return checkIn;

        String selectQuery = "SELECT * FROM "
                + Table_ParentTable + " INNER JOIN "
                + Table_ChildTable + " ON "
                + Table_ParentTable + "." + Key_Geoid + " = "
                + Table_ChildTable + "." + Key_Geoid +
                " WHERE " +
                Table_ChildTable + "." + Key_Sp + " = 1;";

        Cursor cursor = db.rawQuery(selectQuery, null);
        checkIn = new ArrayList<CheckIn>();
        if(cursor.moveToFirst())
        {
            do {
                double Lat = Double.parseDouble(cursor.getString(4));
                double Lng = Double.parseDouble(cursor.getString(5));
                long time = Long.parseLong(cursor.getString(6));

                String address = cursor.getString(1);
                String name = cursor.getString(2);
                CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                checkIn.add(newCheckin);


            }while(cursor.moveToNext());

        }







        return checkIn;
    }

    void update(String name, double lat, double lng, long time, String address)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int geocode = -1;

        String selectQuery = "SELECT * FROM "
                + Table_ParentTable + " INNER JOIN "
                + Table_ChildTable + " ON "
                + Table_ParentTable + "." + Key_Geoid + " = "
                + Table_ChildTable + "." + Key_Geoid +
                " WHERE " +
                Table_ParentTable + "." + Key_Name + " = " + "'" + name + "'" + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        //checkIn = new ArrayList<CheckIn>();
        if(cursor.moveToFirst())
        {
            do {
                geocode = Integer.parseInt(cursor.getString(0));
                /*
                double Lat = Double.parseDouble(cursor.getString(4));
                double Lng = Double.parseDouble(cursor.getString(5));
                long time = Long.parseLong(cursor.getString(6));

                String address = cursor.getString(1);
                String name = cursor.getString(2);
                CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                //checkIn.add(newCheckin);
                */


            }while(cursor.moveToNext());

        }

        String rawquery1 = "UPDATE " + Table_ChildTable
                            + " SET " + Key_Lat + " = " + lat
                            + ", " + Key_Lng + " = " + lng
                            + ", " + Key_Time + " = " + time
                            + " WHERE " + Key_Geoid + " = " + geocode + ";";
        db.execSQL(rawquery1);

        if(address.equals(""))
        {
            address = " No address";
        }
        String rawquery2 = "UPDATE " + Table_ParentTable
                + " SET " + Key_Address + " = '" + address + "'"
                + " WHERE " + Key_Geoid + " = " + geocode + ";";
        db.execSQL(rawquery2);

    }

    public List<CheckIn> getFalseCheckIns()
    {
        i = 1;
        List<CheckIn> CheckIn = null;
        if(i == 1)
        {
            CheckIn = new ArrayList<CheckIn>();
            String selectQuery = "SELECT * FROM "
                    + Table_ParentTable + " INNER JOIN "
                    + Table_ChildTable + " ON "
                    + Table_ParentTable + "." + Key_Geoid + " = "
                    + Table_ChildTable + "." + Key_Geoid +
                    " WHERE " +
                    Table_ChildTable + "." + Key_Sp + " == 3;";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if(cursor.moveToFirst())
            {
                do {

                    double Lat = Double.parseDouble(cursor.getString(4));
                    double Lng = Double.parseDouble(cursor.getString(5));
                    long time = Long.parseLong(cursor.getString(7));

                    String address = cursor.getString(1);
                    String name = cursor.getString(2);

                    CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                    CheckIn.add(newCheckin);







                }while(cursor.moveToNext());
            }
            return CheckIn;
        }

        if(i == 0) {
            CheckIn = new ArrayList<CheckIn>();
            String selectQuery = "SELECT  * FROM " + TABLE_Check_Ins;


            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    double Lat = Double.parseDouble(cursor.getString(1));
                    double Lng = Double.parseDouble(cursor.getString(2));
                    long time = Long.parseLong(cursor.getString(3));
                    String address = cursor.getString(4);
                    String name = cursor.getString(5);

                    CheckIn newCheckin = new CheckIn(Lat, Lng, time, name, address);
                    CheckIn.add(newCheckin);

                } while (cursor.moveToNext());
            }

            return CheckIn;
        }

        return CheckIn;

    }

    public static class CheckIn implements Serializable {
        private double Lat;
        private double Lng;
        private long epochtime;
        private String name;
        private String address;
        public CheckIn(double Lat,double Lng, long epochtime,String name, String address)
        {
            this.Lat = Lat;
            this.Lng = Lng;
            this.epochtime = epochtime;
            this.name = name;
            this.address = address;
        }

        public double returnLat()
        {
            return Lat;
        }

        public double returnLng()
        {
            return Lng;
        }

        public long returnepochtime()
        {
            return epochtime;
        }

        public String returnName()
        {
            return name;
        }

        public String returnAddress()
        {
            return address;
        }

        public void setEpochtime(long newtime){
            epochtime = newtime;
        }

        public void setName(String newname)
        {
            name = newname;
        }

        public void setAddress(String newaddress)
        {
            address = newaddress;
        }
    }













}
