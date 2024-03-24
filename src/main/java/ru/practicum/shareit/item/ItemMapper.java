package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    public ItemDto toDto(Item item);


    public Item fromDto(ItemDto itemDto);
}
