package com.pixelpanel.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HudElementRegistry {
    private final List<HudElement> elements = new ArrayList<>();

    public void add(HudElement element) {
        elements.add(element);
    }

    public void remove(String id) {
        elements.removeIf(e -> e.getId().equals(id));
    }

    public Optional<HudElement> getById(String id) {
        return elements.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public List<HudElement> getAll() {
        return Collections.unmodifiableList(elements);
    }

    public void clear() {
        elements.clear();
    }

    public boolean hasType(HudElementType type) {
        return elements.stream().anyMatch(e -> e.getType() == type);
    }
}
