package dto;

public class Config {
    public String applicationName;
    public String modelPackageName;
    public String daoPackageName;
    public String servicePackageName;
    public String dtoPackageName;
    public String controllerPackageName;
    public Double modelWeight;
    public Double daoWeight;
    public Double serviceWeight;
    public Double dtoWeight;
    public Double controllerWeight;
    public Double decompositionThreshold;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getModelPackageName() {
        return modelPackageName;
    }

    public void setModelPackageName(String modelPackageName) {
        this.modelPackageName = modelPackageName;
    }

    public String getDaoPackageName() {
        return daoPackageName;
    }

    public void setDaoPackageName(String daoPackageName) {
        this.daoPackageName = daoPackageName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
    }

    public String getDtoPackageName() {
        return dtoPackageName;
    }

    public void setDtoPackageName(String dtoPackageName) {
        this.dtoPackageName = dtoPackageName;
    }

    public String getControllerPackageName() {
        return controllerPackageName;
    }

    public void setControllerPackageName(String controllerPackageName) {
        this.controllerPackageName = controllerPackageName;
    }

    public Double getModelWeight() {
        return modelWeight;
    }

    public void setModelWeight(Double modelWeight) {
        this.modelWeight = modelWeight;
    }

    public Double getDaoWeight() {
        return daoWeight;
    }

    public void setDaoWeight(Double daoWeight) {
        this.daoWeight = daoWeight;
    }

    public Double getServiceWeight() {
        return serviceWeight;
    }

    public void setServiceWeight(Double serviceWeight) {
        this.serviceWeight = serviceWeight;
    }

    public Double getDtoWeight() {
        return dtoWeight;
    }

    public void setDtoWeight(Double dtoWeight) {
        this.dtoWeight = dtoWeight;
    }

    public Double getControllerWeight() {
        return controllerWeight;
    }

    public void setControllerWeight(Double controllerWeight) {
        this.controllerWeight = controllerWeight;
    }

    public Double getDecompositionThreshold() {
        return decompositionThreshold;
    }

    public void setDecompositionThreshold(Double decompositionThreshold) {
        this.decompositionThreshold = decompositionThreshold;
    }
}
