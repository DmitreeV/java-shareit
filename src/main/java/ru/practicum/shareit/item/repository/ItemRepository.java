package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private int id = 1;

    private int generateId() {
        return id++;
    }

    public Item saveItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    public Item getItemById(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь не найдена!");
        }
        items.put(item.getId(), item);
        return item;
    }

    public List<Item> getItemByText(String text) {
        List<Item> result = new ArrayList<>();
        for (Item i : items.values()) {
            String name = i.getName().toLowerCase();
            String description = i.getDescription().toLowerCase();
            if ((name.contains(text) || description.contains(text)) && i.getAvailable()) {
                result.add(i);
            }
        }
        return result;
    }
}
