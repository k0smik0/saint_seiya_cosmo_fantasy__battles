/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Singleton;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;

import net.iubris.sscfse.battles_collector.Config;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 13 May 2019
 */
@Singleton
public class VisionServiceProvider extends CredentiableProvider<Vision, VisionServiceProvider> {

    //    private ImageAnnotatorClient imageAnnotatorClient;
    //    private GoogleCredentials googleCredentialsWithScope;
    private GoogleCredential credential;

    @Override
    public VisionServiceProvider init(String credentialsPath) throws FileNotFoundException, IOException {
        //        if (imageAnnotatorClient==null ) {
        //            googleCredentialsWithScope = GoogleCredentials.fromStream(new FileInputStream(credentialsPath) ).createScoped(VisionScopes.all());
        //            final FixedCredentialsProvider fixedCredentialsProvider = FixedCredentialsProvider.create( googleCredentialsWithScope );
        //            final ImageAnnotatorSettings ias = ImageAnnotatorSettings.newBuilder()
        //                    .setCredentialsProvider( fixedCredentialsProvider )
        //                    .build();
        //            imageAnnotatorClient = com.google.cloud.vision.v1.ImageAnnotatorClient.create(ias);
        //        }
        credential = GoogleCredential.fromStream( new FileInputStream(credentialsPath) ).createScoped(VisionScopes.all());
        return this;
    }

    @Override
    public Vision get() {
        try {
            final NetHttpTransport trustedTransport = GoogleNetHttpTransport.newTrustedTransport();
            final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            final Vision s = new Vision.Builder(trustedTransport, jsonFactory, credential)
                    .setApplicationName(Config.APPLICATION_NAME)
                    .build();
            return s;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
