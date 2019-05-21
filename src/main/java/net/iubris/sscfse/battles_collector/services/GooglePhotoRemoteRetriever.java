/**
 *
 */
package net.iubris.sscfse.battles_collector.services;

import com.google.photos.library.v1.proto.MediaItem;

import net.iubris.sscfse.battles_collector._di.providers.BattlesAlbumSearchMediaItemSupplierProvider;

/**
 * @author massimiliano.leone@iubris.net
 *
 * May 21, 2019
 */
public class GooglePhotoRemoteRetriever {
	private final BattlesAlbumSearchMediaItemSupplierProvider battlesAlbumSearchMediaItemSupplierProvider;

	public GooglePhotoRemoteRetriever(BattlesAlbumSearchMediaItemSupplierProvider battlesAlbumSearchMediaItemSupplierProvider) {
		this.battlesAlbumSearchMediaItemSupplierProvider = battlesAlbumSearchMediaItemSupplierProvider;
	}

	public Iterable<MediaItem> retrieve() {
		return battlesAlbumSearchMediaItemSupplierProvider.get().get();
	}
}
