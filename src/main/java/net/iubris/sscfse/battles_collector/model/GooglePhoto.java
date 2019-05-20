package net.iubris.sscfse.battles_collector.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.datanucleus.api.jpa.annotations.SoftDelete;

@Entity
@SoftDelete
public class GooglePhoto implements Comparable<GooglePhoto> {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE)
	private long id;

    private final String photoId;
    private final String filename;
    private final String description;
    private final String baseUrl;
    private final Date creationDate;

    private String albumId;
    private String note;

    public GooglePhoto(String photoId, String filename, String description, String baseUrl, Date creationDate) {
        this.photoId = photoId;
        this.filename = filename;
        this.description = description;
        this.baseUrl = baseUrl;
        this.creationDate = creationDate;
    }

    public GooglePhoto(String photoId, String filename, String description, String baseUrl, Date creationDate, String albumId) {
        this(photoId, filename, description, baseUrl, creationDate);
        this.albumId = albumId;
    }

    public final String getPhotoId() {
        return photoId;
    }

    public final String getFilename() {
        return filename;
    }

    public final String getDescription() {
        return description;
    }

    public final String getBaseUrl() {
        return baseUrl;
    }

    public final Date getCreationDateTime() {
        return creationDate;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

    @Override
    public int compareTo(GooglePhoto o) {
        return filename.compareTo(o.getFilename());
    }

    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss");
    @Override
    public String toString() {
        //        Date date = new Date(creationTimeMillis);
        String s = "[photoId:"+photoId+", filename:"+filename
                +", description:"+description+", url:"+baseUrl
                +", createdAt:"+DATE_FORMATTER.format(creationDate);
        return s;
    }

}
