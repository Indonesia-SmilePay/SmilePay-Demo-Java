package smile;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import id.smile.RemoteUtil;
import id.smile.SignatureUtil;
import id.smile.SmileConstant;
import id.smile.res.AccessTokenRes;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Step2_AccessToken extends BaseTest {

    //from step1
    private static final String privateKeyStr = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDGWPgn9MHGpINP55SCJnBIoti5GU4/50ijdvna6ErZLpwLb0CYIlUaXS6YYv2GMW8SXyT2CaVb53tlS7Y6tSzgVNvGB07wJJkkq66ZLlXEkpsXu4lXOgz1D+jxubrdofbVNj5RK3PC/JQjL4brueuBuXyGSWBfSviCy2DximOPh/yCwslK6Fa8JPwehoBFHzECSOmZkPxg1F7VMxKH6EF/qSt5/KAe9fFwe1Nu6ro5pciFK6gEBTuO+p6fnvUEDepW83Ca0hsTqil7Uy1Ule1soQuQH0RWab6MBRqcfeuk82qDnmCaEAZ+PMdX51vxKMvJgtk7un2vBA4yt7hfJ1PbAgMBAAECggEAEnjjt5joWQ8mOZFYN9zLlUAxTd/I9VOdZLfmYhhDLEHWf4wfaGu+IEPwXHnPoalF7mCVCSLx1wLSb6ci9Am+ga/1fdZdaCkIaC1jB9oUW8fJkObCzjBWV5ZhO+3vtMdqPQYdvKJ+1/h89V/uQVLh14WGTt1Tj9xkE45MW4JnbkzyS3CNrzSIlBl0w1PEyPHoqv4wOZjSinedMsKE0IAXhgOu4hClebkeX+0eBvkVNi17+KHK+Aizf2DwJ6+RUUCeGr7yKdOOBxZxkEEEKwHNRkjG0MH69s3Vs80w2NSM89xYqX8No5dwMC0Hhp/i87k2o/qM+J0BuLI9uee9KpXqUQKBgQDS6VbTXF4O2g88OKvH//4CSsG6N8ySAHmJJfNha4u7kmCQz9iLNblRI4Aoei4KIVdY/kHorSijMSa025ki8ebQLw7G5Me5nqBOiuRqlIbXfTaCxjWggzm434mPs/2998GGPEIm1g+qBML2gv42XqG391hrOFpx0EaozmR6JBT+8QKBgQDwwAlo+JOPLlCvfHiMEu+/bMU7F9HKJDOsgG5fFxScUfBBVhXslpV6h23iXp4v/VmF+5EeCIE4gInXEyj9Yn9gpaL72Gdf8PXyZel9WrRfL3CyH0vnR1DM60FHAFmEFUkFvCmzDyOqZmyt2DpYcd4y9Kfs/Ts/iRfvAFAtoO1XiwKBgE59IZ+0nxg91C+gE2VxgdDOizvGqi2nWZNNeT5G7JBYT/F0N+zOiHGGmZn2pg2FDOGEdXimgBoDH5lso5eamD/fU0t3NlCAlL3F+G0lauzknxWZt7lNPHztS18cJ5C7k9xlrmSPgvLNpNRiOUJ4gwxYUyJLrXTvgmwtqry9ksaxAoGAFkiQFmk7rzsIONX6imyOSFeXAds4jc9AAS16Cc8nFzj2VfXT3awqdcbnQtajKan3iVE5o2ACJeqv13pshteBFr7+EPV8zAKPoToRnIqyu0S216XR7rxJHE6CIkJEBte5hJBgA7TZBkKouIaVD+6qNGk0ydi+jSjxUCvlP/PvQ/UCgYBa/ANDflVEvas7txSmqJJ4mDyExs3lcQ2dEBVj6cbfEGDJH0QiOJwbnTAKnub7nNJEWIzIjmNBLoZBUQ1Ox8rRYqvLs0Edl99Y3CFx4boRuBB690kFABl3XzAwpWX266vZfRCg8BcGqpH/BTuAvSW4gHKCGhyqGlDes8w9OUq+4A==";



    @Test
    public void accessToken() {
        System.out.println("=====> step2 : Create Access Token");

        String timestamp = ZonedDateTime.of(LocalDateTime.now(), SmileConstant.ZONE_ID).format(SmileConstant.DF_0);
        System.out.println("timestamp = " + timestamp);
        String clientKey = SmileConstant.MERCHANT_ID;

        String stringToSign = clientKey + "|" + timestamp;
        System.out.println("stringToSign = " + stringToSign);
        System.out.println("privateKeyStr = " + privateKeyStr);
        String signature = SignatureUtil.createSignature(stringToSign, privateKeyStr);
        System.out.println("signature = " + signature);

        //url
        String url = SmileConstant.BASE_URL + SmileConstant.ACCESS_TOKEN_API;

        //body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("grantType", "client_credentials");
        String jsonBody = jsonObject.toString();

        //post
        String response = RemoteUtil.postJson(url, timestamp, clientKey, signature, jsonBody);
        System.out.println("response = " + response);

        //build res
        Gson gson = new Gson();
        AccessTokenRes res = gson.fromJson(response, AccessTokenRes.class);
        System.out.println("res token = " + res.getAccessToken());

        System.out.println("Please remember the token, use this token for all subsequent api calls.");
    }

    //response = {"responseCode":"2007300","responseMessage":"Successful","accessToken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MDA1NTA1NzAsImV4cCI6MTcwMDU1MTQ3MCwiaWF0IjoxNzAwNTUwNTcwLCJNRVJDSEFOVF9JRCI6InNhbmRib3gtMTAwMDQifQ.LKP5DH0n0Zy2lcUICnhGgAnHRIlK68YPSF94lJ-CbtI","tokenType":"Bearer","expiresIn":"900","additionalInfo":null}

}
