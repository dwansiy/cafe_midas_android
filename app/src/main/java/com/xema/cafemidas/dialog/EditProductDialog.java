package com.xema.cafemidas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.cafemidas.R;
import com.xema.cafemidas.activity.CategoryActivity;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.model.Product;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProductDialog extends Dialog {
    @BindView(R.id.edt_product_name)
    EditText edtProductName;
    @BindView(R.id.edt_product_price)
    EditText edtProductPrice;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.riv_image)
    RoundedImageView rivImage;
    @BindView(R.id.edt_taking_time)
    EditText edtTakingTime;

    private Context mContext;
    private OnRegisterListener listener;
    private File mImage;

    private Product mProduct;

    public EditProductDialog(@NonNull Context context, Product product) {
        super(context);
        mContext = context;
        mProduct = product;
    }

    public interface OnRegisterListener {
        void onRegisterImage(String name, long price, int time, File image, int productId, int type);

        void onRegister(String name, long price, int time, File image, int productId, int type);
    }

    public void setListener(OnRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_edit_product);
        ButterKnife.bind(this);

        edtProductName.setText(mProduct.getName());
        edtProductPrice.setText(String.valueOf(mProduct.getPrice()));
        edtTakingTime.setText(String.valueOf(mProduct.getTakingTime()));
        GlideApp.with(mContext).load(Constants.BASE_URL + mProduct.getImage()).into(rivImage);

        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) {
                String name = edtProductName.getText().toString();
                String price = edtProductPrice.getText().toString();
                String time = edtTakingTime.getText().toString();
                if (!TextUtils.isEmpty(price) && TextUtils.isDigitsOnly(price) && mImage != null && TextUtils.isDigitsOnly(time)) {
                    listener.onRegisterImage(name, Long.parseLong(price), Integer.parseInt(time), mImage, mProduct.getId(), mProduct.getType());
                    dismiss();
                } else if (!TextUtils.isEmpty(price) && TextUtils.isDigitsOnly(price)&& TextUtils.isDigitsOnly(time)) {
                    listener.onRegister(name, Long.parseLong(price), Integer.parseInt(time), mImage, mProduct.getId(), mProduct.getType());
                    dismiss();
                } else {
                    // TODO: 2018-05-26  
                    Toast.makeText(mContext, "형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            } else {
                dismiss();
            }
        });
        rivImage.setOnClickListener(v -> {
            if (mContext instanceof CategoryActivity) {
                ((CategoryActivity) mContext).attemptEditProductImage(v);
            }
        });
    }

    public void showImage(File image) {
        if (isShowing() && rivImage != null) {
            mImage = image;
            GlideApp.with(mContext).load(image).into(rivImage);
        }
    }
}
