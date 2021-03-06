package com.bshuiban.baselibrary.present;

import com.bshuiban.baselibrary.contract.BaseView;
import com.bshuiban.baselibrary.internet.RetrofitService;
import com.bshuiban.baselibrary.model.SubjectBean;
import com.bshuiban.baselibrary.model.User;

/**
 * Created by xinheng on 2018/5/21.<br/>
 * describe：所有学科
 */
public class AllSubjectPresent<V extends BaseView> extends BasePresent<V> {
    public AllSubjectPresent(V v) {
        super(v);
    }
    public void getAllSubject(){
        SubjectBean dataBean = User.getInstance().getSubjectBean();
        if(null!=dataBean&&isEffective()){
            loadAllSubject(dataBean);
            return;
        }//{"userId":""}
        RetrofitService.getInstance().getServiceResult("getVipCourseSubjectList", "{\"userId\":\""+User.getInstance().getUserId()+"\"}", new RetrofitService.CallResult<SubjectBean>(SubjectBean.class) {
            @Override
            protected void success(SubjectBean subjectBean) {
                User.getInstance().setSubjectBean(subjectBean);
                if(null!=subjectBean) {
                    loadAllSubject(subjectBean);
                }else {
                    error("暂无科目");
                }
            }

            @Override
            protected void error(String error) {
                fail(error);
            }
        });
    }
    protected void fail(String s){
        if(isEffective()){
            view.fail(s);
        }
    }
    protected void loadAllSubject(SubjectBean dataBean) {

    }
}
