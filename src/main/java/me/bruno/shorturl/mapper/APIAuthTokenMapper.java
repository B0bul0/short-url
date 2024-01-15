package me.bruno.shorturl.mapper;

import me.bruno.shorturl.dto.APIAuthKeyDTO;
import me.bruno.shorturl.entity.APIAuthKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface APIAuthTokenMapper {

    APIAuthKeyEntity toModel(APIAuthKeyDTO dto);

    APIAuthKeyDTO toDto(APIAuthKeyEntity model);

}
