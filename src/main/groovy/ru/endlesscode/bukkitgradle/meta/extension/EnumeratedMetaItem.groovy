package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.GradleException


class EnumeratedMetaItem extends MetaItem {

    final Set<Object> allowedValues

    EnumeratedMetaItem(String id, boolean required, def value = null, Object... allowedValues = []) {
        super(id, required, value)
        this.allowedValues = new HashSet<>(Arrays.asList(allowedValues))
    }

    @Override
    void validate() {
        if (super.validate()) {
            if (value != null && !allowedValues.contains(value)) {
                throw new GradleException("Plugin metadata parse error: '$id' allowed values is $allowedValues")
            }
        }
    }
}
