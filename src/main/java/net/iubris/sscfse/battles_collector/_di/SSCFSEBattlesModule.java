/**
 *
 */
package net.iubris.sscfse.battles_collector._di;

import com.google.inject.AbstractModule;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class SSCFSEBattlesModule extends AbstractModule {

    @Override
    public void configure() {
        //        bind(PhotosLibraryClient.class).toProvider(PhotosLibraryClientProvider.class);
        //
        //        bind(Album.class).annotatedWith(Names.named(Config.SSCFSE_BATTLES_ALBUM_TITLE)).toProvider(BattlesAlbumProvider.class);
    }

}