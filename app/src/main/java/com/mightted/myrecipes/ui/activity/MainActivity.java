package com.mightted.myrecipes.ui.activity;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerView;

    private SwipeRefreshLayout refreshLayout;

    private Drawer result;

    private RecipeAdapter adapter;

    private GridLayoutManager manager;

    private int lastPosition;

    private static int currentPage = 1;

    private static String currentType = "";

    private static String currentRecipe = "";

    private static boolean isRefreshing;

    private static boolean isLoadingMore;



    private List<RecipeItem> recipeItems;

    private List<RecipeType> recipeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);


        setSupportActionBar(toolbar);

        result = new DrawerBuilder(this)
                .withToolbar(toolbar)
//                .addDrawerItems(
//                        new ExpandableDrawerItem().withName("全部菜谱").withSelectable(false).withSubItems(
//                                new SecondaryDrawerItem().withName("按菜品选择菜谱").withIdentifier(DrawerUtil.CAIPIN).withSelectable(false),
//                                new SecondaryDrawerItem().withName("按工艺选择菜谱").withIdentifier(DrawerUtil.GONGYI).withSelectable(false),
//                                new SecondaryDrawerItem().withName("按菜系选择菜谱").withIdentifier(DrawerUtil.CAIXI).withSelectable(false),
//                                new SecondaryDrawerItem().withName("按人群选择菜谱").withIdentifier(DrawerUtil.RENQUN).withSelectable(false),
//                                new SecondaryDrawerItem().withName("按功能选择菜谱").withIdentifier(DrawerUtil.GONGNENG).withSelectable(false)
//                        )
//                )
//                .addDrawerItems(onLoadType())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        boolean returnType = true;
                        if((int)drawerItem.getIdentifier() > DrawerUtil.ALL && (int)drawerItem.getIdentifier() <= DrawerUtil.GONGNENG) {
                            chooseTypes((int)drawerItem.getIdentifier());
                        }else if((int)drawerItem.getIdentifier() == DrawerUtil.RANDOM) {
                            //// TODO: 2017/5/9 当点击的是试试手气时，将随机选中任意一个菜谱类型进行显示
                            returnType = false;
                        } else if((int)drawerItem.getIdentifier() == DrawerUtil.ALL){

                        } else {
                            RecipeType type = recipeType.get((int)drawerItem.getIdentifier());
                            currentType = type.getCtgId();
                            currentPage = 1;
                            onRefreshList();
//                            initList(currentType,currentRecipe,currentPage);
                            returnType = false;
                        }


