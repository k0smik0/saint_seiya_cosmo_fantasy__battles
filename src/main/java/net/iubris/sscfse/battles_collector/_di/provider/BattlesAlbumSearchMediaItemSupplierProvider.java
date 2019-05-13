/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;

import net.iubris.sscfse.battles_collector.Config;

/**
 * @author k0smik0 - massimiliano.leone@iubris.net
 *
 * May 12, 2019
 */
@Singleton
public class BattlesAlbumSearchMediaItemSupplierProvider implements Provider<SearchMediaItemSupplier> {

	private final Album battlesAlbum;
	private final PhotosLibraryClient photosLibraryClient;

	@Inject
	public BattlesAlbumSearchMediaItemSupplierProvider(@Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum, PhotosLibraryClient photosLibraryClient) {
		this.battlesAlbum = battlesAlbum;
		this.photosLibraryClient = photosLibraryClient;
	}

	@Override
	public SearchMediaItemSupplier get() {
		final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(battlesAlbum.getId()).build();
		final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);
		return searchMediaItemSupplier;
	}

}
