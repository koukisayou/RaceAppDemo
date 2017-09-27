package com.example.sayoukouki.raceappdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteデータベースを扱うためのヘルパー
 * データベースにはレースタイムを記録する
 * Created by SayouKouki on 2017/08/19.
 */

public class CreateProductHelper extends SQLiteOpenHelper {
    /**
     * Constructor
     * @param context
     */
    public CreateProductHelper(Context context){
        super(context,"mydb",null,1);//db名を第2引数で指定
    }

    /**
     * DB作成時に呼ばれる
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //タイム記録用のテーブル record を作成するSQL文
        String sql = "create table record (" +
                "minute integer not null," +
                "seconds integer not null," +
                "millis integer not null," +
                "machine_id integer not null," +
                "id integer primary key autoincrement)";
        db.execSQL(sql);//実行
        //以下は基準タイムとして記録をインサートする
        sql = "insert into record values(0,30,0,0,1)";
        db.execSQL(sql);
        sql = "insert into record values(1,0,0,0,2)";
        db.execSQL(sql);
        sql = "insert into record values(1,30,0,0,3)";
        db.execSQL(sql);
        sql = "insert into record values(2,0,0,0,4)";
        db.execSQL(sql);
        sql = "insert into record values(3,0,0,0,5)";
        db.execSQL(sql);
    }

    /**
     * DBのバージョン更新時に呼ばれる
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
