/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Singleton;

import com.google.photos.library.factories.PhotosLibraryClientFactory;
import com.google.photos.library.v1.PhotosLibraryClient;

import net.iubris.sscfse.battles_collector.Config;

/**
 *
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
@Singleton
public class PhotosLibraryClientProvider extends CredentiableProvider<PhotosLibraryClient, PhotosLibraryClientProvider> {

    private PhotosLibraryClient photosLibraryClient;

    @Override
    public PhotosLibraryClientProvider init(String credentialsPath) throws IOException, GeneralSecurityException {
        photosLibraryClient = PhotosLibraryClientFactory.createClient( credentialsPath, Config.REQUIRED_SCOPES);
        return this;
    }

    @Override
    public PhotosLibraryClient get() {
        return photosLibraryClient;
    }

}
