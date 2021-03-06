/**
 *
 */
package net.iubris.sscfse.battles_collector.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "albumId")
	private List<GooglePhoto> photos;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public List<GooglePhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(List<GooglePhoto> photos) {
		this.photos = photos;
	}
}
