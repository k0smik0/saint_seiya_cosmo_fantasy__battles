/**
 *
 */
package net.iubris.sscfse.battles_collector._di;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;

import net.iubris.sscfse.battles_collector.Config;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class SSCFSEBattlesModule extends AbstractModule {

	@Override
	public void configure() {
		bind(PhotosLibraryClient.class).toProvider(PhotosLibraryClientProvider.class);

		bind(Album.class).annotatedWith(Names.named(Config.SSCFSE_BATTLES_ALBUM_TITLE)).toProvider(BattlesAlbumProvider.class);

		bind(SearchMediaItemSupplier.class).annotatedWith(Names.named("BattlesAlbumSearchMediaItemSupplier")).toProvider(BattlesAlbumSearchMediaItemSupplierProvider.class);
	}

}
