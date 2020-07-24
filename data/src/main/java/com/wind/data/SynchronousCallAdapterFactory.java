package com.wind.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by Phong Huynh on 7/23/2020
 * https://stackoverflow.com/questions/32269064/unable-to-create-call-adapter-for-class-example-simple
 */
public class SynchronousCallAdapterFactory extends CallAdapter.Factory {
    public static CallAdapter.Factory create() {
        return new SynchronousCallAdapterFactory();
    }

    @Override
    public CallAdapter<Object, Object> get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // if returnType is retrofit2.Call, do nothing
        if (returnType.toString().contains("retrofit2.Call")) {
            return null;
        }

        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(Call<Object> call) {
                try {
                    return call.execute().body();
                } catch (Exception e) {
                    throw new RuntimeException(e); // do something better
                }
            }
        };
    }
}