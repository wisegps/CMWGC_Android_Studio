package com.wgc.cmwgc.fragment;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.Until.EncodingHandler;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/11/4.
 */
public class CarTeamFiveFragment extends BaseFragment {

    public static CarTeamFiveFragment instance = null;
    @Bind(R.id.iv_imei_erweima)
    ImageView ivImeiErweima;

    public static CarTeamFiveFragment getInstance() {
        if (instance == null) {
            instance = new CarTeamFiveFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_team_five;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {
        createQECode();
    }

    private void createQECode() {
        try {
            if (!Config.con_serial.equals("")) {
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(Config.con_serial, 350);
                ivImeiErweima.setImageBitmap(qrCodeBitmap);
            }
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
