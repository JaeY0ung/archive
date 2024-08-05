package com.ssafy.los.backend.config.web_socket;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String privateKey;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String clientEmail;

    @Value("${FIREBASE_CLIENT_ID}")
    private String clientId;

    @Value("${FIREBASE_AUTH_URI}")
    private String authUri;

    @Value("${FIREBASE_TOKEN_URI}")
    private String tokenUri;

    @Value("${FIREBASE_AUTH_PROVIDER_X509_CERT_URL}")
    private String authProviderX509CertUrl;

    @Value("${FIREBASE_CLIENT_X509_CERT_URL}")
    private String clientX509CertUrl;

    @PostConstruct
    public void init() {
        try {
            String firebaseConfig = String.format(
                    "{" +
                            "  \"type\": \"service_account\"," +
                            "  \"project_id\": \"%s\"," +
                            "  \"private_key_id\": \"729c24674819d140ad95c368be5cc6011c596154\"," +
                            "  \"private_key\": \"%s\"," +
                            "  \"client_email\": \"%s\"," +
                            "  \"client_id\": \"%s\"," +
                            "  \"auth_uri\": \"%s\"," +
                            "  \"token_uri\": \"%s\"," +
                            "  \"auth_provider_x509_cert_url\": \"%s\"," +
                            "  \"client_x509_cert_url\": \"%s\"" +
                            "}", projectId, privateKey, clientEmail, clientId, authUri, tokenUri,
                    authProviderX509CertUrl, clientX509CertUrl
            );

            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}