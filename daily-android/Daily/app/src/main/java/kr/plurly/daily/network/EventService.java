package kr.plurly.daily.network;

import java.util.List;

import kr.plurly.daily.domain.Event;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface EventService {

    @Multipart
    @POST("/events")
    Observable<Response<Event>> newEvent(@Part("uuid") RequestBody uuid,
                                         @Part("title") RequestBody title,
                                         @Part("content") RequestBody content,
                                         @Part("emotion") RequestBody emotion,
                                         @Part MultipartBody.Part photo);

    @Multipart
    @POST("/events")
    Observable<Response<Event>> newEvent(@Part("uuid") RequestBody uuid,
                                         @Part("title") RequestBody title,
                                         @Part("content") RequestBody content,
                                         @Part("emotion") RequestBody emotion,
                                         @Part MultipartBody.Part photo,
                                         @Part("location") RequestBody location,
                                         @Part("latitude") RequestBody latitude,
                                         @Part("longitude") RequestBody longitude);

    @GET("/events")
    Observable<Response<List<Event>>> fetch(@Query("since") long since);
}
