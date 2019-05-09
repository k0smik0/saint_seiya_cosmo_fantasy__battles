/**
 *
 */
package net.iubris.sscfse.battles_collector._di;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.photos.library.factories.PhotosLibraryClientFactory;
import com.google.photos.library.v1.PhotosLibraryClient;

import net.iubris.sscfse.battles_collector.model.Config;

/**
 *
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
@Singleton
public class PhotosLibraryClientProvider implements Provider<PhotosLibraryClient> {

    private PhotosLibraryClient photosLibraryClient;

    public PhotosLibraryClientProvider init(String credentialsPath) throws IOException, GeneralSecurityException {
        photosLibraryClient = PhotosLibraryClientFactory.createClient( credentialsPath, Config.REQUIRED_SCOPES);
        return this;
    }

    @Override
    public PhotosLibraryClient get() {
        return photosLibraryClient;
    }

}
