package com.example.grandinstrument;

import androidx.annotation.Nullable;

import com.example.grandinstrument.data_base_model.OrderHeader;
import com.example.grandinstrument.utils.Utils;

public class SelectOrderModel {
    private boolean isSelected;
    private String uuid;

    public SelectOrderModel(String uuid) {
        this.uuid = uuid;
    }

    public static int getQtySelected() {
        int qty=0;
        if (Utils.mSelectedList == null){
            return 0;
        }
        for (int i=0; i< Utils.mSelectedList.size();i++){
            SelectOrderModel selectOrderModel = Utils.mSelectedList.get(i);
            if (selectOrderModel.isSelected()){
                qty++;
            }
        }
        return qty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (((SelectOrderModel) obj).uuid.equals(uuid)){
            return true;
        }
        return false;
    }

    public String getUuid() {
        return uuid;
    }
}
