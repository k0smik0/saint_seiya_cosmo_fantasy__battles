/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import java.io.FileInputStream;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;

/**
 * @author massimiliano.leone@iubris.net
 *
 * 13 May 2019
 */
public class ImageAnnotatorClientProvider extends CredentiableProvider<ImageAnnotatorClient, ImageAnnotatorClientProvider> {

    private ImageAnnotatorClient imageAnnotatorClient;

    @Override
    public ImageAnnotatorClient get() {
        return imageAnnotatorClient;
    }

    @Override
    public ImageAnnotatorClientProvider init() throws Exception {
        if (imageAnnotatorClient==null) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath)).createScoped(VisionScopes.all());
            FixedCredentialsProvider fixedCredentialsProvider = FixedCredentialsProvider.create(credentials);

            ImageAnnotatorSettings ias = ImageAnnotatorSettings.newBuilder().setCredentialsProvider(fixedCredentialsProvider).build();
            imageAnnotatorClient = ImageAnnotatorClient.create(ias);
        }
        return this;
    }



}
