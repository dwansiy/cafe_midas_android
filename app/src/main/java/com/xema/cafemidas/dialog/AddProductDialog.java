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
import com.xema.cafemidas.common.GlideApp;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProductDialog extends Dialog {
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

    public AddProductDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public interface OnRegisterListener {
        void onRegister(String name, long price, int time, File image);
    }

    public void setListener(OnRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_add_product);
        ButterKnife.bind(this);

        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) {
                String name = edtProductName.getText().toString();
                String price = edtProductPrice.getText().toString();
                String time = edtTakingTime.getText().toString();
                if (!TextUtils.isEmpty(price) && TextUtils.isDigitsOnly(price) && mImage != null && TextUtils.isDigitsOnly(time)) {
                    listener.onRegister(name, Long.parseLong(price), Integer.parseInt(time), mImage);
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
