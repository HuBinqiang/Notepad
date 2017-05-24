
## 小笔记

### Logo

![logo](https://github.com/HuBinqiang/Notepad/blob/master/img/logo.png)

### 一、实现功能及描述

#### 1. 笔记基本功能（增删改查）

![add](https://github.com/HuBinqiang/Notepad/blob/master/img/add.jpg)

![view](https://github.com/HuBinqiang/Notepad/blob/master/img/view.jpg)

![select](https://github.com/HuBinqiang/Notepad/blob/master/img/select.jpg)

``` java 
// 选择菜单
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
```

![delete](https://github.com/HuBinqiang/Notepad/blob/master/img/delete.jpg)

#### 2. 笔记时间戳显示

![time](https://github.com/HuBinqiang/Notepad/blob/master/img/time.jpg)



#### 3. 笔记搜索（标题和内容）

![search](https://github.com/HuBinqiang/Notepad/blob/master/img/search.jpg)

![result](https://github.com/HuBinqiang/Notepad/blob/master/img/result.jpg)

#### 4. 设置页面

![setting](https://github.com/HuBinqiang/Notepad/blob/master/img/setting.jpg)


#### 5. 笔记本主题

![theme](https://github.com/HuBinqiang/Notepad/blob/master/img/theme.jpg)

```java
// 选择主题
public static void changTheme(Activity activity, Theme theme){
        if (activity == null)
            return;
        int style = R.style.RedTheme;
        switch (theme){
            case BROWN:
                style = R.style.BrownTheme;
                break;
            case BLUE:
                style = R.style.BlueTheme;
                break;
            case BLUE_GREY:
                style = R.style.BlueGreyTheme;
                break;
            case YELLOW:
                style = R.style.YellowTheme;
                break;
            case DEEP_PURPLE:
                style = R.style.DeepPurpleTheme;
                break;
            case PINK:
                style = R.style.PinkTheme;
                break;
            case GREEN:
                style = R.style.GreenTheme;
                break;
            default:
                break;
        }
        activity.setTheme(style);
    }
```

#### 6. 笔记分类

![folder](https://github.com/HuBinqiang/Notepad/blob/master/img/folder.jpg)

![folder](https://github.com/HuBinqiang/Notepad/blob/master/img/folder_edit.jpg)

#### 7. 回收站功能

![crash](https://github.com/HuBinqiang/Notepad/blob/master/img/crash.jpg)

![crash](https://github.com/HuBinqiang/Notepad/blob/master/img/crash_1.jpg)

```java
// 回收站内右上角选择菜单
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
```

#### 8. 左手/右手模式

![ysms](https://github.com/HuBinqiang/Notepad/blob/master/img/ysms.jpg)

``` java
 // 设置侧边栏左右
    private void setMenuListViewGravity(int gravity){
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerRootView.getLayoutParams();
        params.gravity = gravity;
        drawerRootView.setLayoutParams(params);
    }
// 判断是否开启右手模式
        if (rightHandOn != preferenceUtils.getBooleanParam(getString(R.string.right_hand_mode_key))){
            rightHandOn = !rightHandOn;
            if (rightHandOn){
                setMenuListViewGravity(Gravity.END);
            }else{
                setMenuListViewGravity(Gravity.START);
            }
        }
```


### 二、环境

    ANDROID_BUILD_COMPILE_SDK_VERSION=25
    ANDROID_BUILD_TARGET_SDK_VERSION=25
    ANDROID_BUILD_TOOLS_VERSION=25.0.2
    MIN_SDK_VERSION=19
    TARGET_SDK_VERSION=25

### 三、使用第三方

    compile 'com.jakewharton:butterknife:6.1.0'
    compile 'com.readystatesoftware.systembartint:systembartint:1.0.3'
    compile 'com.melnykov:floatingactionbutton:1.3.0'
    compile 'com.rengwuxian.materialedittext:library:2.1.3'
    compile 'com.squareup.dagger:dagger:1.2.2'
    provided 'com.squareup.dagger:dagger-compiler:1.2.2'
    compile 'de.greenrobot:eventbus:2.4.0'
    compile 'com.pnikosis:materialish-progress:1.5'
    compile 'com.nispok:snackbar:2.10.10'
    compile project(':orm-library')
    compile project(':MaterialPreference')

### 四、目录说明

1. adpater:数据适配器
2. model:数据模型
3. module:依赖注入模型
4. ui:界面逻辑
5. utils:工具类
