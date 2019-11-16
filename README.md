# resolutions

The following is a simple REST API that authorizes requests using OAuth 2.0 Bearer tokens.

Each commit on the `presentation` branch correlates to 
the steps necessary to go from an unsecured REST API to the completed product.

To use, simply start up the Keycloak Authorization Server:

```bash
cd etc
docker-compose up
```

And the start up the resource server:

```bash
./mvnw spring-boot:run
```

You can obtain a token using the client credentials grant:

```bash
export TOKEN=`http :9999/token "client_id=client" "client_secret=secret" "grant_type=client_credentials" | jq -r .token`
```

And then query the endpoint:

```bash
http :8080/resolutions "Authorization: Bearer $TOKEN"
```

Add a resolution:

```bash
echo -n "Run for president" | http :8080/resolution
```

And complete it:

```bash
http PUT :8080/resolution/219168d2-1da4-4f8a-85d8-95b4377af3c1/complete
```

Enjoy!
