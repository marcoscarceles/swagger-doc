class UrlMappings {

	static mappings = {

        "/api/$controller/$action/$id" {
            constraints {
                // apply constraints here
            }
        }

        "/api-doc"(namespace: 'swagger-doc', controller: 'swagger', action:'index')
        "/swagger.json"(namespace: 'swagger-doc', controller: 'swagger', action:'swagger')
        "/validate-swagger"(namespace: 'swagger-doc', controller: 'swagger', action:'validate')

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
