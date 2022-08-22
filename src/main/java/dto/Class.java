package dto;

import java.util.List;

public class Class {
    private String className;
    private List<String> methodName;
    private String packageName;
    private String layer;
    private Double weight;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getMethodName() {
        return methodName;
    }

    public void addMethodName(String method) {
        methodName.add(method);
    }

    public void setMethodName(List<String> methodName) {
        this.methodName = methodName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
