package org.wso2.carbon.api.analytics.alerts.admin;

public class DerivedAttributeDto {

    // responseTime, avg(resonseTime)
    private String selectExpressions;

    // group by responseTime etc.
    private String groupByAttributes;

    // time, no. of events
    private String aggregationType;

    // msec, no. of events
    private int aggregationLength;

    public String getSelectExpressions() {
        return selectExpressions;
    }

    public void setSelectExpressions(String selectExpressions) {
        this.selectExpressions = selectExpressions;
    }

    public String getGroupByAttributes() {
        return groupByAttributes;
    }

    public void setGroupByAttributes(String groupByAttributes) {
        this.groupByAttributes = groupByAttributes;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public int getAggregationLength() {
        return aggregationLength;
    }

    public void setAggregationLength(int aggregationLength) {
        this.aggregationLength = aggregationLength;
    }


}
