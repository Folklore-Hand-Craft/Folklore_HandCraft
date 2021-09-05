package com.example.folklore_handcraft;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, version = 1)

public abstract class ProductDataBase extends RoomDatabase {
    public abstract ProductDao  productDao();
}
