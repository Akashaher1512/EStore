package com.java.electro.store.helper;

import com.java.electro.store.dto.PageableResponse;
import com.java.electro.store.dto.UserDto;
import com.java.electro.store.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper {

    // E => entity
    // D => Dto
    public static <E,D> PageableResponse<D> getPageableResponse(Page<E> page , Class<D> type){
        List<E> entity = page.getContent();
        List<D> dtoList = entity.stream().map((object) -> new ModelMapper().map(object ,type)).collect(Collectors.toList());

        PageableResponse<D> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElement(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }


}
