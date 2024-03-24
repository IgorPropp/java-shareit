package ru.practicum.shareit.item.storage;

public class ItemStorageImpl {
//    private Long id = 1L;
//    private final List<Item> items = new ArrayList<>();
//
//    public List<ItemDto> getItems(Long userId) {
//        List<ItemDto> itemsByUserId = new ArrayList<>();
//        for (Item item : items) {
//            if (item.getOwner().equals(userId)) {
//                itemsByUserId.add(ItemMapper.toDto(item));
//            }
//        }
//        log.info("List of all items requested");
//        return itemsByUserId;
//    }
//
//    public ItemDto createItem(Long userId, ItemDto itemDto) {
//        itemDto.setId(id);
//        Item item = ItemMapper.fromDto(userId, itemDto);
//        item.setOwner(userId);
//        items.add(item);
//        log.info("Item id={} created", id);
//        id++;
//        return itemDto;
//    }
//
//    public void deleteItem(Long userId, Long itemId) throws IllegalAccessException {
//        Iterator<Item> itemIterator = items.listIterator();
//        while (itemIterator.hasNext()) {
//            Item item = itemIterator.next();
//            if (item.getId().equals(userId) && item.getId().equals(itemId)) {
//                itemIterator.remove();
//                log.info("Item id={} deleted", itemId);
//                return;
//            }
//        }
//        throw new IllegalAccessException("Incorrect id");
//    }
//
//    public ItemDto updateItem(Long userId, ItemDto itemDto) {
//        for (Item item : items) {
//            if (item.getOwner().equals(userId)) {
//                if (itemDto.getName() != null) {
//                    item.setName(itemDto.getName());
//                }
//                if (itemDto.getDescription() != null) {
//                    item.setDescription(itemDto.getDescription());
//                }
//                if (itemDto.getAvailable() != null) {
//                    item.setAvailable(itemDto.getAvailable());
//                }
//                log.info("Item id={} updated", item.getId());
//                return ItemMapper.toDto(item);
//            }
//        }
//        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incorrect user ID or item is not exist");
//    }
//
//    public ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException {
//        for (Item item : items) {
//            if (item.getId().equals(itemId)) {
//                log.info("Item id={} is requested", itemId);
//                return ItemMapper.toDto(item);
//            }
//        }
//        throw new IllegalAccessException("Incorrect id");
//    }
//
//    public List<ItemDto> searchForItem(Long userId, String string) {
//        List<ItemDto> itemsAfterSearch = new ArrayList<>();
//        if (string.isEmpty()) {
//            log.info("Item search for '{}' is NOT successful", string);
//            return itemsAfterSearch;
//        }
//
//        for (Item item : items) {
//            if (item.getName().toLowerCase().contains(string) &&
//                    item.getAvailable()) {
//                itemsAfterSearch.add(ItemMapper.toDto(item));
//            } else if (item.getDescription().toLowerCase().contains(string) &&
//                    item.getAvailable()) {
//                itemsAfterSearch.add(ItemMapper.toDto(item));
//            }
//        }
//        log.info("Item search for '{}' is successful", string);
//        return itemsAfterSearch;
//    }

}
