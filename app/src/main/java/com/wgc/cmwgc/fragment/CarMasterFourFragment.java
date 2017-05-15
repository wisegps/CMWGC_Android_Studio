package com.wgc.cmwgc.fragment;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.zxing.WriterException;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.Until.EncodingHandler;
import com.wgc.cmwgc.http.WeixinQRCode;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/11/3.
 */
public class CarMasterFourFragment extends BaseFragment {

    public static CarMasterFourFragment instance = null;

    @Bind(R.id.iv_imei_erweima)
    ImageView ivImeiErweima;

    private String imgUrl = "";
    private  WeixinQRCode weixinQRCode;

    public static CarMasterFourFragment getInstance() {
        if (instance == null) {
            instance = new CarMasterFourFragment();
        }
        return instance;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_master_four;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {
        BaseVolley.init(getActivity());
        weixinQRCode = new WeixinQRCode(getActivity());
        getQRCode();
    }



    private void getQRCode(){
        if(!TextUtils.isEmpty(Config.con_serial))
        weixinQRCode.getQRCode(Config.con_serial, new OnSuccess() {
            @Override
            protected void onSuccess(String response) {
                Logger.d("获取二维码返回的信息 ： " + response);
                parseQRCode(response);
            }
        },null);
    }

    private void parseQRCode(String response){
        try {
            JSONObject object = new JSONObject(response);
            if(object.has("status_code")){

            }else if (object.has("url")){
                imgUrl = object.getString("url");
                if(!TextUtils.isEmpty(imgUrl)){
                    if(ivImeiErweima != null)
                    Picasso.with(getActivity())
                            .load(imgUrl)
                            .resize(150,150)
                            .error(R.drawable.ic_erweima_bg)
                            .into(ivImeiErweima);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



//    private void createQECode() {
//        try {
//            if (!Config.con_serial.equals("")) {
//                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(Config.con_serial, 350);
//                ivImeiErweima.setImageBitmap(qrCodeBitmap);
//            }
//        } catch (WriterException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
