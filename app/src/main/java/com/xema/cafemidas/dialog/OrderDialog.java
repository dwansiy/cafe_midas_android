package com.xema.cafemidas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.xema.cafemidas.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDialog extends Dialog {


    @BindView(R.id.dialog_txt_name)
    TextView dialogTxtName;
    @BindView(R.id.dialog_txt_price)
    TextView dialogTxtPrice;
    @BindView(R.id.dialog_btn_minus)
    Button dialogBtnMinus;
    @BindView(R.id.dialog_txt_num)
    TextView dialogTxtNum;
    @BindView(R.id.dialog_btn_plus)
    Button dialogBtnPlus;
    @BindView(R.id.dialog_btn_order)
    Button dialogBtnOrder;
    @BindView(R.id.dialog_btn_cancel)
    Button dialogBtnCancel;

    String title;
    String price;
    @BindView(R.id.dialog_btn_add)
    Button dialogBtnAdd;


    private onOrderListener listener;

    public OrderDialog(@NonNull Context context, String title, String price) {
        super(context);
        this.title = title;
        this.price = price;

    }

    public interface onOrderListener {
        void getNum(String num, int status);
    }

    public void setListener(onOrderListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_order);
        ButterKnife.bind(this);
        dialogTxtNum.setText("1");
        dialogTxtName.setText(title);
        dialogTxtPrice.setText(price);
        dialogBtnOrder.setOnClickListener(v ->
        {
            //1은 바로 주문
            if (listener != null) {
                listener.getNum(dialogTxtNum.getText().toString(), 1);
                dismiss();
            }
        });
        dialogBtnAdd.setOnClickListener(v ->
        {
            //2는 담기
            if (listener != null) {
                listener.getNum(dialogTxtNum.getText().toString(), 2);
                dismiss();
            }
        });


    }

    @OnClick({R.id.dialog_btn_minus, R.id.dialog_btn_plus, R.id.dialog_btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_btn_minus: {
                int num = Integer.parseInt(dialogTxtNum.getText().toString()) - 1;
                dialogTxtNum.setText(String.valueOf(num));
                break;
            }
            case R.id.dialog_btn_plus: {
                int num = Integer.parseInt(dialogTxtNum.getText().toString()) + 1;
                dialogTxtNum.setText(String.valueOf(num));
                break;
            }

            case R.id.dialog_btn_cancel: {
                dismiss();
                break;
            }
        }
    }
}
