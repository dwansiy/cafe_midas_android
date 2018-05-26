package com.xema.cafemidas.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.OrderCategoryAdapter;
import com.xema.cafemidas.adapter.OrderMenuAdapter;
import com.xema.cafemidas.dialog.OrderDialog;
import com.xema.cafemidas.model.BuyDetail;
import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.model.Product;
import com.xema.cafemidas.network.ApiUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderActivity extends AppCompatActivity {

    @BindView(R.id.tb_user_main)
    Toolbar tbUserMain;
    @BindView(R.id.order_list_category)
    RecyclerView orderListCategory;
    @BindView(R.id.order_list_menu)
    RecyclerView orderListMenu;
    @BindView(R.id.order_im_cart)
    ImageView orderImCart;

    private RequestManager mRequestManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter menuAdapter;
    private ArrayList<Category> category_list;
    private ArrayList<Product> menu_list;
    private ArrayList<BuyDetail> buy_detail_list;
    private LinearLayoutManager linearLayoutManager;

    private int po;
    private int cate_po;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        category_list = new ArrayList<>();
        menu_list = new ArrayList<>();
        buy_detail_list = new ArrayList<>();

        ButterKnife.bind(this);
        initToolbar();
        setRecyclerView();

        orderImCart.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, UserBuyDetailActivity.class);
            intent.putExtra("list", buy_detail_list);
            startActivity(intent);
        });
    }


    //구매 시 작동하는 메소드
    public void getNum(String num, int status) {
        int cnt = Integer.parseInt(num);
        int price = (int) menu_list.get(po).getPrice();
        price = price * cnt;
        //바로 주문시
        if (status == 1) {

            buy_detail_list.add(new BuyDetail(menu_list.get(po).getId(),
                    menu_list.get(po).getName(), num, String.valueOf(price)));
            Intent intent = new Intent(getApplicationContext(), UserBuyDetailActivity.class);
            intent.putExtra("list", buy_detail_list);
            startActivity(intent);

        } else {
            buy_detail_list.add(new BuyDetail(menu_list.get(po).getId(),
                    menu_list.get(po).getName(), num, String.valueOf(price)));

            AlertDialog.Builder alert = new AlertDialog.Builder(UserOrderActivity.this);
            alert.setMessage("장바구니로 가겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), UserBuyDetailActivity.class);
                            intent.putExtra("list", buy_detail_list);
                            startActivity(intent);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
        }
    }

    public void get(int position) {
        OrderDialog dialog = new OrderDialog(this, menu_list.get(position).getName()
                , String.valueOf(menu_list.get(position).getPrice()));
        dialog.setListener(this::getNum);
        dialog.show();
    }

    public void onClickCategory(int position) {
        menu_list.clear();

        Call<List<Product>> call = ApiUtil.getProductService().getProductList();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                for (Product orderMenu : response.body()) {
                    if (orderMenu.getType() == category_list.get(position).getId()) {
                        orderMenu.setCategory(category_list.get(position).getName());
                        Log.d("user_order", orderMenu.getName());
                        menu_list.add(orderMenu);
                    }
                }
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(UserOrderActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        mRequestManager = Glide.with(this);
        mAdapter = new OrderCategoryAdapter(category_list, mRequestManager, new OrderCategoryAdapter.onCategoryClickListener() {
            @Override
            public void onClick(View view, int position) {
                cate_po = position;
                onClickCategory(cate_po);
            }
        });
        menuAdapter = new OrderMenuAdapter(menu_list, mRequestManager, new OrderMenuAdapter.onMenuClickListener() {
            @Override
            public void onClick(View view, int position) {
                po = position;
                get(po);
            }
        });

        setCategoryList();
        // menu_list.add(new OrderMenu(0,0,"0","0",0));
        setMenuList();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        orderListCategory.setHasFixedSize(true);
        orderListCategory.setLayoutManager(linearLayoutManager);
        orderListCategory.setAdapter(mAdapter);

        orderListMenu.setHasFixedSize(true);
        orderListMenu.setLayoutManager(new LinearLayoutManager(this));
        orderListMenu.setAdapter(menuAdapter);

    }

    private void setCategoryList() {
        category_list.clear();

        Call<List<Category>> call = ApiUtil.getProductService().getCategoryList();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                category_list.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d("user_order", t.getMessage());
            }
        });

    }

    private void setMenuList() {
        menu_list.clear();

        Call<List<Product>> call = ApiUtil.getProductService().getProductList();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.body().isEmpty()) {
                    for (Product orderMenu : response.body()) {
                        if (!category_list.isEmpty() && orderMenu.getType() == category_list.get(0).getId()) {
                            orderMenu.setCategory(category_list.get(0).getName());
                            menu_list.add(orderMenu);
                        }
                    }
                    menuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(UserOrderActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        setSupportActionBar(tbUserMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
