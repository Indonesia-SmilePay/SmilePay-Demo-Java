package smile;

import com.google.gson.Gson;
import id.smile.SignatureUtil;
import id.smile.SmileConstant;
import id.smile.req.AddressReq;
import id.smile.req.ItemDetailReq;
import id.smile.req.MerchantReq;
import id.smile.req.MoneyReq;
import id.smile.req.PayerReq;
import id.smile.req.ReceiverReq;
import id.smile.req.TradePayinReq;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;


public class Step3_Payin extends BaseTest {

    //accessToken.  from step2
    private String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MTQ0NjcxMDUsImV4cCI6MTcxNDQ2ODAwNSwiaWF0IjoxNzE0NDY3MTA1LCJNRVJDSEFOVF9JRCI6IjEwMDAxIn0.QaB1MLlsgGplQNxtziqbFG_5ZDpJRFN4lIo8qp2_0b0";


    @Test
    public void payin() throws Exception {
        System.out.println("=====> step3 : Payin transaction");

        //url
        String endPointUlr = SmileConstant.PAY_IN_API;
        String url = SmileConstant.BASE_URL + endPointUlr;

        String timestamp = ZonedDateTime.of(LocalDateTime.now(), SmileConstant.ZONE_ID).format(SmileConstant.DF_0);
        System.out.println("timestamp = " + timestamp);
        String partnerId = SmileConstant.MERCHANT_ID;

        //generate parameter
        String merchantOrderNo = "T_" + System.currentTimeMillis();
        String purpose = "Purpose For Transaction from Java SDK";
        String paymentMethod = "W_DANA";

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(SmileConstant.CURRENCY);
        moneyReq.setAmount(new BigDecimal("10000"));

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(partnerId);
        merchantReq.setMerchantName(null);

        //payerReq
        PayerReq payerReq = new PayerReq();
        payerReq.setName("Jeffery");
        payerReq.setPhone("018922990");
        payerReq.setAddress("Jalan Pantai Mutiara TG6, Pluit, Jakarta");
        payerReq.setEmail("jef.gt@gmail.com");
        payerReq.setIdentity(null);

        //receiverReq
        ReceiverReq receiverReq = new ReceiverReq();
        receiverReq.setName("Viva in");
        receiverReq.setPhone("018922990");
        receiverReq.setAddress("Jl. Pluit Karang Ayu 1 No.B1 Pluit");
        receiverReq.setEmail("Viva@mir.com");
        receiverReq.setIdentity(null);

        //itemDetailReq
        ItemDetailReq itemDetailReq = new ItemDetailReq();
        itemDetailReq.setName("mac A1");
        itemDetailReq.setQuantity(1);
        itemDetailReq.setPrice(new BigDecimal("10000"));

        //billingAddress
        AddressReq billingAddress = new AddressReq();
        billingAddress.setCountryCode("Indonesia");
        billingAddress.setCity("jakarta");
        billingAddress.setAddress("Jl. Pluit Karang Ayu 1 No.B1 Pluit");
        billingAddress.setPhone("018922990");
        billingAddress.setPostalCode("14450");

        //shippingAddress
        AddressReq shippingAddress = new AddressReq();
        shippingAddress.setCountryCode("Indonesia");
        shippingAddress.setCity("jakarta");
        shippingAddress.setAddress("Jl. Pluit Karang Ayu 1 No.B1 Pluit");
        shippingAddress.setPhone("018922990");
        shippingAddress.setPostalCode("14450");

        //payinReq
        TradePayinReq payinReq = new TradePayinReq();
        payinReq.setOrderNo(merchantOrderNo);
        payinReq.setPurpose(purpose);
        payinReq.setProductDetail("Product details");
        payinReq.setAdditionalParam("other descriptions");
        payinReq.setItemDetailList(Collections.singletonList(itemDetailReq));
        payinReq.setBillingAddress(billingAddress);
        payinReq.setShippingAddress(shippingAddress);
        payinReq.setMoney(moneyReq);
        payinReq.setMerchant(merchantReq);
        payinReq.setCallbackUrl(null);
        payinReq.setRedirectUrl(null);
        payinReq.setPaymentMethod(paymentMethod);
        payinReq.setPayer(payerReq);
        payinReq.setReceiver(receiverReq);
        payinReq.setExpiryPeriod(null);
        //payinReq.setFeeMethod("Excluded");

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(payinReq);
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

        System.out.println("The pay interface is completed, and you can get your payment link");
    }

}
