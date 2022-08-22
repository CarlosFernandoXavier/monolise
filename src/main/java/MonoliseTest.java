import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Class;
import dto.Config;
import model.Column;
import response.ClassResponse;
import response.MethodResponse;
import response.Microservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class MonoliseTest {

    public static void main(String[] args) {
        executarMonolise();
    }

    public static void executarMonolise() {

        //Trace de execução
        List<String> arquivos = List.of("src/main/resources/gerar_relatorio_por_filial.txt",
                "src/main/resources/buscar_itens.txt",
                "src/main/resources/buscar_item_pelo_id.txt",
                "src/main/resources/buscar_filiais.txt");

        Map<String, List<Class>> functionalities = convertFunctionalityFiles(arquivos);
        Config config = convertApplicationConfigFile();
        setApplicationLayer(functionalities, config);
        Map<String, List<Column>> similarityTable = createSimilarityTable(functionalities);
        List<Microservice> microsservicos = groupFunctionalitiesBySimilatiry(similarityTable, config, functionalities);
        printMicrosservices(microsservicos);
    }

    private static Map<String, List<Class>> convertFunctionalityFiles(List<String> functionalityFiles) {
        Map<String, List<Class>> functionalityMaps = new HashMap<>();
        Class classe;
        List<Class> classes;
        File file;
        String nomeFuncionalidade;
        for (String nomeArquivo : functionalityFiles) {
            file = new File(nomeArquivo);
            nomeFuncionalidade = file.getName().substring(0, file.getName().lastIndexOf("."));
            classes = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null) {
                    String[] linha = st.split(", ");
                    classe = new Class();
                    classe.setClassName(linha[0].substring(linha[0].lastIndexOf(": ") + 2));
                    classe.setPackageName(classe.getClassName().substring(0, classe.getClassName().lastIndexOf(".")));

                    //TODO corrigir o nome dessa variável abaixo
                    List<String> t = new ArrayList<>();
                    t.add(linha[1].substring(linha[1].lastIndexOf(": ") + 2));
                    classe.setMethodName(t);

                    Integer indiceClasse = getIndiceClasse(classes, classe);
                    if (Objects.isNull(indiceClasse)) {
                        classes.add(classe);
                    } else {
                        Class classeA = classes.get(indiceClasse);
                        classes.remove(classeA);
                        classeA.addMethodName(linha[1].substring(linha[1].lastIndexOf(": ") + 2));
                        classes.add(indiceClasse, classeA);
                    }
                }
                functionalityMaps.put(nomeFuncionalidade, classes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return functionalityMaps;
    }

    private static Integer getIndiceClasse(List<Class> classes, Class classe) {
        Integer indice = null;
        if (classes.isEmpty()) return null;

        for (int contador = 0; contador < classes.size(); contador++) {
            if (classes.get(contador).getClassName().equals(classe.getClassName())) {
                indice = contador;
                break;
            }
        }
        return indice;
    }

    //TODO deveria receber o json e converter para Config, mas assim facilita a implementação
    private static Config convertApplicationConfigFile() {
        Config config = new Config();
        config.setApplicationName("TesteAplication");
        config.setModelPackageName("com.unisinos.sistema.adapter.outbound.entity");
        config.setDaoPackageName("com.unisinos.sistema.adapter.outbound.repository");
        config.setServicePackageName("com.unisinos.sistema.application.service");
        config.setDtoPackageName("com.unisinos.sistema.adapter.inbound.model");
        config.setControllerPackageName("com.unisinos.sistema.adapter.inbound.controller");
        config.setModelWeight(1.0);
        config.setDaoWeight(0.5);
        config.setServiceWeight(1.0);
        config.setDtoWeight(0.3);
        config.setControllerWeight(0.5);
        config.setDecompositionThreshold(80.0);
        return config;
    }

    private static void setApplicationLayer(Map<String, List<Class>> functionalities,
                                            Config config) {
        functionalities.forEach((key, classes) -> {
            classes.forEach(classe -> {
                if (classe.getPackageName().equals(config.getModelPackageName())) {
                    classe.setLayer("MODEL");
                    classe.setWeight(config.getModelWeight());
                } else if (classe.getPackageName().equals(config.getDaoPackageName())) {
                    classe.setLayer("REPOSITORY");
                    classe.setWeight(config.getDaoWeight());
                } else if (classe.getPackageName().equals(config.getServicePackageName())) {
                    classe.setLayer("SERVICE");
                    classe.setWeight(config.getServiceWeight());
                } else if (classe.getPackageName().equals(config.getDtoPackageName())) {
                    classe.setLayer("DTO");
                    classe.setWeight(config.getDtoWeight());
                } else if (classe.getPackageName().equals(config.getControllerPackageName())) {
                    classe.setLayer("CONTROLLER");
                    classe.setWeight(config.getControllerWeight());
                } else {
                    classe.setLayer("OTHERS");
                    classe.setWeight(0.1);
                }
            });
        });
    }

    private static Map<String, List<Column>> createSimilarityTable(Map<String, List<Class>> functionalities) {

        Map<String, List<Column>> similarityTable = new HashMap<>();

        functionalities.forEach((functionality1, classes1) -> {
            List<Column> colunas = new ArrayList<>();
            functionalities.forEach((functionality2, classes2) -> {
                if (!functionality1.equals(functionality2)) {
                    Double sumWeightClassFOne = 0.0;

                    for (Class classe : classes1) {
                        sumWeightClassFOne += classe.getWeight();
                    }
                    Double sumWeightClassEquals = 0.0;
                    List<Class> classesIguais = intersection(classes1, classes2);
                    for (Class classe : classesIguais) {
                        sumWeightClassEquals += classe.getWeight();
                    }
                    Double similarity = (classesIguais.size() * sumWeightClassEquals) /
                            (classes1.size() * sumWeightClassFOne) * 100;

                    colunas.add(new Column(functionality2, similarity));
                }
            });
            similarityTable.put(functionality1, colunas);
        });
        return similarityTable;
    }

    private static List<Microservice> groupFunctionalitiesBySimilatiry(Map<String, List<Column>> similarityTable,
                                                                       Config config,
                                                                       Map<String, List<Class>> funcionalidadesMap) {
        List<String> similarities = new ArrayList<>();
        List<Microservice> microservices = new ArrayList<>();

        similarityTable.forEach((row, columns) -> {
            String functionalities = row;
            List<Column> colunasFiltradas = columns.stream()
                    .filter(column -> column.getThreshold() >= config.getDecompositionThreshold())
                    .collect(Collectors.toList());

            if (similarities.isEmpty()) {
                List<ClassResponse> classResponses = new ArrayList<>();
                gerarClassesParaMicrosservico(funcionalidadesMap, functionalities,
                        classResponses);

                for (Column colunaFiltrada : colunasFiltradas) {
                    gerarClassesParaMicrosservico(funcionalidadesMap, colunaFiltrada.getNomeFuncionalidade(),
                            classResponses);
                    functionalities += ", " + colunaFiltrada.getNomeFuncionalidade();
                }

                Microservice micro = new Microservice();
                micro.setId("Microservice " + (microservices.size() + 1));
                micro.setFunctionalities(functionalities);
                micro.setClasses(classResponses);
                microservices.add(micro);
                similarities.add(functionalities);
            } else {
                //PAra cada coluna, preciso ver se a funcionalidade já não existe em algum microsserviço
                Integer indiceMicrosservico1 = getIndiceMicrosservico(similarities, functionalities);
                Integer indiceMicrosservico2 = getIndiceMicrosservico(similarities, colunasFiltradas);

                if (Objects.isNull(indiceMicrosservico1) && Objects.isNull(indiceMicrosservico2)) {
                    List<ClassResponse> classResponses = new ArrayList<>();
                    gerarClassesParaMicrosservico(funcionalidadesMap, functionalities, classResponses);

                    for (Column colunaFiltrada : colunasFiltradas) {
                        gerarClassesParaMicrosservico(funcionalidadesMap, colunaFiltrada.getNomeFuncionalidade(),
                                classResponses);
                        functionalities += ", " + colunaFiltrada.getNomeFuncionalidade();
                    }
                    Microservice micro = new Microservice();
                    micro.setId("Microservice " + (microservices.size() + 1));
                    micro.setFunctionalities(functionalities);
                    micro.setClasses(classResponses);
                    microservices.add(micro);

                    similarities.add(functionalities);
                } else if (!Objects.isNull(indiceMicrosservico1) && Objects.isNull(indiceMicrosservico2)) {
                    Microservice micro = microservices.get(indiceMicrosservico1);
                    microservices.remove(micro);
                    List<ClassResponse> classResponses = micro.getClasses();

                    for (Column colunaFiltrada : colunasFiltradas) {
                        gerarClassesParaMicrosservico(funcionalidadesMap, colunaFiltrada.getNomeFuncionalidade(), classResponses);
                        if (!micro.getFunctionalities().contains(colunaFiltrada.getNomeFuncionalidade())) {
                            micro.setFunctionalities(micro.getFunctionalities() + ", " + colunaFiltrada.getNomeFuncionalidade());
                        }
                    }
                    micro.setClasses(classResponses);
                    microservices.add(indiceMicrosservico1, micro);

                } else if (Objects.isNull(indiceMicrosservico1) && !Objects.isNull(indiceMicrosservico2)) {
                    Microservice micro = microservices.get(indiceMicrosservico2);
                    microservices.remove(micro);
                    List<ClassResponse> classResponses = micro.getClasses();
                    for (Column colunaFiltrada : colunasFiltradas) {
                        gerarClassesParaMicrosservico(funcionalidadesMap, colunaFiltrada.getNomeFuncionalidade(), classResponses);
                        if (!micro.getFunctionalities().contains(colunaFiltrada.getNomeFuncionalidade())) {
                            micro.setFunctionalities(micro.getFunctionalities() + ", " + colunaFiltrada.getNomeFuncionalidade());
                        }
                    }
                    micro.setClasses(classResponses);
                    microservices.add(indiceMicrosservico2, micro);
                }
            }
        });
        return microservices;
    }

    private static void printMicrosservices(List<Microservice> microsservicos) {

        microsservicos.forEach(microservice -> {
            try {
                String json = new ObjectMapper().writeValueAsString(microservice);
                System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private static Integer getIndiceMicrosservico(List<String> microsservicos, String functionality) {
        Integer indice = null;
        for (int contador = 0; contador < microsservicos.size(); contador++) {
            if (microsservicos.get(contador).contains(functionality)) {
                indice = contador;
                break;
            }
        }
        return indice;
    }

    private static Integer getIndiceMicrosservico(List<String> microsservicos, List<Column> functionalities) {
        Integer indice = null;
        for (Column functionality : functionalities) {
            for (int contador = 0; contador < microsservicos.size(); contador++) {
                if (microsservicos.get(contador).contains(functionality.getNomeFuncionalidade())) {
                    indice = contador;
                    break;
                }
            }
            if (!Objects.isNull(indice)) {
                break;
            }
        }
        return indice;
    }

    private static List<Class> intersection(List<Class> classesF1, List<Class> classesF2) {
        List<Class> classesSimilares = new ArrayList<>();
        for (int contador1 = 0; contador1 < classesF1.size(); contador1++) {
            for (int contador2 = 0; contador2 < classesF2.size(); contador2++) {
                if (classesF1.get(contador1).getClassName().equals(classesF2.get(contador2).getClassName())) {
                    if (classesSimilares.isEmpty()) {
                        classesSimilares.add(classesF1.get(contador1));
                    } else if (!classeJaExistente(classesSimilares, classesF1.get(contador1))) {
                        classesSimilares.add(classesF1.get(contador1));
                    }
                }
            }
        }
        return classesSimilares;
    }

    private static boolean classeJaExistente(List<Class> classes, Class classeNova) {
        return classes.stream()
                .filter(classe ->
                        classe.getClassName().equals(classeNova.getClassName()))
                .findFirst()
                .isPresent();
    }

    private static void gerarClassesParaMicrosservico(Map<String, List<Class>> funcionalidadesMap,
                                                      String functionalities,
                                                      List<ClassResponse> classResponses) {

        List<Class> listaClasses = funcionalidadesMap.get(functionalities);

        listaClasses.forEach(classeA -> {
            if (!ehClasseAdicionada(classResponses, classeA)) {
                List<String> methodNames = new ArrayList<>();
                classeA.getMethodName().forEach(methodNames::add);
                MethodResponse methodResponse = new MethodResponse(methodNames);
                ClassResponse classResponse = new ClassResponse(classeA.getClassName(), methodResponse);
                classResponses.add(classResponse);
            } else {
                Integer indice = getIndiceClassedicionada(classResponses, classeA);
                ClassResponse classResponse = classResponses.get(indice);

                //Adicionar apenas os métodos que não estão presentes ainda
                List<String> metodosNaoAdicionados = classeA.getMethodName().stream()
                        .filter(nameMethod -> !ehMetodoJaAdicionado(classResponse.getMethods().getNames(), nameMethod))
                        .collect(Collectors.toList());

                metodosNaoAdicionados.forEach(metodoNaoAdicionado -> classResponse.getMethods().getNames().add(metodoNaoAdicionado));
            }
        });
    }

    private static boolean ehMetodoJaAdicionado(List<String> metodos, String metodoNovo) {
        return metodos.stream().anyMatch(metodo -> metodo.equals(metodoNovo));
    }

    private static boolean ehClasseAdicionada(List<ClassResponse> classResponses, Class classeA) {
        if (classResponses.isEmpty()) return false;
        return classResponses.stream().
                filter(classeResponse -> classeResponse.getName().equals(classeA.getClassName()))
                .findFirst()
                .isPresent();
    }

    private static Integer getIndiceClassedicionada(List<ClassResponse> classResponses, Class classeA) {
        Integer indice = null;
        for (int contador = 0; contador < classResponses.size(); contador++) {
            if (classResponses.get(contador).getName().equals(classeA.getClassName())) {
                indice = contador;
                break;
            }
        }
        return indice;
    }
}
