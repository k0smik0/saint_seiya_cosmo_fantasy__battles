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
public class VisionServiceProvider extends AbstractCredentiableProvider<Vision, VisionServiceProvider> {

	//	private GoogleCredential credential;
	private Vision vision;

	@Override
	public VisionServiceProvider init() throws FileNotFoundException, IOException, GeneralSecurityException {
		if (vision==null) {
			GoogleCredential credential = GoogleCredential.fromStream( new FileInputStream(credentialsPath) ).createScoped(VisionScopes.all());
			NetHttpTransport trustedTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			vision = new Vision.Builder(trustedTransport, jsonFactory, credential)
					.setApplicationName(Config.APPLICATION_NAME)
					.build();
			System.out.println("created vision:"+vision);
		}
		return this;
	}

	@Override
	public Vision get() {
		return vision;
	}

}
