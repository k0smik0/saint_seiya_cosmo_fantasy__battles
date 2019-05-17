/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Singleton;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;

/**
 * @author massimiliano.leone@iubris.net - 2019/05/13
 */
@Singleton
public class ImageAnnotatorClientProvider extends AbstractCredentiableProvider<ImageAnnotatorClient, ImageAnnotatorClientProvider> {

	private ImageAnnotatorClient imageAnnotatorClient;

	@Override
	public ImageAnnotatorClient get() {
		return imageAnnotatorClient;
	}

	@Override
	public ImageAnnotatorClientProvider init() throws FileNotFoundException, IOException {
		if (imageAnnotatorClient==null) {
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath)).createScoped(VisionScopes.all());
			FixedCredentialsProvider fixedCredentialsProvider = FixedCredentialsProvider.create(credentials);

			ImageAnnotatorSettings ias = ImageAnnotatorSettings.newBuilder().setCredentialsProvider(fixedCredentialsProvider).build();
			imageAnnotatorClient = ImageAnnotatorClient.create(ias);

			System.out.println("created imageAnnotatorClient:"+imageAnnotatorClient);
		}
		return this;
	}



}
