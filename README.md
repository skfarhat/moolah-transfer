# Moolah Transfer 

## AccountResource Paths 


| URI                   | Method    | POST Data | Description                                   |
|-----------------------|-----------|-----------|-----------------------------------------------|
| /accounts             | GET       |    -      | Get list of all accounts                      |
| /accounts/{id}        | GET       |    -      | Get the account associated with {id} in JSON  |
| /accounts/{id}/xml    | GET       |    -      | Get the account associated with {id} in XML   |
| /accounts            | POST      | Account to create in JSON           | Create an Account from the POST Data                    |
| /accounts/{id}        | POST      | Account to update in JSON           | Update the Account whose Id matches {id} with the fields in POST Data                     |
| /accounts/{fromId}/transfer/{toId}    |   POST    | Transfer object in JSON | Issue a transfer from account with Id {fromId} to account with Id {toId} |


## Tests & Coverage

To run the test suite, the below command can be issued from the project root directory: 

```
./gradlew test
```

To generate Jacoco coverage reports, run the below command. Reports can be found in `{rootDir}/build/reports/jacoco/`

```
./gradlew test 
./gradlew jacocoTestReport
```
