package com.example.fitnesstrackerapp.ui.adapters;

import com.example.fitnesstrackerapp.data.entity.FitnessActivity;

public class HistoryListItem {

    public enum ItemType {
        HEADER, ITEM
    }

    private final ItemType itemType;
    private final String headerTitle;
    private final FitnessActivity activity;

    public HistoryListItem(String headerTitle) {
        this.itemType = ItemType.HEADER;
        this.headerTitle = headerTitle;
        this.activity = null;
    }

    public HistoryListItem(FitnessActivity activity) {
        this.itemType = ItemType.ITEM;
        this.headerTitle = null;
        this.activity = activity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public FitnessActivity getActivity() {
        return activity;
    }
}