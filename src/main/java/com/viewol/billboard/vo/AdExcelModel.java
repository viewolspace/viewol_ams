package com.viewol.billboard.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

public class AdExcelModel extends BaseRowModel {

    @ExcelProperty(index = 0)
    private String showRoom;

    @ExcelProperty(index = 1)
    private String itemName;

    @ExcelProperty(index = 2)
    private String num;

    @ExcelProperty(index = 3)
    private String size;

    @ExcelProperty(index = 4)
    private String price;

    public String getShowRoom() {
        return showRoom;
    }

    public void setShowRoom(String showRoom) {
        this.showRoom = showRoom;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}