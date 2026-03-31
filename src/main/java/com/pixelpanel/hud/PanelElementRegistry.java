package com.pixelpanel.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PanelElementRegistry {
    private final List<PanelElement> elements = new ArrayList<>();

    public void add(PanelElement element) { elements.add(element); }
    public void remove(String id) {
        elements.stream().filter(e -> e.getId().equals(id)).findFirst().ifPresent(PanelElement::close);
        elements.removeIf(e -> e.getId().equals(id));
    }
    public Optional<PanelElement> getById(String id) {
        return elements.stream().filter(e -> e.getId().equals(id)).findFirst();
    }
    public List<PanelElement> getAll() { return Collections.unmodifiableList(elements); }
    public void clear() { elements.forEach(PanelElement::close); elements.clear(); }
    public boolean hasType(PanelElementType type) {
        return elements.stream().anyMatch(e -> e.getType() == type);
    }
}
