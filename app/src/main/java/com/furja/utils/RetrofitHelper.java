package com.furja.utils;



import com.furja.common.BaseHttpResponse;
import com.furja.common.ProduceNoAndModel;
import com.furja.common.MaterialJson;
import com.furja.iqc.beans.CheckCBList;
import com.furja.iqc.json.NewQCList;
import com.furja.iqc.json.QMAGroupBean;
import com.furja.overall.beans.InspectHistoryLog;

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
import retrofit2.http.Url;

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

    @GET("FJCommonInterface/GetBarCodeInfo/")
    Call<com.furja.iqc.json.MaterialJson> getParts(@Query("BarCode") String barCode);

    @GET("IsCheckCBlist")
    Call<CheckCBList> isCheckCBList(@Query("codebar") String barCode, @Query("comparecodebar") String comparecodebar, @Query("cb") String cb);

    @GET("k3cloud/checkcblist")
    Observable<CheckCBList> checkCBList(@Query("barcode") String barCode, @Query("comparecodebar") String comparecodebar, @Query("cb") String cb);

    @GET("k3cloud/qclist")
    Observable<BaseHttpResponse<NewQCList>> getQcList(@Query("barcode") String barCode,@Query("username") String username);

    @Headers({"Content-Type: application/json"})
    @POST("k3cloud/inspectbill")
    Observable<BaseHttpResponse<String>> postInspectBill(@Body RequestBody requestBody);

    @GET("k3cloud/qmagroup")
    Observable<BaseHttpResponse<List<QMAGroupBean>>> getQMAGroupInfo();

    @GET("k3cloud/passport")
    Observable<BaseHttpResponse<String>> login(@Query("username") String userName,@Query("password") String password);

    @GET("k3cloud/inspecthistory")
    Observable<BaseHttpResponse<List<InspectHistoryLog>>> getInspectHistory(@Query("username") String userName);

    @GET
    Observable<ResponseBody> request(@Url String url);
}
