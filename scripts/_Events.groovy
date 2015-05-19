eventAllTestsStart = {
    if(getBinding().variables["baseName"] == "swagger-doc") {
        if (getBinding().variables.containsKey("functionalTests")) {
            println "Adding functional tests phase"
            functionalTests << "functional"
        }
    }
}