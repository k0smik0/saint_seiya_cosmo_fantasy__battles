/**
 *
 */
package net.iubris.sscfse.battles_collector._di;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.photos.library.suppliers.ListAlbumsSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.ListAlbumsRequest;

import net.iubris.sscfse.battles_collector.model.Config;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 9 May 2019
 */
public class BattlesAlbumProvider implements Provider<Album> {

    private final PhotosLibraryClient photosLibraryClient;

    @Inject
    public BattlesAlbumProvider(PhotosLibraryClientProvider photosLibraryClientProvider) {
        photosLibraryClient = photosLibraryClientProvider.get();
    }

    @Override
    public Album get() {
        final ListAlbumsRequest listAlbumsRequest = ListAlbumsRequest.getDefaultInstance();
        final ListAlbumsSupplier listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClient, listAlbumsRequest);
        final Album album = listAlbumsSupplier.get()
                .parallelStream()
                .filter(a -> a.getTitle().equalsIgnoreCase(Config.SSCFSE_BATTLES_ALBUM_TITLE))
                .findFirst().get();
        return album;
    }

}
