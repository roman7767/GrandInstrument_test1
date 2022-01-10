package com.example.grandinstrument.data_base_model;

public class Goods {
    private String id;
    private String id_1c;
    private String code_1c;
    private String article;
    private String description;
    private String brand;
    private String category;
    private boolean available;
    private float rrc;
    private int in_box;
    private int in_package;

    public int getIn_box() {
        return in_box;
    }

    public void setIn_box(int in_box) {
        this.in_box = in_box;
    }

    public int getIn_package() {
        return in_package;
    }

    public void setIn_package(int in_package) {
        this.in_package = in_package;
    }

    public String getCode_1c() {
        return code_1c;
    }

    public void setCode_1c(String code_1c) {
        this.code_1c = code_1c;
    }

    private boolean good_of_week;
    private float price;
    private float quantity;
    private String date_of_renovation;
    private String url_of_image;


    public boolean isGood_of_week() {
        return good_of_week;
    }

    public void setGood_of_week(boolean good_of_week) {
        this.good_of_week = good_of_week;
    }

    public Goods(String id_1c, String code_1c, String article, String description, String brand, String category, boolean available, float rrc, float price, float quantity, String date_of_renovation, String url_of_image) {
        this.id_1c = id_1c;
        this.code_1c = code_1c;
        this.article = article;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.available = available;
        this.rrc = rrc;
        this.price = price;
        this.quantity = quantity;
        this.date_of_renovation = date_of_renovation;
        this.url_of_image = url_of_image;
    }

    public Goods() {
        this.id_1c = id_1c;
        this.article = article;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public String getUrl_of_image() {
        return url_of_image;
    }

    public String getId() {
        return id;
    }

    public String getId_1c() {
        return id_1c;
    }

    public String getArticle() {
        return article;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public float getRrc() {
        return rrc;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getDate_of_renovation() {
        return date_of_renovation;
    }


    public void setId_1c(String id_1c) {
        this.id_1c = id_1c;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setRrc(float rrc) {
        this.rrc = rrc;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setDate_of_renovation(String date_of_renovation) {
        this.date_of_renovation = date_of_renovation;
    }


    public void setUrl_of_image(String url_of_image) {
        this.url_of_image = url_of_image;
    }
}
