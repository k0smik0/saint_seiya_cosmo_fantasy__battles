package net.iubris.sscfse.battles_collector.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GooglePhoto implements Comparable<GooglePhoto> {

    private final String id;
    private final String filename;
    private final String description;
    private final String baseUrl;
    private final Date creationDate;

    private String note;

    public GooglePhoto(String id, String filename, String description, String baseUrl, Date creationDate) {
        this.id = id;
        this.filename = filename;
        this.description = description;
        this.baseUrl = baseUrl;
        this.creationDate = creationDate;
    }

    public final String getId() {
        return id;
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

    @Override
    public int compareTo(GooglePhoto o) {
        return filename.compareTo(o.getFilename());
    }

    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss");
    @Override
    public String toString() {
        //        Date date = new Date(creationTimeMillis);
        String s = "[id:"+id+", filename:"+filename
                +", description:"+description+", url:"+baseUrl
                +", createdAt:"+DATE_FORMATTER.format(creationDate);
        return s;
    }

}
