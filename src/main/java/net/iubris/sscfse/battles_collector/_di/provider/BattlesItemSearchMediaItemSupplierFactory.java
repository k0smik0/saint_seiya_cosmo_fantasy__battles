/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import javax.inject.Inject;

import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class BattlesItemSearchMediaItemSupplierFactory /*implements Provider<SearchMediaItemSupplier> */{

    private final PhotosLibraryClientProvider photosLibraryClientProvider;
    private final BattlesAlbumFactory battlesAlbumFactory;

    @Inject
    public BattlesItemSearchMediaItemSupplierFactory(PhotosLibraryClientProvider photosLibraryClientProvider, BattlesAlbumFactory battlesAlbumFactory) {
        this.photosLibraryClientProvider = photosLibraryClientProvider;
        this.battlesAlbumFactory = battlesAlbumFactory;
    }


    //    @Override
    public SearchMediaItemSupplier retrieve() {
        final PhotosLibraryClient photosLibraryClient = photosLibraryClientProvider.get();
        final Album album = battlesAlbumFactory.retrieve();
        final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(album.getId()).build();
        final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);
        return searchMediaItemSupplier;
    }

}
