package com.xema.cafemidas.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.xema.cafemidas.dialog.EditProductDialog;
import com.xema.cafemidas.dialog.SimpleTextDialog;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.model.Product;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;
import com.xema.cafemidas.util.PermissionUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private File mProductImage;

    private final static int GALLERY_REQUEST_CODE = 203;
    private final static int CROP_REQUEST_CODE = 303;

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
        mAdapter.setOnDeleteProductListener(this::attemptDeleteProduct);
        mAdapter.setOnEditProductListener(this::attemptEditProduct);
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
                            queryProduct();
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

    private void queryProduct() {
        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getProductService().getProductList().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    List<Product> productList = response.body();
                    if (productList != null && !productList.isEmpty()) {
                        for (Category category : mCategoryList) {
                            category.getChildList().clear();
                        }
                        for (Product product : productList) {
                            for (Category category : mCategoryList) {
                                if (category.getId() == product.getType()) {
                                    category.getChildList().add(product);
                                }
                            }
                        }
                        mAdapter.notifyParentDataSetChanged(true);
                        mAdapter.expandAllParents();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
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

    private AddProductDialog mAddProductDialog;

    private void attemptAddProduct(Category category) {
        mAddProductDialog = new AddProductDialog(this);
        mAddProductDialog.setListener((name, price, time, image) -> {
            addProduct(category, name, price, time, image);
        });
        mAddProductDialog.show();
    }

    private EditProductDialog mEditProductDialog;

    private void attemptEditProduct(Product product, int parentPosition, int childPosition) {
        mEditProductDialog = new EditProductDialog(this, product);
        mEditProductDialog.setListener(new EditProductDialog.OnRegisterListener() {
            @Override
            public void onRegisterImage(String name, long price, int time, File image, int productId, int type) {
                editProductWithImage(name, price, time, image, productId, type);
            }

            @Override
            public void onRegister(String name, long price, int time, File image, int productId, int type) {
                editProduct(name, price, time, image, productId, type);
            }
        });
        mEditProductDialog.show();
    }

    private void attemptDeleteCategory(Category category, int position) {
        SimpleTextDialog dialog = new SimpleTextDialog(mContext, "삭제 확인" + "\n\n" + category.getName() + " 를 정말 삭제하시겠습니까?");
        dialog.setOnPositiveListener("삭제", () -> {
            LoadingProgressDialog.showProgress(mContext);
            ApiUtil.getProductService().deleteCategory(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), category.getId()).enqueue(new Callback<ApiResult>() {
                @Override
                public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                    LoadingProgressDialog.hideProgress();
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        mCategoryList.remove(position);
                        mAdapter.notifyParentDataSetChanged(true);
                        mAdapter.expandAllParents();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                    LoadingProgressDialog.hideProgress();
                    Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void attemptDeleteProduct(Product product, int parentPosition, int childPosition) {
        SimpleTextDialog dialog = new SimpleTextDialog(mContext, "삭제 확인" + "\n\n" + product.getName() + " 를 정말 삭제하시겠습니까?");
        dialog.setOnPositiveListener("삭제", () -> {
            LoadingProgressDialog.showProgress(mContext);
            ApiUtil.getProductService().deleteProduct(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), product.getId()).enqueue(new Callback<ApiResult>() {
                @Override
                public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                    LoadingProgressDialog.hideProgress();
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        mCategoryList.get(parentPosition).getChildList().remove(childPosition);
                        mAdapter.notifyChildRemoved(parentPosition, childPosition);

                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                    LoadingProgressDialog.hideProgress();
                    Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void addProduct(Category category, String name, long price, int time, File image) {
        if (mProductImage == null) {
            Toast.makeText(mContext, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        MediaType multipart = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(multipart, mProductImage);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", mProductImage.getName(), requestFile);
        MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", PreferenceHelper.loadId(mContext));
        MultipartBody.Part pwPart = MultipartBody.Part.createFormData("pw", PreferenceHelper.loadPw(mContext));
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", name);
        MultipartBody.Part pricePart = MultipartBody.Part.createFormData("price", String.valueOf(price));
        MultipartBody.Part timePart = MultipartBody.Part.createFormData("taking_time", String.valueOf(time));
        MultipartBody.Part categoryIdPart = MultipartBody.Part.createFormData("category_id", String.valueOf(category.getId()));

        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getProductService().makeProduct(idPart, pwPart, categoryIdPart, namePart, pricePart, timePart, imagePart).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                mProductImage = null;
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    queryProduct();
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                mProductImage = null;
                LoadingProgressDialog.hideProgress();
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProduct(String name, long price, int time, File image, int productId, int type) {
        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getProductService().editProduct(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), productId, type, price, name, time).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                mProductImage = null;
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    queryProduct();
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                mProductImage = null;
                LoadingProgressDialog.hideProgress();
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProductWithImage(String name, long price, int time, File image, int productId, int type) {
        if (mProductImage == null) {
            Toast.makeText(mContext, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            MediaType multipart = MediaType.parse("multipart/form-data");
            RequestBody requestFile = RequestBody.create(multipart, mProductImage);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", mProductImage.getName(), requestFile);
            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", PreferenceHelper.loadId(mContext));
            MultipartBody.Part pwPart = MultipartBody.Part.createFormData("pw", PreferenceHelper.loadPw(mContext));
            MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", name);
            MultipartBody.Part pricePart = MultipartBody.Part.createFormData("price", String.valueOf(price));
            MultipartBody.Part timePart = MultipartBody.Part.createFormData("taking_time", String.valueOf(time));
            MultipartBody.Part categoryIdPart = MultipartBody.Part.createFormData("category_id", String.valueOf(type));
            MultipartBody.Part productIdPart = MultipartBody.Part.createFormData("menu_id", String.valueOf(productId));

            LoadingProgressDialog.showProgress(mContext);
            ApiUtil.getProductService().editProduct(idPart, pwPart, categoryIdPart, productIdPart, namePart, pricePart, timePart, imagePart).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                    mProductImage = null;
                    LoadingProgressDialog.hideProgress();
                    if (response.code() == 200) {
                        queryProduct();
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                    mProductImage = null;
                    LoadingProgressDialog.hideProgress();
                    Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void attemptEditProductImage(View view) {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startGallery(GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSION_GALLERY:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    startGallery(GALLERY_REQUEST_CODE);
                } else {
                    PermissionUtil.showRationalDialog(mContext, getString(R.string.permission_need_permission));
                }
                break;
        }
    }

    private void startGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    private void startCrop(Uri originalUri, int requestCode) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.colorWhite));
        options.setToolbarWidgetColor(getResources().getColor(R.color.colorGray3));
        options.setToolbarTitle(getString(R.string.title_crop_image));
        options.setLogoColor(getResources().getColor(R.color.colorBlack));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorGray4));
        options.setStatusBarColor(getResources().getColor(R.color.colorBlack));
        options.setCompressionQuality(90);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        if (requestCode == GALLERY_REQUEST_CODE) {
            mProductImage = new File(mContext.getCacheDir(), System.currentTimeMillis() + "_crop");
            UCrop.of(originalUri, Uri.fromFile(mProductImage)).withOptions(options).withAspectRatio(1, 1).withMaxResultSize(500, 500).start(this, CROP_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == GALLERY_REQUEST_CODE) && (resultCode == RESULT_OK) && data != null) {
            if (data.getData() != null) {
                startCrop(data.getData(), requestCode);
            } else {
                Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == CROP_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (mAddProductDialog != null && mAddProductDialog.isShowing()) {
                mAddProductDialog.showImage(mProductImage);
            }
            if (mEditProductDialog != null && mEditProductDialog.isShowing()) {
                mEditProductDialog.showImage(mProductImage);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }
}
