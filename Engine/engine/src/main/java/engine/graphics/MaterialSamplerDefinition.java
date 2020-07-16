package engine.graphics;

import java.util.List;

enum MaterialSamplerDefinition
{
    PBR_TEXTURED(List.of(
            // Units 0 - 2 occupied
            new SamplerToken("albedo",      true,   false,  3),
            new SamplerToken("normal",      false,  false,  4),
            new SamplerToken("metalness",   false,  false,  5),
            new SamplerToken("roughness",   false,  false,  6),
            new SamplerToken("displacement",false,  true,   7),
            new SamplerToken("ao",          false,  true,   8)
    )),
    REFLECTIVE(List.of(
            // Unit 0 occupied
            new SamplerToken("diffuse_texture", true, false, 1),
            new SamplerToken("reflection_map",  true, false, 2)
    )),
    REFRACTIVE(List.of(
            // Unit 0 occupied
            new SamplerToken("diffuse_texture", true, false, 1),
            new SamplerToken("refraction_map",  true, false, 2)
    )),
    SIMPLE(List.of(
            new SamplerToken("diffuse_texture", true,   false,  0),
            new SamplerToken("normal_map",      false,  true,   1)
    )),
    UNLIT(List.of(
            new SamplerToken("texture", true, false, 0)
    ));

    List<SamplerToken> samplerTokens;

    MaterialSamplerDefinition(List<SamplerToken> samplerTokens)
    {
        this.samplerTokens = samplerTokens;
    }

    static class SamplerToken
    {
        final String token;
        final boolean colorData, optional;
        final int unit;

        private SamplerToken(String token, boolean colorData, boolean optional, int unit)
        {
            this.token = token;
            this.colorData = colorData;
            this.optional = optional;
            this.unit = unit;
        }
    }
}