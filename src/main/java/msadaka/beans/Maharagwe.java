package msadaka.beans;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.net.ssl.X509TrustManager;
import java.nio.file.Paths;

public class Maharagwe {

    @Bean
    public OkHttpClient okHttpClient() {
       /* int cacheSize = 10 * 1024 * 1024; // 10MB
        String cacheDir = "/home/fred/appschool/cache";
        String home = System.getProperty("user.home");
        X509TrustManager trustManager = SecurityUtils.trustManagerFor(SecurityUtils.readJavaKeyStore(Paths.get(home + File.pathSeparator + "cert.jks"), "safaricom"));
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .cache(new Cache(new File(cacheDir), cacheSize))
                .sslSocketFactory(SecurityUtils.sslContext(null, new TrustManager[]{trustManager}).getSocketFactory(), trustManager)
                .hostnameVerifier(SecurityUtils.allowAllHostNames())
                .build();*/
        OkHttpClient client = new OkHttpClient();

        return client;
    }
}
