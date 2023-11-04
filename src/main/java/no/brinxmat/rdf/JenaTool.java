package no.brinxmat.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
}
