package net.iubris.sscfse.battles_collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.protobuf.Timestamp;

public class GooglePhoto implements Comparable<GooglePhoto> {

    private final String id;
    private final String filename;
    private final String description;
    private final String baseUrl;
    private final Timestamp creationTime;

    public GooglePhoto(String id, String filename, String description, String baseUrl, Timestamp creationTime) {
        this.id = id;
        this.filename = filename;
        this.description = description;
        this.baseUrl = baseUrl;
        this.creationTime = creationTime;
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

    public final Timestamp getCreationTime() {
        return creationTime;
    }

    @Override
    public int compareTo(GooglePhoto o) {
        return filename.compareTo(o.getFilename());
    }
    
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss");
    @Override
    public String toString() {        
        Date date = new Date(creationTime.getSeconds());
        String s = "[id:"+id+", filename:"+filename
                +", description:"+description+", url:"+baseUrl
                +", createdAt:"+DATE_FORMATTER.format(date);
        return s;
    }

}
