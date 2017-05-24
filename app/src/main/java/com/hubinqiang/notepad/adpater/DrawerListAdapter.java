package com.hubinqiang.notepad.adpater;

import android.content.Context;

import com.hubinqiang.notepad.R;

import java.util.List;


public class DrawerListAdapter extends SimpleListAdapter{

    public DrawerListAdapter(Context mContext, List<String> list) {
        super(mContext, list);
    }

    @Override
    protected int getLayout() {
        return R.layout.drawer_list_item_layout;
    }
}
