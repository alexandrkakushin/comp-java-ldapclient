package ru.ak.model;

import lombok.Data;

/**
 * @author a.kakushin
 */
@Data
public class SearchParameters {

    private String uuid;
    private String baseDn;
    private String filter;

    public String getUuid() {
        return uuid;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public String getFilter() {
        return filter;
    }
}
