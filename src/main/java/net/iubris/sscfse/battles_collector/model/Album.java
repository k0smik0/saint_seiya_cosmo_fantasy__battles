/**
 *
 */
package net.iubris.sscfse.battles_collector.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.datanucleus.api.jpa.annotations.SoftDelete;

/**
 * @author massimiliano.leone@iubris.net
 *
 * May 19, 2019
 */
@Entity
@SoftDelete
public class Album {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private long id;

	private String albumId;

	private List<String> photosIds;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public List<String> getPhotosIds() {
		return photosIds;
	}

	public void setPhotosIds(List<String> photosIds) {
		this.photosIds = photosIds;
	}
}
