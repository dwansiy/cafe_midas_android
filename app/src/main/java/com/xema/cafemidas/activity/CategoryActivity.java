package com.xema.cafemidas.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.CategoryAdapter;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.dialog.AddCategoryDialog;
import com.xema.cafemidas.dialog.AddProductDialog;
import com.xema.cafemidas.dialog.SimpleTextDialog;
import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    private List<Category> mCategoryList;
    private CategoryAdapter mAdapter;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        initAdapter();

        queryCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        ivAdd.setOnClickListener(this::attemptAddCategory);
    }

    private void initAdapter() {
        mCategoryList = new ArrayList<>();
        mAdapter = new CategoryAdapter(this, mCategoryList);
        mAdapter.setOnAddProductListener(this::attemptAddProduct);
        mAdapter.setOnDeleteCategoryListener(this::attemptDeleteCategory);
        rvMain.setAdapter(mAdapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
    }

    private void queryCategory() {
        LoadingProgressDialog.showProgress(this);

        ApiUtil.getProductService().getCategoryList().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    List<Category> categoryList = response.body();
                    if (categoryList != null) {
                        if (categoryList.isEmpty()) {
                            rvMain.setVisibility(View.GONE);
                            llEmpty.setVisibility(View.VISIBLE);
                        } else {
                            llEmpty.setVisibility(View.GONE);
                            rvMain.setVisibility(View.VISIBLE);
                            mCategoryList.clear();
                            mCategoryList.addAll(categoryList);
                            mAdapter.notifyParentDataSetChanged(true);
                            mAdapter.expandAllParents();
                        }
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(CategoryActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void attemptAddCategory(View view) {
        view.setEnabled(false);

        AddCategoryDialog dialog = new AddCategoryDialog(this);
        dialog.setListener(this::addCategory);
        dialog.show();
        view.setEnabled(true);
    }

    private void addCategory(String name) {
        LoadingProgressDialog.showProgress(this);
        ApiUtil.getProductService().makeCategory(PreferenceHelper.loadId(this), PreferenceHelper.loadPw(this), name).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    Category category = response.body();
                    if (category != null) {
                        mCategoryList.add(category);
                        mAdapter.notifyParentDataSetChanged(true);
                        mAdapter.expandAllParents();
                        //queryCategory();
                    } else {
                        Toast.makeText(CategoryActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(CategoryActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptAddProduct(Category category) {
        AddProductDialog dialog = new AddProductDialog(this);
        dialog.setListener((name, price) -> {
            addProduct(category, name, price);
        });
        dialog.show();
    }

    private void attemptDeleteCategory(Category category, int position) {
        SimpleTextDialog dialog = new SimpleTextDialog(mContext, "삭제 확인" + "\n\n" + category.getName() + " 를 정말 삭제하시겠습니까?");
        dialog.setOnPositiveListener("삭제", () -> {
            LoadingProgressDialog.showProgress(mContext);
            ApiUtil.getProductService().deleteCategory(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), category.getId()).enqueue(new Callback<Category>() {
                @Override
                public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                    LoadingProgressDialog.hideProgress();
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        //queryCategory();
                        mCategoryList.remove(position);
                        mAdapter.notifyParentDataSetChanged(true);
                        mAdapter.expandAllParents();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                    LoadingProgressDialog.hideProgress();
                    Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void addProduct(Category category, String name, long price) {
        // TODO: 2018-05-26 메뉴 추가
    }
}
