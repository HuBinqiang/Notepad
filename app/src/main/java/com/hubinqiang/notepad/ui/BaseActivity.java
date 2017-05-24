package com.hubinqiang.notepad.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.WindowManager;

import com.hubinqiang.notepad.App;
import com.hubinqiang.notepad.R;
import com.hubinqiang.notepad.utils.PreferenceUtils;
import com.hubinqiang.notepad.utils.ThemeUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;


public abstract class BaseActivity extends AppCompatActivity {

    private ObjectGraph activityGraph;

    protected PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceUtils = PreferenceUtils.getInstance(this);
        initTheme();
        super.onCreate(savedInstanceState);
        initWindow();
        activityGraph = ((App) getApplication()).createScopedGraph(getModules().toArray());
        activityGraph.inject(this);
        setContentView(getLayoutView());
        ButterKnife.inject(this);
        initToolbar();
    }

    // 初始化主题
    private void initTheme(){
        ThemeUtils.Theme theme = getCurrentTheme();
        ThemeUtils.changTheme(this, theme);
    }

    // 初始化窗口
    @TargetApi(19)
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getStatusBarColor());
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    // 初始化Toolbar
    protected void initToolbar(Toolbar toolbar){
        if (toolbar == null)
            return;
        toolbar.setBackgroundColor(getColorPrimary());
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(R.color.action_bar_title_color);
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public int getStatusBarColor(){
        return getColorPrimary();
    }

    public int getColorPrimary(){
        TypedValue typedValue = new  TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    // 主题设置弹出窗口
    protected AlertDialog.Builder generateDialogBuilder(){
        ThemeUtils.Theme theme = getCurrentTheme();
        AlertDialog.Builder builder;
        int style = R.style.RedDialogTheme;
        switch (theme){
            case BROWN:
                style = R.style.BrownDialogTheme;
                break;
            case BLUE:
                style = R.style.BlueDialogTheme;
                break;
            case BLUE_GREY:
                style = R.style.BlueGreyDialogTheme;
                break;
            case YELLOW:
                style = R.style.YellowDialogTheme;
                break;
            case DEEP_PURPLE:
                style = R.style.DeepPurpleDialogTheme;
                break;
            case PINK:
                style = R.style.PinkDialogTheme;
                break;
            case GREEN:
                style = R.style.GreenDialogTheme;
                break;
            default:
                break;
        }
        builder = new AlertDialog.Builder(this, style);
        return builder;
    }

    protected ThemeUtils.Theme getCurrentTheme(){
        int value = preferenceUtils.getIntParam(getString(R.string.change_theme_key), 0);
        return ThemeUtils.Theme.mapValueToTheme(value);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }


    // 默认的返回finish事件

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
    }

    protected abstract int getLayoutView();

    protected abstract List<Object> getModules();

    protected abstract void initToolbar();
}
