package msadaka.configs;


import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Bean
    public OkHttpClient okHttpClient(){
        //  int cacheSize = 10 * 1024 * 1024; // 10MB
        //  String cacheDir = "/home/fred/appschool/cache";
        //  String home = System.getProperty("user.home");
        //X509TrustManager trustManager = SecurityUtils.trustManagerFor(SecurityUtils.readJavaKeyStore(Paths.get(home+File.pathSeparator+"cert.jks"), "safaricom"));
        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(new HttpLoggingInterceptor())
                // .cache(new Cache(new File(cacheDir), cacheSize))
                //  .sslSocketFactory(SecurityUtils.sslContext(null, new TrustManager[]{trustManager}).getSocketFactory(),trustManager)
                //  .hostnameVerifier(SecurityUtils.allowAllHostNames())
                .build();

        return client;
    }



    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        //stringConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("text","plain",Charset.defaultCharset())));
        stringConverter.setSupportedMediaTypes(Arrays.asList( //
                MediaType.TEXT_PLAIN, //
                MediaType.TEXT_HTML, //
                MediaType.APPLICATION_JSON));
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        messageConverters.add(jsonMessageConverter);
        messageConverters.add(stringConverter);
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(messageConverters);
    }


}