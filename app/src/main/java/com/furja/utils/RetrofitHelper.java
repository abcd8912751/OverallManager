package com.furja.utils;



import com.furja.common.BaseHttpResponse;
import com.furja.alertsop.beans.ProduceNoAndModel;
import com.furja.alertsop.jsonbeans.MaterialJson;
import com.furja.iqc.beans.CheckCBList;
import com.furja.iqc.json.NewQCList;
import com.furja.iqc.json.QMAGroupBean;
import com.furja.overall.beans.InspectHistoryLog;
import com.furja.overall.json.LoginJson;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * retrofit请求数据的接口
 */
public interface RetrofitHelper {
    @GET("FJCommonInterface/GetBarCodeInfo/")
    Observable<MaterialJson> getMaterialJson(@Query("BarCode") String barCode);

    @GET("/FJCommonInterface/GetFurInfo/")
    Observable<MaterialJson> getFurInfo(@Query("BarCode") String barCode);

    @GET("/base/producenoandmodel/")
    Observable<BaseHttpResponse<List<ProduceNoAndModel>>> getProduceModel(@Query("produceno") String produceNo);

    @Headers({"Content-Type: application/json"})
    @POST("base/errorlog")
    Observable<ResponseBody> postErrorLog(@Body RequestBody requestBody);

    @GET("GetLoginList")
    Observable<LoginJson> getLoginCall(@Query("user") String user, @Query("password") String password);

    @GET("FJCommonInterface/GetBarCodeInfo/")
    Call<com.furja.iqc.json.MaterialJson> getParts(@Query("BarCode") String barCode);

    @FormUrlEncoded
    @POST("SendQCListByTable")                  //其中 receive 为"让步接收时",进行让步
    Call<String> postApplyPurchaseOrder(@Field("CodeBar") String barCode, @Field("QCEntryData")String QCEntryData, @Field("FItemId")String userID, @Field("status")String receive);

    @GET("IsCheckCBlist")
    Call<CheckCBList> isCheckCBList(@Query("codebar") String barCode, @Query("comparecodebar") String comparecodebar, @Query("cb") String cb);



    @GET("k3cloud/checkcblist")
    Observable<CheckCBList> checkCBList(@Query("barcode") String barCode, @Query("comparecodebar") String comparecodebar, @Query("cb") String cb);

    @GET("k3cloud/qclist")
    Observable<BaseHttpResponse<NewQCList>> getQcList(@Query("barcode") String barCode);

    @Headers({"Content-Type: application/json"})
    @POST("k3cloud/inspectbill")
    Observable<BaseHttpResponse<String>> postInspectBill(@Body RequestBody requestBody);

    @GET("k3cloud/qmagroup")
    Observable<BaseHttpResponse<List<QMAGroupBean>>> getQMAGroupInfo();

    @GET("k3cloud/passport")
    Observable<BaseHttpResponse<String>> login(@Query("username") String userName,@Query("password") String password);

    @GET("k3cloud/inspecthistory")
    Observable<BaseHttpResponse<List<InspectHistoryLog>>> getInspectHistory();

}
