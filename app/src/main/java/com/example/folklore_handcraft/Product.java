package com.example.folklore_handcraft;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "product_name")
    private String name;

    @ColumnInfo(name = "product_image")
    private  int image;

    @ColumnInfo(name = "product_description")
    private String description;

    @ColumnInfo(name = "product_price")
    private String price;

    @ColumnInfo(name = "contact")
    private String contact;

    public Product(String name, String description, String price, String contact) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.contact = contact;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getContact() {
        return contact;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
