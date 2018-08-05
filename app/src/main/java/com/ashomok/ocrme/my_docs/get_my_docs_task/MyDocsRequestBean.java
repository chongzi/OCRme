package com.ashomok.ocrme.my_docs.get_my_docs_task;

import android.support.annotation.Nullable;

/**
 * Created by iuliia on 12/19/17.
 */

public class MyDocsRequestBean {
    private String userToken;
    private @Nullable
    String startCursor;

    private MyDocsRequestBean(Builder builder) {
        this.startCursor = builder.startCursor;
        this.userToken = builder.userToken;
    }

    public static class Builder {
        private String startCursor;
        private String userToken;

        public Builder startCursor(String startCursor) {
            this.startCursor = startCursor;
            return this;
        }

        public Builder userToken(String userToken) {
            this.userToken = userToken;
            return this;
        }


        public MyDocsRequestBean build() {
            return new MyDocsRequestBean(this);
        }
    }
}
