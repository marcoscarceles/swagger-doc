class UrlMappings {

	static mappings = {

        "/api/$controller/$action/$id" {
            constraints {
                // apply constraints here
            }
        }

        "/api-doc"(controller: 'swagger', action:'index')

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
