Feature: csrf and log-out endpoint
#se hace login y se prueba que el perfil de este usuario se puede ver
    Background:
        * url baseUrl
# esto toma todas las acciones de un escenario y las ejecuta una tras otra
        * call read('login1.feature')
        * def util = Java.type('karate.KarateTests')

    Scenario: user page
        * path 'user/1'
        * method get
        * status 200
        * def userName = util.selectHtml(response, "h1>span")
        * assert userName == 'pepa'
#se prueba que el logout funciona
    Scenario: logout
        * path 'logout'
    # todos los post llevan un atributo csrf
        * form field _csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1)
        * method post
        * print response
        * status 200