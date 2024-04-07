package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor
@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
            return new ItemDto(
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getId(),
                    item.getRequest() != null ? item.getRequest().getId() : null
            );
    }

    public Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }
}
