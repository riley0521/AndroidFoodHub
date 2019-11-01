package com.example.riley.androidfoodhub.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.riley.androidfoodhub.Model.Favorites;
import com.example.riley.androidfoodhub.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by riley on 12/12/2017.
 */

public class Database extends SQLiteAssetHelper {
    private static String DB_NAME = "foodfinal.db";
    private static int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public boolean checkFoodExists(String foodId, String userPhone) {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE userPhone='%s' AND productId='%s'",userPhone,foodId);
        cursor = db.rawQuery(SQLQuery,null);
        if(cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"userPhone","productId","productName","quantity","price","discount","image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,"userPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("userPhone")),
                        c.getString(c.getColumnIndex("productId")),
                        c.getString(c.getColumnIndex("productName")),
                        c.getString(c.getColumnIndex("quantity")),
                        c.getString(c.getColumnIndex("price")),
                        c.getString(c.getColumnIndex("discount")),
                        c.getString(c.getColumnIndex("image"))
                ));
            } while (c.moveToNext());
        }
        return  result;

    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(userPhone,productId,productName,quantity,price,discount,image)VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE userPhone='%s'",userPhone);
        db.execSQL(query);
    }



    public int getCountCart(String userPhone) {

        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE userPhone='%s'",userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET quantity= '%s' WHERE userPhone = '%s' AND productId='%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart(String foodId,String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET quantity= quantity+1 WHERE userPhone='%s' AND productId='%s'",userPhone,foodId);
        db.execSQL(query);
    }

    public void increaseCartDetail(String quantity,String foodId,String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET quantity= quantity+'%s' WHERE userPhone='%s' AND productId='%s'",quantity,userPhone,foodId);
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE userPhone='%s' and productId='%s'",phone,productId);
        db.execSQL(query);
    }

    public void addToFavorites(Favorites food) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(" +
                "FoodId,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDescription,UserPhone) " +
                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodPrice(),
                food.getFoodMenuId(),
                food.getFoodImage(),
                food.getFoodDiscount(),
                food.getFoodDescription(),
                food.getUserPhone());
        db.execSQL(query);
    }

    public void removeFromFavorites(String foodId,String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s' and UserPhone='%s';",foodId,userPhone);
        db.execSQL(query);
    }

    public boolean isFavorites(String foodId,String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s' and UserPhone='%s';",foodId,userPhone);

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favorites> getAllFavorites(String userPhone) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","FoodId","FoodName","FoodPrice","FoodMenuId","FoodImage","FoodDiscount","FoodDescription"};
        String sqlTable = "Favorites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Favorites> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do {
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("FoodId")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodMenuId")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("FoodDiscount")),
                        c.getString(c.getColumnIndex("FoodDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))
                ));
            } while (c.moveToNext());
        }
        return  result;

    }


}
