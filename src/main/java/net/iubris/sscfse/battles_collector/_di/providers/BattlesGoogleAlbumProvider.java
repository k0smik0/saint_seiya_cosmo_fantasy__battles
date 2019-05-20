/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.photos.library.suppliers.ListAlbumsSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.ListAlbumsRequest;

import net.iubris.sscfse.battles_collector.Config;

/**
 * @author k0smik0 - massimiliano.leone@iubris.net
 *
 * May 12, 2019
 */
@Singleton
public class BattlesGoogleAlbumProvider implements Provider<Album> {

	private final PhotosLibraryClient photosLibraryClient;
	private Album album;

	@Inject
	public BattlesGoogleAlbumProvider(PhotosLibraryClient photosLibraryClient) {
		this.photosLibraryClient = photosLibraryClient;
	}

	@Override
	public Album get() {
		if (album==null) {
			final ListAlbumsRequest listAlbumsRequest = ListAlbumsRequest.getDefaultInstance();
			final ListAlbumsSupplier listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClient, listAlbumsRequest);
			album = listAlbumsSupplier.get()
					.parallelStream()
					.filter(a -> a.getTitle().equalsIgnoreCase(Config.SSCFSE_BATTLES_ALBUM_TITLE))
					.findFirst().get();
			System.out.println("Found "+Config.SSCFSE_BATTLES_ALBUM_TITLE+": "+album.getId());
		}
		return album;
	}
}
