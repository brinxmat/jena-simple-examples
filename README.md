# Spreading peace and prosperity through graph technologies

## What is this?

It's a short course on using graph technologies with Java.

## Why would I want to look at this?

You're working on a project that uses graph technologies.

## How do I use this?

1. Pull the project, build the code.
2. Start in the main branch
   - Familiarize yourself with the data in `src/test/resources/simple-data.nt`
   - If you need help to understand the data format `nt`, look at the documentation for [N-Triples](https://www.w3.org/TR/n-triples/)
   - Checkout the branch `task-1`, make the test run
   - A solution can be found in the branch `task-2`
   - Keep doing this until you have completed the tasks
   - You should now be familiar with the basics of RDF, Apache Jena, and SPARQL
3. Fire up a Fuseki instance
   - probably something like
     - `docker pull stain/jena-fuseki`
     - `docker run -p 3030:3030 -e ADMIN_PASSWORD=my_fancy_password stain/jena-fuseki`
     - point your web-browser at [https://localhost:3030](https://localhost:3030)
     - log in with your credentials, i.e. `admin` and whatever you put instead of `my_fancy_password`
     - Do the tasks below.

