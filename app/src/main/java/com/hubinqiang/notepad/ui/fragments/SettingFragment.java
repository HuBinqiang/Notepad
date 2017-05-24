package com.hubinqiang.notepad.ui.fragments;


import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.jenzz.materialpreference.SwitchPreference;
import com.hubinqiang.notepad.R;
import com.hubinqiang.notepad.adpater.ColorsListAdapter;
import com.hubinqiang.notepad.module.DataModule;
import com.hubinqiang.notepad.utils.NoteConfig;
import com.hubinqiang.notepad.utils.PreferenceUtils;
import com.hubinqiang.notepad.utils.ThemeUtils;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 设置页面
 */
public class SettingFragment extends BaseFragment{

    public static final String PREFERENCE_FILE_NAME = "note.settings";

    private SwitchPreference rightHandModeSwitch;

    private boolean rightHandMode;

    private PreferenceUtils preferenceUtils;

    public static SettingFragment newInstance(){
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = PreferenceUtils.getInstance(getActivity());
        addPreferencesFromResource(R.xml.prefs);
        getPreferenceManager().setSharedPreferencesName(PREFERENCE_FILE_NAME);

        rightHandMode = preferenceUtils.getBooleanParam(getString(R.string.right_hand_mode_key));

        rightHandModeSwitch = (SwitchPreference)findPreference(getString(R.string.right_hand_mode_key));
        rightHandModeSwitch.setChecked(rightHandMode);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View listView = view.findViewById(android.R.id.list);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
    }

    public SettingFragment() {
        super();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,  android.preference.Preference preference) {
        if (preference == null)
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        String key = preference.getKey();

        if (TextUtils.equals(key, getString(R.string.change_theme_key))){
           showThemeChooseDialog();
        }


        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DataModule());
    }


    // 显示主题选择弹窗
    private void showThemeChooseDialog(){
        AlertDialog.Builder builder = generateDialogBuilder();
        builder.setTitle(R.string.change_theme);
        Integer[] res = new Integer[]{R.drawable.red_round, R.drawable.brown_round, R.drawable.blue_round,
                R.drawable.blue_grey_round, R.drawable.yellow_round, R.drawable.deep_purple_round,
                R.drawable.pink_round, R.drawable.green_round};
        List<Integer> list = Arrays.asList(res);
        ColorsListAdapter adapter = new ColorsListAdapter(getActivity(), list);
        adapter.setCheckItem(getCurrentTheme().getIntValue());
        GridView gridView = (GridView)LayoutInflater.from(getActivity()).inflate(R.layout.colors_panel_layout, null);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setAdapter(adapter);
        builder.setView(gridView);
        final AlertDialog dialog = builder.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                int value = getCurrentTheme().getIntValue();
                if (value != position){
                    preferenceUtils.saveParam(getString(R.string.change_theme_key), position);
                    changeTheme(ThemeUtils.Theme.mapValueToTheme(position));
                }
            }
        });
    }

    // 改变主题
    private void changeTheme(ThemeUtils.Theme theme){
        if (activity == null)
            return;
        EventBus.getDefault().post(NoteConfig.CHANGE_THEME_EVENT);
        activity.finish();
    }
}
