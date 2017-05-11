package com.mightted.myrecipes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mightted.myrecipes.R;
import com.mightted.myrecipes.bean.RecipeItem;
import com.mightted.myrecipes.db.RecipeType;
import com.mightted.myrecipes.domain.RetrofitClient;
import com.mightted.myrecipes.domain.entity.RecType;
import com.mightted.myrecipes.domain.entity.RecipeDetail;
import com.mightted.myrecipes.domain.entity.RecipeList;
import com.mightted.myrecipes.domain.service.ListService;
import com.mightted.myrecipes.domain.service.TypeService;
import com.mightted.myrecipes.ui.adapter.RecipeAdapter;
import com.mightted.myrecipes.utils.DrawerUtil;
import com.mightted.myrecipes.utils.RetrofitUtil;
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


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText editText;

    private RecyclerView recyclerView;

    private SwipeRefreshLayout refreshLayout;

    private Drawer result;

    private RecipeAdapter adapter;

    private GridLayoutManager manager;

    private int lastPosition;

    private int currentPage = 1;

    private String currentType = "";

    private String currentRecipe = "";

    private boolean isRefreshing;

    private boolean isLoadingMore;

    private Disposable disposable;

    private boolean isStartByIntent = false;



    private List<RecipeItem> recipeItems;

    private List<RecipeType> recipeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        editText = (EditText)findViewById(R.id.search_text);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);


        setSupportActionBar(toolbar);

        result = new DrawerBuilder(this)
                .withToolbar(toolbar)
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
                            currentPage = 1;
                            onRefreshList();
                            initTitle();
                            returnType = false;
                        } else if((int)drawerItem.getIdentifier() == DrawerUtil.ALL){

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
                            currentPage = 1;
                            onRefreshList();
                            initTitle();
//                            initList(currentType,currentRecipe,currentPage);
                            returnType = false;
                        }

                        return returnType;
                    }
                })
                .build();

        initDrawerItems();


        if(recipeItems == null) {
            recipeItems = new ArrayList<>();
            Log.d("MainActivity","recipeItems未能成功初始化!");
        }
        manager = new GridLayoutManager(this,2);
        if(recyclerView == null) {
            Log.d("MainActivity","recyclerView为空");
            recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        }
        recyclerView.setLayoutManager(manager);
        recipeItems = new ArrayList<>();
        adapter = new RecipeAdapter(recipeItems);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if(intent.getStringExtra("recipeType") != null) {
            currentType = intent.getStringExtra("recipeType");
            isStartByIntent = true;
            Log.i("MainActivity","半路进来");
        }

        initTitle();
        initList(currentType,currentRecipe,currentPage);
        initListener();




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

    /**
     * 获取相关菜谱类型数据并进行处理
     * @param ctgId 菜谱类型标签ID，为末级分类标签
     * @param recipe 菜谱名称，默认值为空字符串
     * @param page 列表页数，默认值为20，可修改
     */
    private void initList(final String ctgId,final String recipe, final int page) {

        ListService service = RetrofitClient.getInstance().create(ListService.class);
        service.getList(RetrofitUtil.KEY,ctgId,recipe,page,30)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<RecipeList>() {
                    @Override
                    public void accept(RecipeList recipeList) throws Exception {
//                        Thread.sleep(2*1000);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RecipeList>() {
                    @Override
                    public void accept(RecipeList recipeList) throws Exception {
                        if(recipeList.msg.equals("success")) {
                            Log.i("MainActivity","请求成功返回");
                            if(recipeItems == null) {
                                recipeItems = new ArrayList<>();
                            } else {
                                int size = recipeItems.size();
                                recipeItems.clear();
                                adapter.notifyItemRangeRemoved(0,size);
                            }
                        }
                    }
                })
                .subscribe(new Observer<RecipeList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
//                        Log.i("onSubscribe","当前类型为:"+ctgId + " 当前页数为:"+page);
//                        Log.i("onSubscribe","onSubscribe is called");
                        if(recipeItems == null) {
                            recipeItems = new ArrayList<>();
                            currentPage = 1;
                        }

                    }

                    @Override
                    public void onNext(RecipeList value) {
                        Log.i("MainActivity","onNext is called");
                        List<RecipeDetail.Result> list = value.result.recipeList;

                        if(list.size() > 0) {
                            for(RecipeDetail.Result recipe:list) {
                                RecipeItem item = new RecipeItem();
                                //由于由于部分菜谱没有title标签，取消之
                                // TODO: 2017/5/10 之前由于部分数据没有title而导致无法处理的NullPointException，现在可以使用异常捕获解决了
                                try {
                                    item.setTitle(recipe.recipe.title);

                                } catch (NullPointerException e) {
                                    item.setTitle(recipe.name);
                                }
                                item.setImg(recipe.thumbnail);
                                item.setRecipeId(recipe.menuId);

                                //特么img这里也会出现这个问题，愤怒!使用异常抛出进行处理
                                try {
                                    item.setRecipeImg(recipe.recipe.img);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
//                                    Log.i("onNext","捕获一个空值异常");
                                }

                                if(isLoadingMore && recipeItems.size() > 0) {
                                    isLoadingMore = false;
                                    //必须要在线程外进行，否则会延误删除加载状态item的时间
                                    recipeItems.remove(recipeItems.size()-1);
                                    recyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {

//                                            adapter.notifyItemRemoved();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                }

                                recipeItems.add(item);
                                adapter.notifyItemChanged(recipeItems.size()-1); //选择局部刷新而不是全体瞎鸡儿刷新
//                                adapter.notifyDataSetChanged();
                                if(refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                Log.i("onNext","name为" + recipe.name);
                            }
                        } else {
                            Toast.makeText(MainActivity.this,"已经到头了",Toast.LENGTH_SHORT).show();
                            currentPage --;
                        }

                        if(isLoadingMore) {
                            isLoadingMore = false;
                        }
                        if(isRefreshing) {
                            isRefreshing = false;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"服务器或网络错误",Toast.LENGTH_SHORT).show();
                        Log.i("MainActivity","onError is called");
                    }

                    @Override
                    public void onComplete() {
                        if(disposable != null) {
                            disposable = null;
                        }
                        Log.i("MainActivity","onComplete is called");
                        currentPage ++;


                    }
                });

    }

    /**
     * 监听器初始化函数
     */
    private void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                onRefreshList();

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastPosition = manager.findLastCompletelyVisibleItemPosition(); //表示最后一个完全可见的item的位置
                //如果当前没有处于正在刷新状态，并且已经到达末尾
                //这里判断到达末尾，是通过判断最后可以完全可见的item是不是最后一个item
                //但是有个问题，当下拉刷新的时候，会触发滚动事件，这时由于recipeItems为空，size为0
                //lastPosition刚好为-1，导致会出现两次列表初始化，因此需要加大于0的判断
                if(!isLoadingMore && lastPosition == recipeItems.size()-1 && lastPosition > 0) {
                    Log.i("MainActivity","开始加载更多");
//                    adapter.notifyDataSetChanged();
                    onLoadMore();
                }
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
//                    editText.clearFocus();
                    editText.setVisibility(View.GONE);
                    InputMethodManager manager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(editText.getWindowToken(),0);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(MainActivity.this,"触发事件",Toast.LENGTH_SHORT).show();
                if(actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(editText.getText())) {
                    currentRecipe = editText.getText().toString();
                    currentType="";
                    refreshLayout.setRefreshing(true);
                    onRefreshList();
                }
                return false;
            }
        });
    }

    /**
     * 根据给定的参数，进行列表刷新
     */
    private void onRefreshList() {
        // TODO: 2017/5/11 无论怎么样，对于数据初始化和列表清空，都不应该在对返回数据未知的情况下执行，而是放到网络数据请求得到success的回应后在执行
//        if(recipeItems == null) {
//            recipeItems = new ArrayList<>();
//        } else {
//            int size = recipeItems.size();
//            recipeItems.clear();
//            adapter.notifyItemRangeRemoved(0,size);
//        }
        currentPage = 1;
        initList(currentType,currentRecipe,currentPage);
    }

    /**
     * 加载更多的处理函数
     */
    private void onLoadMore() {
        Log.i("onLoadMore","正在加载更多...");
        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                isLoadingMore = true;
                recipeItems.add(new RecipeItem());
//                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(recipeItems.size()-1);
            }
        });
