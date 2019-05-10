/**
 *
 */
package net.iubris.sscfse.battles_collector;

import javax.inject.Inject;

import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;

import net.iubris.sscfse.battles_collector._di.provider.BattlesAlbumFactory;
import net.iubris.sscfse.battles_collector._di.provider.PhotosLibraryClientProvider;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 10 May 2019
 */
public class SearchMediaItemSupplierFactory {

    private final PhotosLibraryClientProvider photosLibraryClientProvider;
    private final BattlesAlbumFactory battlesAlbumFactory;

    @Inject
    public SearchMediaItemSupplierFactory(PhotosLibraryClientProvider photosLibraryClientProvider, BattlesAlbumFactory battlesAlbumFactory) {
        this.photosLibraryClientProvider = photosLibraryClientProvider;
        this.battlesAlbumFactory = battlesAlbumFactory;
    }

    public SearchMediaItemSupplier create() {
        final Album album = battlesAlbumFactory.retrieve();
        final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(album.getId()).build();
        final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClientProvider.get(), request);
        return searchMediaItemSupplier;
    }
}
