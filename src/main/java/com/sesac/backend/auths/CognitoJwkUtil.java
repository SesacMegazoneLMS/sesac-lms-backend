package com.sesac.backend.auths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class CognitoJwkUtil {

    private CognitoJwkUtil() {
        
    }

    private static final String JWK_URL = "https://cognito-idp.ap-northeast-2.amazonaws.com/ap-northeast-2_78HNDcTxY/.well-known/jwks.json";

    public static PublicKey getPublicKey(String kid) throws Exception {
        URL url = new URL(JWK_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwks = mapper.readTree(connection.getInputStream()).get("keys");

        for (JsonNode jwk : jwks) {
            if (jwk.get("kid").asText().equals(kid)) {
                String n = jwk.get("n").asText();
                String e = jwk.get("e").asText();

                byte[] modulus = Base64.getUrlDecoder().decode(n);
                byte[] exponent = Base64.getUrlDecoder().decode(e);

                RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    new java.math.BigInteger(1, modulus),
                    new java.math.BigInteger(1, exponent)
                );

                return KeyFactory.getInstance("RSA").generatePublic(spec);
            }
        }

        throw new RuntimeException("No matching JWK found for kid: " + kid);
    }
}
