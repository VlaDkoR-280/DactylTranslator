package com.vladkor.dactyltranslator;

import android.graphics.Bitmap;
import java.util.List;



public interface Classifier {

    class Recognition {
        private final String id;
        private final String title;
        private final boolean quant;
        private final Float confidence;

        public Recognition(
                final String id, final String title, final Float confidence, final boolean quant) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.quant = quant;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return title;
        }
    }


    List<Recognition> recognizeImage(Bitmap bitmap);

    void close();
}
