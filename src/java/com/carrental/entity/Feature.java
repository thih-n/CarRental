package com.carrental.entity;

public class Feature {
    private int featureID;
    private String featureName;
    private String urlEndpoint;
    private String httpMethod;
    private String groupName;
    private Boolean isMenu;
    private String iconClass;
    private Integer sortOrder;

    public Feature() {
    }

    public Feature(int featureID, String featureName, String urlEndpoint, String httpMethod, String groupName,
            Boolean isMenu, String iconClass, Integer sortOrder) {
        this.featureID = featureID;
        this.featureName = featureName;
        this.urlEndpoint = urlEndpoint;
        this.httpMethod = httpMethod;
        this.groupName = groupName;
        this.isMenu = isMenu;
        this.iconClass = iconClass;
        this.sortOrder = sortOrder;
    }

    public int getFeatureID() { return featureID; }
    public void setFeatureID(int featureID) { this.featureID = featureID; }

    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }

    public String getUrlEndpoint() { return urlEndpoint; }
    public void setUrlEndpoint(String urlEndpoint) { this.urlEndpoint = urlEndpoint; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Boolean getIsMenu() { return isMenu; }
    public void setIsMenu(Boolean isMenu) { this.isMenu = isMenu; }

    public String getIconClass() { return iconClass; }
    public void setIconClass(String iconClass) { this.iconClass = iconClass; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
