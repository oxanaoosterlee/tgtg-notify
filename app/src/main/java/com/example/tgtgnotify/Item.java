package com.example.tgtgnotify;

public class Item {
    String itemId;
    String displayName;
    String store;
    int price;
    int items_available;

    Item(String id,
         String displayName,
         String store,
         int price,
         int available) {
        this.itemId = id;
        this.displayName = displayName;
        this.store = store;
        this.price = price;
        this.items_available = available;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStore() {
        return store;
    }

    public int getPrice() {
        return price;
    }

    public int getItems_available() {
        return items_available;
    }

    public boolean isAvailable() {
        return items_available > 0;
    }
}
