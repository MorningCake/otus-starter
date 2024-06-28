package ru.morningcake.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.morningcake.data.dto.security.CurrentUser;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private static final ObjectMapper systemMapper;

    static {
        systemMapper = new ObjectMapper();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public CurrentUser base64ToUserDto(String baseString) {
        byte[] tokenBytes = Base64.getUrlDecoder().decode(baseString);
        Map<String, Object> tokenPayload = systemMapper.readValue(new String(tokenBytes, StandardCharsets.UTF_8), HashMap.class);

        // если проверка аутентификации прошла, то записать роли
        // иначе, если проверка не прошла, далее будет залогирована ошибка в errorMessage и список ролей останется пустым, что вызовет 403 при запросе /user/current
        String errorMessage = checkAuthenticationFromAccessList(tokenPayload);
        Set<String> upperCaseRoles = Set.of();
        if (errorMessage == null || errorMessage.isEmpty()) {
            upperCaseRoles = getRolesSetFromTokenPayload(tokenPayload);
        }
        return CurrentUser.builder()
            .employeeNumber(tokenPayload.get("employeenumber").toString())
            .login(tokenPayload.get("sub").toString())
            .email(tokenPayload.get("email").toString())
            .lastName(tokenPayload.getOrDefault("family_name", "").toString())
            .firstName(tokenPayload.get("given_name").toString())
            .middleName(tokenPayload.getOrDefault("patronymic", "").toString())
            .expiredTime(tokenPayload.get("exp").toString())
            .realmRoles(upperCaseRoles)
            .errorMessage(errorMessage)
            .build();
    }

    /**
     * Проверка аутентификации на прикладе по наличию AUTH_ACCESS_NAME в accesslist токена
     * @param tokenPayload tokenPayload
     * @return сообщение об ошибке или null, если ошибки не было
     * @throws SecurityException
     */
    private String checkAuthenticationFromAccessList(Map<String, Object> tokenPayload) throws SecurityException {
        String result = null;
        if (tokenPayload.get("accesslist") instanceof String) {
            String access = ((String) tokenPayload.get("accesslist"));

        } else if (tokenPayload.get("accesslist") instanceof List) {

        } else {
            result = "Access Denied! The token doesn't contain the accessList!";
        }
        return result;
    }

    private String loggingSecurityException(String accesses) {
        return "Access Denied! The token doesn't contain the required accessList element! accesslist: " + accesses;
    }



    private Set<String> getRolesSetFromTokenPayload(Map<String, Object> tokenPayload) {
        Set<String> upperCaseRoles;

        if (tokenPayload.get("groups") instanceof String) {
            String role = ((String) tokenPayload.get("groups")).toUpperCase().replace("CI03364876_", "");

            if (role.equals("NOT_FOUND")) {
                upperCaseRoles = new HashSet<>();
            } else {
                upperCaseRoles = Set.of(role);
            }
        } else if (tokenPayload.get("groups") instanceof List) {
            List<String> realmRoles = (List<String>) tokenPayload.get("groups");
            upperCaseRoles = realmRoles.stream()
                .distinct()
                .map(String::toUpperCase)
                .filter(role -> role.contains("CI03364876_")) // выбор только ролей Фемиды
                .map(role -> role.replace("CI03364876_", "")) //приведение роли СУДИР к роли в системе
                .collect(Collectors.toSet());
        } else {
            upperCaseRoles = new HashSet<>();
        }
        return upperCaseRoles;
    }

}
