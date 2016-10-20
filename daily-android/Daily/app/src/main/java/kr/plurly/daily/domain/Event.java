package kr.plurly.daily.domain;

import android.text.TextUtils;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {

    public static final String FIELD_UUID = "uuid";
    public static final String FIELD_SYNCED = "synced";
    public static final String FIELD_CREATED_AT = "createdAt";

    public static final int EMOTION_UNDEFINED = 0x0000;
    public static final int EMOTION_DISAPPOINTED = 0x0001;
    public static final int EMOTION_SAD = 0x0002;
    public static final int EMOTION_NEUTRAL = 0x0003;
    public static final int EMOTION_GOODIE = 0x0004;
    public static final int EMOTION_HAPPY = 0x0005;

    @PrimaryKey
    private String uuid;

    private String title;
    private String content;

    private String path;
    private int emotion = EMOTION_UNDEFINED;

    private String location;

    private double latitude = -1;
    private double longitude = -1;

    private boolean synced = false;
    private Date createdAt = new Date();


    public String getUUID() { return uuid; }
    public void setUUID(String uuid) { this.uuid = uuid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }


    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public int getEmotion() { return emotion; }
    public void setEmotion(int emotion) { this.emotion = emotion; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }


    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Event))
            return false;

        Event event = (Event) obj;

        return !TextUtils.isEmpty(uuid) && uuid.equals(event.getUUID());
    }

    public boolean hasLocation() { return !(TextUtils.isEmpty(location) || latitude == -1 || longitude == -1); }
}
