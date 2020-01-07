package msadaka.configs;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Bean
    public OkHttpClient okHttpClient() {
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
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
        return converter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}