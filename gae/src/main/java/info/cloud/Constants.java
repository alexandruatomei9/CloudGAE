package info.cloud;

/**
 * Contains the client IDs and scopes for allowed clients consuming the helloworld API.
 */
public class Constants {
  public static final String WEB_CLIENT_ID = "396486651427-9v868j75sdsmjjc45qjvng3a3lhhhu4s.apps.googleusercontent.com";
  public static final String ANDROID_CLIENT_ID = "replace this with your Android client ID";
  public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
  public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
  public static final String API_EXPLORER_CLIENT_ID = com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;
  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
}
