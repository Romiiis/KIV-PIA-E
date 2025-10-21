package com.romiiis.mapper;

import com.romiiis.domain.ProjectState;
import com.romiiis.domain.UserRole;
import com.romiiis.model.ProjectStateDTO;
import com.romiiis.model.UserRoleDTO;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Common mapper for converting between different data types.
 * Includes conversions for time types and enums.
 *
 * @author Roman Pejs
 */
@Mapper(componentModel = "spring")
public interface CommonMapper {

    /**
     * Maps an Instant to an OffsetDateTime in UTC.
     *
     * @param instant the Instant to be mapped
     * @return the mapped OffsetDateTime, or null if the input is null
     */
    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    /**
     * Maps an OffsetDateTime to an Instant.
     *
     * @param offsetDateTime the OffsetDateTime to be mapped
     * @return the mapped Instant, or null if the input is null
     */
    default Instant mapOffsetDateTimeToInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }


    /**
     * Maps a UserRoleDTO to a UserRole.
     *
     * @param dto the UserRoleDTO to be mapped
     * @return the mapped UserRole, or null if the input is null
     */
    default UserRole mapUserRoleDTOToDomain(UserRoleDTO dto) {
        return dto == null ? null : UserRole.valueOf(dto.name());
    }

    /**
     * Maps a UserRole to a UserRoleDTO.
     *
     * @param domain the UserRole to be mapped
     * @return the mapped UserRoleDTO, or null if the input is null
     */
    default UserRoleDTO mapUserRoleToUserRoleDTO(UserRole domain) {
        return domain == null ? null : UserRoleDTO.valueOf(domain.name());
    }


    /**
     * Maps a set of Locale objects to a list of language tags (String).
     *
     * @param languages the set of Locale objects to be mapped
     * @return the mapped list of language tags
     */
    default List<String> mapListLocale(List<Locale> languages) {
        return languages.stream()
                .map(Locale::toLanguageTag)
                .collect(Collectors.toList());

    }

    /**
     * Maps a list of language tags (String) to a set of Locale objects.
     *
     * @param languages the list of language tags to be mapped
     * @return the mapped set of Locale objects
     */
    default Set<Locale> mapListStringToSetLocale(List<String> languages) {
        return languages.stream()
                .map(Locale::forLanguageTag)
                .collect(Collectors.toSet());
    }

    /**
     * Maps a set of Locale objects to a list of language tags (String).
     *
     * @param languages the set of Locale objects to be mapped
     * @return the mapped list of language tags
     */
    default List<String> mapSetLocaleToListString(Set<Locale> languages) {
        return languages.stream()
                .map(Locale::toLanguageTag)
                .collect(Collectors.toList());

    }


    /**
     * Maps a list of language tags (String) to a set of Locale objects.
     *
     * @param locale the language tag to be mapped
     * @return the mapped Locale object
     */
    default String mapLocaleToString(Locale locale) {
        return locale != null ? locale.toLanguageTag() : null;
    }

    /**
     * Maps a language tag (String) to a Locale object.
     *
     * @param code the language tag to be mapped
     * @return the mapped Locale object
     */
    default Locale mapStringToLocale(String code) {
        return code != null ? Locale.forLanguageTag(code) : null;
    }


    /**
     * Maps a ProjectStateDTO to a ProjectState.
     *
     * @param dto the ProjectStateDTO to be mapped
     * @return the mapped ProjectState
     */
    ProjectState mapProjectStateDTOToDomain(ProjectStateDTO dto);


    /**
     * Maps a ProjectState to a ProjectStateDTO.
     *
     * @param domain the ProjectState to be mapped
     * @return the mapped ProjectStateDTO
     */
    ProjectStateDTO mapDomainToProjectStateDTO(ProjectState domain);
}
