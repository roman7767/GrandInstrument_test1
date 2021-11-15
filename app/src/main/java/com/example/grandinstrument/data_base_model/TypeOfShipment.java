package com.example.grandinstrument.data_base_model;

import android.text.Html;

import androidx.annotation.Nullable;

import com.example.grandinstrument.utils.Utils;

public class TypeOfShipment {
    private String code_1c;
    private String name;
    private boolean available;
    private String code_main_type;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCode_main_type() {
        return code_main_type;
    }

    public void setCode_main_type(String code_main_type) {
        this.code_main_type = code_main_type;
    }

    public TypeOfShipment() {
    }

    public TypeOfShipment(String code_1c, String name, boolean available) {
        this.code_1c = code_1c;
        this.name = name;
        this.available = available;
    }

    public String getCode_1c() {
        return code_1c;
    }

    public void setCode_1c(String code_1c) {
        this.code_1c = code_1c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
       return name;
    }

    public static TypeOfShipment getObjectByCode(String code){
        if (Utils.shipmentList == null){
            return null;
        }

        for (int i=1; i < Utils.shipmentList.size();i++){
            TypeOfShipment typeOfShipment = Utils.shipmentList.get(i);
            if (typeOfShipment.getCode_1c().equals(code)){
                return typeOfShipment;
            }
        }

        return null;

    }

    public static int getIndexByCode(String code){
        if (Utils.shipmentList == null){
            return 0;
        }

        for (int i=1; i < Utils.shipmentList.size();i++){
            TypeOfShipment typeOfShipment = Utils.shipmentList.get(i);
            if (typeOfShipment.getCode_1c().equals(code)){
                return i;
            }
        }

        return 0;

    }

}
