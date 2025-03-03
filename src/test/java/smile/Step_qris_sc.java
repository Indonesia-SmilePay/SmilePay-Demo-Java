package smile;

import com.google.gson.Gson;
import id.smile.SignatureUtil;
import id.smile.SmileConstant;
import id.smile.req.InquiryStatusReq;
import id.smile.req.MerchantReq;
import id.smile.req.QrisScReq;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Step_qris_sc extends BaseTest {

    //accessToken.  from step2
    private String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MDYyNTU2MTEsImV4cCI6MTcwNjI1NjUxMSwiaWF0IjoxNzA2MjU1NjExLCJNRVJDSEFOVF9JRCI6IjEwMDAxIn0.gi0kpap7vcoipTvmUONBUzstP090WBdHEZvyROAUB8Q";

    @Test
    public void step_qris_sc() throws Exception {

        System.out.println("=====> step : Step_qris_sc");

        //url
        String endPointUlr = SmileConstant.QRIS_SC_API;
        String url = SmileConstant.BASE_URL + endPointUlr;

        String timestamp = ZonedDateTime.of(LocalDateTime.now(), SmileConstant.ZONE_ID).format(SmileConstant.DF_0);
        System.out.println("timestamp = " + timestamp);
        String partnerId = SmileConstant.MERCHANT_ID;

        //generate parameter
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(partnerId);
        merchantReq.setMerchantName("Java Demo Test");

        QrisScReq qrisScReq = new QrisScReq();
        qrisScReq.setMerchant(merchantReq);
        qrisScReq.setShopName("shop_name_xxxx");
        qrisScReq.setWhatsAppNotification("821874385435");
        qrisScReq.setAdditionalInfo("add");

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(qrisScReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = SignatureUtil.minify(jsonStr);
        System.out.println("minify = " + minify);

        //sha256
        byte[] bytes = SignatureUtil.SHA256(minify);

        //byte2Hex
        String byte2Hex = SignatureUtil.byte2Hex(bytes);

        //toLowerCase
        String lowerCase = byte2Hex.toLowerCase();

        //build
        String stringToSign = "POST" + ":" + endPointUlr + ":" + ACCESS_TOKEN + ":" + lowerCase + ":" + timestamp;
        System.out.println("stringToSign = " + stringToSign);

        //signature
        String signature = SignatureUtil.hmacSHA512(stringToSign, SmileConstant.MERCHANT_SECRET);
        System.out.println("signature = " + signature);

        // create httpClient
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", "Bearer " + ACCESS_TOKEN);
        httpPost.addHeader("X-TIMESTAMP", timestamp);
        httpPost.addHeader("X-SIGNATURE", signature);

        httpPost.addHeader("ORIGIN", "www.yourDomain.com");
        httpPost.addHeader("X-PARTNER-ID", partnerId);
        httpPost.addHeader("X-EXTERNAL-ID", "123729342472347234236");
        httpPost.addHeader("CHANNEL-ID", "95221");

        // set entity
        httpPost.setEntity(new StringEntity(jsonStr, StandardCharsets.UTF_8));

        // send
        HttpResponse response = httpClient.execute(httpPost);

        // response
        HttpEntity httpEntity = response.getEntity();
        String responseContent = EntityUtils.toString(httpEntity, "UTF-8");
        System.out.println("responseContent = " + responseContent);

        // release
        EntityUtils.consume(httpEntity);


    }

    //{"merchantId":"sandbox-10001","additionalInfo":null,"paymentMethodList":[{"paymentMethod":"DANAMON","paymentName":"DANAMON Bank","singleFee":3000,"singleRate":0,"amountLimitMin":10000,"amountLimitMax":1000000000,"settleType":"T1"},{"paymentMethod":null,"paymentName":null,"singleFee":null,"singleRate":null,"amountLimitMin":null,"amountLimitMax":null,"settleType":null},{"paymentMethod":"MANDIRI","paymentName":"Mandiri Bank","singleFee":3300,"singleRate":0,"amountLimitMin":10000,"amountLimitMax":1000000000,"settleType":"T1"}],"responseCode":"2009300","responseMessage":"Successful"}

}
