package ai.z.openapi.core.token;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.utils.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * OkHttp Interceptor that adds an authorization token header
 */
public class
AuthenticationInterceptor implements Interceptor {

    private final ZAiConfig config;

    public AuthenticationInterceptor(ZAiConfig config) {
        Objects.requireNonNull(config.getApiKey(), "Z.ai token required");
        this.config = config;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken;
        if(this.config.isDisableTokenCache()){
            accessToken = this.config.getApiKey();
        }else {
            TokenManager tokenManager = GlobalTokenManager.getTokenManagerV4();
            accessToken = tokenManager.getToken(this.config);
        }
        String source_channel = "java-sdk";
        if(StringUtils.isNotEmpty(config.getSource_channel())){
            source_channel = config.getSource_channel();
        }
        Request request = chain.request()
                .newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .header("x-source-channel", source_channel)
                .header("Accept-Language", "en-US,en")
                .build();
        return chain.proceed(request);
    }
}
