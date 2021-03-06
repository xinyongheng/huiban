package com.bshuiban.baselibrary.view.webview.webActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;

import com.bshuiban.baselibrary.contract.HuiFuDaoListContract;
import com.bshuiban.baselibrary.model.LogUtils;
import com.bshuiban.baselibrary.present.HuiFuDaoListPresent;
import com.bshuiban.baselibrary.view.webview.javascriptInterfaceClass.MessageList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.sql.StatementEvent;

/**
 * Created by xinheng on 2018/5/18.<br/>
 * describe：慧辅导列表
 */
public class HuiFuDaoListActivity extends BaseWebActivity<HuiFuDaoListPresent> implements HuiFuDaoListContract.View {
    /**
     * 本地html的名字
     */
    private final String HTML_FILE_NAME = "coach";
    private boolean jingPin;
    private String jsonListData;
    private String jsonGuessData;
    private String subjectId;
    /**
     * 列表数据与猜你所想结果标识
     * tag >= 2 全部返回
     * -1 精品需要掉用一次
     */
    private int tag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tPresent = new HuiFuDaoListPresent(this);
        setTAG(getLocalClassName());
        loadFileHtml(HTML_FILE_NAME);
        HuiFuDaoHtml messageList = new HuiFuDaoHtml();
        messageList.setOnListener(new MessageList.OnMessageListListener() {
            @Override
            public void refresh() {
                tPresent.refresh();
            }

            @Override
            public void loadMore() {
                tPresent.loadMoreData();
            }
        });
        registerWebViewH5Interface(messageList);
    }

    @Override
    protected void webViewLoadFinished() {
        startDialog();
        tPresent.getAllSubject();
        loadJingPing();
    }

    private void loadJingPing() {
        JsonObject jsonObject = new JsonObject();
        jingPin = true;
        tPresent.reSetStart();
        tPresent.screeningLesson("getHBCourseList", new Gson().toJson(jsonObject));
    }

    @Override
    public void updateList(String json) {
        LogUtils.e(TAG, "updateList: ");
        if (jingPin) {
            loadJavascriptMethod("listMsg", json);
        } else {
            jsonListData = json;
            //loadListAndGuessData();
        }
        if (null != myLoadingDialog && myLoadingDialog.isShowing()) {
            new Handler().postDelayed(() ->
                    dismissDialog()
            ,1500);
        }
    }

    private void loadListAndGuessData() {
        LogUtils.e(TAG, "loadListAndGuessData: ");
        loadJavascriptMethod("xr", (jsonListData), (jsonGuessData));
        jsonGuessData = null;
        jsonListData = null;
    }

    @Override
    public void startDialog() {
        showLoadingDialog();
    }

    @Override
    public void dismissDialog() {
        dismissLoadingDialog();
    }

    @Override
    public void fail(String error) {
        toast(error);
    }

    @Override
    public void loadAllSubject(String json) {
        LogUtils.e(TAG, "loadAllSubject: ");
        loadJavascriptMethod("getListNav", (json));
    }

    @Override
    public void loadGuessWhatYouThink(String json) {
        jsonGuessData = json;
        LogUtils.e(TAG, "loadGuessWhatYouThink: ");
        //loadJavascriptMethod("xr",replaceJson(,json));
        //loadListAndGuessData();
    }

    @Override
    public void addTag() {
        if (!jingPin) {
            ++tag;
            if (tag >= 2) {
                loadListAndGuessData();
                tag = 0;
            }
        }
    }

    @Override
    public void loadScreeningData(String json) {
        loadJavascriptMethod("sxContent", (json));
    }

    class HuiFuDaoHtml extends MessageList {
        @JavascriptInterface
        public void reSetStart() {
            tPresent.reSetStart();
        }

        @JavascriptInterface
        public void loadMoreJingping() {
            if (tPresent.getSubjectId() == -1) {
                tPresent.clearArray();
                tPresent.loadMoreData();
            } else {
                loadJingPing();
            }
        }

        @JavascriptInterface
        public void dealWithJson(String key, String json) {
            if ("getHBCourseList".equals(key)) {
                //tPresent.reSetStart();
                jingPin = false;
                tPresent.screeningLesson("getHBCourseList", json);
            } else {//获取筛选条件
                tPresent.getScreeningData(key, json);
            }
        }

        @JavascriptInterface
        public void updateSubjectId(String subjectId) {
            HuiFuDaoListActivity.this.subjectId = subjectId;
            jingPin = false;
            startFilterPage();
        }

        @JavascriptInterface
        public void itemClick(String courseId) {
            startActivity(new Intent(getApplicationContext(), LessonInfWebActivity.class).putExtra("courseId", courseId));
        }

        private void startFilterPage() {
            startActivityForResult(new Intent(getApplicationContext(), FilterHuiFuDaoWebActivity.class).putExtra("subjectId", subjectId), 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (null != data) {
                    String json = data.getStringExtra("json");
                    tPresent.screeningLesson("getHBCourseList", json);
                }
                break;
        }
    }
}
