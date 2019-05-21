/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import javax.inject.Provider;

import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;

/**
 * @author massimiliano.leone@iubris.net
 *
 * May 20, 2019
 */
public class AlbumSearchMediaItemSupplierProvider implements Provider<SearchMediaItemSupplier> {

	protected final Album googleAlbum;
	protected final PhotosLibraryClient photosLibraryClient;

	public AlbumSearchMediaItemSupplierProvider(Album googleAlbum, PhotosLibraryClient photosLibraryClient) {
		this.googleAlbum = googleAlbum;
		this.photosLibraryClient = photosLibraryClient;
	}

	@Override
	public SearchMediaItemSupplier get() {
		final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(googleAlbum.getId()).build();
		final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);
		return searchMediaItemSupplier;
	}
}
