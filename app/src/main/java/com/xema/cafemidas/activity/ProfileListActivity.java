package com.xema.cafemidas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.ProfileAdapter;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.CommonUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileListActivity extends AppCompatActivity {
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    private List<Profile> mList;
    private ProfileAdapter mAdapter;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        mContext = this;
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        initAdapter();
        //queryProfiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2018-05-27 최적화
        queryProfiles();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbMain.setTitle(getString(R.string.common_loading));
    }

    private void initListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProfileActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_PROFILE);
        });
        // TODO: 2018-05-26
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                attemptSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void attemptSearch(String s) {
        if (TextUtils.isEmpty(s)) {
            //전체 표시
            queryProfiles();
        } else {
            queryProfiles(s);
        }
    }

    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new ProfileAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
    }

    private void queryProfiles() {
        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getAccountService().getProfileList(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext)).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(@NonNull Call<List<Profile>> call, @NonNull Response<List<Profile>> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    List<Profile> profileList = response.body();
                    if (profileList != null) {
                        tbMain.setTitle("사용자 " + mList.size() + "명");
                        if (profileList.isEmpty()) {
                            rvMain.setVisibility(View.GONE);
                            llEmpty.setVisibility(View.VISIBLE);
                        } else {
                            llEmpty.setVisibility(View.GONE);
                            rvMain.setVisibility(View.VISIBLE);
                            mList.clear();
                            mList.addAll(profileList);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Profile>> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: 2018-05-26
        /*
        RealmResults<Person> results = realm.where(Person.class).sort("name").findAll();
        if (results == null || results.size() == 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);

            if (mList != null) mList.clear();
            else mList = new ArrayList<>();
            mList.addAll(results);
            mAdapter.notifyDataSetChanged();
        }

        tbMain.setTitle(getString(R.string.format_count_customer, mList.size()));
        */
    }

    private void queryProfiles(String s) {
        /*
        RealmResults<Person> results = realm.where(Person.class).sort("name").contains("name", s).or().contains("phone", s).findAll();
        if (mList != null) mList.clear();
        else mList = new ArrayList<>();
        mList.addAll(results);
        mAdapter.notifyDataSetChanged();

        // TODO: 2018-02-16 ui업데이트할것 메소드 따로 분리해서
        tbMain.setTitle(getString(R.string.format_search_customer, mList.size()));
        */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_PROFILE && resultCode == RESULT_OK) {
            edtSearch.getText().clear();
            CommonUtil.hideKeyboard(this);
            queryProfiles();
        }
    }


}
