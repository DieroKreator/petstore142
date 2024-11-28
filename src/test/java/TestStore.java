import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.google.gson.Gson;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStore {
    static String ct = "application/json";
    static String uriStore = "https://petstore.swagger.io/v2/store/order";
    static int orderId = 922337201;

    public static String lerArquivoCSV(String arquivoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    @Test @Order(1)
    public void testPostUser() throws IOException{
        // carregar os dados do arquivo json do pet
        String jsonBody = lerArquivoCSV("src/test/resources/json/store.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uriStore)
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(922337201))
            .body("petId", is(602740501))
            .body("quantity", is(1))
            .body("status", is("placed"))
            .body("complete", is(true))
        ;
    }

    @Test @Order(2)
    public void testGetUser(){

        given()
            .contentType(ct)
            .log().all()
        .when()
            .get(uriStore + "/" + orderId)
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(orderId))
            .body("petId", is(602740501))
            .body("quantity", is(1))
            .body("status", is("placed"))
            .body("complete", is(true))
        ;
    }

    // @Test @Order(3)
    // public void testPutUser() throws IOException{
    //     // Configura
    //     String jsonBody = lerArquivoCSV("src/test/resources/json/user2.json");

    //     given()
    //         .contentType(ct)
    //         .log().all()
    //         .body(jsonBody)
    //     // Executa
    //     .when()
    //         .put(uriUser+ "/" + username)
    //     // Valida
    //     .then()
    //         .log().all()
    //         .statusCode(200)
    //         .body("code", is(200))
    //         .body("type", is("unknown"))
    //         .body("message", is("740560201"))
    //     ;
    // }

    // @Test @Order(4)
    // public void testDeleteUser(){
    //     // Configura --> Dados de entrada e saída no começo da Classe

    //     given()
    //         .contentType(ct)
    //         .log().all()
    //     // Executa
    //     .when()
    //         .delete(uriUser + "/" + username)
    //     // Valida
    //     .then()
    //         .log().all()
    //         .statusCode(200) // se comunicou e processou
    //         .body("code", is(200))   // se apagou
    //         .body("type", is("unknown"))
    //         .body("message", is("zeca"))
    //     ;
    // }

    // // Data Driven Testing (DDT) / Teste Direcionado por Dados / Teste com Massa
    // // Teste com Json parametrizado
    // @ParameterizedTest @Order(5)
    // @CsvFileSource(resources = "/csv/userMassa.csv", numLinesToSkip = 1, delimiter = ',')
    // public void testPostUserDDT(
    //     int userId,
    //     String username,
    //     String firstName,
    //     String lastName,
    //     String email,
    //     String password,
    //     String phone,
    //     int userStatus
    // ) 
    // {
    //     User user = new User();

    //     user.id = userId;
    //     user.username = username;
    //     user.firstName = firstName;
    //     user.lastName = lastName;
    //     user.email = email;
    //     user.password = password; 
    //     user.phone = phone; 
    //     user.userStatus = userStatus; 

    //     // Criar um Json para o Body ser enviado a partir da classe User e do CSV
    //     Gson gson = new Gson();
    //     String jsonBody = gson.toJson(user);
        
    //     given()
    //         .contentType(ct)
    //         .log().all()
    //         .body(jsonBody)
    //     .when()
    //         .post(uriUser)
    //     .then()
    //         .log().all()
    //         .statusCode(200)
    //         .body("code", is(200))
    //         .body("type", is("unknown"))
    //         .body("message", is(String.valueOf(userId)));
    // }
}
