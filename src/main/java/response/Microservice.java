package response;

import java.util.List;

public class Microservice {
    private String id;
    private String functionalities;
    private List<ClassResponse> classes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(String functionalities) {
        this.functionalities = functionalities;
    }

    public List<ClassResponse> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassResponse> classes) {
        this.classes = classes;
    }
}
