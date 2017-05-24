package com.hubinqiang.notepad.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hubinqiang.notepad.R;
import com.hubinqiang.notepad.module.DataModule;
import com.hubinqiang.notepad.ui.fragments.SettingFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_setting;
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DataModule());
    }

    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
        toolbar.setTitle(R.string.setting);
    }

    private void init(){
        SettingFragment settingFragment = SettingFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, settingFragment).commit();
    }

}
