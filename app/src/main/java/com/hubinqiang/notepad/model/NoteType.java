package com.hubinqiang.notepad.model;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;


public class NoteType{

    @JSONField(serialize=false, deserialize=false)
    public final static int ALL_COUNT = 4;

    private List<String> types = new ArrayList<>();

    public void addType(String type){
        if (types != null && types.size() < ALL_COUNT && !TextUtils.isEmpty(type)){
            types.add(type);
        }
    }



    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
