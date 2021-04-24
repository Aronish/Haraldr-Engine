package haraldr.ui.groups;

import haraldr.ui.components.UIPositionable;

public record ConstraintInsertData(UIPositionable component, UIConstraint... constraint) {}