//        adapter.notifyItemInserted(recipeItems.size()-1);
        initList(currentType,currentRecipe,currentPage);
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
                if(recipeTypes.size() == 0) {
//                    Log.i("showTypeDialog","数据库中无数据?");
                    initDB(searchType);

                } else {
                    onShowDialog(recipeTypes);
                }

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
     * 初次选择菜谱类型选择时，将菜谱类型保存进数据库中
     * @param searchType 要展示的菜谱类型二级列表项
     */
    private void initDB(final String searchType) {
        TypeService service = RetrofitClient.getInstance().create(TypeService.class);
        service.initRecipeType(RetrofitUtil.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<RecType>() {
                    @Override
                    public void accept(RecType recType) throws Exception {
                        if(recType.msg.equals("success")) {
                            Log.i("initRecipeType","分类标签查询请求成功");
                        } else {
                            Log.i("initRecipeType","分类标签查询请求失败");
                            Log.i("initRecipeType",recType.msg);
                        }
                    }
                })
//                .flatMap(new Function<RecType, ObservableSource<RecType.Result.SearchType>>() {
//                    @Override
//                    public ObservableSource<RecType.Result.SearchType> apply(RecType recType) throws Exception {
//                        return Observable.fromIterable(recType.result.types);
////                        return recType.result.types;
//                    }
//                })
//                .subscribe(new Observer<RecType.Result.SearchType>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(RecType.Result.SearchType value) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

                .subscribe(new Observer<RecType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("initRecipeType","onSubscribe is called");
                    }

                    @Override
                    public void onNext(RecType value) {
                        Log.i("initRecipeType","onNext is called");
//                        Log.i("initRecipeType",value.result.all.name);
                        List<RecType.Result.SearchType> types = value.result.types;
                        for(RecType.Result.SearchType type:types) {

                            List<RecType.Result.SearchType.Type> recipeTypes = type.types;
                            for(RecType.Result.SearchType.Type recipeType:recipeTypes) {

                                RecipeType recipe = new RecipeType();
                                recipe.setCtgId(recipeType.info.ctgId);
                                recipe.setName(recipeType.info.name);
                                recipe.setParentId(recipeType.info.parentId);
                                recipe.setChoosed(false);
//                                Log.i("initRecipeType",recipeType.info.ctgId +" " + recipeType.info.name +" " +recipeType.info.parentId);
                                recipe.save();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"服务器或网络错误",Toast.LENGTH_SHORT).show();
                        Log.i("initRecipeType","onError is called");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("initRecipeType","onComplete is called");
                        List<RecipeType> recipeTypes = DataSupport.where("parentId = ?",searchType).find(RecipeType.class);
                        onShowDialog(recipeTypes);
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
        if(editText.hasFocus()) {
            editText.clearFocus();
        } else {
            super.onBackPressed();
            if(isStartByIntent) {
                Log.i("onBackPressed","半路进来的被杀死了");
                finish();
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
                onSearchBarGetFocus(editText);
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
        if(disposable != null) {
            disposable.dispose();
        }
    }
}
