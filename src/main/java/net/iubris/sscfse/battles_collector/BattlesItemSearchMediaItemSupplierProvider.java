/**
 *
 */
package net.iubris.sscfse.battles_collector;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;

import net.iubris.sscfse.battles_collector._di.BattlesAlbumProvider;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class BattlesItemSearchMediaItemSupplierProvider implements Provider<SearchMediaItemSupplier> {

    private final BattlesAlbumProvider battlesAlbumProvider;
    private final PhotosLibraryClient photosLibraryClient;

    @Inject
    public BattlesItemSearchMediaItemSupplierProvider(PhotosLibraryClient photosLibraryClient, BattlesAlbumProvider battlesAlbumProvider) {
        this.photosLibraryClient = photosLibraryClient;
        this.battlesAlbumProvider = battlesAlbumProvider;
    }

    @Override
    public SearchMediaItemSupplier get() {
        final Album album = battlesAlbumProvider.get();
        final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(album.getId()).build();
        final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);
        return searchMediaItemSupplier;
    }

}
