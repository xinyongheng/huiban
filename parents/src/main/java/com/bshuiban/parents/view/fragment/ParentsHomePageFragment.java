package com.bshuiban.parents.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;

import com.bshuiban.baselibrary.model.User;
import com.bshuiban.baselibrary.utils.DialogUtils;
import com.bshuiban.baselibrary.view.activity.ClassActivity;
import com.bshuiban.baselibrary.view.activity.ClassScheduleActivity;
import com.bshuiban.baselibrary.view.webview.javascriptInterfaceClass.MessageList;
import com.bshuiban.baselibrary.view.webview.webActivity.GuanZhuListActivity;
import com.bshuiban.baselibrary.view.webview.webActivity.HuiFuDaoListActivity;
import com.bshuiban.baselibrary.view.webview.webActivity.LessonInfWebActivity;
import com.bshuiban.baselibrary.view.webview.webActivity.LiuYanMsgListActivity;
import com.bshuiban.baselibrary.view.webview.webActivity.NoticeActivity;
import com.bshuiban.baselibrary.view.webview.webFragment.HomePageFragment;
import com.bshuiban.parents.view.webView.webActivity.PatentsParentConfirmWebActivity;
import com.bshuiban.parents.view.webView.webActivity.TodayHomeworkWebActivity;

/**
 * Created by xinheng on 2018/5/15.<br/>
 * describe：学生首页
 * 1.获取今日学生课表
 * 2.慧辅导两条数据
 * 3.留言列表 message
 *
 *
 * H5-android
 * 删除--deleteMessageItem
 * 回复--replayMessage
 * 加载更多--
 */
public class ParentsHomePageFragment extends HomePageFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setWebViewSetting(mWebView);
        StuHomePageHtml stuHomePageHtml=new StuHomePageHtml();
        stuHomePageHtml.setOnListener(new MessageList.MessageListListener() {
            @Override
            public void deleteMessageItem(String messageId, String pid) {
                DialogUtils.showMessageSureCancelDialog(getActivity(),"确认删除此留言？", v -> tPresent.delete(messageId,pid));
            }

            @Override
            public void replayMessage(String json) {
                tPresent.addRecevier(json);
            }

            @Override
            public void loadMore() {
                tPresent.loadMoreData();
            }

            @Override
            public void refresh() {
                tPresent.refresh();
            }
        });
        loadFileHtml("parents");
        registerWebViewH5Interface(stuHomePageHtml);
        return view;
    }

    @Override
    protected void webViewLoadFinished() {
        String userId = User.getInstance().getUserId();
        tPresent.getTodaySchedule(userId);//今日课表
        tPresent.getHuiFuDaoTwoData();//慧辅导两条数据
        //tPresent.getMessageList(userId);//留言列表
        loadJavascriptMethod("userimgs",User.getInstance().getUserData().getIcoPath());
    }

    private void toNextActivity(int type) {
        Log.e(TAG, "toNextActivity: type="+type );
        Class<?> cls = null;
        Intent intent=new Intent();
        switch (type) {
            // 0 班级，1 课表，2 通知，3 关注，4 慧辅导，5 学习资源，6 去留言
            case 0://班级
                cls= ClassActivity.class;
                break;
            case 1://课表
                cls=ClassScheduleActivity.class;
                break;
            case 2://通知
                cls= NoticeActivity.class;
                //cls= LessonListActivity.class;
                //intent.putExtra("send",true);
                break;
            case 3://关注
                cls= GuanZhuListActivity.class;
                break;
            case 4: //慧辅导
                cls= HuiFuDaoListActivity.class;
                break;
            case 6:// 去留言
                intent.putExtra("name",User.getInstance().getUserName());
                cls=LiuYanMsgListActivity.class;
                break;
            case 7://相关课程
                //cls=LessonListActivity.class;
                try {
                    cls=Class.forName("com.bshuiban.teacher.view.webView.webActivity.LessonListActivity");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 8://今日作业
                cls= TodayHomeworkWebActivity.class;
                break;
            case 9://家长确认
                cls= PatentsParentConfirmWebActivity.class;
                break;
            default:
                //学习资源
                cls=null;
        }
        if(null!=cls) {
            intent.setClass(getActivity(),cls);
            startActivity(intent);
        }
    }

    class StuHomePageHtml extends MessageList {
        /**
         * 进入下一个页面
         * @param type 0 班级，1 课表，2 通知，3 关注，4 慧辅导，5 学习资源，6 去留言
         */
        @JavascriptInterface
        public void toNextActivity(int type) {
            getActivity().runOnUiThread(() -> {
                ParentsHomePageFragment.this.toNextActivity(type);
            });
        }
        @JavascriptInterface
        public void toNextHuiFuActivity(String courseId) {
            getActivity().runOnUiThread(() -> startActivity(new Intent(getActivity(),LessonInfWebActivity.class).putExtra("courseId",courseId)));
        }
        /**
         * 切换侧边栏
         */
        @JavascriptInterface
        public void toggleSlide(){
            getActivity().runOnUiThread(()->{
                mListener.transportData("toggleSlide");
            });
        }
        @JavascriptInterface
        public void reply(int index){
            getActivity().runOnUiThread(()->{
                tPresent.getReplyMessage(index);
            });
        }
        @JavascriptInterface
        @Override
        public void getMoreData(boolean action) {
            if(action) {
                tPresent.getTodaySchedule(User.getInstance().getUserId());
            }
            //super.getMoreData(action);
        }
    }
}
