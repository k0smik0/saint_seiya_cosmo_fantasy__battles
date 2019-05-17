/**
 *
 */
package net.iubris.sscfse.battles_collector.model;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Table;
import com.google.photos.library.v1.proto.MediaItem;
import com.meyling.guava.TableCollector;

/**
 * @author massimiliano.leone@iubris.net
 *
 *         May 14, 2019
 */
public class MediaItemsTransformer {

	public static List<GooglePhoto> mediaItemsAsList(Iterable<MediaItem> iterable) {
		List<GooglePhoto> googlePhotos = StreamSupport.stream(iterable.spliterator(), false)
				// MediaItem
				.map(mi -> {
					return new GooglePhoto(mi.getId(), mi.getFilename(), mi.getDescription(), mi.getBaseUrl(),
							new Date(mi.getMediaMetadata().getCreationTime().getSeconds() * 1000));
				}).collect(Collectors.toList());
		return googlePhotos;
	}

	public static Table<String, String, GooglePhoto> mediaItemsAsTable(Iterable<MediaItem> iterable) {
		Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable = StreamSupport.stream(iterable.spliterator(), false)
				// MediaItem
				.map(mi -> {
					return new GooglePhoto(mi.getId(), mi.getFilename(), mi.getDescription(), mi.getBaseUrl(),
							new Date(mi.getMediaMetadata().getCreationTime().getSeconds() * 1000));
				}).collect(TableCollector.toHashBasedTable(GooglePhoto::getBaseUrl, GooglePhoto::getFilename, Function.identity()));

		// urlOrFilenameToGooglePhotoTable.cellSet().forEach(c->{
		// System.out.println("rowKey:"+c.getRowKey()+", columnKey:"+c.getColumnKey()+",
		// value:"+c.getValue());
		// });

		return urlOrFilenameToGooglePhotoTable;
	}
}
