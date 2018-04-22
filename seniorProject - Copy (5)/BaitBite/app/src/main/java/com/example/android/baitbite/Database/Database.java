package com.example.android.baitbite.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.android.baitbite.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper{
    private static final String DB_NAME = "BaitBiteDB.db";
    private static final int DB_VERSION = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts(){
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"DishName", "DishID","Quantity","Price","Discount"};
        String sqlTable = "OrderDetail";

        queryBuilder.setTables(sqlTable);
        Cursor crusor = queryBuilder.query(database, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(crusor.moveToFirst()){
            do{
                result.add(new Order(crusor.getString(crusor.getColumnIndex("DishID")),
                        crusor.getString(crusor.getColumnIndex("DishName")),
                        crusor.getString(crusor.getColumnIndex("Quantity")),
                        crusor.getString(crusor.getColumnIndex("Price")),
                        crusor.getString(crusor.getColumnIndex("Discount"))
                ));
            }while (crusor.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format(
                "INSERT INTO OrderDetail(DishID, DishName, Quantity, Price, Discount)" +
                        "VALUES('%s', '%s', '%s', '%s', '%s');",
                order.getDishID(),
                order.getDishName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());

        database.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        database.execSQL(query);
    }



}
