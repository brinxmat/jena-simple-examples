package no.brinxmat.rdf;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class JenaTool {

    private JenaTool() {
        // NO-OP
    }

    public static List<URI> extractSubjects(Model model) {

        // Uses iterator since we have it (iterator has toList method, which we use later).

        var subjectIterator = model.listSubjects();

        var subjects = new ArrayList<URI>();

        while (subjectIterator.hasNext()) {
            var current = subjectIterator.nextResource();
            subjects.add(URI.create(current.getURI()));
        }

        return subjects;
    }

    public static List<URI> extractProperties(Model model) {
        var statementIterator = model.listStatements();
        var properties = new ArrayList<URI>();

        while (statementIterator.hasNext()) {
            var current = statementIterator.nextStatement().getPredicate();
            properties.add(URI.create(current.getURI()));
        }
        return properties;
    }

    public static List<RDFNode> extractObjects(Model model) {
        var objectIterator = model.listObjects();
        var objects = new ArrayList<RDFNode>();
        while (objectIterator.hasNext()) {
            var current = objectIterator.next();
            objects.add(current);
        }
        return objects;
    }

    public static List<URI> extractUriObjects(Model model) {
        return extractObjects(model).stream()
                .filter(RDFNode::isURIResource)
                .map(RDFNode::asResource)
                .map(Resource::getURI)
                .map(URI::create)
                .toList();
    }

    public static List<URI> extractTypes(Model model) {

        // Using Iterator::toList

        return model.listObjectsOfProperty(RDF.type).toList().stream()
                .map(RDFNode::asResource)
                .map(Resource::getURI)
                .map(URI::create)
                .toList();
    }

    public static List<URI> extractPetAnimalTypes(Model model) {
        var hasPet = model.createProperty("https://fakeschema.org/hasPet");
        return model.listObjectsOfProperty(hasPet).toList()
                .stream()
                .map(pet -> model.listStatements(pet.asResource(), RDF.type, (RDFNode) null).toList())
                .flatMap(List::stream)
                .filter(i -> i.getPredicate().equals(RDF.type))
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(Resource::getURI)
                .map(URI::create)
                .filter(JenaTool::isNotPetRock)
                .distinct()
                .sorted()
                .toList();
    }

    private static boolean isNotPetRock(URI type) {
        return !type.equals(URI.create("https://fakeschema.org/PetRock"));
    }

    public static List<URI> extractPetAnimalTypes(Model model, String queryString) {
        try (var queryExecution = QueryExecutionFactory.create(queryString, model)) {
            var resultSet = queryExecution.execSelect();
            var results = new ArrayList<URI>();
            while (resultSet.hasNext()) {
                var querySolution = resultSet.next();
                if (nonNull(querySolution)) {
                    var current = querySolution.get("petType").toString();
                    results.add(URI.create(current));
                }
            }
            return results;
        }
    }

    public static List<URI> extractPetAnimalTypesUsingInference(Model model, String queryString) {
        return extractPetAnimalTypes(model, queryString);
    }

    public static Map<String, Integer> extractOwnerAgeReport(Model model, String queryString) {
        try (var queryExecution = QueryExecutionFactory.create(queryString, model)) {
            var resultSet = queryExecution.execSelect();
            var results = new HashMap<String, Integer>();
            while (resultSet.hasNext()) {
                var querySolution = resultSet.next();
                if (nonNull(querySolution)) {
                    var currentName = querySolution.get("name").asLiteral().getString();
                    var currentAge = Integer.parseInt(querySolution.get("age").asLiteral().getString());
                    results.put(currentName, currentAge);
                }
            }
            return results;
        }
    }

    public static boolean ask(Model model, String queryString) {
        try (var queryExecution = QueryExecutionFactory.create(queryString, model)) {
            return queryExecution.execAsk();
        }
    }

    public static String serializeResult(Model model, String queryString) {
        try (var queryExecution = QueryExecutionFactory.create(queryString, model)) {
            var resultModel = queryExecution.execDescribe();
            var document = JsonDocument.of(toJsonReader(resultModel));
            var context = JsonDocument.of(getPersonFrame());
            return JsonLd.frame(document, context).get().toString();
        } catch (JsonLdError e) {
            throw new RuntimeException(e);
        }
    }

    private static StringReader toJsonReader(Model resultModel) {
        var outputStream = new ByteArrayOutputStream();
        RDFDataMgr.write(outputStream, resultModel, Lang.JSONLD);
        return new StringReader(outputStream.toString());
    }

    private static InputStream getPersonFrame() {
        return JenaTool.class.getResourceAsStream("/jsonld-context.json");
    }
}
