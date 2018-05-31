package com.bshuiban.baselibrary.present;

import com.bshuiban.baselibrary.contract.LiuYanMsgListContract;
import com.bshuiban.baselibrary.internet.RetrofitService;
import com.bshuiban.baselibrary.model.ResultBean;
import com.bshuiban.baselibrary.model.User;
import com.bshuiban.baselibrary.utils.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by xinheng on 2018/5/18.<br/>
 * describe：
 */
public class LiuYanMsgListParent extends ListPresent<LiuYanMsgListContract.View> implements LiuYanMsgListContract.Parent {

    private final String messageUserId;

    public LiuYanMsgListParent(LiuYanMsgListContract.View view, String messageUserId) {
        super(view);
        this.messageUserId = messageUserId;
    }

    @Override
    public void getInterNetData() {
        call = RetrofitService.getInstance().getServiceResult("getUserLeaveList", TextUtils.getUserIdListJson(messageUserId, start, limit), callHTMLJsonArray);
    }

    @Override
    public void deleteMessageItem(String messageId, String pid) {
        String key;
        String json;
        if (null == pid) {
            key = "delComment";
            json = "{\"userId\"," + User.getInstance().getUserId() + ",\"messageId\":\"" + messageId + "\"}";
        } else {
            key = "delCommentReply";
            json = "{\"pid\"," + pid + ",\"messageId\":\"" + messageId + "\"}";
        }
        call = RetrofitService.getInstance().getServiceResult(key, json, new RetrofitService.CallResult<ResultBean>(ResultBean.class) {
            @Override
            protected void success(ResultBean resultBean) {
                refresh();
            }

            @Override
            protected void error(String error) {
                if (isEffective()) {
                    view.fail(error);
                }
            }
        });
    }

    @Override
    public void replayMessage(String json) {
        JsonElement parse = new JsonParser().parse(json);
        if (parse.isJsonObject()) {
            JsonObject jsonObject = parse.getAsJsonObject();
            jsonObject.addProperty("userId", User.getInstance().getUserId());
            json = gson.toJson(jsonObject);
            RetrofitService.getInstance().getServiceResult("addLeaveMessage", json, new RetrofitService.CallResult<ResultBean>(ResultBean.class) {
                @Override
                protected void success(ResultBean resultBean) {
                    refresh();
                    view.fail("成功");
                }

                @Override
                protected void error(String error) {
                    if(isEffective()){
                        view.fail(error);
                    }
                }
            });
        }
    }
    @Override
    public void getReplyMessage(int index) {

    }
}
