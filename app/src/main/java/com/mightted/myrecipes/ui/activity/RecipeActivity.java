package com.mightted.myrecipes.ui.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mightted.myrecipes.R;
import com.mightted.myrecipes.ui.fragment.RecipeFragment;

public class RecipeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView toolbarImg;

    private CollapsingToolbarLayout collapsingToolbarLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbarImg = (ImageView)findViewById(R.id.toolbar_img);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(intent.getStringExtra("recipeTitle"));
        }
        Glide.with(RecipeActivity.this).load(intent.getStringExtra("recipeImg")).into(toolbarImg);


        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_content,new RecipeFragment(recipeId)).commit();
    }

    //要动态改变title的话，需要使用CollapsingToolbarLayout!


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
//            collapsingToolbarLayout.setTitle("aaaa");
        }
        return true;
    }
}
