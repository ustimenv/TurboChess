Feature:  register and login
#test para saber si el registro de un usuario se hace correctamente
  #se prueba iniciar sesion con este usuario recien creado
  Background:
    * url baseUrl
    * def util = Java.type('karate.KarateTests')

    Given path 'register'
    When method get
    Then status 200
    * string response = response
    * def csrf = util.selectAttribute(response, "input[name=_csrf]", "value");

  Scenario: new user register - get
    Given path 'register'
    When method get
    Then status 200

  Scenario: new user register - post
    Given path 'signup_form'
    And form field username = 'test'
    And form field password = 'test'
    And form field passwordConfirm = 'test'
    And form field _csrf = csrf
    When method post
    Then status 200

  Scenario: login with the new user
    Given path 'login'
    And form field username = 'test'
    And form field password = 'test'
    And form field _csrf = csrf
    When method post
    Then status 200
    * string response = response
    * def h4s = util.selectHtml(response, "h4");
    And match h4s contains 'User'


