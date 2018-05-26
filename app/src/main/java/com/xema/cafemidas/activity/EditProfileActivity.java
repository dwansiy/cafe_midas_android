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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.cafemidas.R;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;
import com.xema.cafemidas.util.PermissionUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xema0 on 2018-05-26.
 */

public class EditProfileActivity extends AppCompatActivity {
    @BindView(R.id.iv_profile)
    RoundedImageView ivProfile;
    @BindView(R.id.iv_edit_profile_image)
    ImageView ivEditProfileImage;
    @BindView(R.id.edt_nickname)
    EditText edtNickname;
    @BindView(R.id.edt_comment)
    EditText edtComment;
    @BindView(R.id.btn_edit)
    Button btnEdit;
    private Context mContext;

    private Profile mProfile;

    private File mProfileImage;

    private final static int GALLERY_REQUEST_CODE = 203;
    private final static int CROP_REQUEST_CODE = 303;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;
        ButterKnife.bind(this);

        if (getIntent() != null) {
            mProfile = getIntent().getParcelableExtra("profile");

            initData();
            initListeners();
        }
    }

    private void initData() {
        GlideApp.with(this).load(Constants.BASE_URL + mProfile.getProfileImage()).into(ivProfile);
        edtComment.setText(mProfile.getComment());
        edtNickname.setText(mProfile.getName());
    }

    private void initListeners() {
        ivEditProfileImage.setOnClickListener(this::attemptEditProfileImage);
        btnEdit.setOnClickListener(v -> {
            String id = PreferenceHelper.loadId(mContext);
            String pw = PreferenceHelper.loadPw(mContext);
            String name = edtNickname.getText().toString();
            String comment = edtComment.getText().toString();

            LoadingProgressDialog.showProgress(mContext);

            if (mProfileImage == null) {
                if (mProfile.getUid() == PreferenceHelper.loadMyProfile(mContext).getUid()) {
                    ApiUtil.getAccountService().editProfile(id, pw, name, comment).enqueue(mEditProfileCallback);
                } else {
                    ApiUtil.getAccountService().editProfileById(id, pw, mProfile.getUid(), name, comment).enqueue(mEditProfileCallback);
                }
            } else {
                MediaType multipart = MediaType.parse("multipart/form-data");
                RequestBody requestFile = RequestBody.create(multipart, mProfileImage);
                MultipartBody.Part image = MultipartBody.Part.createFormData("image", mProfileImage.getName(), requestFile);
                MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", id);
                MultipartBody.Part pwPart = MultipartBody.Part.createFormData("pw", pw);
                MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", name);
                MultipartBody.Part commentPart = MultipartBody.Part.createFormData("comment", comment);
                if (mProfile.getUid() == PreferenceHelper.loadMyProfile(mContext).getUid()) {
                    ApiUtil.getAccountService().editProfile(idPart, pwPart, namePart, commentPart, image).enqueue(mEditProfileCallback);
                } else {
                    MultipartBody.Part uidPart = MultipartBody.Part.createFormData("uid", String.valueOf(mProfile.getUid()));
                    ApiUtil.getAccountService().editProfileById(idPart, pwPart, namePart, uidPart, commentPart, image).enqueue(mEditProfileCallback);
                }
            }
        });
    }

    Callback<Profile> mEditProfileCallback = new Callback<Profile>() {
        @Override
        public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
            LoadingProgressDialog.hideProgress();
            if (response.code() == 200) {
                Profile body = response.body();
                if (body != null)
                    PreferenceHelper.saveMyProfile(mContext, body);
                Toast.makeText(mContext, "수정되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            LoadingProgressDialog.hideProgress();
            Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    };

    private void attemptEditProfileImage(View view) {
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
            mProfileImage = new File(mContext.getCacheDir(), System.currentTimeMillis() + "_crop");
            UCrop.of(originalUri, Uri.fromFile(mProfileImage)).withOptions(options).withAspectRatio(1, 1).withMaxResultSize(500, 500).start(this, CROP_REQUEST_CODE);
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
            GlideApp.with(this).load(mProfileImage).into(ivProfile);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            //UCrop.getError(data).printStackTrace();
            Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }

}
