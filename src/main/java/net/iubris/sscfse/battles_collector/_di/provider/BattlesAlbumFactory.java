/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import javax.inject.Inject;

import com.google.photos.library.suppliers.ListAlbumsSupplier;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.ListAlbumsRequest;

import net.iubris.sscfse.battles_collector.model.Config;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class BattlesAlbumFactory /*implements Provider<Album> */{

    private final PhotosLibraryClientProvider photosLibraryClientProvider;

    @Inject
    public BattlesAlbumFactory(PhotosLibraryClientProvider photosLibraryClientProvider) {
        this.photosLibraryClientProvider = photosLibraryClientProvider;
    }

    //    @Override
    public Album getNO() {
        //        final ListAlbumsRequest listAlbumsRequest = ListAlbumsRequest.getDefaultInstance();
        //        final ListAlbumsSupplier listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClient, listAlbumsRequest);
        //        final Album album = listAlbumsSupplier.get()
        //                .parallelStream()
        //                .filter(a -> a.getTitle().equalsIgnoreCase(Config.SSCFSE_BATTLES_ALBUM_TITLE))
        //                .findFirst().get();
        //        return album;
        return null;
    }

    public Album retrieve() {
        final ListAlbumsRequest listAlbumsRequest = ListAlbumsRequest.getDefaultInstance();
        final ListAlbumsSupplier listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClientProvider.get(), listAlbumsRequest);
        final Album album = listAlbumsSupplier.get()
                .parallelStream()
                .filter(a -> a.getTitle().equalsIgnoreCase(Config.SSCFSE_BATTLES_ALBUM_TITLE))
                .findFirst().get();
        return album;
    }

}
