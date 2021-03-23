package proxydummies

class UrlMappings {

    static mappings = {

        "/**"(controller: 'proxy', action:'index')

        "/"(controller: "setup", action:  [GET: "index"])

        "/setup"(controller: "setup", action:  [GET: "index"])

        "/setup/api/rule/search"(controller: "api", action:  [GET: "searchRules"])
        "/setup/api/rule/getRuleDatabaseBody"(controller: "api", action:  [GET: "getRuleDatabaseBody"])
        "/setup/api/rule/create"(controller: "api", action:  [POST: "createRule"])
        "/setup/api/rule/update"(controller: "api", action:  [POST: "updateRule"])
        "/setup/api/rule/enable"(controller: "api", action:  [POST: "enableRule"])
        "/setup/api/rule/disable"(controller: "api", action:  [POST: "disableRule"])
        "/setup/api/rule/delete"(controller: "api", action:  [POST: "deleteRule"])
        "/setup/api/rule/search"(controller: "api", action:  [GET: "searchRules"])
        "/setup/api/rule/export"(controller: "api", action:  [GET: "exportRule"])
        "/setup/api/rule/import"(controller: "api", action:  [POST: "importRule"])

        "/setup/api/configuration/find"(controller: "api", action:  [POST: "getConfiguration"])
        "/setup/api/configuration/update"(controller: "api", action:  [POST: "updateConfiguration"])

        "500"(view:'/error')
        "404"(view: 'notFound')
    }
}
