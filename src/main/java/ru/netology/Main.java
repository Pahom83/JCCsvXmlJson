package ru.netology;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //конвертирование SCV в JSON, задача 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "./src/data.csv";
        List<Employee> listScv = parseCSV(columnMapping, fileName);
        String jsonScv = listToJson(listScv);
        writeString(jsonScv, "./src/fromScv.json");

        //конвертирование XML в JSON, задача 2
        List<Employee> listXml = parseXML("./src/data.xml");
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "./src/fromXml.json");

        //извлечение объектов из json-файла, задача 3
        String json = readString("./src/fromXml.json");
        List<Employee> list = jsonToList(json);
        printListToConsole(list);
    }

    private static String readString(String s) {
        StringBuilder string = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(s))) {
            String read;
            while ((read = reader.readLine()) != null) {
                string.append(read);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return string.toString();
    }

    public static List<Employee> parseXML(String xmlFile) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFile));
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                employeeList.add(new Employee(
                        Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                ));
            }
        }
        return employeeList;
    }

    public static void writeString(String json, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list.addAll(csv.parse());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void printListToConsole(List<Employee> list) {
        for (Employee e : list) {
            System.out.println(e);
        }
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONArray array = (JSONArray) parser.parse(json);
            for (Object s : array) {
                list.add(gson.fromJson(s.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}