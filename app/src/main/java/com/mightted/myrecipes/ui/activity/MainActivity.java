package com.mightted.myrecipes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mightted.myrecipes.R;

import com.mightted.myrecipes.dagger.components.DaggerListComponent;
import com.mightted.myrecipes.dagger.modules.ListModule;
import com.mightted.myrecipes.databinding.ActivityMainBinding;
import com.mightted.myrecipes.databindings.handlers.LoadItemHandler;
import com.mightted.myrecipes.databindings.handlers.SearchHandler;
import com.mightted.myrecipes.databindings.models.ListViewModel;
import com.mightted.myrecipes.databindings.models.RecipeItem;
import com.mightted.myrecipes.db.RecipeType;
import com.mightted.myrecipes.databindings.models.RecyclerConfiguration;
import com.mightted.myrecipes.utils.DrawerUtil;
import com.mightted.myrecipes.utils.LogUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private Drawer result;

    private int lastPosition;

    private String currentType = "";

    private int currentTotal = 0;

    private boolean isStartByIntent = false;

    private List<RecipeItem> recipeItems = new ArrayList<>();

    @Inject
    public RecyclerConfiguration configuration;

    @Inject
    public ListViewModel listViewModel;

    private List<RecipeType> recipeType;

    private ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getStringExtra("recipeType") != null) {
            currentType = intent.getStringExtra("recipeType");
            isStartByIntent = true;
            LogUtil.i("MainActivity","半路进来");
        }

        DaggerListComponent.builder().listModule(new ListModule(new GridLayoutManager(this,2),recipeItems)).build().inject(this);
        listViewModel.setCurrentType(currentType);
        LogUtil.i("MainActivity",listViewModel.getCurrentType());
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setSearchHandler(new SearchHandler());
        binding.setRecyclerViewConfiguration(configuration);
        binding.setItemList(recipeItems);
        binding.setLoadItemHandler(new LoadItemHandler());
        binding.setListViewModel(listViewModel);

        setSupportActionBar(binding.toolbar);
