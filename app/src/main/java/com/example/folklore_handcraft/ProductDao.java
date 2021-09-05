package com.example.folklore_handcraft;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertOne(Product product);

    @Query("SELECT * FROM product WHERE id like :id ")
    Product findById(long id);

    @Query("SELECT * FROM Product")
    List<Product> findAll();

    @Delete
    void delete(Product product);
}
