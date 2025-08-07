package ai.z.openapi.utils;

import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.token.HttpRequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for creating and configuring OkHttpClient instances. This class provides
 * factory methods to create properly configured HTTP clients with authentication,
 * timeouts, and connection pooling based on Z.AI configuration.
 *
 */
public final class OkHttps {

	// Default timeout values
	private static final int DEFAULT_CALL_TIMEOUT_SECONDS = 30;

	private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 10;

	private static final int DEFAULT_READ_TIMEOUT_SECONDS = 10;

	private static final int DEFAULT_WRITE_TIMEOUT_SECONDS = 10;

	// Private constructor to prevent instantiation
	private OkHttps() {
		throw new UnsupportedOperationException("OkHttps is a utility class and cannot be instantiated");
	}

	/**
	 * Creates a configured OkHttpClient instance based on the provided configuration. The
	 * client will include: - Authentication interceptor - Configured timeouts (call,
	 * connect, read, write) - Connection pooling
	 * @param config the Z.AI configuration containing timeout and connection settings
	 * @return a fully configured OkHttpClient instance
	 * @throws IllegalArgumentException if config is null
	 */
	public static OkHttpClient create(ZaiConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("Configuration cannot be null");
		}

		OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new HttpRequestInterceptor(config));

		// Configure timeouts
		configureTimeouts(builder, config);

		// Configure connection pool
		configureConnectionPool(builder, config);

		return builder.build();
	}

	/**
	 * Configures timeout settings for the OkHttpClient builder.
	 * @param builder the OkHttpClient builder to configure
	 * @param config the configuration containing timeout settings
	 */
	private static void configureTimeouts(OkHttpClient.Builder builder, ZaiConfig config) {
		TimeUnit timeUnit = config.getTimeOutTimeUnit();

		// Configure call timeout
		if (config.getRequestTimeOut() > 0) {
			builder.callTimeout(config.getRequestTimeOut(), timeUnit);
		}
		else {
			builder.callTimeout(DEFAULT_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}

		// Configure connect timeout
		if (config.getConnectTimeout() > 0) {
			builder.connectTimeout(config.getConnectTimeout(), timeUnit);
		}
		else {
			builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}

		// Configure read timeout
		if (config.getReadTimeout() > 0) {
			builder.readTimeout(config.getReadTimeout(), timeUnit);
		}
		else {
			builder.readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}

		// Configure write timeout
		if (config.getWriteTimeout() > 0) {
			builder.writeTimeout(config.getWriteTimeout(), timeUnit);
		}
		else {
			builder.writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
	}

	/**
	 * Configures connection pool settings for the OkHttpClient builder.
	 * @param builder the OkHttpClient builder to configure
	 * @param config the configuration containing connection pool settings
	 */
	private static void configureConnectionPool(OkHttpClient.Builder builder, ZaiConfig config) {
		ConnectionPool connectionPool = new ConnectionPool(config.getConnectionPoolMaxIdleConnections(),
				config.getConnectionPoolKeepAliveDuration(), config.getConnectionPoolTimeUnit());
		builder.connectionPool(connectionPool);
	}

}