//
        result = new DrawerBuilder(this)
                .withToolbar(binding.toolbar)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        boolean returnType = true;
                        if((int)drawerItem.getIdentifier() > DrawerUtil.ALL && (int)drawerItem.getIdentifier() <= DrawerUtil.GONGNENG) {
                            chooseTypes((int)drawerItem.getIdentifier());
                        }else if((int)drawerItem.getIdentifier() == DrawerUtil.RANDOM) {
                            //// TODO: 2017/5/9 当点击的是试试手气时，将随机选中任意一个菜谱类型进行显示
                            Random random = new Random();
                            currentType = "00" + Integer.toString(random.nextInt(58)+10001007);
                            listViewModel.setCurrentType(currentType);
                            listViewModel.setCurrentRecipe("");
                            listViewModel.setRefreshing(true);
                            initTitle();
                            returnType = false;
                        } else if((int)drawerItem.getIdentifier() == DrawerUtil.ALL){
                            returnType = true;
                        } else if((int)drawerItem.getIdentifier() == DrawerUtil.ABOUT) {

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("关于本应用")
                                    .setMessage(R.string.about)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        } else {
                            RecipeType type = recipeType.get((int)drawerItem.getIdentifier());
                            currentType = type.getCtgId();
                            listViewModel.setCurrentType(currentType);
                            listViewModel.setCurrentRecipe("");
                            listViewModel.setRefreshing(true);
                            initTitle();
                            returnType = false;
                        }

                        return returnType;
                    }
                })
                .build();

        initTitle();
        initDrawerItems();
        initListener();
        listViewModel.setRefreshing(true);

    }

    /**
     * 根据选择的菜谱类型改变title
     */
    private void initTitle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RecipeType type = DataSupport.select("name").where("ctgId = ?",currentType).findFirst(RecipeType.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(type != null && getSupportActionBar()!= null) {
                            getSupportActionBar().setTitle(type.getName());
                        }
                    }
                });
            }
        }).start();
    }

    private void initListener() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.i("MainActivity","监听下拉动作");
                binding.getListViewModel().setRefreshing(true);
            }
        });

        binding.recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastPosition = ((GridLayoutManager)configuration.getManager()).findLastCompletelyVisibleItemPosition(); //表示最后一个完全可见的item的位置
                //如果当前没有处于正在刷新状态，并且已经到达末尾
                //这里判断到达末尾，是通过判断最后可以完全可见的item是不是最后一个item
                //但是有个问题，当下拉刷新的时候，会触发滚动事件，这时由于recipeItems为空，size为0
                //lastPosition刚好为-1，导致会出现两次列表初始化，因此需要加大于0的判断
                if(lastPosition == recipeItems.size()-1 && lastPosition > 0) {
                    Log.i("MainActivity","开始加载更多");
                    if(currentTotal != recipeItems.size()) {
                        listViewModel.onDealList();
                    }
                }
            }
        });
    }



    /**
     * 菜谱二级菜单处理函数
     * @param type 二级菜单选项
     */
    private void chooseTypes(final int type) {
        final String searchType;
        switch (type) {
            case DrawerUtil.CAIPIN:
                searchType = "0010001002"; //按菜品选择菜谱
                break;
            case DrawerUtil.GONGYI:
                searchType = "0010001003"; //按工艺选择菜谱
                break;
            case DrawerUtil.CAIXI:
                searchType = "0010001004"; //按菜系选择菜谱
                break;
            case DrawerUtil.RENQUN:
                searchType = "0010001005"; //按人群选择菜谱
                break;
            case DrawerUtil.GONGNENG:
                searchType = "0010001006"; //按功能选择菜谱
                break;
            default:
                searchType = "0010001002"; //默认选择，按菜品选择菜谱
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<RecipeType> recipeTypes = DataSupport.where("parentId = ?",searchType).find(RecipeType.class);
                    onShowDialog(recipeTypes);
            }
        }).start();
    }

    /**
     * 菜谱类型选择三级菜单，展现方式为弹出对话框
     * @param recipeTypes 菜谱选择二级菜单选项
     */
    private void onShowDialog(List<RecipeType> recipeTypes) {
        final String[] types = new String[recipeTypes.size()];
        final boolean[] booleans = new boolean[recipeTypes.size()];
        for(int i = 0; i < recipeTypes.size(); i++) {
            types[i] = recipeTypes.get(i).getName();
            booleans[i] = recipeTypes.get(i).isChoosed();
        }

        final List<RecipeType> finalRecipeTypes = recipeTypes;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMultiChoiceItems(
                                types, booleans,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        booleans[which] = isChecked;
                                    }
                                }
                        )
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int k = 0; k < booleans.length; k++) {
                                            finalRecipeTypes.get(k).setChoosed(booleans[k]);
                                            finalRecipeTypes.get(k).save();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //// TODO: 2017/5/9 如果点击确定键前后，没有改变任何内容的话，不应该重新加载布局
                                                initDrawerItems();
                                            }
                                        });

                                    }
                                }).start();
                            }
                        }).show();
            }
        });
    }

    /**
     *  从数据库中加载已收藏的标签，允许动态加载
     * @return  收藏的标签数据
     */
    private PrimaryDrawerItem[] onLoadType() {
        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                if(recipeType != null) {
                    recipeType.clear();
                }
                recipeType = DataSupport.select("name","ctgId").where("isChoosed = ?","1").find(RecipeType.class);
            }
        });
        temp.start();
        try {
            temp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PrimaryDrawerItem[] typeItems = new PrimaryDrawerItem[recipeType.size()+2];
        //默认会有一个item，因此不会出现返回数组空值的情况而引起Exception
        typeItems[0] = new PrimaryDrawerItem()
                .withName("试试手气")
                .withIdentifier(DrawerUtil.RANDOM)
                .withSelectable(false);
        if(recipeType.size() > 0) {
            Log.i("onLoadType","预选项为空");
            for(int i = 0; i < recipeType.size(); i++) {
                typeItems[i+1] = new PrimaryDrawerItem()
                        .withName(recipeType.get(i).getName())
                        .withIdentifier(i)
                        .withSelectable(true);
            }
        }
        typeItems[recipeType.size()+1] = new PrimaryDrawerItem()
                .withName("关于")
                .withIdentifier(DrawerUtil.ABOUT)
                .withSelectable(false);
        return typeItems;
    }

    /**
     * 初始化或者重新加载MaterialDrawer的item
     */
    private void initDrawerItems() {
        if(result != null) {
            result.removeAllItems();
            result.addItems(
                    new ExpandableDrawerItem().withName("全部菜谱").withSelectable(false).withIdentifier(DrawerUtil.ALL).withSubItems(
                            new SecondaryDrawerItem().withName("按菜品选择菜谱").withIdentifier(DrawerUtil.CAIPIN).withSelectable(false),
                            new SecondaryDrawerItem().withName("按工艺选择菜谱").withIdentifier(DrawerUtil.GONGYI).withSelectable(false),
                            new SecondaryDrawerItem().withName("按菜系选择菜谱").withIdentifier(DrawerUtil.CAIXI).withSelectable(false),
                            new SecondaryDrawerItem().withName("按人群选择菜谱").withIdentifier(DrawerUtil.RENQUN).withSelectable(false),
                            new SecondaryDrawerItem().withName("按功能选择菜谱").withIdentifier(DrawerUtil.GONGNENG).withSelectable(false)
                    ),
                    new DividerDrawerItem()
            );
            result.addItems((IDrawerItem[]) onLoadType());
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.searchText.hasFocus()) {
            binding.searchText.clearFocus();
        } else {
            super.onBackPressed();
            if(result.isDrawerOpen()) {
                result.closeDrawer();
            } else {
                if(isStartByIntent) {
                    Log.i("onBackPressed","半路进来的被杀死了");
                    finish();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                onSearchBarGetFocus(binding.searchText);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if(view != null && view instanceof EditText) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                int rawX = (int)ev.getRawX();
                int rawY = (int)ev.getRawY();
                if(!rect.contains(rawX,rawY)) {
                    view.clearFocus();
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void onSearchBarGetFocus(final EditText editText) {
        if(editText.getVisibility() == View.GONE) {
            editText.setVisibility(View.VISIBLE);
        }
        editText.requestFocus();
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(editText,0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listViewModel.getDisposable() != null) {
            listViewModel.getDisposable().dispose();
        }
    }
}
