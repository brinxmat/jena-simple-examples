package no.brinxmat.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void shouldReturnAllCats() {
        var model = getModel("/animals.nt");
        var subjects = JenaTool.extractTypes(model);
        var expected = List.of(URI.create("https://schema.org/Person"));
        assertEquals(expected, subjects);
    }

    private static Model getModel(String file) {
        var model = ModelFactory.createDefaultModel();
        try (var data = JenaTest.class.getResourceAsStream(file)) {
            if (isNull(data)) {
                throw new RuntimeException("No data for file: " + file);
            }
            RDFDataMgr.read(model, data, Lang.NTRIPLES);
            return model;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
