/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;

import net.iubris.sscfse.battles_collector.Config;

/**
 * @author k0smik0 - massimiliano.leone@iubris.net
 *
 * May 12, 2019
 */
@Singleton
public class BattlesAlbumSearchMediaItemSupplierProvider extends AlbumSearchMediaItemSupplierProvider {
	@Inject
	public BattlesAlbumSearchMediaItemSupplierProvider(@Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum, PhotosLibraryClient photosLibraryClient) {
		super(battlesAlbum, photosLibraryClient);
	}
}
