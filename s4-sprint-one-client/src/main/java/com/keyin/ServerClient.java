package com.keyin;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;



import java.io.IOException;
import java.util.Scanner;

public class ServerClient {

    private static String serverBaseUrl;

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static String performRequest(String url, String method, String requestBody) throws IOException {
        HttpRequestBase request;

        switch (method) {
            case "GET":
                request = new HttpGet(url);
                break;
            case "POST":
                HttpPost postRequest = new HttpPost(url);
                if (requestBody != null && !requestBody.isEmpty()) {
                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    postRequest.setEntity(entity);
                }
                request = postRequest;
                break;
            case "PUT":
                HttpPut putRequest = new HttpPut(url);
                if (requestBody != null && !requestBody.isEmpty()) {
                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    putRequest.setEntity(entity);
                }
                request = putRequest;
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        request.addHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);

        return EntityUtils.toString(response.getEntity());
    }

    public static void interactWithMenu(Scanner scanner) {
        boolean exit = false;
        boolean isTestPurpose = false;
    
        while (!exit) {
            if (!isTestPurpose) {
                System.out.println("Is this for test purposes? (yes/no): ");
                String testPurposeInput = scanner.next().toLowerCase();
                
                if ("yes".equals(testPurposeInput)) {
                    isTestPurpose = true;
                } else if ("no".equals(testPurposeInput)) {
                    isTestPurpose = false;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                    continue;
                }
            }
    
            System.out.println("Choose an endpoint to interact with:");
            System.out.println("1. Aircraft");
            System.out.println("2. Airports");
            System.out.println("3. Cities");
            System.out.println("4. Passengers");
            System.out.println("5. List Airports in Cities");
            System.out.println("0. Exit");
    
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
    
                if (isTestPurpose) {
                    setServerUrlForTest();
                } else {
                    setServerUrlForNonTest();
                }
                
    
                switch (choice) {
                    case 1:
                        interactWithAircraft(scanner);
                        break;
                    case 2:
                        interactWithAirports(scanner);
                        break;
                    case 3:
                        interactWithCities(scanner);
                        break;
                    case 4:
                        interactWithPassengers(scanner);
                        break;
                    case 5:
                        listAirportsInCities();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }
            private static void setServerUrlForTest() {
        serverBaseUrl = "http://localhost:8081";
    }
    
    private static void setServerUrlForNonTest() {
        serverBaseUrl = "http://localhost:8080";
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        interactWithMenu(scanner);
        scanner.close();
    }

    
    
    private static void interactWithAircraft(Scanner scanner) {
            while (true) {
                System.out.println("Choose an action for Aircraft:");
                System.out.println("1. List all Aircraft");
                System.out.println("2. Create an Aircraft");
                System.out.println("3. Update an Aircraft");
                System.out.println("4. Delete an Aircraft");
                System.out.println("0. Go back to the main menu");
    
                int choice = scanner.nextInt();
    
                switch (choice) {
                    case 1:
                        try {
                            String aircraftList = performGetRequest(serverBaseUrl + "/api/aircraft");
                            System.out.println("List of Aircraft:\n" + aircraftList);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            System.out.println("Enter aircraft details:");
                            System.out.print("Type: ");
                            String type = scanner.next();
                            System.out.print("Airline Name: ");
                            String airlineName = scanner.next();
                            System.out.print("Number of Passengers: ");
                            int numberOfPassengers = scanner.nextInt();
    
                            String aircraftJson = "{\"type\":\"" + type + "\",\"airlineName\":\"" + airlineName + "\",\"numberOfPassengers\":" + numberOfPassengers + "}";
                            String response = performPostRequest(serverBaseUrl + "/api/aircraft", aircraftJson);
                            System.out.println("Response: " + response);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            System.out.print("Enter Aircraft ID to update: ");
                            long aircraftId = scanner.nextLong();
    
                            System.out.println("Enter updated aircraft details:");
                            System.out.print("Type: ");
                            String updatedType = scanner.next();
                            System.out.print("Airline Name: ");
                            String updatedAirlineName = scanner.next();
                            System.out.print("Number of Passengers: ");
                            int updatedNumberOfPassengers = scanner.nextInt();
    
                            String updatedAircraftJson = "{\"type\":\"" + updatedType + "\",\"airlineName\":\"" + updatedAirlineName + "\",\"numberOfPassengers\":" + updatedNumberOfPassengers + "}";
                            String updateResponse = performPutRequest(serverBaseUrl + "/api/aircraft/" + aircraftId, updatedAircraftJson);
                            System.out.println("Update Response: " + updateResponse);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            System.out.print("Enter Aircraft ID to delete: ");
                            long aircraftId = scanner.nextLong();
    
                            String deleteResponse = performDeleteRequest(serverBaseUrl + "/api/aircraft/" + aircraftId);
                            System.out.println("Delete Response: " + deleteResponse);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            }
        }
    

    private static void interactWithAirports(Scanner scanner) {
        while (true) {
            System.out.println("Choose an action for Airports:");
            System.out.println("1. List all Airports");
            System.out.println("2. Create an Airport");
            System.out.println("3. Update an Airport");
            System.out.println("4. Delete an Airport");
            System.out.println("0. Go back to main menu");
    
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    try {
                        String airportList = performGetRequest("http://localhost:8080/api/airports");
                        System.out.println("List of Airports:\n" + airportList);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                    case 2:
                    try {
                        System.out.println("Enter airport details:");
                        System.out.print("Name: ");
                        String name = scanner.next();
                        System.out.print("Code: ");
                        String code = scanner.next();
                        System.out.print("City ID: ");
                        long cityId = scanner.nextLong();
                        
                
                        String airportJson = String.format("{ \"name\": \"%s\", \"code\": \"%s\", \"city\": { \"id\": %d } }", name, code, cityId);

                
                        String response = performPostRequest("http://localhost:8080/api/airports", airportJson);
                        System.out.println("Response: " + response);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        System.out.print("Enter Airport ID to update: ");
                        long airportId = scanner.nextLong();
    
                        System.out.println("Enter updated airport details:");
                        System.out.print("Name: ");
                        String updatedName = scanner.next();
                        System.out.print("Code: ");
                        String updatedCode = scanner.next();
                        System.out.print("City ID: ");
                        long updatedCityId = scanner.nextLong();
    
                        String updatedAirportJson = "{\"name\":\"" + updatedName + "\",\"code\":\"" + updatedCode + "\",\"cityId\":" + updatedCityId + "}";
                        String updateResponse = performPutRequest(serverBaseUrl + "/api/airports/" + airportId, updatedAirportJson);
                        System.out.println("Update Response: " + updateResponse);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        System.out.print("Enter Airport ID to delete: ");
                        long airportId = scanner.nextLong();
    
                        String deleteResponse = performDeleteRequest(serverBaseUrl + "/api/airports" + airportId);
                        System.out.println("Delete Response: " + deleteResponse);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    

    private static void interactWithCities(Scanner scanner) {
        
        while (true) {
            System.out.println("Choose an action for Cities:");
            System.out.println("1. List all Cities");
            System.out.println("2. Create a City");
            System.out.println("3. Update a City");
            System.out.println("4. Delete a City");
            System. out. println("0. Go back to main menu");
    
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    try {
                        String cityList = performGetRequest("http://localhost:8080/api/cities");
                        System.out.println("List of Cities:\n" + cityList);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        System.out.println("Enter city details:");
                        System.out.print("Name: ");
                        String name = scanner.next();
                        System.out.print("province: ");
                        String province = scanner.next();
                        System.out.print("Population: ");
                        int population = scanner.nextInt();
    
                        String cityJson = "{\"name\":\"" + name + "\",\"province\":\"" + province + "\",\"population\":" + population + "}";
                        String response = performPostRequest("http://localhost:8080/api/cities", cityJson);
                        System.out.println("Response: " + response);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        System.out.print("Enter City ID to update: ");
                        long cityId = scanner.nextLong();
    
                        System.out.println("Enter updated city details:");
                        System.out.print("Name: ");
                        String updatedName = scanner.next();
                        System.out.print("province: ");
                        String updatedprovince = scanner.next();
                        System.out.print("Population: ");
                        int updatedPopulation = scanner.nextInt();
    
                        String updatedCityJson = "{\"name\":\"" + updatedName + "\",\"province\":\"" + updatedprovince + "\",\"population\":" + updatedPopulation + "}";
                        String updateResponse = performPutRequest(serverBaseUrl + "/api/cities/" + cityId, updatedCityJson);
                        System.out.println("Update Response: " + updateResponse);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        System.out.print("Enter City ID to delete: ");
                        long cityId = scanner.nextLong();
    
                        String deleteResponse = performDeleteRequest(serverBaseUrl + "/api/cities" + cityId);
                        System.out.println("Delete Response: " + deleteResponse);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }
    

    private static void interactWithPassengers(Scanner scanner) {
            while (true) {
                System.out.println("Choose an action for Passengers:");
                System.out.println("1. List all Passengers");
                System.out.println("2. Create a Passenger");
                System.out.println("3. Update a Passenger");
                System.out.println("4. Delete a Passenger");
                System.out.println("0. Go back to the main menu");
    
                int choice = scanner.nextInt();
    
                switch (choice) {
                    case 1:
                        try {
                            String passengerList = performGetRequest("http://localhost:8080/api/passengers");
                            System.out.println("List of Passengers:\n" + passengerList);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            System.out.println("Enter passenger details:");
                            System.out.print("First Name: ");
                            String firstName = scanner.next();
                            System.out.print("Last Name: ");
                            String lastName = scanner.next();
                            System.out.print("Phone Number: ");
                            String phoneNumber = scanner.next();
    
                            String passengerJson = "{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"phoneNumber\":\"" + phoneNumber + "\"}";
                            String response = performPostRequest("http://localhost:8080/api/passengers", passengerJson);
                            System.out.println("Response: " + response);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            System.out.print("Enter Passenger ID to update: ");
                            long passengerId = scanner.nextLong();
    
                            System.out.println("Enter updated passenger details:");
                            System.out.print("First Name: ");
                            String updatedFirstName = scanner.next();
                            System.out.print("Last Name: ");
                            String updatedLastName = scanner.next();
                            System.out.print("Phone Number: ");
                            String updatedPhoneNumber = scanner.next();
    
                            String updatedPassengerJson = "{\"firstName\":\"" + updatedFirstName + "\",\"lastName\":\"" + updatedLastName + "\",\"phoneNumber\":\"" + updatedPhoneNumber + "\"}";
                            String updateResponse = performPutRequest(serverBaseUrl + "/api/passengers/" + passengerId, updatedPassengerJson);
                            System.out.println("Update Response: " + updateResponse);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            System.out.print("Enter Passenger ID to delete: ");
                            long passengerId = scanner.nextLong();
    
                            String deleteResponse = performDeleteRequest(serverBaseUrl + "/api/passengers" + passengerId);
                            System.out.println("Delete Response: " + deleteResponse);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            }
        }

        private static void listAirportsInCities() {
            try {
                String airportsEndpoint = serverBaseUrl + "/api/airports";
                String citiesEndpoint = serverBaseUrl + "/api/cities";
    
                HttpGet airportsRequest = new HttpGet(airportsEndpoint);
                HttpResponse airportsResponse = httpClient.execute(airportsRequest);
                String airportsResponseJson = EntityUtils.toString(airportsResponse.getEntity());
    
                HttpGet citiesRequest = new HttpGet(citiesEndpoint);
                HttpResponse citiesResponse = httpClient.execute(citiesRequest);
                String citiesResponseJson = EntityUtils.toString(citiesResponse.getEntity());
    
                JSONArray citiesArray = new JSONArray(citiesResponseJson);
                JSONArray airportsArray = new JSONArray(airportsResponseJson);
    
                for (int i = 0; i < citiesArray.length(); i++) {
                    JSONObject cityObject = citiesArray.getJSONObject(i);
                    String cityName = cityObject.getString("name");
                    System.out.println("Airports in " + cityName + ":");
    
                    for (int j = 0; j < airportsArray.length(); j++) {
                        JSONObject airportObject = airportsArray.getJSONObject(j);
                        JSONObject airportCityObject = airportObject.getJSONObject("city");
    
                        if (airportCityObject.has("id") && airportCityObject.getLong("id") == cityObject.getLong("id")) {
                            String airportName = airportObject.getString("name");
                            String airportCode = airportObject.getString("code");
                            System.out.println("  - " + airportName + ", " + airportCode);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        }
        

    
    

    public static String performGetRequest(String url) throws IOException, InterruptedException {
        return performRequest(url, "GET", "");
    }

    public static String performPostRequest(String url, String requestBody) throws IOException, InterruptedException {
        return performRequest(url, "POST", requestBody);
    }

    public static String performPutRequest(String url, String requestBody) throws IOException, InterruptedException {
        return performRequest(url, "PUT", requestBody);
    }

    public static String performDeleteRequest(String url) throws IOException, InterruptedException {
        return performRequest(url, "DELETE", "");
    }

}
    

