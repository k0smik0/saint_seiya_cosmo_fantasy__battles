/**
 *
 */
package net.iubris.sscfse.battles_collector._di;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;

import net.iubris.sscfse.battles_collector.Config;
import net.iubris.sscfse.battles_collector._di.providers.BattlesGoogleAlbumProvider;
import net.iubris.sscfse.battles_collector._di.providers.BattlesAlbumSearchMediaItemSupplierProvider;
import net.iubris.sscfse.battles_collector._di.providers.ImageAnnotatorClientProvider;
import net.iubris.sscfse.battles_collector._di.providers.PhotosLibraryClientProvider;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class SSCFSEBattlesModule extends AbstractModule {

	@Override
	public void configure() {
		bind(PhotosLibraryClient.class).toProvider(PhotosLibraryClientProvider.class);

		bind(Album.class).annotatedWith(Names.named(Config.SSCFSE_BATTLES_ALBUM_TITLE)).toProvider(BattlesGoogleAlbumProvider.class);

		bind(SearchMediaItemSupplier.class).annotatedWith(Names.named(BattlesAlbumSearchMediaItemSupplier)).toProvider(BattlesAlbumSearchMediaItemSupplierProvider.class);

		//        bind(Optional)
		//        bind(new OptionVision.class).toProvider(VisionServiceProvider.class);
		//        OptionalBinder.newOptionalBinder(binder(), Vision.class).setBinding().toProvider(VisionServiceProvider.class);

		bind(ImageAnnotatorClient.class).toProvider(ImageAnnotatorClientProvider.class);
	}

	public static final String BattlesAlbumSearchMediaItemSupplier = "BattlesAlbumSearchMediaItemSupplier";

}
