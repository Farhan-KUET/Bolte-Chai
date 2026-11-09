package com.farhan.boltechai.data.model;

import com.farhan.boltechai.AppConstants;

import java.util.List;

/**
 * Model class for ARASAAC API response.
 */
public class PictogramResult {
    private int _id;
    private List<Keyword> keywords;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public String getLabel(String lang) {
        if (keywords != null && !keywords.isEmpty()) {
            return keywords.get(0).getKeyword();
        }
        return "Pictogram";
    }

    public String getImageUrl() {
        return AppConstants.PICTOGRAM_IMAGE_URL + _id + "/" + _id + "_500.png";
    }

    public static class Keyword {
        private String keyword;
        private int type;
        private boolean hasLocution;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isHasLocution() {
            return hasLocution;
        }

        public void setHasLocution(boolean hasLocution) {
            this.hasLocution = hasLocution;
        }
    }
}
