package com.example.tgtgnotify;

import java.util.ArrayList;

public class ItemContainer {
        ArrayList<Item> items;

    public void ItemContainer(){
        this.items = new ArrayList<Item>();
    }


    public void addItem(Item item){
        items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }




}