//                        switch ((int)drawerItem.getIdentifier()) {
//                            case DrawerUtil.CAIPIN:
//                                break;
//                            case DrawerUtil.GONGYI:
//                                break;
//                            case DrawerUtil.CAIXI:
//                                break;
//                            case DrawerUtil.RENQUN:
//                                break;
//                            case DrawerUtil.GONGNENG:
//                                break;
//                        }
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

        initList(currentType,currentRecipe,currentPage);
        initListener();




    }

    private void initList(final String ctgId,final String recipe, final int page) {

        ListService service = RetrofitClient.getInstance().create(ListService.class);
        service.getList(RetrofitUtil.KEY,ctgId,recipe,page,30)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RecipeList>() {
                    @Override
                    public void accept(RecipeList recipeList) throws Exception {
                        if(recipeList.msg.equals("success")) {
                            Log.i("MainActivity","请求成功返回");
                        }
                    }
                })
                .subscribe(new Observer<RecipeList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("onSubscribe","当前类型为:"+ctgId + " 当前页数为:"+page);
                        Log.i("onSubscribe","onSubscribe is called");
                        if(recipeItems == null) {
                            recipeItems = new ArrayList<>();
                            currentPage = 1;
                        }

                        if(isLoadingMore && recipeItems.size() > 0) {
                            recipeItems.remove(recipeItems.size()-1);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNext(RecipeList value) {
                        Log.i("MainActivity","onNext is called");
                        List<RecipeDetail.Result> list = value.result.recipeList;

                        if(list.size() > 0) {
                            for(RecipeDetail.Result recipe:list) {
                                RecipeItem item = new RecipeItem();
                                item.setTitle(recipe.name);
                                item.setImg(recipe.thumbnail);
                                item.setRecipeId(recipe.menuId);
                                item.setRecipeImg(recipe.recipe.img);
                                recipeItems.add(item);
                                adapter.notifyItemChanged(recipeItems.size()-1);
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

//                        Log.i("onNext","isLoading变为false: " + isLoading);
//                        isLoading = false;
                        if(isLoadingMore) {
                            isLoadingMore = false;
                        }
                        if(isRefreshing) {
                            isRefreshing = false;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("MainActivity","onError is called");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("MainActivity","onComplete is called");
                        currentPage ++;


                    }
                });

    }

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

                lastPosition = manager.findLastCompletelyVisibleItemPosition();
//                Log.i("MainActivity","the current position is " + lastPosition + "and isLoading is " + isLoadingMore);
                if(!isLoadingMore && lastPosition == recipeItems.size()-1 && lastPosition > 0) {
                    recipeItems.add(new RecipeItem());
                    isLoadingMore = true;
                    adapter.notifyDataSetChanged();
                    onLoadMore();
                }
            }
        });
    }

    private void onRefreshList() {
        if(recipeItems == null) {
            recipeItems = new ArrayList<>();
        } else {
            int size = recipeItems.size();
            recipeItems.clear();
            adapter.notifyItemRangeRemoved(0,size);
        }
        currentPage = 1;
        initList(currentType,currentRecipe,currentPage);
    }

    private void onLoadMore() {
        Log.i("onLoadMore","正在加载更多...");
        initList(currentType,currentRecipe,currentPage);
    }

    private void chooseTypes(final int type) {
        final String searchType;
        switch (type) {
            case DrawerUtil.CAIPIN:
                searchType = "0010001002";
                break;
            case DrawerUtil.GONGYI:
                searchType = "0010001003";
                break;
            case DrawerUtil.CAIXI:
                searchType = "0010001004";
                break;
            case DrawerUtil.RENQUN:
                searchType = "0010001005";
                break;
            case DrawerUtil.GONGNENG:
                searchType = "0010001006";
                break;
            default:
                searchType = "0010001002";
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<RecipeType> recipeTypes = DataSupport.where("parentId = ?",searchType).find(RecipeType.class);
                if(recipeTypes.size() == 0) {
                    Log.i("showTypeDialog","数据库中无数据?");
                    initDB(searchType);

                } else {
                    onShowDialog(recipeTypes);
                }

            }
        }).start();
    }

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
//                                                    finalRecipeTypes.get(k).updateAll("ctgId = ?",finalRecipeTypes.get(k).getCtgId());
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //// TODO: 2017/5/9 如果点击确定键前后，没有改变任何内容的话，不应该重新加载布局
                                                initDrawerItems();
//                                                if(result.isDrawerOpen()) {
//                                                    result.closeDrawer();
//                                                }
                                            }
                                        });

                                    }
                                }).start();
                            }
                        }).show();
            }
        });
    }

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
                        Log.i("initRecipeType",value.result.all.name);
                        List<RecType.Result.SearchType> types = value.result.types;
                        for(RecType.Result.SearchType type:types) {
//                            Log.i("initRecipeType","进入第一层");
//                            Log.i("initRecipeType",type.info.name);
                            List<RecType.Result.SearchType.Type> recipeTypes = type.types;
                            for(RecType.Result.SearchType.Type recipeType:recipeTypes) {
//                                Log.i("initRecipeType","记入第二层");
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

        PrimaryDrawerItem[] typeItems = new PrimaryDrawerItem[recipeType.size()+1];
//        Random random = new Random();
        typeItems[0] = new PrimaryDrawerItem()
                .withName("试试手气")
//                .withIdentifier(random.nextInt(58)+10001007)
                .withIdentifier(DrawerUtil.RANDOM)
                .withSelectable(false);
        if(recipeType.size() > 0) {
            Log.i("onLoadType","预选项为空");
            for(int i = 0; i < recipeType.size(); i++) {
                typeItems[i+1] = new PrimaryDrawerItem()
                        .withName(recipeType.get(i).getName())
                        .withIdentifier(i)
                        .withSelectable(false);
            }
        }
        return typeItems;
    }

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
                    )
            );
            result.addItems(onLoadType());
        }
    }

}
