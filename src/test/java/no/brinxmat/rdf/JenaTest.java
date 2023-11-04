package no.brinxmat.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JenaTest {

    @Test
    void shouldReturnAllSubjects() {
        var model = getModel("/simple-data.nt");
        var subjects = JenaTool.extractSubjects(model);
        assertEquals(1, subjects.size());
    }

    @Test
    void shouldReturnAllProperties() {
        var model = getModel("/simple-data.nt");
        var subjects = JenaTool.extractProperties(model);
        assertEquals(3, subjects.size());
    }

    @Test
    void shouldReturnAllObjects() {
        var model = getModel("/simple-data.nt");
        var subjects = JenaTool.extractObjects(model);
        assertEquals(3, subjects.size());
    }

    @Test
    void shouldReturnAllUriObjects() {
        var model = getModel("/simple-data.nt");
        var subjects = JenaTool.extractUriObjects(model);
        assertEquals(1, subjects.size());
    }

    @Test
    void shouldReturnAllTypes() {
        var model = getModel("/simple-data.nt");
        var subjects = JenaTool.extractTypes(model);
        var expected = List.of(URI.create("https://schema.org/Person"));
        assertEquals(expected, subjects);
    }

    // Task 1: fix the code to return animals

    @Test
    void shouldReturnAllPetAnimalTypes() {
        var model = getModel("/animals.nt");
        var subjects = JenaTool.extractPetAnimalTypes(model);
        var expected = List.of(URI.create("https://fakeschema.org/Cat"),
                URI.create("https://fakeschema.org/Dog"),
                URI.create("https://fakeschema.org/Horse"));
        assertEquals(expected, subjects);
    }

    @Test
    void shouldReturnAllPetAnimalTypesUsingSparql() {
        var model = getModel("/animals.nt");
        var query = stringFromResources("/animal-types.sparql");

        var subjects = JenaTool.extractPetAnimalTypes(model, query);
        var expected = List.of(URI.create("https://fakeschema.org/Cat"),
                URI.create("https://fakeschema.org/Dog"),
                URI.create("https://fakeschema.org/Horse"));
        assertEquals(expected, subjects);
    }

    // Task 3: do the same using inference.

    @Test
    void shouldReturnAllPetAnimalTypesUsingInference() {
        var model = getModel("/animals.nt");
        addToModel(model, "/fakeschema.ttl");
        var expected = List.of(URI.create("https://fakeschema.org/Cat"),
                URI.create("https://fakeschema.org/Dog"),
                URI.create("https://fakeschema.org/Horse"));
        var queryString = stringFromResources("/animal-inference.sparql");
        var actual = JenaTool.extractPetAnimalTypesUsingInference(model, queryString);
        assertEquals(expected, actual);
    }

    // Task 4
    @Test
    void shouldReturnReportOfHumansAndAges() {
        var model = getModel("/animals.nt");
        var expected = Map.of("Eric", 30, "Ian", 33, "Jim", 29, "Paul", 30);
        var queryString = stringFromResources("/age.sparql");
        var actual = JenaTool.extractOwnerAgeReport(model, queryString);
        assertEquals(expected, actual);
    }

    // Task 5
    @Test
    void shouldReturnWhetherEwanHasCat() {
        var model = getModel("/animals.nt");
        addToModel(model, "/fakeschema.ttl");
        var queryString = stringFromResources("/does-ewan-have-a-cat.sparql");
        var actual = JenaTool.ask(model, queryString);
        assertFalse(actual);
    }

    private void addToModel(Model model, String file) {
        var data = inputStreamFromResources(file);
        var serialization = "ttl".equalsIgnoreCase(file.substring(file.lastIndexOf(".") + 1))
                ? Lang.TURTLE
                : Lang.NTRIPLES;
            RDFDataMgr.read(model, data, serialization);
    }

    private static String stringFromResources(String file) {
        try (var inputStream = inputStreamFromResources(file)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Model getModel(String file) {
        var model = ModelFactory.createDefaultModel();
        var data = inputStreamFromResources(file);
        RDFDataMgr.read(model, data, Lang.NTRIPLES);
        return model;
    }

    private static InputStream inputStreamFromResources(String file) {
        var data = JenaTest.class.getResourceAsStream(file);
        if (isNull(data)) {
            throw new RuntimeException("No data for file: " + file);
        }
        return data;
    }
}
