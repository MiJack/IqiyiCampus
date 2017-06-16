/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.mijack.meme.remote;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Response;


/**
 * Common class used by API responses.
 *
 * @param <T>
 */
public class ApiResponse<T> {
    public final int code;
    private String msg;
    @Nullable
    public final T body;
    @Nullable
    public final String errorReason;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorReason = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if (response.isSuccessful()) {
            body = response.body();
            if (body instanceof HasErrorReason) {
                errorReason = ((HasErrorReason) body).errorReason();
            } else {
                errorReason = null;
            }
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException ignored) {
//                    Timber.e(ignored, "error while parsing response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            errorReason = message;
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

}
