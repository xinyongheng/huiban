package com.bshuiban.baselibrary;

import com.bshuiban.baselibrary.internet.RetrofitService;
import com.bshuiban.baselibrary.present.ClassActivityPresent;
import com.bshuiban.baselibrary.present.ClassSchedulePresent;
import com.bshuiban.baselibrary.present.HomePageParent;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        //assertEquals(4, 2 + 2);
    }
    @Test
    public void text() throws InterruptedException {
        //new ClassActivityPresent(null).askInternetForClassActivityData("3000153",0,20);
        //new ClassSchedulePresent(null).askInternetForScheduleData("3000153");
        //new HomePageParent(null).test();{"userId":"2030246","start":0,"limit":20}
        new HomePageParent(null).getMessageList("2030246");
//        RetrofitService.getInstance().getServiceResult("getClassNoticeList","{\"userId\":\"2030246\",\"start\":0,\"limit\":20}",new RetrofitService.CallTest());

        Thread.sleep(3000);

    }
}