package me.bruno.shorturl.mapper;

import me.bruno.shorturl.dto.ShortURLDTO;
import me.bruno.shorturl.entity.ShortURLEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShortURLMapper {

    ShortURLEntity toModel(ShortURLDTO dto);

    ShortURLDTO toDto(ShortURLEntity model);

}
