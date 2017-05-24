package com.hubinqiang.notepad.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hubinqiang.notepad.R;
import com.hubinqiang.notepad.adpater.BaseRecyclerViewAdapter;
import com.hubinqiang.notepad.adpater.DrawerListAdapter;
import com.hubinqiang.notepad.adpater.NotesAdapter;
import com.hubinqiang.notepad.adpater.SimpleListAdapter;
import com.hubinqiang.notepad.model.Note;
import com.hubinqiang.notepad.model.NoteOperateLog;
import com.hubinqiang.notepad.model.NoteType;
import com.hubinqiang.notepad.module.DataModule;
import com.hubinqiang.notepad.utils.JsonUtils;
import com.hubinqiang.notepad.utils.NoteConfig;
import com.hubinqiang.notepad.utils.PreferenceUtils;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import net.tsz.afinal.FinalDb;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{


    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.refresher)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.edit_note_type)
    Button editNoteTypeButton;

    @InjectView(R.id.left_drawer_listview)
    ListView mDrawerMenuListView;

    @InjectView(R.id.left_drawer)
    View drawerRootView;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @InjectView(R.id.progress_wheel)
    ProgressWheel progressWheel;

    @Inject
    FinalDb finalDb;

    private ActionBarDrawerToggle mDrawerToggle;

    private SearchView searchView;

    private NotesAdapter recyclerAdapter;

    private int mCurrentNoteType;

    private boolean rightHandOn = false;


    private boolean hasUpdateNote = false;

    private boolean hasEditClick = false;

    private  List<String> noteTypelist;

    private final String  CURRENT_NOTE_TYPE_KEY = "CURRENT_NOTE_TYPE_KEY";

    private final String  PROGRESS_WHEEL_KEY = "PROGRESS_WHEEL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mCurrentNoteType = savedInstanceState.getInt(CURRENT_NOTE_TYPE_KEY);
            progressWheel.onRestoreInstanceState(savedInstanceState.getParcelable(PROGRESS_WHEEL_KEY));
        }
        // 初始化
        initToolbar();
        initDrawerView();
        initRecyclerView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_NOTE_TYPE_KEY, mCurrentNoteType);
        Parcelable parcelable = progressWheel.onSaveInstanceState();
        outState.putParcelable(PROGRESS_WHEEL_KEY, parcelable);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 判断是否开启右手模式
        if (rightHandOn != preferenceUtils.getBooleanParam(getString(R.string.right_hand_mode_key))){
            rightHandOn = !rightHandOn;
            if (rightHandOn){
                setMenuListViewGravity(Gravity.END);
            }else{
                setMenuListViewGravity(Gravity.START);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 是否有待更新的笔记
        if (hasUpdateNote){
            changeToSelectNoteType(mCurrentNoteType);
            hasUpdateNote = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // 更改笔记事件、更改笔记分类事件、更改笔记本主题事件判断
    public void onEvent(Integer event){
        switch (event){
            case NoteConfig.NOTE_UPDATE_EVENT:
                hasUpdateNote = true;
                break;
            case NoteConfig.NOTE_TYPE_UPDATE_EVENT:
                initDrawerListView();
                break;
            case NoteConfig.CHANGE_THEME_EVENT:
                this.recreate();
                break;
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DataModule());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 配置文件更新
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (toolbar != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //searchItem.expandActionView();
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint(getString(R.string.search_note));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerAdapter.getFilter().filter(s);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                recyclerAdapter.setUpFactor();
                refreshLayout.setEnabled(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refreshLayout.setEnabled(true);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_more:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(drawerRootView)){
            mDrawerLayout.closeDrawer(drawerRootView);
            return true;
        }
        moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
    }

    // 初始化侧边栏数据
    private void initDrawerListView(){
        String json = preferenceUtils.getStringParam(PreferenceUtils.NOTE_TYPE_KEY);
        if (!TextUtils.isEmpty(json)){
            noteTypelist = JsonUtils.parseNoteType(json);
        }else{
            noteTypelist = Arrays.asList(getResources().getStringArray(R.array.drawer_content));
            NoteType type = new NoteType();
            type.setTypes(noteTypelist);
            String text = JsonUtils.jsonNoteType(type);
            preferenceUtils.saveParam(PreferenceUtils.NOTE_TYPE_KEY, text);
        }

        SimpleListAdapter adapter = new DrawerListAdapter(this, noteTypelist);
        mDrawerMenuListView.setAdapter(adapter);
        mDrawerMenuListView.setItemChecked(mCurrentNoteType, true);
        toolbar.setTitle(noteTypelist.get(mCurrentNoteType));
    }

    // 初始化侧边栏
    private void initDrawerView() {
        initDrawerListView();
        mDrawerMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerMenuListView.setItemChecked(position, true);
                openOrCloseDrawer();
                mCurrentNoteType = position;
                changeToSelectNoteType(mCurrentNoteType);
                if (mCurrentNoteType == NoteConfig.NOTE_TRASH_TYPE) {
                    fab.hide();
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                    fab.show();
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(noteTypelist.get(mCurrentNoteType));
                if (hasEditClick){
                    Intent intent = new Intent(MainActivity.this, EditNoteTypeActivity.class);
                    startActivity(intent);
                    hasEditClick = false;
                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        rightHandOn = preferenceUtils.getBooleanParam(getString(R.string.right_hand_mode_key));
        if (rightHandOn){
            setMenuListViewGravity(Gravity.END);
        }
    }



    // 初始化笔记列表
    private void initRecyclerView(){
        showProgressWheel(true);
        initItemLayout();
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new NotesAdapter(initItemData(mCurrentNoteType), this);
        recyclerAdapter.setOnInViewClickListener(R.id.notes_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Note>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, Note values) {
                        super.OnClickListener(parentV, v, position, values);
                        if (mCurrentNoteType == NoteConfig.NOTE_TRASH_TYPE)
                            return;
                        startNoteActivity(NoteActivity.VIEW_NOTE_TYPE, values);
                    }
                });
        recyclerAdapter.setOnInViewClickListener(R.id.note_more,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Note>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, Note values) {
                        super.OnClickListener(parentV, v, position, values);
                        showPopupMenu(v, values);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);
        fab.attachToRecyclerView(recyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                recyclerAdapter.setDownFactor();
            }

            @Override
            public void onScrollUp() {
                recyclerAdapter.setUpFactor();
            }
        });
        showProgressWheel(false);
        refreshLayout.setColorSchemeColors(getColorPrimary());
        refreshLayout.setOnRefreshListener(this);
    }

    @OnClick(R.id.fab)
    public void newNote(View view){
        Note note = new Note();
        note.setType(mCurrentNoteType);
        startNoteActivity(NoteActivity.CREATE_NOTE_TYPE, note);
    }

    @OnClick(R.id.edit_note_type)
    public void editNoteType(View view){
        hasEditClick = true;
        openOrCloseDrawer();
    }



    private void changeToSelectNoteType(int type){
        showProgressWheel(true);
        recyclerAdapter.setList(initItemData(type));
        showProgressWheel(false);
    }


    private void openOrCloseDrawer() {
        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.closeDrawer(drawerRootView);
        } else {
            mDrawerLayout.openDrawer(drawerRootView);
        }
    }

    // 设置笔记右上角弹出菜单
    private void showPopupMenu(View view, final Note note) {
        PopupMenu popup = new PopupMenu(this, view);

        String move = getString(R.string.move_to);
        if (mCurrentNoteType == NoteConfig.NOTE_TRASH_TYPE){
            for (int i=0; i< noteTypelist.size()-1; i++){
                popup.getMenu().add(Menu.NONE, i, Menu.NONE, move + noteTypelist.get(i));
            }
            popup.getMenu().add(Menu.NONE, noteTypelist.size()-1, Menu.NONE, getString(R.string.delete_forever));
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id < noteTypelist.size() - 1) {
                        note.setType(id);
                        finalDb.update(note);
                        changeToSelectNoteType(mCurrentNoteType);
                    } else {
                        showDeleteForeverDialog(note);
                    }
                    return true;
                }
            });

        } else {
            popup.getMenuInflater()
                    .inflate(R.menu.menu_notes_more, popup.getMenu());
            popup.setOnMenuItemClickListener(
                    new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_forever:
                                    showDeleteForeverDialog(note);
                                    break;
                                case R.id.edit:
                                    startNoteActivity(NoteActivity.EDIT_NOTE_TYPE, note);
                                    break;
                                case R.id.move_to_trash:
                                    note.setType(NoteConfig.NOTE_TRASH_TYPE);
                                    finalDb.update(note);
                                    changeToSelectNoteType(mCurrentNoteType);
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
        }
        popup.show();
    }

    // 显示是否永久删除该笔记
    private void showDeleteForeverDialog(final Note note){
        AlertDialog.Builder builder = generateDialogBuilder();
        builder.setTitle(R.string.delete_forever_message);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        for (NoteOperateLog log : note.getLogs().getList()){
                            finalDb.delete(log);
                        }
                        finalDb.delete(note);
                        changeToSelectNoteType(mCurrentNoteType);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };
        builder.setPositiveButton(R.string.sure, listener);
        builder.setNegativeButton(R.string.cancel, listener);
        builder.show();
    }

    // 打开其中一个笔记
    private void startNoteActivity(int oprType, Note value){
        Intent intent = new Intent(this, NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(NoteActivity.OPERATE_NOTE_TYPE_KEY, oprType);
        EventBus.getDefault().postSticky(value);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // 设置侧边栏左右
    private void setMenuListViewGravity(int gravity){
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerRootView.getLayoutParams();
        params.gravity = gravity;
        drawerRootView.setLayoutParams(params);
    }

    //初始化分类数据
    private List<Note> initItemData(int noteType) {
        List<Note> itemList = null;
        switch (noteType){
            case NoteConfig.NOTE_STUDY_TYPE:
            case NoteConfig.NOTE_WORK_TYPE:
            case NoteConfig.NOTE_OTHER_TYPE:
            case NoteConfig.NOTE_TRASH_TYPE:
                itemList = finalDb.findAllByWhere(Note.class, "type = " + noteType, "lastOprTime", true);
                break;
            default:
                break;
        }
        return itemList;
    }


    private void showProgressWheel(boolean visible){
        progressWheel.setBarColor(getColorPrimary());
        if (visible){
            if (!progressWheel.isSpinning())
                progressWheel.spin();
        }else{
            progressWheel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressWheel.isSpinning()) {
                        progressWheel.stopSpinning();
                    }
                }
            }, 500);
        }
    }



    // 初始化笔记列表
    private void initItemLayout(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onRefresh() {

    }
}
