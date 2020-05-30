float when_gt(float x, float y)
{
    return max(sign(x - y), 0.0f);
}

vec3 calculatePointLight
(
    vec3 lightPosition, vec3 lightColor, float constant, float linear, float quadratic,     //Light Properties
    vec3 ambientColor, vec3 diffuseColor, float specularStrength, float specularExponent,   //Material Properties
    vec3 normal, vec3 viewDirection                                                         //Mesh and world properties
)
{
    vec3 direction = normalize(lightPosition - v_FragmentPosition);
    vec3 halfWayDirection = normalize(direction + viewDirection);
    //Ambient
    vec3 ambient = lightColor * ambientColor;
    //Diffuse
    float diffuseFactor = max(dot(normal, direction), 0.0f);
    vec3 diffuse = lightColor * diffuseColor * diffuseFactor;
    //Specular
    vec3 specular = lightColor * specularStrength * pow(max(dot(normal, halfWayDirection), 0.0f), specularExponent);// * when_gt(diffuseFactor, 0.0f);
    //Attenuation
    float distance = length(lightPosition - v_FragmentPosition);
    float attenuation = 1.0f / (constant + linear * distance + quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

vec3 calculateSpotlight
(
    vec3 lightPosition, vec3 lightDirection, vec3 lightColor, float innerCutoff, float outerCutoff,
    vec3 ambientColor, vec3 diffuseColor, float specularStrength, float specularExponent,
    vec3 normal, vec3 viewDirection
)
{
    vec3 direction = normalize(lightPosition - v_FragmentPosition);
    vec3 halfWayDirection = normalize(direction + viewDirection);
    float theta = dot(direction, normalize(-lightDirection));
    float epsilon = innerCutoff - outerCutoff;
    float intensity = clamp((theta - outerCutoff) / epsilon, 0.0f, 1.0f);
    //Ambient
    vec3 ambient = lightColor * ambientColor;
    //Diffuse
    float diffuseFactor = max(dot(normal, direction), 0.0f);
    vec3 diffuse = lightColor * diffuseColor * diffuseFactor;
    //Specular
    vec3 specular = lightColor * specularStrength * pow(max(dot(normal, halfWayDirection), 0.0f), specularExponent);// * when_gt(diffuseFactor, 0.0f);
    //Cutoff
    diffuse *= intensity;
    specular *= intensity;
    return (diffuse + specular);
}

vec3 calculateDirectionalLight
(
    vec3 lightDirection, vec3 lightColor,
    vec3 ambientColor, vec3 diffuseColor, float specularStrength, float specularExponent,
    vec3 normal, vec3 viewDirection
)
{
    vec3 direction = normalize(-lightDirection);
    vec3 halfWayDirection = normalize(direction + viewDirection);//Ambient
    vec3 ambient = lightColor * ambientColor;
    //Diffuse
    float diffuseFactor = max(dot(normal, direction), 0.0f);
    vec3 diffuse = lightColor * diffuseColor * diffuseFactor;
    //Specular
    vec3 specular = lightColor * specularStrength * pow(max(dot(normal, halfWayDirection), 0.0f), specularExponent);// * when_gt(diffuseFactor, 0.0f);

    return (ambient + diffuse + specular);
}