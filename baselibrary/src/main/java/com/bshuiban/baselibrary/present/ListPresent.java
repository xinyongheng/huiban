package com.bshuiban.baselibrary.present;

import android.util.Log;

import com.bshuiban.baselibrary.contract.BaseView;
import com.bshuiban.baselibrary.contract.ListContract;
import com.bshuiban.baselibrary.internet.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * Created by xinheng on 2018/5/18.<br/>
 * describe：
 */
public class ListPresent<T extends BaseView> extends BasePresent<T> implements ListContract.Present {
    protected int limit=10;
    protected int start;
    protected JsonArray jsonArray;
    protected Gson gson=new Gson();
    protected RetrofitService.CallHTMLJsonArray callHTMLJsonArray= new RetrofitService.CallHTMLJsonArray() {
        @Override
        protected void success(JsonArray msg) {
            Log.e("TAG", "success: "+msg);
            if(isEffective()){
                if(null==jsonArray){
                    jsonArray=msg;
                    updateView(gson.toJson(jsonArray));
                }else{
                    if(null!=msg&&msg.size()>0) {
                        jsonArray.addAll(msg);
                        updateView(gson.toJson(jsonArray));
                    }
                }
            }
        }

        @Override
        protected void fail(String error) {
            if(isEffective()){
                view.fail(error);
            }
        }
    };
    public ListPresent(T t) {
        super(t);
    }

    @Override
    public void loadMoreData() {
        start+=limit;
        getInterNetData();
    }

    @Override
    public void refresh() {
        start=0;
        getInterNetData();
    }

    @Override
    public void getInterNetData() {

    }

    @Override
    public void updateView(String json) {

    }
}
