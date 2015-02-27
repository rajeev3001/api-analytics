package org.wso2.carbon.api.analytics.alerts.core;

public class AlertConfigurationCondition {

    public enum Operation {
      GREATER_THAN(">"), LESS_THAN("<"), EQUALS("=="), NOT_EQUALS("!=");
        private String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return this.symbol;
        }
    };

    private String attribute;
    private Object targetValue;
    private Operation operation;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Object getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Object targetValue) {
        this.targetValue = targetValue;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
