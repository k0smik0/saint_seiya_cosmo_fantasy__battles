/**
 *
 */
package net.iubris.sscfse.battles_collector;

import com.google.inject.AbstractModule;
import com.google.photos.library.v1.PhotosLibraryClient;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class SSCFSEBattlesModule extends AbstractModule {

    @Override
    public void configure() {
        bind(PhotosLibraryClient.class).toProvider(PhotosLibraryClientProvider.class);
    }

}
