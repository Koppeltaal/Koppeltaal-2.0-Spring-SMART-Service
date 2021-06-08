# Koppeltaal-2.0-Spring-SMART-Service
This dependency injects its services into an existing Spring Boot application. 
This adds the following functionality:

* a JWKS endpoint (through `spring-boot-starter-jwks`)
* A `SmartServiceConfiguration` object to configure the FHIR Server
* A `SmartClientCredentialService` that automatically requests the `access_token`
  and refreshes it when needed
* A `JwtValidationService` that verifies if the JWT auth  token is still valid. 
  If not, it will refresh
*  Resource services that bidirectionally manage data fom the FHIR store via `DTO` objects  
  
The above will be achieved by simply adding configuring the `application.properties`.
To get the `access_token`, use `SmartClientCredentialService.getAccessToken()`

## Available properties

We can list 3 properties. See below for an example:
```properties
fhir.smart.service.fhirServerUrl=https://staging-fhir-server.koppeltaal.headease.nl/fhir
fhir.smart.service.clientId=epd-client-id
fhir.smart.service.scope=read/*
```

_Note: A secret isn't needed as the SMART service  will sign the JWT with credentials
provided by the JWKS library (through `spring-boot-starter-jwks`)_

## Limitations
Currently, the code is expecting the SMART backend service signature to use the `RS512` algorithm.

## Building the software

### Requirements

#### Build software
* Java 11
* Maven 3

#### Downloading/pushing shared libraries

Koppeltaal 2.0 uses shared libraries as certain functionality (e.g. JWKS or SMART Backend Services)
are used in many components. These shared libraries are published
to [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).
In order to download these, you'll need a GitHub
[Personal Access Token](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token)
with at least the  `read:packages` scope.

After you have this token, you must add GitHub as a Maven `server` to your `~/.m2/settings.xml`.

The `<server>` tag should be added like this, replace the username and password:

```xml

<server>
  <id>github</id>
  <username>{{YOUR_GITHUB_USERNAME}}</username>
  <password>{{YOUR_GITHUB_PERSONAL_ACCESS_TOKEN}}</password>
</server>
```