Feature: create and join a room
  Background:
    * def login_response = call read('classpath:karate/login/login1.feature')
    * def csrf_token = login_response.csrf

Scenario: create room
  Given url 'http://localhost:8080/api/room/create'
#    And header X-CSRF-TOKEN = csrf_token
  When method POST
  Then status 200
  And match $ contains {room_code_assigned:"#notnull", colour_assigned:"#notnull"}
