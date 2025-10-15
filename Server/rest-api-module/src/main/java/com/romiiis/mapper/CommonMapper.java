package com.romiiis.mapper;

import com.romiiis.domain.UserRole;
import com.romiiis.model.UserRoleDTO;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    // region Time conversions
    default OffsetDateTime mapInstantToOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    default Instant mapOffsetDateTimeToInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
    // endregion

    // region Enum conversions
    default UserRole mapUserRoleDTOToDomain(UserRoleDTO dto) {
        return dto == null ? null : UserRole.valueOf(dto.name());
    }

    default UserRoleDTO mapDomainToUserRoleDTO(UserRole domain) {
        return domain == null ? null : UserRoleDTO.valueOf(domain.name());
    }
    // endregion

    default Set<Locale> mapListLanguages(List<String> languages) {
        return languages.stream()
                .map(Locale::forLanguageTag)
                .collect(Collectors.toSet());
    }

    default List<String> mapSetLanguages(Set<Locale> languages) {
        return languages.stream()
                .map(Locale::toLanguageTag)
                .collect(Collectors.toList());

    }
}
